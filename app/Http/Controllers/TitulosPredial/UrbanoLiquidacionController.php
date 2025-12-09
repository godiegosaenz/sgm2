<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\TituloRural;
use DB;
class UrbanoLiquidacionController extends Controller
{
    public function index()
    {
        // $consultaData=DB::connection('sqlsrv')->table('sgm_financiero.ren_liquidacion as tp')
        // return view('liquidacion_urbano.index');
    }

    public function buscarDeudaPredio(Request $request){ 
        try{
            $tipo=$request->tipo;
            $tipo_per=$request->tipo_per;
            $valor=$request->valor;

            $liquidacionRuralAct=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            ->select('tp.Titpr_Nombres as nombres','tp.Titpr_RUC_CI','tp.TitPr_DireccionCont as direccion','tp.TitPr_Estado as ruc')
            ->where(function($query)use($tipo,$valor, $tipo_per) {
                if($tipo==1){
                    $query->where('Titpr_RUC_CI', '=', $valor);
                }else if($tipo==2){
                    $query->where('tp.Pre_CodigoCatastral', '=', $valor);
                }else{
                    $query->where('tp.Titpr_Nombres', 'LIKE', "%$valor%");
                }
                
            })            
            ->whereIn('tp.TitPr_Estado',['E','N']) //E=Emitidos, C=Cancelado, Q=Cancelado Nueva Emitido, N=Nueva Emision
            ->distinct()
            ->limit(10)
            ->get();
            
            if(sizeof($liquidacionRuralAct)>0){
                return (['data'=>$liquidacionRuralAct,'error'=>false]); 
            }
           
            $liquidacionRuralAct=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->select('cv.CarVe_Nombres as nombres','cv.CarVe_CI as Titpr_RUC_CI'
           ,'cv.CarVe_Calle as TitPr_DireccionCont','cv.carVe_RUC as ruc')
            ->where(function($query)use($tipo,$valor, $tipo_per) {
                if($tipo==1){
                    $query->where('CarVe_CI', '=', $valor)
                    ->orWhere('carVe_RUC',$valor);
                }else if($tipo==2){
                    $query->where('cv.Pre_CodigoCatastral', '=', $valor);
                }else{
                    $query->where('cv.CarVe_Nombres', 'LIKE', "%$valor%");

                }
                
            }) 
            ->whereNotNull('CarVe_Nombres')           
            ->whereIn('cv.CarVe_Estado',['E']) //E=Emitidos, C=Cancelado, Q=Cancelado Nueva Emitido, N=Nueva Emision
            ->distinct()
            ->limit(10)
            ->get();            
               
            return (['data'=>$liquidacionRuralAct,'error'=>false]); 


        } catch (\Throwable $th) {
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th,'error'=>true]); 
        } 
    }

    
}
