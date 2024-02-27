<?php

namespace App\Http\Controllers\reportes;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Support\Facades\DB;

class LiquidacionReporteController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function reporteRemision($id)
    {
        $predio = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id','clave_cat')->where('num_predio', '=', $id)->first();
        $liquidacionUrbana = null;
        $clave_cat2 = 0;
        $num_predio2 = $id;
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
            $suma_interes = $DatosLiquidacion->sum('interes');
            $suma_recargo = $DatosLiquidacion->sum('recargo');
            $suma_emision = $DatosLiquidacion->sum('total_pago');
            $suma_total = $DatosLiquidacion->sum('suma_emision_interes_recargos');
            $clave_cat2 = $predio->clave_cat;

        }



        $data = [
            'title' => 'Reporte de liquidacion',
            'date' => date('m/d/Y'),
            'DatosLiquidacion' => $DatosLiquidacion,
            'suma_interes' => $suma_interes,
            'suma_recargo' => $suma_recargo,
            'suma_emision' => $suma_emision,
            'suma_total' => $suma_total,
            'clave_cat2' => $clave_cat2,
            'num_predio2' => $num_predio2
        ];

        $pdf = PDF::loadView('reportes.reporteLiquidacionRemision', $data);

        return $pdf->download('reporteLiquidacion.pdf');
    }
}
