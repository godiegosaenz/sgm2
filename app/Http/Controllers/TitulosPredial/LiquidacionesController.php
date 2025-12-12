<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\TituloRural;
use DB;
class LiquidacionesController extends Controller
{
    public function index()
    {
        return view('liquidacion.index');
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

    public function consultarTitulos($cedula){
        try {
            $prediosRurales=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
             ->where(function ($query) use($cedula){
                $query->where('carVe_CI',$cedula)
                ->orWhere('carVe_RUC',$cedula);
            })
            ->whereIn('cv.CarVe_Estado',['E'])
            ->pluck('Pre_CodigoCatastral')
            ->toArray();
            // dd($prediosRurales);


            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave','cv.CarVe_FechaEmision as fecha_emi','cv.CarVe_NumTitulo as num_titulo','cv.CarVe_CI as num_ident','cv.CarVe_Estado','cv.CarVe_Nombres as nombre_per','cv.CarVe_ValorEmitido as valor_emitido','cv.CarVe_TasaAdministrativa as tasa','CarVe_Calle as direcc_cont','cv.Carve_Recargo as recargo','cv.Carve_Descuento as descuento')
            //->where('cv.Pre_CodigoCatastral', '=', $clave)
            // ->where(function ($query) use($cedula){
            //     $query->where('carVe_CI',$cedula)
            //     ->orWhere('carVe_RUC',$cedula);
            // })
            ->whereIN('cv.Pre_CodigoCatastral',$prediosRurales)

            ->whereIn('cv.CarVe_Estado',['E']) //E=Emitidos, N=Nueva Emision
            // ->where('Pre_Tipo','Rural')
            // ->orderby('cv.CarVe_NumTitulo','desc')
            ->orderby('cv.Pre_CodigoCatastral','asc')
            
            ->distinct()
            ->get();
            // dd($liquidacionRural);

            $mes_Actual=date('m');
            $aplica_remision=0;
            if($mes_Actual<7){
                $aplica_remision=1;
            }
            $total_valor=0;
            $exoneracion_3era_edad=[];
            $exoneracion_discapacidad=[];
            foreach($liquidacionRural as $key=> $data){
                $valor=0;
                $subtotal=0;
                $subtotal=number_format($data->valor_emitido,2);

                if($aplica_remision==0){
                    $anio=explode("-",$data->num_titulo);
                    $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                    ->where('IntMo_Año',$anio)
                    ->select('IntMo_Valor')
                    ->first();

                    $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->valor_emitido - $data->tasa));                
                    $valor=number_format($valor,2);
                    $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                }else{
                    $cero=0;
                    $valor=number_format($cero,2);
                    $liquidacionRural[$key]->porcentaje_intereses=number_format($cero,2);
                }
                $liquidacionRural[$key]->subtotal_emi=$subtotal;
                
                $liquidacionRural[$key]->intereses=$valor;

                $total_pago=($valor + $data->valor_emitido + $data->recargo) - $data->descuento;

                $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);

                $total_valor=$total_valor+$total_pago;
                //$total_valor=$total_valor;
              
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

            $liquidacionActual=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral as clave','tp.TitPr_FechaEmision as fecha_emi','tp.TitPr_NumTitulo as num_titulo','tp.Titpr_RUC_CI as num_ident' ,'tp.TitPr_Estado','tp.TitPr_Nombres as nombre_per','tp.TitPr_ValorEmitido as valor_emitido','tp.TitPr_TasaAdministrativa as tasa','TitPr_DireccionCont as direcc_cont','tp.TitPr_Descuento as descuento'
            ,'tp.TitPr_Recargo as recargo')
            //->where('tp.Pre_CodigoCatastral', '=', $clave)
            ->where('tp.Titpr_RUC_CI',$cedula)
            
            ->whereIn('tp.TitPr_Estado',['E','N'])
            // ->where('Pre_Tipo','Rural')
            // ->orderby('tp.TitPr_NumTitulo','desc')
            ->orderby('tp.Pre_CodigoCatastral','asc')
            
            ->get();
            // dd("a");

            foreach($liquidacionActual as $key=> $data){
                $subtotal=0;
                $subtotal=number_format($data->valor_emitido,2);
                $valor=0;
                $anio=explode("-",$data->num_titulo);
                $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                ->where('IntMo_Año',$anio)
                ->select('IntMo_Valor')
                ->first();
                
                if(!is_null($consultaInteresMora)){
                    $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->valor_emitido - $data->tasa));
                    
                    $valor=number_format($valor,2);

                    $liquidacionActual[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                    $liquidacionActual[$key]->intereses=$valor;

                    $total_pago=($valor +$data->valor_emitido + $data->recargo) - $data->descuento;
                    $liquidacionActual[$key]->total_pagar=number_format($total_pago,2);
                }else{
                    $cero=0;
                    $liquidacionActual[$key]->porcentaje_intereses=number_format($cero,2);
                    $liquidacionActual[$key]->intereses=number_format($cero,2);

                    $total_pago=($valor +$data->valor_emitido+ $data->recargo) - $data->descuento;
                    $liquidacionActual[$key]->total_pagar=number_format($total_pago,2);
                }
                $liquidacionActual[$key]->subtotal_emi=$subtotal;

                $total_valor=$total_valor+$total_pago;
                //$total_valor=number_format($total_valor,2);

                $buscar_exon=DB::connection('sqlsrv')->table('REBAJA_VALOR')
                ->where('TitPrCarVe_NumTitulo',$data->num_titulo)
                ->select('Reb_Codigo')
                ->first();

                if(!is_null($buscar_exon)){
                    if($buscar_exon->Reb_Codigo=='01'){
                        array_push($exoneracion_3era_edad, $data->num_titulo);
                        $liquidacionActual[$key]->exoneracion='Mayor';
                    }else if ($buscar_exon->Reb_Codigo=='02'){
                        array_push($exoneracion_discapacidad, $data->num_titulo);
                        $liquidacionActual[$key]->exoneracion='Discapacidad';
                    }else{
                        $liquidacionActual[$key]->exoneracion='No';
                    }
                }else{
                    $liquidacionActual[$key]->exoneracion='No';
                }
            }
            // $resultado = $liquidacionRural->merge($liquidacionActual);
            // $resultado = $liquidacionRural->merge($liquidacionActual)->sortByDesc('num_titulo')->values();
            $resultado = $liquidacionRural
            ->merge($liquidacionActual)
            ->sortBy([
                // ['num_titulo', 'desc'],
                ['clave', 'desc'],
                ['num_titulo', 'desc'],
            ])
            ->values();

            // dd("s"); 

            return ["resultado"=>$resultado, 
                    "total_valor"=>number_format($total_valor,2),
                    "exoneracion_3era_edad"=>$exoneracion_3era_edad,
                    "exoneracion_discapacidad"=>$exoneracion_discapacidad,
                    "error"=>false,
                    "aplica_remision"=>$aplica_remision
            ];
        } catch (\Exception $e) {
             //\Log::error($e);
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function pdfLiquidacion($cedula, $lugar){
        try{
            if($lugar==1){
                $consulta=$this->consultarTitulosUrb($cedula);
            }else{
                $consulta=$this->consultarTitulos($cedula);
            }
           
            if($consulta["error"]==true){
                return ["mensaje"=>$consulta["mensaje"], "error"=>true];
            }
            // dd($consulta["resultado"]);
            $nombrePDF="Liquidacion.pdf";                               
            $pdf = \PDF::loadView('reportes.reporteLiquidacionRemisionRural', ['DatosLiquidacion'=>$consulta["resultado"],"ubicacion"=>$lugar]);

            // return $pdf->download('reporteLiquidacion.pdf');
            $pdf->setPaper("A4", "landscape");
            $estadoarch = $pdf->stream();

            //lo guardamos en el disco temporal
            \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk('public')->exists($nombrePDF);
            if($exists_destino){
                return response()->json([
                    'error'=>false,
                    'pdf'=>$nombrePDF
                ]);
            }else{
                return response()->json([
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ]);
            }

        } catch (\Exception $e) {
            //\Log::error($e);
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function buscarDeudaPrediourb(Request $request){
        try{
            $tipo=$request->tipo;
            $dataContribuyente=[];
           
            if($tipo==1){
                $matricula=$request->valor;
                $dataContribuyente=DB::connection('pgsql')->table('sgm_app.cat_predio as p')
                ->leftJoin('sgm_app.cat_predio_propietario as pp','pp.predio','p.id')
                ->leftJoin('sgm_app.cat_ente as e','e.id','pp.ente')
                ->select('e.id','ci_ruc as Titpr_RUC_CI',DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS nombres"),'direccion')
                ->where('p.num_predio',$matricula)
                ->where('pp.estado','A')
                ->get();

            }else if($tipo==2){
                $clave=$request->valor;
                $dataContribuyente=DB::connection('pgsql')->table('sgm_app.cat_predio as p')
                ->leftJoin('sgm_app.cat_predio_propietario as pp','pp.predio','p.id')
                ->leftJoin('sgm_app.cat_ente as e','e.id','pp.ente')
                ->select('e.id','ci_ruc as Titpr_RUC_CI',DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS nombres"),'direccion')
                ->where('p.clave_cat',$clave)
                ->where('pp.estado','A')
                ->get();
            }else {
                $valor=$request->valor;
               
                $dataContribuyente=DB::connection('pgsql')->table('sgm_app.cat_ente as e')
                ->select('id','ci_ruc as Titpr_RUC_CI','direccion',DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS nombres"))
                // ->where('e.id',$id_contr)
                ->WhereRaw("LOWER(nombres) LIKE LOWER(?)", ["%$valor%"])
                ->orWhereRaw("LOWER(apellidos) LIKE LOWER(?)", ["%$valor%"])
                ->limit(10)
                ->get();
            }
            if(sizeof($dataContribuyente)>0){
                foreach($dataContribuyente as $key=> $item){
                    $telefonos = DB::connection('pgsql')->table('sgm_app.ente_telefono')
                    ->select('telefono')
                    ->where('ente', $item->id)
                    ->pluck('telefono');
                    
                    $dataContribuyente[$key]->telf=$telefonos;

                    $correos = DB::connection('pgsql')->table('sgm_app.ente_correo')
                    ->select('email')
                    ->where('ente', $item->id)
                    ->pluck('email');
                    
                    $dataContribuyente[$key]->email=$correos;
                }
            }
            
            return (['data'=>$dataContribuyente,'false'=>true]); 

        } catch (\Throwable $th) {
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th->getMessage(),'error'=>true]); 
        } 
    }

     public function consultarTitulosUrb($cedula){
        try {
            $predios_contribuyente= DB::connection('pgsql')->table('sgm_app.cat_ente as e')
            ->join('sgm_app.cat_predio_propietario as pp', 'pp.ente', '=', 'e.id')
            ->where('pp.estado','A')
            ->where('e.ci_ruc',$cedula)
            ->pluck('pp.predio')
            ->toArray();
            
            $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
            ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
            ->leftJoin('sgm_app.cat_ente', 'sgm_financiero.ren_liquidacion.comprador', '=', 'sgm_app.cat_ente.id')
            ->select('sgm_financiero.ren_liquidacion.id',
            'sgm_financiero.ren_liquidacion.id_liquidacion as num_titulo',
            'sgm_financiero.ren_liquidacion.total_pago',
            'sgm_financiero.ren_liquidacion.estado_liquidacion',
            'sgm_financiero.ren_liquidacion.predio',
            'sgm_financiero.ren_liquidacion.anio',
            'sgm_financiero.ren_liquidacion.nombre_comprador AS nombre_per',
            'sgm_app.cat_predio.clave_cat as clave',
            'sgm_app.cat_ente.nombres',
            'sgm_app.cat_ente.apellidos',
            DB::raw("CONCAT(sgm_app.cat_ente.apellidos, ' ', sgm_app.cat_ente.nombres) AS nombre_contr1"),
            'sgm_app.cat_ente.ci_ruc',
            'sgm_app.cat_predio.num_predio',
            DB::raw("CONCAT(sgm_app.cat_predio.calle, ' y ', sgm_app.cat_predio.calle_s) AS direcc_cont"),
            'sgm_financiero.ren_liquidacion.saldo as subtotal_emi',
        
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
                    ) AS total_pagar'), DB::raw("
                        (
                            SELECT
                                CASE
                                    WHEN (ren_liquidacion.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
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
                            WHERE d.liquidacion = ren_liquidacion.id AND d.rubro = 2
                            LIMIT 1
                        ) AS descuento
                    "),
                    
                    DB::raw("
                        (
                            SELECT
                                CASE
                                     WHEN (ren_liquidacion.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                        ROUND((ren_liquidacion.saldo * (
                                            SELECT ROUND((porcentaje / 100), 2) 
                                            FROM sgm_financiero.ren_intereses i
                                            WHERE i.anio = ren_liquidacion.anio
                                            LIMIT 1
                                        )), 2)
                                    ELSE
                                        0.00
                                    END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = ren_liquidacion.id AND d.rubro = 2
                            LIMIT 1
                        ) AS intereses
                    "),

                    DB::raw("
                        (
                            SELECT
                                CASE
                                    WHEN ren_liquidacion.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                        ROUND((d.valor * 0.10), 2)
                                    WHEN ren_liquidacion.anio < EXTRACT(YEAR FROM NOW()) THEN
                                        ROUND((d.valor * 0.10), 2)
                                    ELSE
                                        0.00
                                END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = ren_liquidacion.id AND d.rubro = 2
                            LIMIT 1
                        ) AS recargo
                    "))
            
            ->whereIn('sgm_financiero.ren_liquidacion.predio',$predios_contribuyente)
            ->where('sgm_app.cat_predio.estado','A')
            ->whereNotIN('estado_liquidacion',[1,3,4,5])
            ->orderby('clave_cat','desc')
            ->orderBy('anio', 'asc')
            ->get();
            $total_valor=0;
            foreach($liquidacionUrbana as $key=>$data){
                $total_valor=$total_valor+$data->total_pagar;
            }
            return ["resultado"=>$liquidacionUrbana, 
                    "total_valor"=>number_format($total_valor,2),
                    "exoneracion_3era_edad"=>0,
                    "exoneracion_discapacidad"=>0,
                    "error"=>false,
                    "aplica_remision"=>0
            ];
        } catch (\Exception $e) {
             //\Log::error($e);
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }
}
