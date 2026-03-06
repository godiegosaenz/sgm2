<?php

namespace App\Http\Controllers\EmisionTitulo;

use App\Http\Controllers\Controller;
use App\Models\CarteraVencidaRural;
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
                    $bom=floatval(str_replace(',', '', $obtenerCV->valorCarVe_ValComerPredio_comer_predio));
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

}