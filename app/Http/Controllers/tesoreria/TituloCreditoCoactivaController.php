<?php

namespace App\Http\Controllers\tesoreria;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Models\PsqlEnte;
use App\Models\PsqlLiquidacion;
use App\Models\PsqlPropietarioPredio;
use Exception;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Support\Facades\Gate;

class TituloCreditoCoactivaController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        //Gate::authorize('impresion_titulos_urb', PsqlLiquidacion::class);
        if(!Auth()->user()->hasPermissionTo('Impresion de titulos Urbanos'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        $num_predio = 0;
        return view('tesoreria.TitulosCreditosCoactiva',compact('num_predio'));
    }

    
    public function buscarContribuyenteUrbano(Request $request){

        $data = [];
        if($request->has('q')){
            $search = $request->q;
            $data=DB::connection('pgsql')->table('sgm_app.cat_ente')
            ->where(function($query)use($search){
                $query->where('ci_ruc', 'ilike', '%'.$search.'%')
                // ->orwhere('nombres', 'ilike', '%'.$search.'%')
                // ->orwhere('apellidos', 'ilike', '%'.$search.'%')
                ->orwhere(DB::raw("CONCAT(nombres, ' ', apellidos)"), 'ilike', '%'.$search.'%')
                ->orwhere(DB::raw("CONCAT(apellidos, ' ', nombres )"), 'ilike', '%'.$search.'%');
            })            
            ->select('id','ci_ruc',DB::raw("CONCAT(apellidos, ' ', nombres) AS nombre"))
            ->take(50)->get();

        }
        return response()->json($data);
        // return response()->json($data);
    }

     public function buscarClaveCatastralUrbano(Request $request){

        $data = [];
        if($request->has('q')){
            $search = $request->q;
            // $data=DB::connection('pgsql')->table('sgm_app.cat_predio_propietario as pp')
            // ->leftjoin('sgm_app.cat_ente as e','e.id','pp.ente')
            // ->join('sgm_app.cat_predio as p', 'p.id', '=', 'pp.ente')
            // // ->where('pp.estado','A')
            // ->where(function($query)use($search){
            //     $query->where('p.clave_cat', 'ilike', '%'.$search.'%');
            // })            
            // ->select('p.clave_cat',DB::raw("CONCAT(apellidos, ' ', nombres) AS nombre"))
            // ->take(50)->get();


             $data=DB::connection('pgsql')->table('sgm_app.cat_predio as p')
            ->leftjoin('sgm_app.cat_predio_propietario as pp','p.id','pp.predio')
            ->leftjoin('sgm_app.cat_ente as e', 'e.id', '=', 'pp.ente')
            // ->where('pp.estado','A')
            ->where(function($query)use($search){
                $query->where('p.clave_cat', 'ilike', '%'.$search.'%');
            })            
            ->select('p.clave_cat',DB::raw("CONCAT(apellidos, ' ', nombres) AS nombre"))
            ->take(50)->get();

        }
        return response()->json($data);
        // return response()->json($data);
    }

    public function consulta(Request $r)
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
        ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat','sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos','sgm_app.cat_ente.ci_ruc',
        
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
            $num_predio=$r->num_predio;
            return view('tesoreria.TitulosCreditosCoactiva',compact('liquidacionUrbana','num_predio'));
        }else{
            return redirect('tituloscoactiva/')->with('status', 'No existe liquidaciones pendientes');
        }

        } catch (Exception $e) {
            // Log the message locally OR use a tool like Bugsnag/Flare to log the error
            return redirect('tituloscoactiva/')->with('status', 'Problema de conexion '.$e->getMessage());

        }
    }

    public function reporteTitulosCoactiva(Request $r)
    {
        
        $dataArray = array();
        foreach($r->checkLiquidacion as $clave => $valor){
            $liquidacion = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as liq')
            ->leftJoin('sgm_app.cat_ente as en', 'en.id', '=', 'liq.comprador')
            ->leftJoin('sgm_app.cat_predio as pre', 'pre.id', '=', 'liq.predio')
            ->leftJoin('sgm_app.cat_ciudadela as cdla', 'cdla.id', '=', 'pre.ciudadela')
            ->select(
                'liq.num_liquidacion',
                'liq.anio',
                'liq.avaluo_municipal',
                'liq.avaluo_construccion',
                'liq.avaluo_solar',
                'liq.fecha_ingreso',
                'liq.total_pago',
                'pre.num_predio',
                'saldo',
                'liq.id',
                'liq.anio',
                'en.direccion',
                DB::raw("
                    CASE
                        WHEN liq.comprador IS NULL THEN liq.nombre_comprador
                        ELSE CASE en.es_persona
                            WHEN TRUE THEN COALESCE(en.apellidos, '') || ' ' || COALESCE(en.nombres, '')
                            ELSE COALESCE(en.razon_social, '')
                        END
                    END AS nombres
                "),
                DB::raw("
                    CASE
                        WHEN liq.comprador IS NULL THEN 'S/N'
                        ELSE (SELECT ci_ruc FROM sgm_app.cat_ente WHERE cat_ente.id = liq.comprador)
                    END AS cedula
                "),
                DB::raw("cdla.nombre || ' MZ: ' || pre.urb_mz || ' SL: ' || pre.urb_solarnew AS direccion1"),
                'pre.clave_cat as cod_predial',
                DB::raw("(SELECT razon_social FROM sgm_application.empresa) AS empresa"),
                DB::raw("
                    (
                        SELECT
                            CASE
                                WHEN (liq.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
                                    (ROUND(d.valor * (
                                        SELECT porcentaje
                                        FROM sgm_app.ctlg_descuento_emision
                                        WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                        AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)) / 100, 2) * (-1))
                                WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN
                                    (ROUND((d.valor * 0.1), 2) + ROUND((liq.saldo) *
                                    (SELECT ROUND((porcentaje / 100), 2) FROM sgm_financiero.ren_intereses i WHERE i.anio = liq.anio), 2))
                                ELSE
                                    ROUND((d.valor * 0.1), 2)
                            END AS valor_complemento
                        FROM sgm_financiero.ren_det_liquidacion d
                        WHERE d.liquidacion = liq.id AND d.rubro = 2
                    ) AS valor_complemento
                "),

                DB::raw("
                        (
                            SELECT
                                CASE
                                    WHEN (liq.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
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
                            WHERE d.liquidacion = liq.id AND d.rubro = 2
                            LIMIT 1
                        ) AS desc
                    "),
                    
                    DB::raw("
                        (
                            SELECT
                                CASE
                                     WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                        ROUND((liq.saldo * (
                                            SELECT ROUND((porcentaje / 100), 2) 
                                            FROM sgm_financiero.ren_intereses i
                                            WHERE i.anio = liq.anio
                                            LIMIT 1
                                        )), 2)
                                    ELSE
                                        0.00
                                    END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = liq.id AND d.rubro = 2
                            LIMIT 1
                        ) AS interes
                    "),

                    DB::raw("
                        (
                            SELECT
                                CASE
                                    WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                        ROUND((d.valor * 0.10), 2)
                                    WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                        ROUND((d.valor * 0.10), 2)
                                    ELSE
                                        0.00
                                END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = liq.id AND d.rubro = 2
                            LIMIT 1
                        ) AS recargos
                    "),

                      DB::raw("
                    (
                        SELECT
                            ROUND((
                                COALESCE(liq.saldo, 0)
                                +
                                COALESCE((
                                    CASE
                                        WHEN (liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) < 7) THEN
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
                                    WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                        ROUND((liq.saldo * (
                                            SELECT ROUND((porcentaje / 100), 2) 
                                            FROM sgm_financiero.ren_intereses i
                                            WHERE i.anio = liq.anio
                                            LIMIT 1
                                        )), 2)
                                        ELSE 0
                                    END
                                ), 0)
                                +
                                COALESCE((
                                    CASE
                                        WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                            ROUND((d.valor * 0.10), 2)
                                        WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                            ROUND((d.valor * 0.10), 2)
                                        ELSE 0
                                    END
                                ), 0)
                            ), 2)
                        FROM sgm_financiero.ren_det_liquidacion d
                        WHERE d.liquidacion = liq.id 
                        AND d.rubro = 2
                        LIMIT 1
                    ) AS total_complemento
                "),

                'liq.id_liquidacion'
            )
            ->where('liq.id', $valor)
            ->get();
            //dd($liquidacion);
        

            $fecha_hoy=date('Y-m-d');
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
            $fecha_timestamp = strtotime($fecha_hoy);    
            $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);
    

            $rubros = DB::connection('pgsql')->table('sgm_financiero.ren_det_liquidacion as rdl')
                                                ->join('sgm_financiero.ren_rubros_liquidacion as rrl', 'rdl.rubro', '=', 'rrl.id')
                                                ->select('rdl.id', 'rdl.liquidacion', 'rdl.rubro', 'rdl.valor', 'rdl.estado', 'rrl.descripcion')
                                                ->where('rdl.liquidacion', $valor)
                                                ->get();
                                               
            $liquidacion['rubros'] = $rubros;

            array_push($dataArray, $liquidacion);
        }
       

        $data = [
            'title' => 'Reporte de liquidacion',
            'date' => date('m/d/Y'),
            'DatosLiquidacion' => $dataArray,
            'fecha_formateada'=>$fecha_formateada
        ];

        $pdf = PDF::loadView('reportes.reporteTitulos', $data);

        // return $pdf->stream("aa.pdf");

        return $pdf->download('reporteTitulo.pdf');
    }

    //
    public function reporteTitulosCoactivaUrb(Request $r)
    {
        try{
            $dataArray = array();
            
            foreach($r->checkLiquidacion as $clave => $valor){
                $liquidacion = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as liq')

                // ->leftJoin('sgm_app.cat_ente as en', 'en.id', '=', 'liq.comprador')
                ->leftJoin('sgm_app.cat_predio as pre', 'pre.id', '=', 'liq.predio')
                ->join('sgm_app.cat_predio_propietario as pp', 'pp.predio', '=', 'pre.id')           
                ->leftJoin('sgm_app.cat_ente as en', 'pp.ente', '=', 'en.id')
                ->leftJoin('sgm_app.cat_ciudadela as cdla', 'cdla.id', '=', 'pre.ciudadela')
                ->select(
                    'liq.num_liquidacion',
                    'liq.anio',
                    'liq.avaluo_municipal',
                    'liq.avaluo_construccion',
                    'liq.avaluo_solar',
                    'liq.fecha_ingreso',
                    'liq.total_pago',
                    'pre.num_predio',
                    'saldo',
                    'liq.id',
                    'liq.anio',
                    'en.direccion',
                    'en.ci_ruc as cedula',
                    DB::raw("
                        CASE
                            WHEN liq.comprador IS NULL THEN liq.nombre_comprador
                            ELSE CASE en.es_persona
                                WHEN TRUE THEN COALESCE(en.apellidos, '') || ' ' || COALESCE(en.nombres, '')
                                ELSE COALESCE(en.razon_social, '')
                            END
                        END AS nombres
                    "),
                    // DB::raw("
                    //     CASE
                    //         WHEN liq.comprador IS NULL THEN 'S/N'
                    //         ELSE (SELECT ci_ruc FROM sgm_app.cat_ente WHERE cat_ente.id = liq.comprador)
                    //     END AS cedula
                    // "),
                    DB::raw("cdla.nombre || ' MZ: ' || pre.urb_mz || ' SL: ' || pre.urb_solarnew AS direccion1"),
                    'pre.clave_cat as cod_predial',
                    DB::raw("(SELECT razon_social FROM sgm_application.empresa) AS empresa"),
                    DB::raw("
                        (
                            SELECT
                                CASE
                                    WHEN (liq.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
                                        (ROUND(d.valor * (
                                            SELECT porcentaje
                                            FROM sgm_app.ctlg_descuento_emision
                                            WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                            AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)) / 100, 2) * (-1))
                                    WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN
                                        (ROUND((d.valor * 0.1), 2) + ROUND((liq.saldo) *
                                        (SELECT ROUND((porcentaje / 100), 2) FROM sgm_financiero.ren_intereses i WHERE i.anio = liq.anio), 2))
                                    ELSE
                                        ROUND((d.valor * 0.1), 2)
                                END AS valor_complemento
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = liq.id
                            AND d.rubro = 2
                        ) AS valor_complemento
                    "),

                    DB::raw("
                            (
                                SELECT
                                    CASE
                                        WHEN (liq.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
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
                                WHERE d.liquidacion = liq.id AND d.rubro = 2
                                LIMIT 1
                            ) AS desc
                        "),
                        
                        DB::raw("
                            (
                                SELECT
                                    CASE
                                        WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                            ROUND((liq.saldo * (
                                                SELECT ROUND((porcentaje / 100), 2) 
                                                FROM sgm_financiero.ren_intereses i
                                                WHERE i.anio = liq.anio
                                                LIMIT 1
                                            )), 2)
                                        ELSE
                                            0.00
                                        END
                                FROM sgm_financiero.ren_det_liquidacion d
                                WHERE d.liquidacion = liq.id 
                               
                                LIMIT 1
                            ) AS interes
                        "),

                        DB::raw("
                            (
                                SELECT
                                    CASE
                                        WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                            ROUND((d.valor * 0.10), 2)
                                        WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                            ROUND((d.valor * 0.10), 2)
                                        ELSE
                                            0.00
                                    END
                                FROM sgm_financiero.ren_det_liquidacion d
                                WHERE d.liquidacion = liq.id AND d.rubro = 2
                                LIMIT 1
                            ) AS recargos
                        "),

                         DB::raw('
                        (
                            SELECT
                                ROUND((
                                    COALESCE(liq.saldo, 0)

                                    +
                                    COALESCE((
                                        CASE
                                            WHEN (liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) < 7) THEN
                                                ROUND(
                                                    COALESCE((
                                                        SELECT SUM(d.valor)
                                                        FROM sgm_financiero.ren_det_liquidacion d
                                                        WHERE d.liquidacion = liq.id
                                                        AND d.rubro = 2
                                                    ),0)
                                                    * (
                                                        SELECT porcentaje
                                                        FROM sgm_app.ctlg_descuento_emision
                                                        WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                                        AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                                        LIMIT 1
                                                    ) / 100
                                                , 2) * (-1)
                                            ELSE 0
                                        END
                                    ), 0)

                                    +
                                    COALESCE((
                                        CASE
                                            WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN
                                                ROUND((liq.saldo * (
                                                    SELECT ROUND((porcentaje / 100), 2)
                                                    FROM sgm_financiero.ren_intereses i
                                                    WHERE i.anio = liq.anio
                                                    LIMIT 1
                                                )), 2)
                                            ELSE 0
                                        END
                                    ), 0)

                                    +
                                    COALESCE((
                                        CASE
                                            WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                                ROUND(COALESCE((
                                                    SELECT SUM(d.valor)
                                                    FROM sgm_financiero.ren_det_liquidacion d
                                                    WHERE d.liquidacion = liq.id
                                                    AND d.rubro = 2
                                                ),0) * 0.10, 2)
                                            WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                                ROUND(COALESCE((
                                                    SELECT SUM(d.valor)
                                                    FROM sgm_financiero.ren_det_liquidacion d
                                                    WHERE d.liquidacion = liq.id
                                                    AND d.rubro = 2
                                                ),0) * 0.10, 2)
                                            ELSE 0
                                        END
                                    ), 0)

                                ), 2)
                        ) AS total_complemento
                    '),

                    'liq.id_liquidacion'
                )
                ->where('liq.id', $valor)
                ->where('pp.estado','A')
                ->get();
                //dd($liquidacion);
            

                $fecha_hoy=date('Y-m-d');
                setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
                $fecha_timestamp = strtotime($fecha_hoy);    
                $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);
        

                $rubros = DB::connection('pgsql')->table('sgm_financiero.ren_det_liquidacion as rdl')
                                                    ->join('sgm_financiero.ren_rubros_liquidacion as rrl', 'rdl.rubro', '=', 'rrl.id')
                                                    ->select('rdl.id', 'rdl.liquidacion', 'rdl.rubro', 'rdl.valor', 'rdl.estado', 'rrl.descripcion')
                                                    ->where('rdl.liquidacion', $valor)
                                                    ->get();
                                                
                $liquidacion['rubros'] = $rubros;

                array_push($dataArray, $liquidacion);
            }
        
            //dd($dataArray);
            $data = [
                'title' => 'Reporte de liquidacion',
                'date' => date('m/d/Y'),
                'DatosLiquidacion' => $dataArray,
                'fecha_formateada'=>$fecha_formateada
            ];
            $nombrePDF="reporteTituloUrbano.pdf";
            $pdf = PDF::loadView('reportes.reporteTitulos', $data);

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


        }catch (\Throwable $e) {

            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error' .$e->getMessage()
            ]);

        }
    }

    public function buscaContribuyente($idliquidacion){
        try{   
            $buscaContruyente=DB::connection('pgsql')->table('sgm_app.cat_ente as en')
            ->leftJoin('sgm_financiero.ren_liquidacion as liq', 'en.id', '=', 'liq.comprador')
            ->where('liq.id',$idliquidacion)
            ->select('en.id','en.ci_ruc','en.nombres','en.apellidos','en.direccion')
            ->first();

            // if(is_null($buscaContruyente)){
            //     $buscaContruyente=DB::connection('pgsql')->table('sgm_app.cat_predio_propietario as pp')
            //     ->leftJoin('sgm_financiero.ren_liquidacion as liq', 'pp.predio', '=', 'liq.predio')
            //     ->leftJoin('sgm_app.cat_ente as en', 'en.id', '=', 'pp.ente')
            //     ->where('liq.id',$idliquidacion)
            //     ->where('pp.estado','A')
            //     ->select('en.id','en.ci_ruc','en.nombres','en.apellidos','en.direccion')
            //     ->first();
            // }

            return (['data'=>$buscaContruyente,'error'=>false]); 
            
        } catch (\Throwable $th) {
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th,'error'=>true]); 
        } 
    }

    public function actualizaContribuyente(Request $request){
        
        DB::beginTransaction(); 

        try{

            $cedula=$request->cedula;
            $nombres=$request->nombres;
            $apellidos=$request->apellidos;
            $direccion=$request->direccion;

            //por cedula
            $buscaContribuyente=PsqlEnte::where('ci_ruc',$cedula)
            ->orWhere('ci_ruc', substr($cedula, 0, 10))
            ->first();

            if(!is_null($buscaContribuyente)){

                $buscaContribuyente->ci_ruc=$cedula;
                $buscaContribuyente->nombres=$nombres;
                $buscaContribuyente->apellidos=$apellidos;
                $buscaContribuyente->direccion=$direccion;
                $buscaContribuyente->save();
                
                foreach($request->id_liquidacion as $data){                   
                    $actualizaNombreComprador=PsqlLiquidacion::find($data);
                    $actualizaNombreComprador->nombre_comprador=$nombres." ".$apellidos;
                    $actualizaNombreComprador->comprador=$buscaContribuyente->id;
                    $actualizaNombreComprador->save();    
                    
                    // $actualizaPropietarioPredio=PsqlPropietarioPredio::where('predio',$actualizaNombreComprador->predio)
                    // ->orderBy('id','desc')
                    // ->first();

                    // $actualizaPropietarioPredio->ente=$buscaContribuyente->id;
                    // $actualizaPropietarioPredio->estado='A';
                    // $actualizaPropietarioPredio->save();
                }

                DB::commit(); // Confirmar los cambios en la BD

                return [
                    'error' => false,
                    'mensaje' => 'Su informacion fue actualizada exitosamente.',
                ];
                
            }else{
                //si no encuentra x cedula busca el ultimo predio de las liquidaciones seleccionadas
                $buscaPredioProp=DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as liq')
                ->whereIn('id',$request->id_liquidacion)
                ->whereNotNull('predio')
                ->select('id_liquidacion','predio')
                ->get()->last();

                if(!is_null($buscaPredioProp)){
                    //buscamos el propietario
                    $buscaPropietario=DB::connection('pgsql')->table('sgm_app.cat_predio_propietario')
                    ->where('predio',$buscaPredioProp->predio)
                    ->where('estado','A')
                    ->select('ente')
                    ->get()->last();

                    if(is_null($buscaPropietario)){                        
                        DB::rollback();
                        return (['mensaje'=>'No se encontro propietario en las liquidaciones','error'=>true]);
                    }

                    $buscaContribuyente=PsqlEnte::where('id',$buscaPropietario->ente)
                    ->first();

                    if(!is_null($buscaContribuyente)){
                        $buscaContribuyente->ci_ruc=$cedula;
                        $buscaContribuyente->nombres=$nombres;
                        $buscaContribuyente->apellidos=$apellidos;
                        $buscaContribuyente->direccion=$direccion;
                        $buscaContribuyente->save();
                        foreach($request->id_liquidacion as $data){                           
                            $actualizaNombreComprador=PsqlLiquidacion::find($data);
                            $actualizaNombreComprador->nombre_comprador=$nombres." ".$apellidos;
                            $actualizaNombreComprador->comprador=$buscaContribuyente->id;
                            $actualizaNombreComprador->save();       
                            
                            // $actualizaPropietarioPredio=PsqlPropietarioPredio::where('predio',$actualizaNombreComprador->predio)
                            // ->orderBy('id','desc')
                            // ->first();

                            // $actualizaPropietarioPredio->ente=$buscaContribuyente->id;
                            // $actualizaPropietarioPredio->estado='A';
                            // $actualizaPropietarioPredio->save();
                        }

                        DB::commit(); // Confirmar los cambios en la BD

                        return [
                            'error' => false,
                            'mensaje' => 'Su informacion fue actualizada exitosamente.',
                        ];  
                    }
                }else{
                    DB::rollback();
                    return (['mensaje'=>'No se encontro predio en las liquidaciones','error'=>true]); 
                }
            }

           

        } catch (\Throwable $th) {
            DB::rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th,'error'=>true]); 
        } 

    }

     public function actualizaContribuyente_resp(Request $request){
        
        DB::beginTransaction(); 
        try{
            $id_contribuyente=$request->id;
            $cedula=$request->cedula;
            $nombres=$request->nombres;
            $apellidos=$request->apellidos;
            $direccion=$request->direccion;
            $id_liquidacion=$request->id_liquidacion;

            $verificaCedula=PsqlEnte::where('ci_ruc',$cedula)
            ->where('id','!=',$id_contribuyente)
            ->first();
            if(!is_null($verificaCedula)){
                return [
                    'error' => true,
                    'mensaje' => 'La cedula ya existe para otro contribuyente.',
                ];
            }

            $actualizaContribuyente=PsqlEnte::find($id_contribuyente);
            $actualizaContribuyente->ci_ruc=$cedula;
            $actualizaContribuyente->nombres=$nombres;
            $actualizaContribuyente->apellidos=$apellidos;
            $actualizaContribuyente->direccion=$direccion;
            
            if($actualizaContribuyente->save()){
                $actualizaNombreComprador=PsqlLiquidacion::find($id_liquidacion);
                $actualizaNombreComprador->nombre_comprador=$nombres." ".$apellidos;
                $actualizaNombreComprador->save();

                DB::commit(); // Confirmar los cambios en la BD

                return [
                    'error' => false,
                    'mensaje' => 'Su informacion fue actualizada exitosamente.',
                ];  
            }

            

        } catch (\Throwable $th) {
            DB::rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th,'error'=>true]); 
        } 

    }
}
