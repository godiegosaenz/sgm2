<?php

namespace App\Http\Controllers\rentas;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlPaClaseContribuyente;
use App\Models\PsqlProvincia;
use App\Models\PsqlCanton;
use App\Models\PsqlParroquia;
use Exception;
use Illuminate\Support\Facades\Validator;
use App\Models\PsqlCatastroContribuyente;
use App\Models\CatastroLocal;
use App\Models\PsqlActividadesCont;
use Illuminate\Validation\Rule;
use GuzzleHttp\Client;
use DB;
class CatastroContribuyente extends Controller
{
    private $clientNacional = null;

    public function __construct(){
        try{
            $ip="https://srienlinea.sri.gob.ec/movil-servicios/";
          
            $this->clientNacional = new Client([
                'base_uri' =>$ip,
                'verify' => false,
            ]);

        }catch(Exception $e){
            Log::error($e->getMessage());
        }
    }
    public function index()
    {
        $data = PsqlCatastroContribuyente::all();
        $PsqlProvincia = PsqlProvincia::all();
        $totalCatastro = $data->count();
        $totalCatastroActivo = $data->where('estado', '1')->count();
        $totalCatastroInactivo = $data->where('estado', '2')->count();
        return view('rentas.catastroContribuyente',compact('totalCatastro','totalCatastroActivo','totalCatastroInactivo','PsqlProvincia'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $cantones = [];
        $parroquias = [];
        $PsqlProvincia = PsqlProvincia::all();
        $clase = PsqlPaClaseContribuyente::all();
        return view('rentas.catastroContribuyenteCrear',compact('clase','PsqlProvincia','cantones','parroquias'));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $r)
    {
        try {
            $attributes = [
                'propietario_id' => 'Propietario',
                'ruc' => 'RUC',
                'observacion' => 'Observacion',
                'tipo' => 'Tipo de exoneracion',
                'estado_contribuyente_id' => 'Estado contribuyente',
                'estado_establecimiento_id' => 'Estado establecimiento',
                'clase_contribuyente_id' => 'Clase contribuyente',
                'tipo_contribuyente_id' => 'Tipo de contribuyente',
            ];
            $messages = [
                'actividades.required' => 'Debe seleccionar al menos una actividad comercial',
                'required' => 'El campo :attribute es obligatorio.',
                'max' => 'El campo :attribute no puede exceder los :max caracteres.',
                'size' => 'El campo :attribute debe tener exactamente :size caracteres.',
                'email' => 'El campo :attribute debe ser un email válido.',
                'numeric' => 'El campo :attribute debe ser numérico.',
                'digits' => 'El campo :attribute debe tener :digits dígitos.',
                'regex' => 'El formato del campo :attribute no es válido.',
                'boolean' => 'El campo :attribute debe ser verdadero o falso.',
                'date' => 'El campo :attribute debe ser una fecha válida.',
                'array' => 'El campo :attribute debe ser una lista.',
                'min' => 'El campo :attribute debe tener al menos :min elementos.',
                'nullable' => 'El campo :attribute es opcional.',
                'unique' => 'El valor del campo :attribute ya ha sido registrado y debe ser único.',
            ];
            $reglas = [
                'propietario_id' => ['bail','required', Rule::unique('App\Models\PsqlCatastroContribuyente','propietario_id')],
                'representante_id' => 'nullable',
                //'ruc' => 'required|size:13|unique:psql_catastro_contribuyente,ruc',
                'ruc' => ['bail','size:13','required',
                    function ($attribute, $value, $fail) {
                        // Consulta en la segunda base de datos
                        $exists = PsqlCatastroContribuyente::where('ruc', $value)->exists();

                        if ($exists) {
                            $fail('El RUC ya está registrado en otro usuario');
                        }
                    },
                ],
                'propietario' => '',
                'representante' => '',
                'nombresPropietario2' => '',
                'nombresRepresentante2' => '',
                'razon_social' => 'required|max:255',
                'estado_contribuyente_id' => 'required',
                'nombre_comercial' => 'max:255',
                'clase_contribuyente_id' => 'required',
                'tipo_contribuyente_id' => 'required',
                'estado_establecimiento_id' => 'required',
                'tipo_local' => 'required',
                'obligado_contabilidad' => 'nullable|boolean',
                'es_matriz' => 'nullable|boolean',
                'es_turismo' => 'nullable|boolean',
                'actividades' => 'required|array|min:1',

                'fecha_inicio_actividad' => 'required|date',
                'fecha_actualizacion_actividades' => 'nullable|date',
                'fecha_reinicio_actividades' => 'nullable|date',
                'fecha_suspension_definitiva' => 'nullable|date',
                'provincia_id' => 'required',
                'canton_id' => 'required',
                'parroquia_id' => 'required',
                'calle_principal' => 'required|max:255',
                'calle_secundaria' => 'max:255',
                'referencia_ubicacion' => 'required|max:255',
                'direccion' => 'max:255',
                'correo' => 'required|email',
                'telefono' => 'required|numeric|digits:10|regex:/^09[0-9]{8}$/',
            ];
            $validator = Validator::make($r->all(),$reglas,$messages,$attributes);

            if ($validator->fails()) {
                $cantones = PsqlCanton::where('id_provincia',$r->provincia_id)->get();
                $parroquia = PsqlParroquia::where('id_canton',$r->canton_id)->get();
                return redirect('catastrocontribuyente')
                ->withErrors($validator)
                ->withInput()
                ->with('cantones', $cantones)
                ->with('parroquia', $parroquia);
            }

           // Obtener los datos validados
        $validatedData = $validator->validated();
        // dd($r->all());

        // Crear el nuevo contribuyente
        $contribuyente = PsqlCatastroContribuyente::create([
            'ruc' => $validatedData['ruc'],
            'razon_social' => $validatedData['razon_social'],
            'estado_contribuyente_id' => $validatedData['estado_contribuyente_id'],
            'fecha_inicio_actividades' => $validatedData['fecha_inicio_actividad'],
            'fecha_actualizacion_actividades' => $validatedData['fecha_actualizacion_actividades'] ?? null, // Opcional
            'fecha_reinicio_actividades' => $validatedData['fecha_reinicio_actividades'] ?? null, // Opcional
            'fecha_suspension_definitiva' => $validatedData['fecha_suspension_definitiva'] ?? null, // Opcional
            'obligado_contabilidad' => $validatedData['obligado_contabilidad'] ?? false, // Opcional
            'tipo_contribuyente' => $validatedData['tipo_contribuyente_id'],
            //'num_establecimiento' => $validatedData['num_establecimiento'] ?? null, // Opcional
            'nombre_fantasia_comercial' => $validatedData['nombre_comercial'] ?? null, // Opcional
            'estado_establecimiento' => $validatedData['estado_establecimiento_id'],
            'provincia_id' => $validatedData['provincia_id'],
            'canton_id' => $validatedData['canton_id'],
            'parroquia_id' => $validatedData['parroquia_id'],
            'calle_principal' => $validatedData['calle_principal'],
            'calle_secundaria' => $validatedData['calle_secundaria'] ?? null, // Opcional
            'referencia_ubicacion' => $validatedData['referencia_ubicacion'] ?? null, // Opcional
            'correo_1' => $validatedData['correo'],
            'telefono' => $validatedData['telefono'],
            'local_propio' => $validatedData['tipo_local'] ?? null, // Opcional
            'es_matriz' => $validatedData['es_matriz']  ?? false, // Opcional
            'es_turismo' => $validatedData['es_turismo']  ?? false, // Opcional
            'usuario_ingreso' => auth()->user()->name, // Usuario que crea el registro
            'propietario_id' => $validatedData['propietario_id']?? null, // Usuario que crea el registro
            'representante_legal_id' => $validatedData['representante_id']?? null, // Usuario que crea el registro
            'clase_contribuyente_id' => $validatedData['clase_contribuyente_id'], // Usuario que crea el registro
        ]);
        // 4. Guardar las actividades relacionadas (solo los IDs como mencionaste)
        foreach ($r->actividades as $key => $actividad) {
            $contribuyente->actividades()->attach($key);
        }

        //guardar en la tabla local el establecimiento matriz
        $registra=new CatastroLocal();
        $registra->idcatastro_contr=$contribuyente->id;
        $registra->provincia_id=$contribuyente->provincia_id;
        $registra->canton_id=$contribuyente->canton_id;
        $registra->parroquia_id=$contribuyente->parroquia_id;
        $registra->calle_principal=$contribuyente->calle_principal;
        $registra->calle_secundaria=$contribuyente->calle_secundaria;
        $registra->referencia_ubicacion=$contribuyente->referencia_ubicacion;
        $registra->actividad_descripcion=$contribuyente->nombre_fantasia_comercial;
        $registra->estado_establecimiento=$contribuyente->estado_establecimiento;
        $registra->local_propio=$contribuyente->local_propio;
        $registra->estado='A';
        $registra->fecha_registr=date('Y-m-d H:i:s');
        $registra->id_usuario_reg=auth()->user()->idpersona;
      
        if($registra->save()){

        }else{
            return redirect('catastrocontribuyente')->with('error', 'Ocurrió un error al registrar el local');
        }

        return redirect('catastrocontribuyente')->with('success', 'Contribuyente creado exitosamente');
        } catch (Exception $e) {
             // Redirigir con un mensaje de error
            return redirect('catastrocontribuyente')->with('error', 'Ocurrió un error al registrar el contribuyente. '.$e->getMessage());
        }
    }

    public function buscarContribuyente(Request $request){
        $data = [];
        if($request->has('q')){
            $search = $request->q;
            $data=DB::connection('pgsql')->table('sgm_app.cat_ente as ente')
            ->join('sgm_patente.pa_catastro_contribuyente as cont','cont.propietario_id','ente.id')
            ->where(function($query)use($search){
                $query->where('ente.ci_ruc', 'ilike', '%'.$search.'%')
                ->orwhere(DB::raw("CONCAT(apellidos, ' ', nombres)"), 'ilike', '%'.$search.'%');
            })   
            ->where('cont.estado_contribuyente_id',1)         
            ->select('cont.id AS idper','ci_ruc as documento',DB::raw("CONCAT(apellidos,' ',nombres) AS nombre"))
            ->take(10)->get();

        }
        return response()->json($data);
    }
    
    public function buscarActividad(Request $request){
        $data = [];
        if($request->has('q')){
            $search = $request->q;
            $data=DB::connection('pgsql')->table('sgm_patente.pa_ctlg_actividades_comerciales')
            ->where(function($query)use($search){
                $query->where('ciiu', 'ilike', '%'.$search.'%')
                ->orwhere('descripcion', 'ilike', '%'.$search.'%');
            })            
            ->select('id','ciiu','descripcion AS nombre')
            ->take(10)->get();

        }
        return response()->json($data);
    }

    public function buscarRucContribuyente(Request $request){
        $data = [];
        if($request->has('q')){
            $cedula = $request->q;
           
            $response = $this->clientNacional->request('GET', "api/v1.0/deudas/porIdentificacion/{$cedula}",[
                'headers' => [
                    // 'Authorization'=>'bearer '.$token,
                    'Content-Type' => 'application/json'
                ],
            ]);
            
            $responseBody = json_decode($response->getBody(), true); 
            
            $data[] = [
                'id' => $responseBody['contribuyente']['identificacion'] ?? null,
                'text' => $responseBody['contribuyente']['nombreComercial'] ?? 'Sin nombre'
            ];
        }

        // Siempre se devuelve la variable $data, esté llena o vacía
        return response()->json($data);
    }

    public function store_respaldo(Request $r)
    {
        try {
            $attributes = [
                'propietario_id' => 'Propietario',
                'ruc' => 'RUC',
                'observacion' => 'Observacion',
                'tipo' => 'Tipo de exoneracion',
                'estado_contribuyente_id' => 'Estado contribuyente',
                'estado_establecimiento_id' => 'Estado establecimiento',
                'clase_contribuyente_id' => 'Clase contribuyente',
                'tipo_contribuyente_id' => 'Tipo de contribuyente',
            ];
            $messages = [
                'actividades.required' => 'Debe seleccionar al menos una actividad comercial',
                'required' => 'El campo :attribute es obligatorio.',
                'max' => 'El campo :attribute no puede exceder los :max caracteres.',
                'size' => 'El campo :attribute debe tener exactamente :size caracteres.',
                'email' => 'El campo :attribute debe ser un email válido.',
                'numeric' => 'El campo :attribute debe ser numérico.',
                'digits' => 'El campo :attribute debe tener :digits dígitos.',
                'regex' => 'El formato del campo :attribute no es válido.',
                'boolean' => 'El campo :attribute debe ser verdadero o falso.',
                'date' => 'El campo :attribute debe ser una fecha válida.',
                'array' => 'El campo :attribute debe ser una lista.',
                'min' => 'El campo :attribute debe tener al menos :min elementos.',
                'nullable' => 'El campo :attribute es opcional.',
                'unique' => 'El valor del campo :attribute ya ha sido registrado y debe ser único.',
            ];
            $reglas = [
                'propietario_id' => ['bail','required', Rule::unique('App\Models\PsqlCatastroContribuyente','propietario_id')],
                'representante_id' => 'nullable',
                //'ruc' => 'required|size:13|unique:psql_catastro_contribuyente,ruc',
                'ruc' => ['bail','size:13','required',
                    function ($attribute, $value, $fail) {
                        // Consulta en la segunda base de datos
                        $exists = PsqlCatastroContribuyente::where('ruc', $value)->exists();

                        if ($exists) {
                            $fail('El RUC ya está registrado en otro usuario');
                        }
                    },
                ],
                'propietario' => '',
                'representante' => '',
                'nombresPropietario2' => '',
                'nombresRepresentante2' => '',
                'razon_social' => 'required|max:255',
                'estado_contribuyente_id' => 'required',
                'nombre_comercial' => 'max:255',
                'clase_contribuyente_id' => 'required',
                'tipo_contribuyente_id' => 'required',
                'estado_establecimiento_id' => 'required',
                'tipo_local' => 'required',
                'obligado_contabilidad' => 'nullable|boolean',
                'es_matriz' => 'nullable|boolean',
                'es_turismo' => 'nullable|boolean',
                'actividades' => 'required|array|min:1',

                'fecha_inicio_actividad' => 'required|date',
                'fecha_actualizacion_actividades' => 'nullable|date',
                'fecha_reinicio_actividades' => 'nullable|date',
                'fecha_suspension_definitiva' => 'nullable|date',
                'provincia_id' => 'required',
                'canton_id' => 'required',
                'parroquia_id' => 'required',
                'calle_principal' => 'required|max:255',
                'calle_secundaria' => 'max:255',
                'referencia_ubicacion' => 'required|max:255',
                'direccion' => 'max:255',
                'correo' => 'required|email',
                'telefono' => 'required|numeric|digits:10|regex:/^09[0-9]{8}$/',
            ];
            $validator = Validator::make($r->all(),$reglas,$messages,$attributes);

            if ($validator->fails()) {
                $cantones = PsqlCanton::where('id_provincia',$r->provincia_id)->get();
                $parroquia = PsqlParroquia::where('id_canton',$r->canton_id)->get();
                return redirect('catastrocontribuyente')
                ->withErrors($validator)
                ->withInput()
                ->with('cantones', $cantones)
                ->with('parroquia', $parroquia);
            }

           // Obtener los datos validados
        $validatedData = $validator->validated();

        // Crear el nuevo contribuyente
        $contribuyente = PsqlCatastroContribuyente::create([
            'ruc' => $validatedData['ruc'],
            'razon_social' => $validatedData['razon_social'],
            'estado_contribuyente_id' => $validatedData['estado_contribuyente_id'],
            'fecha_inicio_actividades' => $validatedData['fecha_inicio_actividad'],
            'fecha_actualizacion_actividades' => $validatedData['fecha_actualizacion_actividades'] ?? null, // Opcional
            'fecha_reinicio_actividades' => $validatedData['fecha_reinicio_actividades'] ?? null, // Opcional
            'fecha_suspension_definitiva' => $validatedData['fecha_suspension_definitiva'] ?? null, // Opcional
            'obligado_contabilidad' => $validatedData['obligado_contabilidad'] ?? false, // Opcional
            'tipo_contribuyente' => $validatedData['tipo_contribuyente_id'],
            //'num_establecimiento' => $validatedData['num_establecimiento'] ?? null, // Opcional
            'nombre_fantasia_comercial' => $validatedData['nombre_comercial'] ?? null, // Opcional
            'estado_establecimiento' => $validatedData['estado_establecimiento_id'],
            'provincia_id' => $validatedData['provincia_id'],
            'canton_id' => $validatedData['canton_id'],
            'parroquia_id' => $validatedData['parroquia_id'],
            'calle_principal' => $validatedData['calle_principal'],
            'calle_secundaria' => $validatedData['calle_secundaria'] ?? null, // Opcional
            'referencia_ubicacion' => $validatedData['referencia_ubicacion'] ?? null, // Opcional
            'correo_1' => $validatedData['correo'],
            'telefono' => $validatedData['telefono'],
            'local_propio' => $validatedData['tipo_local'] ?? null, // Opcional
            'es_matriz' => $validatedData['es_matriz']  ?? false, // Opcional
            'es_turismo' => $validatedData['es_turismo']  ?? false, // Opcional
            'usuario_ingreso' => auth()->user()->name, // Usuario que crea el registro
            'propietario_id' => $validatedData['propietario_id']?? null, // Usuario que crea el registro
            'representante_legal_id' => $validatedData['representante_id']?? null, // Usuario que crea el registro
            'clase_contribuyente_id' => $validatedData['clase_contribuyente_id'], // Usuario que crea el registro
        ]);
        // 4. Guardar las actividades relacionadas (solo los IDs como mencionaste)
        foreach ($r->actividades as $key => $actividad) {
            $contribuyente->actividades()->attach($key);
        }

        //guardar en la tabla local el establecimiento matriz
        $registra=new CatastroLocal();
        $registra->idcatastro_contr=$contribuyente->id;
        $registra->provincia_id=$contribuyente->provincia_id;
        $registra->canton_id=$contribuyente->canton_id;
        $registra->parroquia_id=$contribuyente->parroquia_id;
        $registra->calle_principal=$contribuyente->calle_principal;
        $registra->calle_secundaria=$contribuyente->calle_secundaria;
        $registra->referencia_ubicacion=$contribuyente->referencia_ubicacion;
        $registra->actividad_descripcion="";
        $registra->estado_establecimiento=$contribuyente->estado_establecimiento;
        $registra->local_propio=$contribuyente->local_propio;
        $registra->estado='A';
        $registra->fecha_registr=date('Y-m-d H:i:s');
        $registra->id_usuario_reg=auth()->user()->idpersona;
        dd($registra);
        if($registra->save()){

        }else{
            return redirect('catastrocontribuyente')->with('error', 'Ocurrió un error al registrar el local');
        }

        return redirect('catastrocontribuyente')->with('success', 'Contribuyente creado exitosamente');
        } catch (Exception $e) {
             // Redirigir con un mensaje de error
            return redirect('catastrocontribuyente')->with('error', 'Ocurrió un error al registrar el contribuyente. '.$e->getMessage());
        }
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        // $contribuyente = PsqlCatastroContribuyente::findOrFail($id);
        $contribuyente=DB::connection('pgsql')->table('sgm_patente.pa_catastro_contribuyente as co')
        ->leftJoin('sgm_app.cat_ente as e','e.id','co.propietario_id')
        ->leftJoin('sgm_app.cat_provincia as p','p.id','co.provincia_id')
        ->leftJoin('sgm_app.cat_canton as c','c.id','co.canton_id')
        ->leftJoin('sgm_app.cat_parroquia as pa','pa.id','co.parroquia_id')
        ->leftJoin('sgm_patente.pa_clase_contribuyente as cc','cc.id','co.clase_contribuyente_id')
        ->where('co.id',$id)
        ->select('co.ruc','co.razon_social','co.id','co.ruc_representante_legal','nombre_representante_legal',
            'estado_contribuyente_id','fecha_inicio_actividades','fecha_actualizacion_actividades','fecha_reinicio_actividades'
            ,'fecha_suspension_definitiva','obligado_contabilidad','tipo_contribuyente','calle_principal','calle_secundaria',
            'referencia_ubicacion','co.direccion','correo_1','co.telefono','cc.nombre as clase_cont','e.fecha_nacimiento'
            ,DB::raw("EXTRACT(YEAR FROM age(e.fecha_nacimiento)) AS edad_contribuyente"),'c.nombre as nombre_canton'
            ,'p.descripcion as nombre_provincia','pa.descripcion as nombre_parroquia','co.archivo_ruc','co.archivo_artesano',
            'co.es_artesano',
            DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS contribuyente"))
        ->get();

        foreach($contribuyente as $key=>$value){
            $actividades=DB::connection('pgsql')->table('sgm_patente.pa_actividad_contribuyente as act')
            ->leftJoin('sgm_patente.pa_ctlg_actividades_comerciales as nom_act','nom_act.id','act.Actividad_comercial_id')
            ->where('Catastro_contribuyente_id',$value->id)
            ->where('act.estado','A')
            ->select(DB::raw("CONCAT(nom_act.descripcion) AS actividad"),'ciiu')
            ->get(); 
            $contribuyente[$key]->actividades=$actividades;
            // $value->act = $actividades->pluck('actividad')->toArray(); 

            $locales=DB::connection('pgsql')->table('sgm_patente.pa_locales')
            ->select('calle_principal','calle_secundaria','referencia_ubicacion','actividad_descripcion',
            'estado_establecimiento','local_propio')
            // ->select('calle_principal')
            ->where('idcatastro_contr',$value->id)
            ->get();
           

            $contribuyente[$key]->locales=$locales;
        }

        $contribuyente=$contribuyente[0];
        $PsqlProvincia = PsqlProvincia::all();

        $clase = PsqlPaClaseContribuyente::all();
        // dd($contribuyente);
        return view('rentas.catastroContribuyenteVer',compact('contribuyente','PsqlProvincia','clase'));
    }

    public function getCanton(Request $r){
        $cantones = PsqlCanton::where('id_provincia',$r->idprovincia)->get();
        $html = '<option value="" id="optionSelectCanton">Seleccione canton</option>';
        foreach ($cantones as $key => $value) {
            $html .= '<option value="'.$value->id.'">'.$value->nombre.'</option>';
        }
        //echo json_encode($cantones);
        return $html;
    }

    public function getParroquia(Request $r){
        $cantones = PsqlParroquia::where('id_canton',$r->idcanton)->get();
        $html = '<option value="" id="optionSelectParroquia">Seleccione parroquia</option>';
        foreach ($cantones as $key => $value) {
            $html .= '<option value="'.$value->id.'">'.$value->descripcion.'</option>';
        }
        //echo json_encode($cantones);
        return $html;
    }

    public function datatable(Request $r){
        $listacatastro = PsqlCatastroContribuyente::all();
        return Datatables($listacatastro)
        ->editColumn('obligado_contabilidad', function($listacatastro){
                if($listacatastro->estado_contribuyente_id == true){
                    return 'SI';
                }else{
                    return 'NO';
                }
            })
        ->editColumn('estado_contribuyente_id', function($listacatastro){
                if($listacatastro->estado_contribuyente_id == 1){
                    return '<span class="badge text-bg-success">Activo</span>';
                }else{
                    return '<span class="badge text-bg-danger">Inactivo</span>';
                }
            })
        ->addColumn('action', function ($listacatastro) {
            return '<a class="btn btn-primary btn-sm" onclick="seleccionarcontribuyente(\''.$listacatastro->id.'\')">Seleccionar</a>';
        })
        ->rawColumns(['action','estado_contribuyente_id','obligado_contabilidad'])
        ->make(true);
    }

    public function datatable2(Request $r){
        $listacatastro = PsqlCatastroContribuyente::all();

        return Datatables($listacatastro)
        ->editColumn('obligado_contabilidad', function($listacatastro){
            if($listacatastro->estado_contribuyente_id == true){
                return 'SI';
            }else{
                return 'NO';
            }
        })
        ->editColumn('estado_contribuyente_id', function($listacatastro){
            if($listacatastro->estado_contribuyente_id == 1){
                return '<span class="badge text-bg-success">Activo</span>';
            }else{
                return '<span class="badge text-bg-danger">Inactivo</span>';
            }
        })
        ->addColumn('action', function ($listacatastro) {
            $buttonPersona = '';
            // $buttonPersona .= '<a class="btn btn-primary btn-sm" href="'.route('show.catastro',$listacatastro->id).'">Ver</a> 
            // <a class="btn btn-success btn-sm" onclick="abrirModal('.$listacatastro->id.')">Locales</a> ';
            // return $buttonPersona;

            $buttonPersona .= '<a class="btn btn-primary btn-sm" href="'.route('show.catastro',$listacatastro->id).'">Ver</a> 
          ';
            return $buttonPersona;

        })
        ->rawColumns(['action','estado_contribuyente_id','obligado_contabilidad'])
        ->make(true);

    }

    public function getCatastroContribuyente(Request $r){
        $PsqlCatastroContribuyente = PsqlCatastroContribuyente::find($r->id);
        // Verifica si se encontró el registro
        if ($PsqlCatastroContribuyente) {
            return response()->json([
                'contribuyente' => $PsqlCatastroContribuyente,
                'propietarios' => $PsqlCatastroContribuyente->propietario, // Incluye los datos de los propietarios
                'representante_legal' => $PsqlCatastroContribuyente->representante_legal, // Incluye los datos de los propietarios
                'clase_contribuyente' => $PsqlCatastroContribuyente->clase_contribuyente, // Incluye los datos de los propietarios
                'actividades' => $PsqlCatastroContribuyente->actividades, // Incluye los datos de los propietarios
                'locales' => $PsqlCatastroContribuyente->locales,
            ]);
        } else {
            return response()->json(['message' => 'Registro no encontrado'], 404);
        }
    }

    public function guardaLocal(Request $request){
      
        try{
            if(isset($request->idEditarLocal) && $request->idEditarLocal>0){
                $verifica=CatastroLocal::where('provincia_id',$request->prov)
                ->where('canton_id',$request->cant)
                ->where('parroquia_id',$request->parr)
                ->where('calle_principal',$request->calle_princ)
                ->where('calle_secundaria',$request->calle_secund)
                ->where('referencia_ubicacion',$request->referencia)
                ->where('actividad_descripcion',$request->descr)
                ->where('estado_establecimiento',$request->establ)
                ->where('local_propio',$request->tipo)
                ->where('idcatastro_contr',$request->idcont)
                ->where('id','!=',$request->idEditarLocal)
                ->first();

                if(!is_null($verifica)){
                    if($verifica->estado=="A"){
                        return (['mensaje'=>'Ya existe la informacionxx','error'=>true]);
                    }
                }else{
                    //lo actualizamos
                    $verifica=CatastroLocal::find($request->idEditarLocal);
                    $verifica->idcatastro_contr=$request->idcont;
                    $verifica->provincia_id=$request->prov;
                    $verifica->canton_id=$request->cant;
                    $verifica->parroquia_id=$request->parr;
                    $verifica->calle_principal=$request->calle_princ;
                    $verifica->calle_secundaria=$request->calle_secund;
                    $verifica->referencia_ubicacion=$request->referencia;
                    $verifica->actividad_descripcion=$request->descr;
                    $verifica->estado_establecimiento=$request->establ;
                    $verifica->local_propio=$request->tipo;
                    $verifica->estado='A';
                    $verifica->fecha_actualiza=date('Y-m-d H:i:s');
                    $verifica->id_usuario_act=auth()->user()->idpersona;
                    if($verifica->save()){
                        return (['mensaje'=>'Informacion ingresada exitosamente','error'=>false]);
                    }        

                }

            }else{
                //comprobamos que no haya sido ingresado
                $verifica=CatastroLocal::where('provincia_id',$request->prov)
                ->where('canton_id',$request->cant)
                ->where('parroquia_id',$request->parr)
                ->where('calle_principal',$request->calle_princ)
                ->where('calle_secundaria',$request->calle_secund)
                ->where('referencia_ubicacion',$request->referencia)
                ->where('actividad_descripcion',$request->descr)
                ->where('estado_establecimiento',$request->establ)
                ->where('local_propio',$request->tipo)
                ->where('idcatastro_contr',$request->idcont)
                ->first();

                if(!is_null($verifica)){
                    //si ya existe activo              
                    if($verifica->estado=="A"){
                        return (['mensaje'=>'Ya existe la informacionxx','error'=>true]);
                    }else{
                        //lo actualizamos
                        $verifica->idcatastro_contr=$request->idcont;
                        $verifica->provincia_id=$request->prov;
                        $verifica->canton_id=$request->cant;
                        $verifica->parroquia_id=$request->parr;
                        $verifica->calle_principal=$request->calle_princ;
                        $verifica->calle_secundaria=$request->calle_secund;
                        $verifica->referencia_ubicacion=$request->referencia;
                        $verifica->actividad_descripcion=$request->descr;
                        $verifica->estado_establecimiento=$request->establ;
                        $verifica->local_propio=$request->tipo;
                        $verifica->estado='A';
                        $verifica->fecha_actualiza=date('Y-m-d H:i:s');
                        $verifica->id_usuario_act=auth()->user()->idpersona;
                        if($verifica->save()){
                            return (['mensaje'=>'Informacion ingresada exitosamente','error'=>false]);
                        }        

                    }
                }

                //registramos 
                $registra=new CatastroLocal();
                $registra->idcatastro_contr=$request->idcont;
                $registra->provincia_id=$request->prov;
                $registra->canton_id=$request->cant;
                $registra->parroquia_id=$request->parr;
                $registra->calle_principal=$request->calle_princ;
                $registra->calle_secundaria=$request->calle_secund;
                $registra->referencia_ubicacion=$request->referencia;
                $registra->actividad_descripcion=$request->descr;
                $registra->estado_establecimiento=$request->establ;
                $registra->local_propio=$request->tipo;
                $registra->estado='A';
                $registra->fecha_registr=date('Y-m-d H:i:s');
                $registra->id_usuario_reg=auth()->user()->idpersona;
                if($registra->save()){
                    return (['mensaje'=>'Informacion ingresada exitosamente','error'=>false]);
                } 
            }   
          
        } catch (\Throwable $e) {
            dd($e);
            // DB::connection('pgsql')->rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde','error'=>true]); 
        }
        
    }

    public function guardaActividad(Request $request){
      
        try{           
            //comprobamos que no haya sido ingresado
            $verifica=PsqlActividadesCont::where('Actividad_comercial_id',$request->cmb_actividad)
            ->where('Catastro_contribuyente_id',$request->cmb_propietario)
            ->first();

            if(!is_null($verifica)){
                if($verifica->estado=="A"){
                    //si ya existe activo              
                    return (['mensaje'=>'Ya existe la informacion','error'=>true]);
                }
                
                $verifica->Actividad_comercial_id=$request->cmb_actividad;
                $verifica->Catastro_contribuyente_id=$request->cmb_propietario;
                $verifica->estado="A";
                $verifica->idusuario_ingresa=auth()->user()->id;
                $verifica->fecha_ingresa=date('Y-m-d H:i:s');
                if($verifica->save()){
                    return (['mensaje'=>'Informacion ingresada exitosamente','error'=>false]);
                } 

            }

            //registramos 
            $registra=new PsqlActividadesCont();
            $registra->Actividad_comercial_id=$request->cmb_actividad;
            $registra->Catastro_contribuyente_id=$request->cmb_propietario;
            $registra->estado="A";
            $registra->idusuario_ingresa=auth()->user()->id;
            $registra->fecha_ingresa=date('Y-m-d H:i:s');
            if($registra->save()){
                return (['mensaje'=>'Informacion ingresada exitosamente','error'=>false]);
            } 
                     
        } catch (\Throwable $e) {
            dd($e);
            // DB::connection('pgsql')->rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde','error'=>true]); 
        }
        
    }

    public function eliminarActividad($id){
      
        try{           
            //comprobamos que no haya sido ingresado
            $elimina=PsqlActividadesCont::find($id);
            $elimina->estado="I";
            $elimina->idusuarioact=auth()->user()->id;
            $elimina->fecha_actualiza=date('Y-m-d H:i:s');
            $elimina->save();

            return (['mensaje'=>'Informacion eliminada exitosamente','error'=>false]);
            
                     
        } catch (\Throwable $e) {
            dd($e);
            // DB::connection('pgsql')->rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde','error'=>true]); 
        }
        
    }

    public function listarLocales($id){
      
        try{
            $locales=CatastroLocal::with('provincia','canton', 'parroquia')
            ->where('idcatastro_contr',$id)->get();
        
            return (['resultado'=>$locales,'error'=>false]);
           
        } catch (\Throwable $e) {
            dd($e);
            // DB::connection('pgsql')->rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde','error'=>true]); 
        }
        
    }

    public function listarActividades($id){
      
        try{
            $actividad=DB::connection('pgsql')->table('sgm_patente.pa_actividad_contribuyente as act_cont')
            ->leftJoin('sgm_patente.pa_ctlg_actividades_comerciales as ac','ac.id','act_cont.Actividad_comercial_id')
            ->where('act_cont.Catastro_contribuyente_id',$id)
            ->select('ac.ciiu','ac.descripcion','act_cont.Actividad_comercial_id as idActividad','act_cont.id')
            ->where('act_cont.estado','A')
            ->get();
        
            return (['resultado'=>$actividad,'error'=>false]);
           
        } catch (\Throwable $e) {
            dd($e);
            // DB::connection('pgsql')->rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde','error'=>true]); 
        }
        
    }

    public function detalleContribuyente($id)
    {
        // $contribuyente = PsqlCatastroContribuyente::findOrFail($id);
        $contribuyente=DB::connection('pgsql')->table('sgm_patente.pa_catastro_contribuyente as co')
        ->leftJoin('sgm_app.cat_ente as e','e.id','co.propietario_id')
        ->leftJoin('sgm_app.cat_provincia as p','p.id','co.provincia_id')
        ->leftJoin('sgm_app.cat_canton as c','c.id','co.canton_id')
        ->leftJoin('sgm_app.cat_parroquia as pa','pa.id','co.parroquia_id')
        ->leftJoin('sgm_patente.pa_clase_contribuyente as cc','cc.id','co.clase_contribuyente_id')
        ->where('co.id',$id)
        ->select('co.ruc','co.razon_social','co.id','co.ruc_representante_legal','nombre_representante_legal',
            'estado_contribuyente_id','fecha_inicio_actividades','fecha_actualizacion_actividades','fecha_reinicio_actividades'
            ,'fecha_suspension_definitiva','obligado_contabilidad','tipo_contribuyente','calle_principal','calle_secundaria',
            'referencia_ubicacion','co.direccion','correo_1','co.telefono','cc.nombre as clase_cont','e.fecha_nacimiento'
            ,DB::raw("EXTRACT(YEAR FROM age(e.fecha_nacimiento)) AS edad_contribuyente"),'c.nombre as nombre_canton'
            ,'p.descripcion as nombre_provincia','pa.descripcion as nombre_parroquia','es_artesano','clase_contribuyente_id',
            'provincia_id','canton_id','parroquia_id',
            DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS contribuyente"))
        ->first();

        
        return (['resultado'=>$contribuyente,'error'=>false]);
        
    }

    public function reporteContribuyente($id){
        
       $contribuyente=DB::connection('pgsql')->table('sgm_patente.pa_catastro_contribuyente as co')
        ->leftJoin('sgm_app.cat_ente as e','e.id','co.propietario_id')
        ->leftJoin('sgm_app.cat_provincia as p','p.id','co.provincia_id')
        ->leftJoin('sgm_app.cat_canton as c','c.id','co.canton_id')
        ->leftJoin('sgm_app.cat_parroquia as pa','pa.id','co.parroquia_id')
        ->leftJoin('sgm_patente.pa_clase_contribuyente as cc','cc.id','co.clase_contribuyente_id')
        ->where('co.id',$id)
        ->select('co.ruc','co.razon_social','co.id','co.ruc_representante_legal','nombre_representante_legal',
            'estado_contribuyente_id','fecha_inicio_actividades','fecha_actualizacion_actividades','fecha_reinicio_actividades'
            ,'fecha_suspension_definitiva','obligado_contabilidad','tipo_contribuyente','calle_principal','calle_secundaria',
            'referencia_ubicacion','co.direccion','correo_1','co.telefono','cc.nombre as clase_cont','e.fecha_nacimiento'
            ,DB::raw("EXTRACT(YEAR FROM age(e.fecha_nacimiento)) AS edad_contribuyente"),'c.nombre as nombre_canton'
            ,'p.descripcion as nombre_provincia','pa.descripcion as nombre_parroquia','co.es_artesano',
            DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS contribuyente"))
        ->get();

        foreach($contribuyente as $key=>$value){
            $actividades=DB::connection('pgsql')->table('sgm_patente.pa_actividad_contribuyente as act')
            ->leftJoin('sgm_patente.pa_ctlg_actividades_comerciales as nom_act','nom_act.id','act.Actividad_comercial_id')
            ->where('Catastro_contribuyente_id',$value->id)
            ->where('act.estado','A')
            ->select(DB::raw("CONCAT(nom_act.descripcion) AS actividad"),'ciiu')
            ->get(); 
            $contribuyente[$key]->actividades=$actividades;
            // $value->act = $actividades->pluck('actividad')->toArray(); 

            $locales=DB::connection('pgsql')->table('sgm_patente.pa_locales')
            ->select('calle_principal','calle_secundaria','referencia_ubicacion','actividad_descripcion',
            'estado_establecimiento','local_propio')
            // ->select('calle_principal')
            ->where('idcatastro_contr',$value->id)
            ->get();
           

            $contribuyente[$key]->locales=$locales;
        }
        $nombrePDF="contribuyente.pdf";
        $data=$contribuyente[0];
        // dd($data);
        $pdf = \PDF::loadView('reportes.contribuyente',['data'=>$data]);
        // return $pdf->stream('reporte_contribuyente.pdf');
        $estadoarch = $pdf->stream();

        \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
        $exists_destino = \Storage::disk('public')->exists($nombrePDF); 
        if($exists_destino){ 
            return [
                'error'=>false,
                'pdf'=>$nombrePDF
            ];
        }else{
            return[
                'error'=>true,
                'mensaje'=>'No se pudo crear el documento'
            ];
        }

    }

  

}
