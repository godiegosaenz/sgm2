<?php

namespace App\Http\Controllers\EmisionTitulo;

use App\Http\Controllers\Controller;
use App\Models\CarteraVencidaRural;
use App\Models\TituloRural;
use Illuminate\Http\Request;
use App\Models\PsqlEnte;
use Illuminate\Support\Facades\DB;
use App\Http\Controllers\TitulosPredial\CobroTituloRuralController;

class EmisionTituloRuralController extends Controller
{
    private $titulo= null;
    public function __construct() {
        $this->titulo = new CobroTituloRuralController;
    }
    public function index(){
        return view('emision_rural.index');
    }

    public function consultarTitulosBaja($cedula, $notifica=1){
        try {
            $prediosRurales=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
             ->where(function ($query) use($cedula){
                $query->where('CarVe_CI',$cedula)
                ->orWhere('CarVe_RUC',$cedula);
            })
            ->whereIn('cv.CarVe_Estado',['B'])
            ->pluck('Pre_CodigoCatastral')
            ->toArray();
            
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave','cv.CarVe_FechaEmision as fecha_emi','cv.CarVe_NumTitulo as num_titulo','cv.CarVe_CI as num_ident','cv.CarVe_Estado','cv.CarVe_Nombres as nombre_per','cv.CarVe_ValorEmitido as valor_emitido','cv.CarVe_TasaAdministrativa as tasa','CarVe_Calle as direcc_cont','cv.Carve_Recargo as recargo','cv.Carve_Descuento as descuento','CarVe_Observaciones as motivo')
            ->where('P.Pre_Tipo','Rural')
            ->whereIN('cv.Pre_CodigoCatastral',$prediosRurales)
            ->whereIn('cv.CarVe_Estado',['B']) //
            ->orderby('cv.Pre_CodigoCatastral','asc')            
            ->distinct()
            ->get();
          
            $mes_Actual=date('m');
           
            $aplica_remision=0;
            if($mes_Actual<7){
                $aplica_remision=1;
            }
            $aplica_remision=0;
            $total_valor=0;
            $exoneracion_3era_edad=[];
            $exoneracion_discapacidad=[];
            foreach($liquidacionRural as $key=> $data){
                $valor=0;
                $subtotal=0;
                $subtotal=number_format($data->valor_emitido,2);
                $anio=explode("-",$data->num_titulo);
                if($aplica_remision==0){
                   
                    $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                    ->where('IntMo_Año',$anio)
                    ->select('IntMo_Valor')
                    ->first();
                    
                    $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->valor_emitido - $data->tasa));   
                    $valor = floatval(str_replace(',', '', $valor));             
                    $valor=number_format($valor,2);
                    
                    $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                }else{
                    $cero=0;
                    $valor=number_format($cero,2);
                    $liquidacionRural[$key]->porcentaje_intereses=number_format($cero,2);
                }
                $valor = floatval(str_replace(',', '', $valor));
                $subtotal = floatval(str_replace(',', '', $subtotal));
                $liquidacionRural[$key]->subtotal_emi=$subtotal;
                
                $liquidacionRural[$key]->intereses=$valor;

                // $total_pago=($valor + $data->valor_emitido + $data->recargo) - $data->descuento;
                $total_pago = (
                    (is_numeric($valor) ? $valor : 0) + 
                    (is_numeric($data->valor_emitido) ? $data->valor_emitido : 0) + 
                    (is_numeric($data->recargo) ? $data->recargo : 0) - 
                    (is_numeric($data->descuento) ? $data->descuento : 0)
                );

                $total_pago = floatval(str_replace(',', '', $total_pago));
                // dd($total_pago);

                $liquidacionRural[$key]->total_pagar=$total_pago;
                $liquidacionRural[$key]->anio=$anio[0];

                $total_valor=$total_valor+$total_pago;
                
              
                $buscar_exon=DB::connection('sqlsrv')->table('REBAJA_VALOR')
                ->where('TitPrCarVe_NumTitulo',$data->num_titulo)
                ->select('Reb_Codigo')
                ->first();

                if(!is_null($buscar_exon)){
                    if($buscar_exon->Reb_Codigo=='01'){
                        array_push($exoneracion_3era_edad, $data->num_titulo);
                        $liquidacionRural[$key]->exoneracion='Mayor';
                    }else if ($buscar_exon->Reb_Codigo=='02'){
                        array_push($exoneracion_discapacidad, $data->num_titulo);
                        $liquidacionRural[$key]->exoneracion='Discapacidad';
                    }else{
                        $liquidacionRural[$key]->exoneracion='No';
                    }
                }else{
                    $liquidacionRural[$key]->exoneracion='No';
                }

            }
            $liquidacionActual=[];
            // dd($liquidacionRural);
            
           
            return ["resultado"=>$liquidacionRural, 
                    "total_valor"=>number_format($total_valor,2),
                    "exoneracion_3era_edad"=>$exoneracion_3era_edad,
                    "exoneracion_discapacidad"=>$exoneracion_discapacidad,
                    "error"=>false,
                    "aplica_remision"=>$aplica_remision
            ];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tardexx ".$e->getMessage()." Linea=> ".$e->getLine(), "error"=>true];
        }
    }

    public function simulacionEmisionRural(Request $request){
        try{
            $crearPdf=$this->titulo->pdfTituloPrevio($request->checkLiquidacion,'nocopia','S');
            if($crearPdf['error']==true){
                DB::Rollback();
                return ["mensaje"=>$crearPdf["mensaje"], "error"=>true];
            }
            return ["mensaje"=>"Documentos generados exitosamente", "error"=>false, "pdf"=>$crearPdf['pdf']];
        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function generacionEmisionRural(Request $request){
        try{
            foreach($request->checkLiquidacion as $item){
                $solo_anio=explode("-",$item);
              
                if($solo_anio[0]==date('Y')){
                    // $anio_actual=[];
                    // array_push($anio_actual,$item);
                }else{
                   

                    $obtenerCV=CarteraVencidaRural::where('CarVe_NumTitulo',$item)
                    ->where('CarVe_Estado','B')
                    ->first();
                    
                    $salarioBase=DB::connection('sqlsrv')->table('RBU')
                    ->where('Rbu_Año',$solo_anio[0])
                    ->select('Rbu_Valor')
                    ->first();

                    $base_im=floatval(str_replace(',', '', $obtenerCV->CarVe_BaseImponible));
                    $salarioBase=$salarioBase->Rbu_Valor;

                    $max=$salarioBase *15;
                  
                    if($base_im<=$max){
                        $ipuNew=0;                       
                        
                    }else{
                       $ipuNew=$base_im *  0.001;
                    }
                    $bom=floatval(str_replace(',', '', $obtenerCV->CarVe_ValComerPredio));
                    $bom_new=$bom * 0.00015;

                    $cedulaUser=auth()->user()->persona->cedula;
                   
                    $usuario_sic=DB::connection('sqlsrv')->table('USUARIOSIC')
                    ->where('USU_CEDULA', $cedulaUser)
                    ->where('USU_ACTIVO',1)
                    ->select('USU_PERFIL','USU_NICK')
                    ->first();

                    $carteraVencida=DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
                    ->where('CarVe_NumTitulo',$item)
                    ->where('Carve_Estado','B')
                    ->update([
                        "CarVe_FechaEmision" => date('Y-d-m 00:00:00.000'), // ✔ Fecha automática correcta
                        "Carve_Estado" => "E",
                        "Usu_usuario" => $usuario_sic->USU_NICK,
                        "CarVe_IPU" => round($ipuNew, 2),
                        "CarVe_Bomberos" => round($bom_new, 2) // ✔ formato numérico válido
                    ]);

                }

            }

            $crearPdf=$this->titulo->pdfTituloPrevio($request->checkLiquidacion,'nocopia','S');
            if($crearPdf['error']==true){
                DB::Rollback();
                return ["mensaje"=>$crearPdf["mensaje"], "error"=>true];
            }
            return ["mensaje"=>"Documentos generados exitosamente", "error"=>false, "pdf"=>$crearPdf['pdf']];
        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function vistaGenerar(){
        return view('emision_rural.generar');
    }

     public function consultarPredios($cedula, $notifica=1){
        try {
           
            $prediosRuralesCV=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->Join('PREDIO as p', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->Join('UBICACION as u', 'u.Ubi_Codigo', '=', 'p.Ubi_Codigo')
            ->Join('UBICACION as up', 'up.Ubi_Codigo', '=', 'u.Ubi_CodigoPadre')
                ->where(function ($query) use($cedula){
                $query->where('CarVe_CI',$cedula)
                ->orWhere('CarVe_RUC',$cedula);
            })
            ->select('cv.Pre_CodigoCatastral','up.Ubi_Descripcion','Pre_NombrePredio')
            ->whereIn('cv.CarVe_Estado',['C','E'])
            ->where('Pre_Estado','A')
            ->distinct('p.Pre_CodigoCatastral')
            ->get();
            

            if(sizeof($prediosRuralesCV)>0){
                foreach($prediosRuralesCV as $key=> $data){
                    $cv=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
                    ->where('Pre_CodigoCatastral',$data->Pre_CodigoCatastral)
                    ->whereIn('cv.CarVe_Estado',['C','E'])
                    ->select('CarVe_ValComerPredio','CarVe_BaseImponible','CarVe_ValTotalTerrPredio','CarVe_ValTotalEdifPredio','CarVe_ValOtrasInver','CarVe_RebajaHipotec')
                    ->orderby('CarVe_NumTitulo','desc')
                    ->first();

                    $prediosRuralesCV[$key]->valor_comercial=number_format($cv->CarVe_ValComerPredio,2);
                    $prediosRuralesCV[$key]->base_imponible=number_format($cv->CarVe_BaseImponible,2);
                    $prediosRuralesCV[$key]->valor_terreno=number_format($cv->CarVe_ValTotalTerrPredio,2);
                    $prediosRuralesCV[$key]->valor_edif_predio=number_format($cv->CarVe_ValTotalEdifPredio,2);
                    $prediosRuralesCV[$key]->otras_inv=number_format($cv->CarVe_ValOtrasInver,2);
                    $prediosRuralesCV[$key]->rebaja_hipo=number_format($cv->CarVe_RebajaHipotec,2);
                   
                }
            }
           

            $prediosRuralesTA=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            ->Join('PREDIO as p', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->Join('UBICACION as u', 'u.Ubi_Codigo', '=', 'p.Ubi_Codigo')
            ->Join('UBICACION as up', 'up.Ubi_Codigo', '=', 'u.Ubi_CodigoPadre')
            ->where('Titpr_RUC_CI',$cedula)
            ->select('tp.Pre_CodigoCatastral','up.Ubi_Descripcion','Pre_NombrePredio')
            ->whereIn('tp.TitPr_Estado',['C','E','Q','N'])
            ->where('Pre_Estado','A')
            ->distinct('p.Pre_CodigoCatastral')
            ->get();

         

            if(sizeof($prediosRuralesTA)>0){
                foreach($prediosRuralesTA as $key=> $data){
                    $tp=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
                    ->where('Pre_CodigoCatastral',$data->Pre_CodigoCatastral)
                    ->whereIn('tp.TitPr_Estado',['C','E','Q','N'])
                    ->select('TitPr_ValComerPredio','TitPr_BaseImponible','TitPr_ValTotalTerrPredio','TitPr_ValTotalEdifPredio','TitPr_ValOtrasInver','TitPr_RebajaHipotec')
                    ->orderby('TitPr_NumTitulo','desc')
                    ->first();

                    $prediosRuralesTA[$key]->valor_comercial=number_format($tp->TitPr_ValComerPredio,2);
                    $prediosRuralesTA[$key]->base_imponible=number_format($tp->TitPr_BaseImponible,2);
                    $prediosRuralesTA[$key]->valor_terreno=number_format($tp->TitPr_ValTotalTerrPredio,2);
                    $prediosRuralesTA[$key]->valor_edif_predio=number_format($tp->TitPr_ValTotalEdifPredio,2);
                    $prediosRuralesTA[$key]->otras_inv=number_format($tp->TitPr_ValOtrasInver,2);
                    $prediosRuralesTA[$key]->rebaja_hipo=number_format($tp->TitPr_ValOtrasInver,2);
                   
                }
            }

           $prediosRurales = $prediosRuralesCV
            ->merge($prediosRuralesTA)
            ->unique('Pre_CodigoCatastral')
            ->values();
           
            return ["resultado"=>$prediosRurales
            ];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tardexx ".$e->getMessage()." Linea=> ".$e->getLine(), "error"=>true];
        }
    }

    public function simulacionEmisionRuralNew(Request $request){
        try{
            // dd($request->all());
            $predios=$request->checkPredio;
            $anio=$request->anio_emi;
           
            $verificaEstadoTitulo=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereIN('Pre_CodigoCatastral',$predios)
            ->whereIn('cv.CarVe_Estado',['C'])
            ->select('Pre_CodigoCatastral')
            ->whereRaw('LEFT(CarVe_NumTitulo, 4) = ?', [$anio])
            ->distinct()
            ->get();
            
            if(sizeof($verificaEstadoTitulo)>0){
                $titulosPagados = $verificaEstadoTitulo
                    ->pluck('Pre_CodigoCatastral')
                    ->toArray();

                $mensaje = 'Los siguientes títulos ya fueron pagados para el año ' .$request->anio_emi. ': ' . implode(', ', $titulosPagados);

                return response()->json([
                    'error' => true,
                    'mensaje' => $mensaje
                ]);
           
            }

            $cedulaUser=auth()->user()->persona->cedula;
                    
            $usuario_sic=DB::connection('sqlsrv')->table('USUARIOSIC')
            ->where('USU_CEDULA', $cedulaUser)
            ->where('USU_ACTIVO',1)
            ->select('USU_PERFIL','USU_NICK')
            ->first();

            $name_user="ADMINISTRADOR";
            if(!is_null($usuario_sic)){
                $name_user=$usuario_sic->USU_NICK;
            }


            $valor_comercial=$request->ValorComercial;
            $base_imponible=$request->BaseImponible;
            $nombre_predio=$request->NombrePredio;
            $sitio_barrio=$request->SitioBarrio;

            $liquidacion=[];
            foreach($request->checkPredio as $key=> $item){

                $salarioBase=DB::connection('sqlsrv')->table('RBU')
                ->where('Rbu_Año',$anio)
                ->select('Rbu_Valor')
                ->first();

                $base_im=floatval(str_replace(',', '', $base_imponible[$key]));
               
                $salarioBase=$salarioBase->Rbu_Valor;
               
                $max=$salarioBase *15;
               
                if($base_im<=$max){-
                    $ipuNew=0;    
                  
                }else{
                    $ipuNew=$base_im *  0.001;
                }


                $bom=floatval(str_replace(',', '', $valor_comercial[$key]));
                $bom_new=$bom * 0.00015;

                $existeCV=CarteraVencidaRural::where('Pre_CodigoCatastral',$item)
                ->whereIn('CarVe_Estado',['E','C'])
                ->whereRaw('LEFT(CarVe_NumTitulo, 4) = ?', [$anio])
                ->orderby('CarVe_NumTitulo','desc')
                ->first();

             
                if(!is_null($existeCV)){
                    if($existeCV->CarVe_Estado!='C'){
                    
                        $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                        ->where('IntMo_Año',$anio)
                        ->select('IntMo_Valor')
                        ->first();
                    
                        $valor=(($consultaInteresMora->IntMo_Valor/100) * ($existeCV->CarVe_ValorEmitido - $existeCV->CarVe_TasaAdministrativa));   
                        $valor = floatval(str_replace(',', '', $valor));             
                        $valor_intereses=number_format($valor,2);

                     

                        $carteraVencida=DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
                        ->where('CarVe_NumTitulo',$existeCV->CarVe_NumTitulo)
                        ->update([
                            "CarVe_FechaEmision" => date('Y-d-m 00:00:00.000'), // ✔ Fecha automática correcta
                            "Carve_Estado" => "E",
                            "Usu_usuario" => $name_user,
                            "CarVe_IPU" => round($ipuNew, 2),
                            "CarVe_Interes" => round($valor_intereses, 2),  
                            "CarVe_Observaciones" => "Generado por sgm2",                          
                            "CarVe_Bomberos" => round($bom_new, 2), // ✔ formato numérico válido
                            "CarVe_ValorEmitido" => round(
                                $ipuNew + $existeCV->Carve_Valor1 +$existeCV->CarVe_TasaAdministrativa + $bom_new, 2
                            )
                        ]);

                        array_push($liquidacion, $existeCV->CarVe_NumTitulo);
                    }
                }else{

                    $existeTP=TituloRural::where('Pre_CodigoCatastral',$item)
                    ->whereIn('TitPr_Estado',['C','E','Q','N'])
                    ->orderby('TitPr_NumTitulo','desc')
                    ->first();

                    $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                    ->where('IntMo_Año',$anio)
                    ->select('IntMo_Valor')
                    ->first();
                
                    $valor=(($consultaInteresMora->IntMo_Valor/100) * ($existeTP->TitPr_ValorEmitido - $existeTP->TitPr_TasaAdministrativa));   
                    $valor = floatval(str_replace(',', '', $valor));             
                    $valor_intereses=number_format($valor,2);

                                        
                    $cantidad = strlen((string)$existeTP->Titpr_RUC_CI);
                    $ruc='0000000000000';
                    $cedula='0000000000';
                    if($cantidad==10){
                        $cedula=$existeTP->Titpr_RUC_CI;
                    }else{
                        $ruc=$existeTP->Titpr_RUC_CI;
                    }

                    if(!is_null($existeTP)){
                        $obtenerUltimo=CarteraVencidaRural::whereRaw('LEFT(CarVe_NumTitulo, 4) = ?', [$anio])
                        ->orderby('CarVe_NumTitulo','desc')
                        ->select('CarVe_NumTitulo')
                        ->first();
                        $obtenerUltimo=explode("-",$obtenerUltimo->CarVe_NumTitulo);
                        $secuencia=$obtenerUltimo[1]+1;
                        $secuencia=str_pad($secuencia, 6, "0", STR_PAD_LEFT);
                     
                        $num_titulo=$anio."-".$secuencia."-PR";
                       
                        $cero=0;
                        $carteraVencida=DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
                        ->insert([
                            "Pre_CodigoCatastral" => $existeTP->Pre_CodigoCatastral,
                            "CarVe_RUC" => $ruc,
                            "CarVe_CI" => $cedula,
                            "CarVe_Nombres" => $existeTP->TitPr_Nombres,
                            "CarVe_Calle" => $nombre_predio[$key],
                            "CarVe_ValTotalTerrPredio" => round($existeTP->TitPr_ValTotalTerrPredio, 2),
                            "CarVe_ValTotalEdifPredio" => round($existeTP->TitPr_ValTotalEdifPredio, 2),
                            "CarVe_ValCultivos" => round($existeTP->TitPr_ValCultivos, 2),
                            "CarVe_ValForestales" => round($existeTP->TitPr_ValForestales, 2),
                            "CarVe_ValObrasInter" => round($existeTP->TitPr_ValObrasInter, 2),
                            "CarVe_ValOtrasInver" => round($existeTP->TitPr_ValOtrasInver, 2),
                            "CarVe_ValComerPredio" => round($existeTP->TitPr_ValComerPredio, 2),
                            "CarVe_RebajaHipotec" => round($existeTP->TitPr_RebajaHipotec, 2),
                            "CarVe_BaseImponible" => round($existeTP->TitPr_BaseImponible, 2),
                            "CarVe_RebajaGeneral" => $cero,
                            "CarVe_IPU" => round($ipuNew, 2),
                            "CarVe_Magisterio" => $cero,
                            "CarVe_EducacionElemental" => $cero,
                            "CarVe_MedicinaRural" => $cero,
                            "CarVe_EstablecEducativos" => $cero,
                            "CarVe_SolNoEdif" => round($existeTP->TitPr_SolNoEdif, 2),
                            "CarVe_ConstObsoleta" => round($existeTP->TitPr_ConstObsoleta, 2),
                            "CarVe_SNERecargo" => round($existeTP->TitPr_SNERecargo, 2),
                            "CarVe_ViviendaRural" => $cero,
                            "CarVe_FechaEmision" => date('Y-d-m 00:00:00.000'), // ✔ Fecha automática correcta
                            "CarVe_TasaAdministrativa" => round($existeTP->TitPr_TasaAdministrativa, 2),
                            "CarVe_OtrosAdicionales" => $cero,
                            "CarVe_Bomberos" => round($bom_new, 2),
                            // "CarVe_ValorEmitido" => round($existeTP->TitPr_ValorEmitido, 2),
                            "CarVe_ValorEmitido" => round(
                                $ipuNew + $existeTP->Carve_Valor1 +$existeTP->CarVe_TasaAdministrativa + $bom_new, 2
                            ),
                            "CarVe_NumTitulo" => $num_titulo,
                            "CarVe_Interes" => round($valor_intereses, 2),        
                            "Carve_Estado" => "E",
                            "CarVe_Observaciones" => "Generado por sgm2",
                            "Usu_usuario" => $name_user,
                            
                        ]);

                        array_push($liquidacion, $num_titulo);

                    }
                }

            }

            $crearPdf=$this->titulo->pdfTituloPrevio($liquidacion,'nocopia','S');
            if($crearPdf['error']==true){
                DB::Rollback();
                return ["mensaje"=>$crearPdf["mensaje"], "error"=>true];
            }
            return ["mensaje"=>"Documentos generados exitosamente", "error"=>false, "pdf"=>$crearPdf['pdf']];
        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

}