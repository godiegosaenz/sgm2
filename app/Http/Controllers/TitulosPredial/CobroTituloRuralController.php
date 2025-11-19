<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use App\Models\TransitoVehiculo;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class CobroTituloRuralController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        if(!Auth()->user()->hasPermissionTo('Cobro Titulo Rural'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('cobroTituloRural.index');
    }

    public function buscar(Request $request){ 
        try{
            $tipo=$request->tipo;
            $tipo_per=$request->tipo_per;
            $valor=$request->valor;

            // $liquidacionRuralCV=\DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            // ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            // ->leftJoin('PREDIO as p', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            // ->select('cv.Pre_CodigoCatastral','c.Ciu_Apellidos','c.Ciu_Nombres','cv.CarVe_RUC'
            // ,'cv.CarVe_CI','cv.CarVe_Calle','cv.CarVe_direccPropietario as direccion')
            // ->where(function($query)use($tipo,$valor, $tipo_per) {
            //     if($tipo==1 && $tipo_per==1){
            //         $query->where('CarVe_CI', '=', $valor);
            //     }else{
            //         $query->where('cv.Pre_CodigoCatastral', '=', $valor);
            //     }
                
            // })            
            // ->whereIn('cv.CarVe_Estado',['E'])
            // ->distinct()
            // ->get();

            // if(sizeof($liquidacionRuralCV)==0){
            //     $liquidacionRuralAct=\DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            //     ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'tp.Titpr_RUC_CI')
            //     ->leftJoin('PREDIO as p', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            //     ->select('tp.Pre_CodigoCatastral','c.Ciu_Apellidos','c.Ciu_Nombres','tp.Titpr_RUC_CI'
            //     ,'tp.Titpr_RUC_CI','p.Pre_NombrePredio')
            //     ->where(function($query)use($tipo,$valor, $tipo_per) {
            //         if($tipo==1 && $tipo_per==1){
            //             $query->where('Titpr_RUC_CI', '=', $valor);
            //         }else{
            //             $query->where('tp.Pre_CodigoCatastral', '=', $valor);
            //         }
                    
            //     })            
            //     ->whereIn('tp.TitPr_Estado',['E'])
            //     ->distinct()
            //     ->get();
               
            //     return (['data'=>$liquidacionRuralAct,'error'=>false]); 

            // }

            // return (['data'=>$liquidacionRuralCV,'error'=>false]); 

             $liquidacionRuralAct=\DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'tp.Titpr_RUC_CI')
            ->leftJoin('PREDIO as p', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral','c.Ciu_Apellidos','c.Ciu_Nombres','tp.Titpr_RUC_CI'
            ,'tp.Titpr_RUC_CI','p.Pre_NombrePredio','tp.TitPr_DireccionCont')
            ->where(function($query)use($tipo,$valor, $tipo_per) {
                if($tipo==1 && $tipo_per==1){
                    $query->where('Titpr_RUC_CI', '=', $valor);
                }else{
                    $query->where('tp.Pre_CodigoCatastral', '=', $valor);
                }
                
            })            
            ->whereIn('tp.TitPr_Estado',['E'])
            ->distinct()
            ->get();
               
                return (['data'=>$liquidacionRuralAct,'error'=>false]); 


        } catch (\Throwable $th) {
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th,'error'=>true]); 
        } 
    }

    public function consultarTitulos($clave)
    {
        try {
            $liquidacionRural=\DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave','cv.CarVe_FechaEmision as fecha_emi','cv.CarVe_NumTitulo as num_titulo','cv.CarVe_CI as num_ident','cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','cv.CarVe_Nombres as nombre_per','cv.CarVe_ValorEmitido as valor_emitido','cv.CarVe_TasaAdministrativa as tasa','CarVe_Calle as direcc_cont')
            ->where('cv.Pre_CodigoCatastral', '=', $clave)
            
            ->whereIn('cv.CarVe_Estado',['E'])
            // ->where('Pre_Tipo','Rural')
            ->orderby('CarVe_NumTitulo','asc')
            ->get();

            foreach($liquidacionRural as $key=> $data){
                $anio=explode("-",$data->num_titulo);
                $consultaInteresMora=\DB::connection('sqlsrv')->table('INTERES_MORA as im')
                ->where('IntMo_Año',$anio)
                ->select('IntMo_Valor')
                ->first();
               

                $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->valor_emitido - $data->tasa));
                
                $valor=number_format($valor,2);

                $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                $liquidacionRural[$key]->intereses=$valor;

                $total_pago=$valor +$data->valor_emitido;
                $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);
            }
            $liquidacionActual=[];

            $liquidacionActual=\DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'tp.Titpr_RUC_CI')
            ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral as clave','tp.TitPr_FechaEmision as fecha_emi','tp.TitPr_NumTitulo as num_titulo','tp.Titpr_RUC_CI as num_ident' ,'tp.TitPr_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','tp.TitPr_Nombres as nombre_per','tp.TitPr_ValorEmitido as valor_emitido','tp.TitPr_TasaAdministrativa as tasa','TitPr_DireccionCont as direcc_cont')
            ->where('tp.Pre_CodigoCatastral', '=', $clave)
            
            ->whereIn('tp.TitPr_Estado',['E'])
            // ->where('Pre_Tipo','Rural')
            ->orderby('TitPr_NumTitulo','asc')
            ->get();

            foreach($liquidacionActual as $key=> $data){
                $anio=explode("-",$data->num_titulo);
                $consultaInteresMora=\DB::connection('sqlsrv')->table('INTERES_MORA as im')
                ->where('IntMo_Año',$anio)
                ->select('IntMo_Valor')
                ->first();
                
                if(!is_null($consultaInteresMora)){
                    $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->valor_emitido - $data->tasa));
                    
                    $valor=number_format($valor,2);

                    $liquidacionActual[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                    $liquidacionActual[$key]->intereses=$valor;

                    $total_pago=$valor +$data->valor_emitido;
                    $liquidacionActual[$key]->total_pagar=number_format($total_pago,2);
                }else{
                    $liquidacionActual[$key]->porcentaje_intereses=0;
                    $liquidacionActual[$key]->intereses=0;

                    $total_pago=$valor +$data->valor_emitido;
                    $liquidacionActual[$key]->total_pagar=number_format($total_pago,2);
                }
            }
            $resultado = $liquidacionRural->merge($liquidacionActual);

            return ["resultado"=>$resultado, "error"=>false];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }
}