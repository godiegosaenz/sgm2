<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Exception;

class TituloRuralController extends Controller
{

    public function consultaTitulos($tipo,$valor)
    {
        try {
        
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral','cv.CarVe_FechaEmision','cv.CarVe_NumTitulo','cv.CarVe_CI'
            ,'cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','cv.CarVe_Nombres','cv.CarVe_ValorEmitido')
            ->where(function($query)use($tipo,$valor) {
                if($tipo==1){
                    $query->where('CarVe_CI', '=', $valor);
                }else{
                    $query->where('cv.Pre_CodigoCatastral', '=', $valor);
                }
                
            })
            
            // ->whereIn('cv.CarVe_Estado',['E'])
            ->where('Pre_Tipo','Rural')
            ->orderby('CarVe_NumTitulo','desc')
            ->get();

            foreach($liquidacionRural as $key=> $data){
                $anio=explode("-",$data->CarVe_NumTitulo);
                $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                ->where('IntMo_Año',$anio)
                ->select('IntMo_Valor')
                ->first();

                $valor=(($consultaInteresMora->IntMo_Valor/100) * $data->CarVe_ValorEmitido);
                
                $valor=number_format($valor,2);

                $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                $liquidacionRural[$key]->intereses=$valor;

                $total_pago=$valor +$data->CarVe_ValorEmitido;
                $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);
            }

            return ["resultado"=>$liquidacionRural, "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function reportetest(Request $r){
        try{
            $dataArray = array();
            foreach($r->checkLiquidacion as $clave => $valor){
                $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
                ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
                ->leftJoin('PROPIETARIO as pr', 'pr.Ciu_Cedula', '=', 'cv.CarVe_CI')
                ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
                ->select('cv.Pre_CodigoCatastral','cv.CarVe_FechaEmision','cv.CarVe_NumTitulo','cv.CarVe_CI'
                ,'cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','p.Pre_NombrePredio','cv.CarVe_ValTotalTerrPredio'
                ,'cv.CarVe_ValTotalEdifPredio','cv.CarVe_ValOtrasInver','cv.CarVe_ValComerPredio','cv.CarVe_RebajaHipotec'
                ,'cv.CarVe_BaseImponible','cv.CarVe_IPU','cv.CarVe_TasaAdministrativa','cv.CarVe_Bomberos'
                ,'cv.CarVe_ValorEmitido','pr.Pro_DireccionDomicilio')
                ->where('CarVe_NumTitulo', '=', $valor)
                ->get();

                foreach($liquidacionRural as $key=> $data){
                    $anio=explode("-",$data->CarVe_NumTitulo);
                    $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                    ->where('IntMo_Año',$anio)
                    ->select('IntMo_Valor')
                    ->first();

                    $valor=(($consultaInteresMora->IntMo_Valor/100) * $data->CarVe_ValorEmitido);
                    
                    $valor=number_format($valor,2);

                    $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                    $liquidacionRural[$key]->intereses=$valor;

                    $total_pago=$valor +$data->CarVe_ValorEmitido;
                    $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);
                }
                         
                array_push($dataArray, $liquidacionRural);
            }
          
            return ["resultado"=>$dataArray, "error"=>false];
            
        }catch (\Throwable $e) {
           
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'.$e
            ]);
            
        }
    }

}