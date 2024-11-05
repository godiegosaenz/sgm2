<?php

namespace App\Http\Controllers\psql\ente;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlEnte;
use App\Models\PsqlCtlgItem;
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
        return view('ente.ente');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('ente.enteCrear');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        //
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
