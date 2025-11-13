<?php

namespace App\Http\Controllers;


use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use App\Models\ExoneracionAnterior;
use App\Models\RemisionInteres;
use App\Models\RemisionLiquidacion;
use App\Models\PsqlLiquidacion;
use Exception;
use Carbon\Carbon;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Gate;

class RemisionInteresController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        //Gate::authorize('index', RemisionInteres::class);
        if(!Auth()->user()->hasPermissionTo('Lista de remisiones'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        $RemisionInteres = RemisionInteres::orderBy('id', 'desc')->get();
        return view('tesoreria.remisionInteresLista', compact('RemisionInteres'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //Gate::authorize('create', RemisionInteres::class);
        
        if(!Auth()->user()->hasPermissionTo('Remision de interes'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        $num_predio = 0;
        return view('tesoreria.remisionInteresNuevo',compact('num_predio'));
    }

    public function consulta(Request $r)
    {

        $data = array();
        $predio_id = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id')->where('num_predio', '=', $r->num_predio)->first();
        //se obtiene las liquidaciones urbanas
        $num_predio = $r->num_predio;


        $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
                                        ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                        ->leftJoin('sgm_app.cat_ente', 'sgm_financiero.ren_liquidacion.comprador', '=', 'sgm_app.cat_ente.id')
                                        ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat','sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos')
                                        ->where('predio','=',$predio_id->id)
                                        ->whereNot(function($query){
                                            $query->where('estado_liquidacion', 4)
                                            ->orWhere('estado_liquidacion', '=', 5);
                                        })
                                        ->orderBy('anio', 'desc')
                                        ->get();

        return view('tesoreria.remisionInteresNuevo',compact('liquidacionUrbana','num_predio'));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $r)
    {
         //DB::beginTransaction();
         //try {
            $attributes = [
                'num_resolucion' => 'Codigo de resolución',
                'ruta_resolucion' => 'Archivo',
                'observacion' => 'Observacion',
                'tipo' => 'Estado remision',
            ];
            $messages = [
                'num_resolucion.required' => 'El campo Codigo de resolucion es requerido.',
                'ruta_resolucion.required' => 'El campo cargar resolucion es requerido.',
                'unique' => 'El numero de cedula ingresado ya existe',
                'size' => 'El campo :attribute debe tener menos :size',
                'max' => 'El campo :attribute no debe exceder los :max kb',
                'mimes' => 'El campo :attribute debe ser pdf',
            ];

            $reglas = [
                'num_resolucion' => 'required|max:3000',
                'ruta_resolucion' => 'required|file|mimes:pdf|max:2048',
                'observacion' => 'required',
                'tipo' => 'required',
            ];

            $validator = Validator::make($r->all(),$reglas,$messages,$attributes);

            /*foreach($r->checkLiquidacion as $clave => $valor){
                $liquidacion = DB::connection('pgsql')
                                                ->table('sgm_financiero.ren_liquidacion')
                                                ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                                ->where('sgm_financiero.ren_liquidacion.id','=',$valor)
                                                ->count();
                if($liquidacion == 1){
                    $validator->errors()->add(
                        'num_resolucion', 'Una de las liquidaciones seleccionadas ya tiene aplicada exoneracion de años anteriores'
                    );
                    return response()->json(['estado' => 'error','errors'=>$validator->errors()],419);
                }
            }*/

            if ($validator->fails()) {

                //return response()->json(['estado' => 'error','errors'=>$validator->errors()],419);
            }

            $archivo_exoneracion = $r->file('ruta_resolucion')->store('remision');

            $RemisionInteres = new RemisionInteres();
            $RemisionInteres->num_predio = $r->inputMatricula;
            $RemisionInteres->num_resolucion = $r->num_resolucion;
            $RemisionInteres->observacion = $r->observacion;
            $RemisionInteres->ruta_resolucion = $archivo_exoneracion;
            $RemisionInteres->estado = $r->tipo;
            $RemisionInteres->usuario = auth()->user()->email;
            $RemisionInteres->usuariosgm = auth()->user()->name;
            $RemisionInteres->valorInteres = 0.00;
            $RemisionInteres->valorRecargo = 0.00;
            $RemisionInteres->valorTotal = 0.00;
            $RemisionInteres->save();

            foreach($r->checkLiquidacion as $clave => $valor){
                //obtener los datos de la liquidacion
                $liquidacion = DB::connection('pgsql')
                                                ->table('sgm_financiero.ren_liquidacion')
                                                ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                                ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat')
                                                ->where('sgm_financiero.ren_liquidacion.id','=',$valor)
                                                ->get();
                foreach($liquidacion as $l){

                    /*DB::table('personas')->insert([
                        'liquidacion_id' => $l->id,
                        'cod_liquidacion' => $l->id_liquidacion,
                        'valor_total' => $l->total_pago,
                        'valor_remision' => 0.00,
                        'contribuyente' => $l->nombre_comprador,
                        'remision_interes_id' => $RemisionInteres->id,

                    ]);*/

                    $RemisionLiquidacion = new RemisionLiquidacion();
                    $RemisionLiquidacion->liquidacion_id = $l->id;
                    $RemisionLiquidacion->cod_liquidacion = $l->id_liquidacion;
                    $RemisionLiquidacion->emision = $l->total_pago;
                    $RemisionLiquidacion->valor_total_sin_remision = $l->total_pago;
                    $RemisionLiquidacion->valor_total_con_remision = 0.0;
                    $RemisionLiquidacion->valor_remision = 0.00;
                    $RemisionLiquidacion->interes = 0.00;
                    $RemisionLiquidacion->recargo = 0.00;
                    $RemisionLiquidacion->contribuyente = $l->nombre_comprador;
                    $RemisionLiquidacion->remision_interes_id = $RemisionInteres->id;
                    $RemisionLiquidacion->save();

                }

            }
            return redirect()->route('show.remision', ['id' => $RemisionInteres->id]);
            //DB::commit();
            //return response()->json(['estado' => 'ok','success'=>'La remision ha sido creada, realice el pago para luego aplicarla']);
        //} catch (Exception $e) {
            //DB::rollback();
            // if an exception happened in the try block above
            // $ExoneracionAnterior->delete();
            //return response()->json(['estado' => 'error','success'=>'¡Importante! . La aplicacion presento un error de conexion, intente mas tarde'.$e,'msj' => $e],500);
        //}

    }

    /**
     * Display the specified resource.
     */
    public function show(Request $r, $id)
    {
        $RemisionInteres = RemisionInteres::find($id);
        //return $RemisionInteres;
        return view('tesoreria.remisionInteresDetalle',compact('RemisionInteres'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(RemisionInteres $remisionInteres)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $r, $id)
    {
        $RemisionLiquidacion = RemisionLiquidacion::where('remision_interes_id',$id)->get();
        foreach($RemisionLiquidacion as $valor){

            //1 obtener liquidacion
            $liquidacion = DB::connection('pgsql')
                                                ->table('sgm_financiero.ren_liquidacion')
                                                ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                                ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat')
                                                ->where('sgm_financiero.ren_liquidacion.id','=',$valor->liquidacion_id)
                                                ->get();

            //2 obtener pago
            $ren_pago = DB::connection('pgsql')
                                        ->table('sgm_financiero.ren_pago')
                                        ->select('sgm_financiero.ren_pago.*')
                                        ->where('sgm_financiero.ren_pago.liquidacion','=',$valor->liquidacion_id)
                                        ->get();

            //borrar interes y recargo
            $interes = 0.00;
            $recargo = 0.00;
            $total = 0.00;
            $total_remision = 0.00;
            $total_actual = 0.00;
            $id_pago_detalle = 0;

            foreach($ren_pago as $p){

                $interes = $p->interes;
                $recargo = $p->recargo;
                $total = $p->valor;
                $id_pago_detalle = $p->id;



            }
            $total_actual = $total - ($interes + $recargo);
            $afectacion = DB::connection('pgsql')
                        ->table('sgm_financiero.ren_pago')
                        ->where('sgm_financiero.ren_pago.liquidacion', $valor->liquidacion_id)
                        ->update(['interes' => 0.00,'recargo' => 0.00, 'valor' => $total_actual]);

                        //actualizar remision liquidaciones
            $RemisionLiquidacion = RemisionLiquidacion::find($valor->id);
            $RemisionLiquidacion->valor_remision = $total_remision;
            $RemisionLiquidacion->valor_total_con_remision = $total - ($interes + $recargo);
            $RemisionLiquidacion->valor_total_sin_remision = $total;
            $RemisionLiquidacion->valor_remision = $interes + $recargo;
            $RemisionLiquidacion->interes = $interes;
            $RemisionLiquidacion->recargo = $recargo;
            $RemisionLiquidacion->save();


            $afectacion = DB::connection('pgsql')
                ->table('sgm_financiero.ren_pago_detalle')
                ->where('sgm_financiero.ren_pago_detalle.pago','=', $id_pago_detalle)
                ->where('sgm_financiero.ren_pago_detalle.tipo_pago', '=', 1)
                ->update(['valor' => $total_actual]);

        }

        $RemisionInteres = RemisionInteres::find($id);
        $RemisionInteres->estado = 'aplicado';
        $RemisionInteres->save();
        return redirect()->back()->with('success', 'La remision fue aplicada');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(RemisionInteres $remisionInteres)
    {
        //
    }

    public function datatables(Request $r){
        $RemisionInteres = RemisionInteres::orderBy("id", "desc")->get();
        return Datatables($RemisionInteres)
                ->editColumn('estado', function ($RemisionInteres) {
                    if($RemisionInteres->estado == 'creado'){
                        return '<span class="badge bg-primary">Creado</span>';
                    }else if($RemisionInteres->estado == 'aplicado'){
                        return '<span class="badge bg-success">Aplicado</span>';
                    }else{
                        return '<span class="badge bg-warning">Incompleto</span>';
                    }
                })
                ->addColumn('action', function ($RemisionInteres) {
                    $botonesCita = '';
                    $botonesCita .= '<a href="'.route('show.remision',$RemisionInteres->id).'" class="btn btn-primary btn-sm"><i class="bi bi-eye"></i></a> ';

                    return $botonesCita;
                })
                ->rawColumns(['estado','action'])
                ->make(true);
    }

    public function consultaLiquidacionConRemision(Request $r){
       
        if(!Auth()->user()->hasPermissionTo('Reporte de liquidaciones'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        // return view('tesoreria.consultaLiquidacionConRemision');
        $num_predio = 0;
        return view('tesoreria.liquidacionRemision',compact('num_predio'));
    }

    public function storeConsultaLiquiadacionesConRemision1(Request $r){
        $attributes = [
            'inputMatricula' => 'Matricula inmobiliaria',
        ];
        $messages = [
            'required' => 'El campo matricula inmobiliaria es requerido.',
        ];

        $reglas = [
            'inputMatricula' => 'required',
        ];

        $validator = Validator::make($r->all(),$reglas,$messages,$attributes);

        if ($validator->fails()) {
            return redirect('remision/consulta/liquidacion')
                        ->withErrors($validator)
                        ->withInput();
        }
        $validated = $validator->validated();

        if($r->inputMatricula != null){
            //$predio_id = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id')->where('num_predio', '=', $r->inputMatricula)->first();
            //se obtiene las liquidaciones urbanas

            $predio = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id','clave_cat')->where('num_predio', '=', $r->inputMatricula)->first();
            $liquidacionUrbana = null;
            $clave_cat2 = 0;
            $num_predio2 = $r->inputMatricula;
            $suma_interes = 0;
            $suma_recargo = 0;
            $suma_emision = 0;
            $suma_total = 0;

            foreach($predio as $p){
                $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
                                    ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                    ->leftJoin('sgm_app.cat_ente', 'sgm_financiero.ren_liquidacion.comprador', '=', 'sgm_app.cat_ente.id')
                                    ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat','sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos')
                                    ->where('predio','=',$predio->id)
                                    ->where('estado_liquidacion','=',2)
                                    /*->whereNot(function($query){
                                        $query->where('estado_liquidacion', 4)
                                        ->orWhere('estado_liquidacion', '=', 5);
                                    })*/
                                    ->orderBy('anio', 'desc')
                                    ->get();

                $DatosLiquidacion = $liquidacionUrbana->map(function ($liq) {
                    $data['id_liquidacion'] = $liq->id_liquidacion;
                    $data['total_pago'] = $liq->total_pago;
                    $data['estado_liquidacion'] = $liq->estado_liquidacion;
                    $data['predio'] = $liq->predio;
                    $data['anio'] = $liq->anio;
                    $data['clave_cat'] = $liq->clave_cat;
                    $data['nombre_comprador'] = $liq->nombre_comprador;
                    $data['nombres'] = $liq->nombres;
                    $data['apellidos'] = $liq->apellidos;
                    $porcentaje_interes = DB::connection('pgsql')->table('sgm_financiero.ren_intereses')->select('porcentaje')->where('anio', '=', $liq->anio)->first();

                    $fechaactual = date('Y');
                    if($liq->anio < $fechaactual){
                        $data['interes'] = round((floatval($liq->total_pago) * floatval($porcentaje_interes->porcentaje)) / 100,2);
                    }else{
                        $data['interes'] = 0.00;
                    }

                    $impuesto_predial = DB::connection('pgsql')->table('sgm_financiero.ren_det_liquidacion')->select('sgm_financiero.ren_det_liquidacion.*')->where('liquidacion', '=', $liq->id)->where('rubro', '=', 2)->first();
                    $data['recargo'] = 0.00;
                    if(!is_null($impuesto_predial)){
                        if($liq->anio < $fechaactual){
                            $data['recargo'] = round($impuesto_predial->valor * 0.10,2);
                        }else{
                            $data['recargo'] = 0.00;
                        }
                    }
                    $data['suma_emision_interes_recargos'] = $data['total_pago'] + $data['interes'] + $data['recargo'];

                    return $data;
                });


            }
            $suma_interes = $DatosLiquidacion->sum('interes');
            $suma_recargo = $DatosLiquidacion->sum('recargo');
            $suma_emision = $DatosLiquidacion->sum('total_pago');
            $suma_total = $DatosLiquidacion->sum('suma_emision_interes_recargos');
            return view('tesoreria.consultaLiquidacionConRemision',compact('liquidacionUrbana','clave_cat2','num_predio2','DatosLiquidacion','suma_interes','suma_emision','suma_recargo','suma_total'));

        }

    }

    public function storeConsultaLiquiadacionesConRemision(Request $r)
    {   
       
        try {
        $data = array();
        $tipo=$r->tipo;
        $num_predio=$r->num_predio;
        $clave=$r->clave;
        $nombre=$r->cmb_nombres;
        
        if($tipo!=3){
            $predio_id = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id')
            // ->where('num_predio', '=', $r->num_predio)
            ->where(function($query) use($tipo, $num_predio, $clave, $nombre) {
                if($tipo==1){
                    $query->where('num_predio', '=', $num_predio);
                }else if($tipo==2){
                    $query->where('clave_cat', '=', $clave);
                }else{
                    // $query->where('clave_cat', '=', $clave);
                }
            })
            ->first();
           $num_predio = $predio_id->id;
        }else{
            $num_predio = $r->num_predio;
        }
        
        //se obtiene las liquidaciones urbanas
        // $num_predio = $r->num_predio;
        // dd($num_predio);

        $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
        ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
        ->leftJoin('sgm_app.cat_ente', 'sgm_financiero.ren_liquidacion.comprador', '=', 'sgm_app.cat_ente.id')
        ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat','sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos','sgm_app.cat_ente.ci_ruc','sgm_app.cat_predio.num_predio',
        
                    DB::raw('
                    (
                        SELECT
                            ROUND((
                                COALESCE(ren_liquidacion.saldo, 0)
                                +
                                COALESCE((
                                    CASE
                                        WHEN (ren_liquidacion.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) < 7) THEN
                                            ROUND(d.valor * (
                                                SELECT porcentaje
                                                FROM sgm_app.ctlg_descuento_emision
                                                WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                                AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                                LIMIT 1
                                            ) / 100, 2) * (-1)
                                        ELSE 0
                                    END
                                ), 0)
                                +
                                COALESCE((
                                    CASE
                                    WHEN (ren_liquidacion.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                        ROUND((ren_liquidacion.saldo * (
                                            SELECT ROUND((porcentaje / 100), 2) 
                                            FROM sgm_financiero.ren_intereses i
                                            WHERE i.anio = ren_liquidacion.anio
                                            LIMIT 1
                                        )), 2)
                                        ELSE 0
                                    END
                                ), 0)
                                +
                                COALESCE((
                                    CASE
                                        WHEN ren_liquidacion.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                            ROUND((d.valor * 0.10), 2)
                                        WHEN ren_liquidacion.anio < EXTRACT(YEAR FROM NOW()) THEN
                                            ROUND((d.valor * 0.10), 2)
                                        ELSE 0
                                    END
                                ), 0)
                            ), 2)
                        FROM sgm_financiero.ren_det_liquidacion d
                        WHERE d.liquidacion = ren_liquidacion.id 
                        AND d.rubro = 2
                        LIMIT 1
                    ) AS total_complemento'))
        ->where(function($query) use($tipo, $num_predio, $nombre) {
            if($tipo!=3){
                $query->where('predio','=',$num_predio);
            }else{
                $query->where('comprador','=',$nombre);
            }   
        })
        
        // ->whereNot(function($query){
        //     $query->where('estado_liquidacion', 4)
        //     ->orWhere('estado_liquidacion', '=', 5);
        // })
        ->whereNotIN('estado_liquidacion',[1,3,4,5])
        ->orderby('clave_cat','desc')
        ->orderBy('anio', 'desc')
        ->get();

                                        // dd($liquidacionUrbana);
        if(count($liquidacionUrbana) >= 1) {
            // $num_predio=$r->num_predio;
            $num_predio=$liquidacionUrbana[0]->num_predio;
            return view('tesoreria.liquidacionRemision',compact('liquidacionUrbana','num_predio'));
        }else{
            return redirect('remision/consulta/liquidacion/')->with('status', 'No existe liquidaciones pendientes');
        }

        } catch (Exception $e) {
            // Log the message locally OR use a tool like Bugsnag/Flare to log the error
            return redirect('remision/consulta/liquidacion/')->with('status', 'Problema de conexion '.$e->getMessage());

        }
    }

     public function reporteLiquidacion(Request $r)
    {   
       
        try {
        $data = array();
        $tipo=$r->tipo;
        $num_predio=$r->num_predio;
        $clave=$r->clave;
        $nombre=$r->cmb_nombres;
        
        if($tipo!=3){
            $predio_id = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id')
            // ->where('num_predio', '=', $r->num_predio)
            ->where(function($query) use($tipo, $num_predio, $clave, $nombre) {
                if($tipo==1){
                    $query->where('num_predio', '=', $num_predio);
                }else if($tipo==2){
                    $query->where('clave_cat', '=', $clave);
                }else{
                    // $query->where('clave_cat', '=', $clave);
                }
            })
            ->first();
           $num_predio = $predio_id->id;
        }else{
            $num_predio = $r->num_predio;
        }
        
        //se obtiene las liquidaciones urbanas
        // $num_predio = $r->num_predio;
        // dd($num_predio);

        $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
        ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
        ->leftJoin('sgm_app.cat_ente', 'sgm_financiero.ren_liquidacion.comprador', '=', 'sgm_app.cat_ente.id')
        ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat','sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos','sgm_app.cat_ente.ci_ruc','sgm_app.cat_predio.num_predio','sgm_financiero.ren_liquidacion.saldo',
        
                    DB::raw('
                    (
                        SELECT
                            ROUND((
                                COALESCE(ren_liquidacion.saldo, 0)
                                +
                                COALESCE((
                                    CASE
                                        WHEN (ren_liquidacion.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) < 7) THEN
                                            ROUND(d.valor * (
                                                SELECT porcentaje
                                                FROM sgm_app.ctlg_descuento_emision
                                                WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                                AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                                LIMIT 1
                                            ) / 100, 2) * (-1)
                                        ELSE 0
                                    END
                                ), 0)
                                +
                                COALESCE((
                                    CASE
                                    WHEN (ren_liquidacion.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                        ROUND((ren_liquidacion.saldo * (
                                            SELECT ROUND((porcentaje / 100), 2) 
                                            FROM sgm_financiero.ren_intereses i
                                            WHERE i.anio = ren_liquidacion.anio
                                            LIMIT 1
                                        )), 2)
                                        ELSE 0
                                    END
                                ), 0)
                                +
                                COALESCE((
                                    CASE
                                        WHEN ren_liquidacion.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                            ROUND((d.valor * 0.10), 2)
                                        WHEN ren_liquidacion.anio < EXTRACT(YEAR FROM NOW()) THEN
                                            ROUND((d.valor * 0.10), 2)
                                        ELSE 0
                                    END
                                ), 0)
                            ), 2)
                        FROM sgm_financiero.ren_det_liquidacion d
                        WHERE d.liquidacion = ren_liquidacion.id 
                        AND d.rubro = 2
                        LIMIT 1
                    ) AS total_complemento'), DB::raw("
                        (
                            SELECT
                                CASE
                                    WHEN (ren_liquidacion.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
                                        ROUND(d.valor * (
                                            SELECT porcentaje
                                            FROM sgm_app.ctlg_descuento_emision
                                            WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                            AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                            LIMIT 1
                                        ) / 100, 2) * (-1)
                                    ELSE
                                        0.00
                                END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = ren_liquidacion.id AND d.rubro = 2
                            LIMIT 1
                        ) AS desc
                    "),
                    
                    DB::raw("
                        (
                            SELECT
                                CASE
                                     WHEN (ren_liquidacion.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                        ROUND((ren_liquidacion.saldo * (
                                            SELECT ROUND((porcentaje / 100), 2) 
                                            FROM sgm_financiero.ren_intereses i
                                            WHERE i.anio = ren_liquidacion.anio
                                            LIMIT 1
                                        )), 2)
                                    ELSE
                                        0.00
                                    END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = ren_liquidacion.id AND d.rubro = 2
                            LIMIT 1
                        ) AS interes
                    "),

                    DB::raw("
                        (
                            SELECT
                                CASE
                                    WHEN ren_liquidacion.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                        ROUND((d.valor * 0.10), 2)
                                    WHEN ren_liquidacion.anio < EXTRACT(YEAR FROM NOW()) THEN
                                        ROUND((d.valor * 0.10), 2)
                                    ELSE
                                        0.00
                                END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = ren_liquidacion.id AND d.rubro = 2
                            LIMIT 1
                        ) AS recargos
                    "))
        ->where(function($query) use($tipo, $num_predio, $nombre) {
            if($tipo!=3){
                $query->where('predio','=',$num_predio);
            }else{
                $query->where('comprador','=',$nombre);
            }   
        })
        
        // ->whereNot(function($query){
        //     $query->where('estado_liquidacion', 4)
        //     ->orWhere('estado_liquidacion', '=', 5);
        // })
        ->whereNotIN('estado_liquidacion',[1,3,4,5])
        ->orderby('clave_cat','desc')
        ->orderBy('anio', 'desc')
        ->get();

        $nombrePDF="Liquidacion.pdf";                                // dd($liquidacionUrbana);
        $pdf = \PDF::loadView('reportes.reporteLiquidacionRemision', ['DatosLiquidacion'=>$liquidacionUrbana]);

        // return $pdf->download('reporteLiquidacion.pdf');
        $pdf->setPaper("A4", "landscape");
        $estadoarch = $pdf->stream();

        //lo guardamos en el disco temporal
        \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
        $exists_destino = \Storage::disk('public')->exists($nombrePDF);
        if($exists_destino){
            return response()->json([
                'error'=>false,
                'pdf'=>$nombrePDF
            ]);
        }else{
            return response()->json([
                'error'=>true,
                'mensaje'=>'No se pudo crear el documento'
            ]);
        }

        } catch (Exception $e) {
            // Log the message locally OR use a tool like Bugsnag/Flare to log the error
             return response()->json([
                'error'=>true,
                'mensaje'=>'No se pudo crear el documento'.$e
            ]);

        }
    }

    public function descargarPdf($archivo){
        try{

            $exists_destino = \Storage::disk('public')->exists($archivo);

            if($exists_destino){
                return response()->download( storage_path('app/public/'.$archivo))->deleteFileAfterSend(true);
            }else{
                return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
            }

        } catch (\Throwable $th) {
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
        }
    }


    public function download($id){
        $RemisionInteres = RemisionInteres::find($id);
        /*$file = Storage::disk('public')->get('y8sxSozFXd1vmWrfxw7Vlsg5ENpLY7D1RnNBG1ex.pdf');
        $header = [
        'Content-Type' => 'pdf'
        ];*/
        $path = $RemisionInteres->ruta_resolucion;

            //Name of the file the user will see
        $slug = Str::slug($RemisionInteres->num_resolucion).'.pdf';

            $headers = [
                'Content-Type' => 'application/pdf',
            ];

            return Storage::download($path, $slug, $headers);

        //return  response()->download('app/'.$ExoneracionAnterior->ruta_resolucion);
        //$fileSize = \File::size(public_path('image/house2.jpeg'));
        //return Storage::download($ExoneracionAnterior->ruta_resolucion,'holamundo',$header);
        //dd(Storage::url('exoneracion/y8sxSozFXd1vmWrfxw7Vlsg5ENpLY7D1RnNBG1ex.pdf'));
    }

}
