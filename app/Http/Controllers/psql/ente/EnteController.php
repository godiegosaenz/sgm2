<?php

namespace App\Http\Controllers\psql\ente;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlEnte;
use App\Models\PsqlCtlgItem;
use App\Models\PsqlEnteTelefono;
use App\Models\PsqlEnteCorreo;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class EnteController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        if(!Auth()->user()->hasPermissionTo('Lista de clientes'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('ente.ente');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        if(!Auth()->user()->hasPermissionTo('Ingresar cliente'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('ente.enteCrear');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $request->validate([
            'ci_ruc' => 'required|string|max:20',
            'nombres' => 'required|string|max:100',
            'apellidos' => 'required|string|max:100',
            'es_persona' => 'required|boolean',
            'direccion' => 'nullable|string|max:255',
            'fecha_nacimiento' => 'nullable|date',
            'correo' => 'nullable|email',
            'telefono' => 'nullable|string|max:20',
        ]);

        DB::beginTransaction();
        try{
            $validaPersona=PsqlEnte::where('ci_ruc',$request->ci_ruc)
            ->first();

            if(!is_null($validaPersona)){
                if($validaPersona->estado==="A"){
                //     return response()->json(['message' => 'Ya existe una persona con ese numero de identificacion','error'=>true], 500);
                // }else{
                    $validaPersona->ci_ruc = $request->ci_ruc;
                    if($request->es_persona== 1)
                    {
                        $tipo_documento=605;
                        $validaPersona->nombres = strtoupper(str_replace(' ', ' ', $request->nombres));
                        $validaPersona->apellidos = strtoupper(str_replace(' ', ' ', $request->apellidos));
                    }else
                    {
                        $tipo_documento=606;
                        $validaPersona->nombres = strtoupper(str_replace(' ', ' ', $request->nombres));
                        $validaPersona->apellidos = strtoupper(str_replace(' ', ' ', $request->apellidos));
                        $validaPersona->razon_social = strtoupper(str_replace(' ', ' ', $request->nombres));
                        $validaPersona->nombre_comercial = strtoupper(str_replace(' ', ' ', $request->apellidos));
                    }
                    $validaPersona->es_persona = $request->es_persona;
                    $validaPersona->direccion = strtoupper(str_replace(' ', ' ', $request->direccion));
                    $validaPersona->fecha_nacimiento = $request->fecha_nacimiento;
                    $validaPersona->telefono = $request->telefono;
                    $validaPersona->correo = $request->correo;
                    $validaPersona->tipo_documento = $tipo_documento;

                    $validaPersona->save();
                    if(isset($request->es_transito)){
                        $guardaTelefonoCorreo=$this->storeTlfEmail($request->telefono, $request->correo, $validaPersona->id);
                    }
                    return response()->json(['message' => 'Persona actualida correctamente'], 200);
                }
            }

            $ente = new PsqlEnte();
            $ente->ci_ruc = $request->ci_ruc;
            if($request->es_persona== 1)
            {
                $tipo_documento=605;
                $ente->nombres = strtoupper(str_replace(' ', ' ', $request->nombres));
                $ente->apellidos = strtoupper(str_replace(' ', ' ', $request->apellidos));
            }else
            {
                $tipo_documento=606;
                $ente->nombres = strtoupper(str_replace(' ', ' ', $request->nombres));
                $ente->apellidos = strtoupper(str_replace(' ', ' ', $request->apellidos));
                $ente->razon_social = strtoupper(str_replace(' ', ' ', $request->nombres));
                $ente->nombre_comercial = strtoupper(str_replace(' ', ' ', $request->apellidos));
            }
            $ente->es_persona = $request->es_persona;
            $ente->direccion = strtoupper(str_replace(' ', '', $request->direccion));
            $ente->fecha_nacimiento = $request->fecha_nacimiento;
            $ente->tipo_documento = $tipo_documento;
            

            // $ente->telefono = $request->telefono;
            // $ente->correo = $request->correo;
            $ente->save();
            if(isset($request->es_transito)){
                $guardaTelefonoCorreo=$this->storeTlfEmail($request->telefono, $request->correo, $ente->id);
            }

           

            return response()->json(['message' => 'Persona creada correctamente'], 200);
        } catch (\Exception $e) {
            // En caso de error, deshacer la transacción
            DB::rollBack();

            return redirect()->back()->withErrors('Hubo un error al actualizar el ente: ' . $e->getMessage());
        }
    }

    public function storeTlfEmail($telefono, $correo, $id){
        try{
            $verificaTlfo=PsqlEnteTelefono::where('ente',$id)->delete();
            $verificaCorreo=PsqlEnteCorreo::where('ente',$id)->delete();

            $guardaTlfo=new PsqlEnteTelefono();
            $guardaTlfo->telefono=$telefono;
            $guardaTlfo->ente=$id;
            $guardaTlfo->save();

            $guardaCorreo=new PsqlEnteCorreo();
            $guardaCorreo->email=$correo;
            $guardaCorreo->ente=$id;
            $guardaCorreo->save();

            return ["error"=>false];
        } catch (\Exception $e) {
          
            return ["error"=>true];
        }
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        return view('ente.enteEditar');
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        $ente = PsqlEnte::find($id);
        $ctlg_item = PsqlCtlgItem::where('catalogo',29)->get();
        return view('ente.enteEditar',compact('ente','ctlg_item'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $r, string $id)
    {
        $messages = [
            'required' => 'El campo :attribute es requerido.',
            'unique' => 'El numero de :attribute ingresado ya existe',
            'size' => 'El campo :attribute debe tener exactamente :size caracteres',
            'max' => 'El campo :attribute no debe exceder los :max caracteres',
            'telefono.required' => 'Debe ingresar al menos un número de teléfono.',
            'telefono.*.regex' => 'Cada número de teléfono debe ser válido y tener 10 dígitos.',
            'correo.required' => 'Debe ingresar al menos un correo.',
            'correo.*.email' => 'Cada correo debe ser válido.',
        ];
        if($r->tipo_persona == 'natural'){
            if($r->discapacidad == 'on'){
                $reglas = [
                        'cedula' => ['bail','required','size:10', Rule::unique('App\Models\PsqlEnte','ci_ruc')->ignore($id)],
                        'nombres' => 'bail|required|max:250',
                        'apellidos' => 'bail|required|max:250',
                        'fecha_nacimiento' => 'required|date',
                        'direccion' => 'required',
                        'contabilidad' => 'bail',
                        'tipo_persona' => 'required',
                        'telefono' => 'required|max:12',
                        'tipo_discapacidad' => 'required',
                        'porcentaje_discapacidad' => 'required',
                        'correo' => 'required|array|min:1', // Asegura que `correo` sea un array y tenga al menos un elemento
                        'correo.*' => 'required|email', // Cada elemento del array `correo` debe ser un email válido
                        'telefono' => 'required|array|min:1', // Asegura que `telefono` sea un array y tenga al menos un elemento
                        'telefono.*' => 'required|regex:/^[0-9]{10}$/', // Cada teléfono debe ser un número de 10 dígitos
                ];
            }else{
                $reglas = [
                    'cedula' => ['bail','required','size:10', Rule::unique('App\Models\PsqlEnte','ci_ruc')->ignore($id)],
                    'nombres' => 'bail|required|max:250',
                    'apellidos' => 'bail|required|max:250',
                    'fecha_nacimiento' => 'required|date',
                    'direccion' => 'required',
                    'contabilidad' => 'bail',
                    'tipo_persona' => 'required',
                    'telefono' => 'required|max:12',
                    'tipo_discapacidad' => '',
                    'porcentaje_discapacidad' => '',
                    'correo' => 'required|array|min:1', // Asegura que `correo` sea un array y tenga al menos un elemento
                    'correo.*' => 'required|email', // Cada elemento del array `correo` debe ser un email válido
                    'telefono' => 'required|array|min:1', // Asegura que `telefono` sea un array y tenga al menos un elemento
                    'telefono.*' => 'required|regex:/^[0-9]{10}$/', // Cada teléfono debe ser un número de 10 dígitos
                ];
            }
        }else{

            $reglas = [
                'ruc' => ['bail','required','size:13', Rule::unique('App\Models\PsqlEnte','ci_ruc')->ignore($id)],
                'razon_social' => 'bail|required|max:250',
                'nombre_comercial' => 'bail|max:250',
                'fecha_nacimiento' => 'required',
                'estadoCivil' => 'required',
                'direccion' => 'required',
                'contabilidad' => 'bail',
                'tipo_persona' => 'required',
                'telefono' => 'required|max:12',
                'correo' => 'required|array|min:1', // Asegura que `correo` sea un array y tenga al menos un elemento
                'correo.*' => 'required|email', // Cada elemento del array `correo` debe ser un email válido
                'telefono' => 'required|array|min:1', // Asegura que `telefono` sea un array y tenga al menos un elemento
                'telefono.*' => 'required|regex:/^[0-9]{10}$/', // Cada teléfono debe ser un número de 10 dígitos
            ];
        }


        $validator = Validator::make($r->all(),$reglas,$messages);

        if ($validator->fails()) {
            return redirect('/ente/editar/'.$id)
                        ->withErrors($validator)
                        ->withInput();
        }

        // Iniciar una transacción
        DB::beginTransaction();

        try {
            $ente = PsqlEnte::findOrFail($id);

            $validatedData = $validator->validated();
            $tipo_documento = $validatedData['tipo_persona'] === 'natural'
            ? '605' // Valor para persona natural
            : '606'; // Valor para persona jurídica

            $timestamp = now()->timestamp;
            $fechaLegible = Carbon::createFromTimestamp($timestamp)->toDateTimeString();

            // Crear el nuevo contribuyente
            $ente->update([
                'ci_ruc' => $validatedData['cedula'] ?? $validatedData['ruc'],
                'nombres' => $validatedData['nombres'],
                'apellidos' => $validatedData['apellidos'],
                'es_persona' => $validatedData['tipo_persona'] === 'natural', // true si es 'natural', false si es 'juridica',
                'direccion' => $validatedData['direccion'] ?? null, // Opcional
                'fecha_nacimiento' => $validatedData['fecha_nacimiento'] ?? $validatedData['fecha_constitucion'], // Opcional
                'estado' => 'A', // Opcional
                'lleva_contabilidad' => $validatedData['contabilidad'] ?? false, // Opcional
                'tipo_documento' => $tipo_documento,
                'discapacidad' => $validatedData['tipo_discapacidad'] ?? null,
                'porcentaje' => $validatedData['porcentaje_discapacidad'] ?? null,
                //'num_establecimiento' => $validatedData['num_establecimiento'] ?? null, // Opcional
                'razon_social' => $validatedData['nombre_comercial'] ?? null, // Opcional
                'nombre_comercial' => $validatedData['estado_establecimiento_id'] ?? null,
                'user_mod' => Auth()->user()->name, // Usuario que crea el registro
                'fecha_mod' => $fechaLegible, // Usuario que crea el registro
            ]);


            // Obtener correos actuales en la base de datos
            $currentCorreos = $ente->correo->pluck('email')->toArray();

            // Obtener correos nuevos desde los datos validados
            $newCorreos = $validatedData['correo'];

            // Filtrar correos nuevos, eliminando aquellos que ya existen en la base de datos
            $newCorreos = array_diff($newCorreos, $currentCorreos);

             // Agregar nuevos correos
            foreach ($newCorreos as $id => $correo) {

                $ente->correo()->create(['email' => $correo, 'ente' => $ente->id]);
            }


            // Obtener correos actuales en la base de datos
            $currentTelefonos = $ente->telefono->pluck('telefono')->toArray();

            // Obtener correos nuevos desde los datos validados
            $newTelefonos = $validatedData['telefono'];

            // Filtrar correos nuevos, eliminando aquellos que ya existen en la base de datos
            $newTelefonos = array_diff($newTelefonos, $currentTelefonos);


            foreach ($newTelefonos as $id => $telefono) {
                $ente->telefono()->create(['telefono' => $telefono, 'ente' => $ente->id]);
            }

            // Confirmar la transacción
            DB::commit();

            return redirect()->route('edit.ente', $ente->id)->with('success', 'Ente actualizado exitosamente.');

        } catch (\Exception $e) {
            // En caso de error, deshacer la transacción
            DB::rollBack();

            return redirect()->back()->withErrors('Hubo un error al actualizar el ente: ' . $e->getMessage());
        }

    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }

    public function datatables(Request $r){
        if($r->ajax()){
            if($r->tipo == 'propietario'){
                $listapersona = PsqlEnte::select('id','ci_ruc','nombres','apellidos')->get();
                return Datatables($listapersona)
                ->addColumn('action', function ($listapersona) {
                    $buttonPersona = '';
                    return '<a class="btn btn-primary btn-sm" onclick="seleccionarpropietario(\''.$listapersona->id.'\',\''.$listapersona->ci_ruc.'\',\''.$listapersona->nombres.'\',\''.$listapersona->apellidos.'\')">Seleccionar</a>';
                    return $buttonPersona;

                })
                ->rawColumns(['action'])
                ->make(true);
            }else{
                $listapersona = PsqlEnte::select('id','ci_ruc','nombres','apellidos')->get();
                return Datatables($listapersona)
                ->addColumn('action', function ($listapersona) {
                    $buttonPersona = '';
                    return '<a class="btn btn-primary btn-sm" onclick="seleccionarrepresentante(\''.$listapersona->id.'\',\''.$listapersona->ci_ruc.'\',\''.$listapersona->nombres.'\',\''.$listapersona->apellidos.'\')">Seleccionar</a>';
                    return $buttonPersona;

                })
                ->rawColumns(['action'])
                ->make(true);
            }

       };
    }

    public function getEnteCedula(Request $r){
        $query = $r->input('query');
        $data = PsqlEnte::select('id','ci_ruc','nombres','apellidos')->where('ci_ruc',$query)->first();
        // dd($data);
        if ($data) {
            // Devolver la información en formato JSON
            return response()->json($data, 200);
        } else {
            // Devolver un error 404 si no se encuentra nada
            return response()->json(['message' => 'No encontrado'], 404);
        }
    }

    public function datatablesente(){
        $listapersona = PsqlEnte::select('id','ci_ruc','nombres','apellidos')->get();
        return Datatables($listapersona)
        ->addColumn('action', function ($listapersona) {
            $buttonPersona = '';
            $buttonPersona .= '<a class="btn btn-warning btn-sm" href="'.route('edit.ente',$listapersona->id).'"><i class="bi bi-pencil"></i></a> ';
            return $buttonPersona;
        })
        ->rawColumns(['action'])
        ->make(true);
    }
}
