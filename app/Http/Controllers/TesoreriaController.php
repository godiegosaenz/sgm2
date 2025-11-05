<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Models\ExoneracionAnterior;
use App\Models\ExoneracionDetalle;
use App\Models\ExoneracionDetalleLiquidacion;
use Illuminate\Support\Facades\Validator;
use Datatables;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Gate;


use Illuminate\Support\Str;

use Exception;

class TesoreriaController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        //Gate::authorize('index', ExoneracionAnterior::class);
        if(!Auth()->user()->hasPermissionTo('Lista exoneracion'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('tesoreria.exoneracion');
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create(Request $request)
    {
        /*if ($request->user()->cannot('index',)) {
            abort(403);
        }*/
        //Gate::authorize('create', ExoneracionAnterior::class);
        if(!Auth()->user()->hasPermissionTo('Exoneracion tercera edad'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('tesoreria.consulta');
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $r)
    {
        //DB::beginTransaction();
        try {
            $attributes = [
                'num_resolucion' => 'Codigo de resolución',
                'ruta_resolucion' => 'Archivo',
                'observacion' => 'Observacion',
                'tipo' => 'Tipo de exoneracion',
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

                return response()->json(['estado' => 'error','errors'=>$validator->errors()],419);
            }

            $archivo_exoneracion = $r->file('ruta_resolucion')->store('exoneracion');

            $ExoneracionAnterior = new ExoneracionAnterior();
            $ExoneracionAnterior->num_predio = $r->num_predio;
            $ExoneracionAnterior->num_resolucion = $r->num_resolucion;
            $ExoneracionAnterior->observacion = $r->observacion;
            $ExoneracionAnterior->ruta_resolucion = $archivo_exoneracion;
            $ExoneracionAnterior->tipo = $r->tipo;
            $ExoneracionAnterior->usuario = auth()->user()->email;
            $ExoneracionAnterior->save();

            $impuesto_predial = 0;
            $impuesto_predial_anterior = 0;


            foreach($r->checkLiquidacion as $clave => $valor){
                //obtener los datos de la liquidacion
                $liquidacion = DB::connection('pgsql')
                                                ->table('sgm_financiero.ren_liquidacion')
                                                ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                                ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat')
                                                ->where('sgm_financiero.ren_liquidacion.id','=',$valor)
                                                ->get();
                foreach($liquidacion as $l){
                    //obtener el detalle de la liquidacion
                    $detalleliquidacion = DB::connection('pgsql')->table('sgm_financiero.ren_det_liquidacion')
                                        ->join('sgm_financiero.ren_rubros_liquidacion', 'sgm_financiero.ren_det_liquidacion.rubro', '=', 'sgm_financiero.ren_rubros_liquidacion.id')
                                        ->select('ren_det_liquidacion.*','ren_rubros_liquidacion.descripcion')
                                        ->where('ren_det_liquidacion.liquidacion','=',$l->id)
                                        ->get();
                    $total = 0;
                    $total_anterior = $l->total_pago;
                    foreach($detalleliquidacion as $dl){
                        $arrayRubro[$dl->id] = $dl->rubro;
                        //se verifica si el rubro es 2 de impuesto predial
                        if($dl->rubro === 2){
                            $impuesto_predial_anterior = $dl->valor;
                            if($r->tipo == 'tercera_edad'){
                                $impuesto_predial = 0;
                                DB::connection('pgsql')
                                                ->table('sgm_financiero.ren_det_liquidacion')
                                                ->where('id', $dl->id)
                                                ->update(['valor' => 0]);
                                $total = $total + 0;
                            }elseif($r->tipo == 'tercera_edad_50'){
                                $impuesto_predial = $dl->valor - ($dl->valor * 0.50);
                                $total = $total + $impuesto_predial;
                                DB::connection('pgsql')
                                                ->table('sgm_financiero.ren_det_liquidacion')
                                                ->where('id', $dl->id)
                                                ->update(['valor' => $impuesto_predial]);
                            }else{
                                $impuesto_predial = $dl->valor - ($dl->valor * 0.50);
                                $total = $total + $impuesto_predial;
                                DB::connection('pgsql')
                                                ->table('sgm_financiero.ren_det_liquidacion')
                                                ->where('id', $dl->id)
                                                ->update(['valor' => $impuesto_predial]);

                            }

                        }else{
                            $total = $total + $dl->valor;
                        }
                    }
                    //return $total;
                    //actualizar el saldo en la liquidacion
                    DB::connection('pgsql')
                                ->table('sgm_financiero.ren_liquidacion')
                                ->where('id', $l->id)
                                ->update(['saldo' => $total,'total_pago' => $total]);

                    $arrayTotal[$l->id] = $total;
                    //insertar datos en la tabla exoneracion_anterior

                    $ExoneracionDetalle = new ExoneracionDetalle();
                    $ExoneracionDetalle->liquidacion_id = $l->id;
                    $ExoneracionDetalle->cod_liquidacion = $l->id_liquidacion;
                    $ExoneracionDetalle->valor = $total;
                    $ExoneracionDetalle->valor_anterior = $total_anterior;
                    $ExoneracionDetalle->impuesto_predial_anterior = $impuesto_predial_anterior;
                    $ExoneracionDetalle->impuesto_predial_actual = $impuesto_predial;
                    $ExoneracionDetalle->det_liquidacion = $detalleliquidacion;
                    $ExoneracionDetalle->exoneracion_id = $ExoneracionAnterior->id;
                    $ExoneracionDetalle->save();

                    //guardar el detalle
                    foreach($detalleliquidacion as $det){
                        $ExoneracionDetalleLiquidacion = new ExoneracionDetalleLiquidacion();
                        $ExoneracionDetalleLiquidacion->rubro = $det->rubro;
                        $ExoneracionDetalleLiquidacion->descripcion = $det->descripcion;
                        $ExoneracionDetalleLiquidacion->valor = $det->valor;
                        $ExoneracionDetalleLiquidacion->exoneracion_detalles_id = $ExoneracionDetalle->id;
                        $ExoneracionDetalleLiquidacion->save();
                    }

                }

            }
            //DB::commit();
            return response()->json(['estado' => 'ok','success'=>'La aplicacion de la exoneracion de años anteriores se aplicó con exito']);
        } catch (Exception $e) {
            //DB::rollback();
            // if an exception happened in the try block above
            // $ExoneracionAnterior->delete();
            return response()->json(['estado' => 'error','success'=>'¡Importante! . La aplicacion presento un error de conexion, intente mas tarde'.$e,'msj' => $e],500);
        }


    }

    /**
     * Display the specified resource.
     *
     * @param  \App\Models\LiquidationSequence  $liquidationSequence
     * @return \Illuminate\Http\Response
     */
    public function show(Request $r, $id)
    {
        $ExoneracionAnterior = ExoneracionAnterior::find($id);

        return view('tesoreria.exoneracionDetalle',compact('ExoneracionAnterior'));
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  \App\Models\LiquidationSequence  $liquidationSequence
     * @return \Illuminate\Http\Response
     */
    public function edit()
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \App\Models\LiquidationSequence  $liquidationSequence
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  \App\Models\LiquidationSequence  $liquidationSequence
     * @return \Illuminate\Http\Response
     */
    public function destroy()
    {
        //
    }

    public function datatables(Request $r){
        $ExoneracionAnterior = ExoneracionAnterior::orderBy("id", "desc")->get();
        return Datatables($ExoneracionAnterior)
                ->addColumn('action', function ($ExoneracionAnterior) {
                    $botonesCita = '';
                    $botonesCita .= '<a href="'.route('detalle.exoneracion',$ExoneracionAnterior->id).'" class="btn btn-primary btn-sm"><i class="bi bi-eye"></i></a> ';
                    $botonesCita .= '<a target="_blank" href="'.route('imprimir.reporte.exoneracion',$ExoneracionAnterior->id).'" class="btn btn-secondary btn-sm"><i class="bi bi-filetype-pdf"></i></a>';

                    return $botonesCita;
                })
                ->rawColumns(['action'])
                ->make(true);
    }

    public function consulta(Request $r)
    {
        if($r->num_predio != null){
            $data = array();
            $predio_id = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id')->where('num_predio', '=', $r->num_predio)->first();
            //se obtiene las liquidaciones urbanas
            $liquidacionUrbana = array();
            $liquidacionUrbana[1] = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
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

            $data['estado'] = 'ok';
            $data['mensaje'] = 'Existen Valores pendientes de pago';
            $data['liquidacionUrbana'] = $liquidacionUrbana;
            return json_encode($data);
        }
    }

    public function download($id){
        $ExoneracionAnterior = ExoneracionAnterior::find($id);
        /*$file = Storage::disk('public')->get('y8sxSozFXd1vmWrfxw7Vlsg5ENpLY7D1RnNBG1ex.pdf');
        $header = [
        'Content-Type' => 'pdf'
        ];*/
        $path = $ExoneracionAnterior->ruta_resolucion;

            //Name of the file the user will see
        $slug = Str::slug($ExoneracionAnterior->num_resolucion).'.pdf';

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
