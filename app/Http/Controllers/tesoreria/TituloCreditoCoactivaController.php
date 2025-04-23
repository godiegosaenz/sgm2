<?php

namespace App\Http\Controllers\tesoreria;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Exception;
use Barryvdh\DomPDF\Facade\Pdf;

class TituloCreditoCoactivaController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $num_predio = 0;
        return view('tesoreria.TitulosCreditosCoactiva',compact('num_predio'));
    }

    public function consulta(Request $r)
    {
        try {
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
        if(count($liquidacionUrbana) >= 1) {
            return view('tesoreria.TitulosCreditosCoactiva',compact('liquidacionUrbana','num_predio'));
        }else{
            return redirect('tituloscoactiva/')->with('status', 'No existe la matricula ingresada');
        }

        } catch (Exception $e) {
            // Log the message locally OR use a tool like Bugsnag/Flare to log the error
            return redirect('tituloscoactiva/')->with('status', 'Problema de conexion '.$e->getMessage());

        }
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
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
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
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
                DB::raw("cdla.nombre || ' MZ: ' || pre.urb_mz || ' SL: ' || pre.urb_solarnew AS direccion"),
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
                'liq.id_liquidacion'
            )
            ->where('liq.id', $valor)
            ->get();
            
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
}
