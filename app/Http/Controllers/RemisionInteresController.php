<?php

namespace App\Http\Controllers;


use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use App\Models\RemisionInteres;
use App\Models\RemisionLiquidacion;
use Exception;
use Carbon\Carbon;

class RemisionInteresController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('tesoreria.remisionInteresLista');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('tesoreria.remisionInteres');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $r)
    {
         //DB::beginTransaction();
         try {
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
                'num_resolucion' => 'required|max:30',
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
            $RemisionInteres->num_predio = $r->num_predio;
            $RemisionInteres->num_resolucion = $r->num_resolucion;
            $RemisionInteres->observacion = $r->observacion;
            $RemisionInteres->ruta_resolucion = $archivo_exoneracion;
            $RemisionInteres->estado = $r->tipo;
            $RemisionInteres->usuario = auth()->user()->email;
            $RemisionInteres->usuariosgm = 'pquinonez';
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
            //DB::commit();
            return response()->json(['estado' => 'ok','success'=>'La remision ha sido creada, realice el pago para luego aplicarla']);
        } catch (Exception $e) {
            //DB::rollback();
            // if an exception happened in the try block above
            // $ExoneracionAnterior->delete();
            return response()->json(['estado' => 'error','success'=>'¡Importante! . La aplicacion presento un error de conexion, intente mas tarde'.$e,'msj' => $e],500);
        }

    }

    /**
     * Display the specified resource.
     */
    public function show(Request $r, $id)
    {
        $RemisionInteres = RemisionInteres::find($id);

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
        return view('tesoreria.consultaLiquidacionConRemision');
    }

    public function storeConsultaLiquiadacionesConRemision(Request $r){
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
                    if($liq->anio < $fechaactual){
                        $data['recargo'] = round($impuesto_predial->valor * 0.10,2);
                    }else{
                        $data['recargo'] = 0.00;
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

}
