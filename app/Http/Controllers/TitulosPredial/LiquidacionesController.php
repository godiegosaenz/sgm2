<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use App\Models\RuralEnteCorreo;
use App\Models\RuralEnteTelefono;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\TituloRural;
use App\Models\NotificacionCoactiva;
use DB;
use Storage;
use Mail;
use Illuminate\Support\Str;
use App\Models\PsqlEnte;
use App\Models\PsqlEnteTelefono;
use App\Models\PsqlEnteCorreo;
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
            ->select('tp.Titpr_Nombres as nombres','tp.Titpr_RUC_CI','tp.TitPr_DireccionCont as direccion_p','tp.TitPr_Estado as ruc')
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
                foreach($liquidacionRuralAct as $key=>$data){
                    if(strlen($data->Titpr_RUC_CI)==10){
                        $direccion=DB::connection('sqlsrv')->table('PROPIETARIO')
                        ->select('Pro_CiudadDomicilio','Pro_DireccionDomicilio')
                        ->where('Ciu_Cedula',$data->Titpr_RUC_CI)
                        ->first();
                        if(!is_null($direccion)){
                            $liquidacionRuralAct[$key]->direccion=$direccion->Pro_CiudadDomicilio."-".$direccion->Pro_DireccionDomicilio;
                        }
                    }else{
                        $direccion=DB::connection('sqlsrv')->table('PROPIETARIO')
                        ->select('Pro_CiudadDomicilio','Pro_DireccionDomicilio')
                        ->where('Ins_Ruc',$data->Titpr_RUC_CI)
                        ->first();
                        if(!is_null($direccion)){
                            $liquidacionRuralAct[$key]->direccion=$direccion->Pro_CiudadDomicilio."-".$direccion->Pro_DireccionDomicilio;
                        }
                    }

                    
                    $telefonos = DB::connection('sqlsrv')->table('TELEFONO_CONTRIBUYENTE')
                    ->select('telefono')
                    ->where('cedula_ruc', $data->Titpr_RUC_CI)
                    ->pluck('telefono');
                    
                    $liquidacionRuralAct[$key]->telf=$telefonos;

                    $correos = DB::connection('sqlsrv')->table('CORREO_CONTRIBUYENTE')
                    ->select('correo as email')
                    ->where('cedula_ruc', $data->Titpr_RUC_CI)
                    ->pluck('email');
                 
                    $liquidacionRuralAct[$key]->email=$correos;
                
                }
                return (['data'=>$liquidacionRuralAct,'error'=>false]); 
            }
           
            $liquidacionRuralAct=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->select('cv.CarVe_Nombres as nombres','cv.CarVe_CI as Titpr_RUC_CI'
           ,'cv.CarVe_Calle as TitPr_DireccionCont','cv.CarVe_RUC as ruc')
            ->where(function($query)use($tipo,$valor, $tipo_per) {
                if($tipo==1){
                    $query->where('CarVe_CI', '=', $valor)
                    ->orWhere('CarVe_RUC',$valor);
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

    public function consultarTitulos($cedula, $notifica=1){
        try {
            $prediosRurales=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
             ->where(function ($query) use($cedula){
                $query->where('CarVe_CI',$cedula)
                ->orWhere('CarVe_RUC',$cedula);
            })
            ->whereIn('cv.CarVe_Estado',['E'])
            ->pluck('Pre_CodigoCatastral')
            ->toArray();
            
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave','cv.CarVe_FechaEmision as fecha_emi','cv.CarVe_NumTitulo as num_titulo','cv.CarVe_CI as num_ident','cv.CarVe_Estado','cv.CarVe_Nombres as nombre_per','cv.CarVe_ValorEmitido as valor_emitido','cv.CarVe_TasaAdministrativa as tasa','CarVe_Calle as direcc_cont','cv.Carve_Recargo as recargo','cv.Carve_Descuento as descuento')
            ->whereIN('cv.Pre_CodigoCatastral',$prediosRurales)
            ->whereIn('cv.CarVe_Estado',['E']) //E=Emitidos, N=Nueva Emision
            ->orderby('cv.Pre_CodigoCatastral','asc')            
            ->distinct()
            ->get();
          
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

            $liquidacionActual=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral as clave','tp.TitPr_FechaEmision as fecha_emi','tp.TitPr_NumTitulo as num_titulo','tp.Titpr_RUC_CI as num_ident' ,'tp.TitPr_Estado','tp.TitPr_Nombres as nombre_per','tp.TitPr_ValorEmitido as valor_emitido','tp.TitPr_TasaAdministrativa as tasa','TitPr_DireccionCont as direcc_cont','tp.TitPr_Descuento as descuento'
            ,'tp.TitPr_Recargo as recargo')
            ->where('tp.Titpr_RUC_CI',$cedula)            
            ->whereIn('tp.TitPr_Estado',['E','N'])
            ->orderby('tp.Pre_CodigoCatastral','asc')            
            ->get();
           
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
                $liquidacionActual[$key]->anio=$anio[0];

                $total_valor=$total_valor+$total_pago;
               
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
           
            $resultado = $liquidacionRural
            ->merge($liquidacionActual)
            ->sortBy([
                ['clave', 'desc'],
                ['num_titulo', 'asc'],
            ])
            ->values();
           
           if(!is_null($notifica)){
                $buscarNotificacion=$this->notificacionContribuyente($cedula,'R', $resultado[0]->nombre_per);
                if($buscarNotificacion["error"]==true){
                    return ["mensaje"=>$buscarNotificacion["mensaje"], "error"=>true];
                }

                return ["resultado"=>$resultado, 
                    "total_valor"=>number_format($total_valor,2),
                    "exoneracion_3era_edad"=>$exoneracion_3era_edad,
                    "exoneracion_discapacidad"=>$exoneracion_discapacidad,
                    "error"=>false,
                    "aplica_remision"=>$aplica_remision,
                    "notificaciones"=>$buscarNotificacion["resultado"]
                ];
            }

            return ["resultado"=>$resultado, 
                    "total_valor"=>number_format($total_valor,2),
                    "exoneracion_3era_edad"=>$exoneracion_3era_edad,
                    "exoneracion_discapacidad"=>$exoneracion_discapacidad,
                    "error"=>false,
                    "aplica_remision"=>$aplica_remision
            ];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getLine(), "error"=>true];
        }
    }

    public function pdfLiquidacion($cedula, $lugar){
        try{
            $tipo_agrupado="";
            if($lugar==1){
                $consulta=$this->consultarTitulosUrb($cedula, null);
                if($consulta["error"]==true){
                    return ["mensaje"=>$consulta["mensaje"], "error"=>true];
                }               

                #agrupamos
                $listado_final=[];
                foreach ($consulta["resultado"] as $key => $item){                
                    if(!isset($listado_final[$item->num_predio])) {
                        $listado_final[$item->num_predio]=array($item);
                
                    }else{
                        array_push($listado_final[$item->num_predio], $item);
                    }
                } 
            }else{
                $consulta=$this->consultarTitulos($cedula, null);
                if($consulta["error"]==true){
                    return ["mensaje"=>$consulta["mensaje"], "error"=>true];
                }

                #agrupamos
                $listado_final=[];
                foreach ($consulta["resultado"] as $key => $item){                
                    if(!isset($listado_final[$item->clave])) {
                        $listado_final[$item->clave]=array($item);
                
                    }else{
                        array_push($listado_final[$item->clave], $item);
                    }
                } 
            }
           
            $nombrePDF="Liquidacion".date('YmdHis').".pdf";                               
            $pdf = \PDF::loadView('reportes.reporteLiquidacionRemisionRural', ['DatosLiquidacion'=>$listado_final,"ubicacion"=>$lugar]);

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
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function pagoVoluntario($cedula, $lugar){
        try{
            $tipo_agrupado="";
            $nombre_persona="";
            $direcc_cont="";
            if($lugar==1){
                $consulta=$this->consultarTitulosUrb($cedula, null);
                if($consulta["error"]==true){
                    return ["mensaje"=>$consulta["mensaje"], "error"=>true];
                }               

                #agrupamos
                $listado_final=[];
                foreach ($consulta["resultado"] as $key => $item){                
                    if(!isset($listado_final[$item->num_predio])) {
                        $listado_final[$item->num_predio]=array($item);
                
                    }else{
                        array_push($listado_final[$item->num_predio], $item);
                    }

                    $nombre_persona=$item->nombre_per;
                    $direcc_cont=$item->direcc_cont;
                    if(is_null($item->nombre_per)){
                        $nombre_persona=$item->nombre_contr1;
                    }
                } 
            }else{
                $consulta=$this->consultarTitulos($cedula, null);
                if($consulta["error"]==true){
                    return ["mensaje"=>$consulta["mensaje"], "error"=>true];
                }

                #agrupamos
                $listado_final=[];
                               
                foreach ($consulta["resultado"] as $key => $item){                
                    if(!isset($listado_final[$item->clave])) {
                        $listado_final[$item->clave]=array($item);
                
                    }else{
                        array_push($listado_final[$item->clave], $item);
                    }

                    $nombre_persona=$item->nombre_per;
                    $direcc_cont=$item->direcc_cont;
                    if(is_null($item->nombre_per)){
                        $nombre_persona=$item->nombre_contr1;
                    }
                } 
            }
           
            $nombrePDF="PagoVoluntario".date('YmdHis').".pdf";                               
            $pdf = \PDF::loadView('reportes.pagoVoluntarioPredio', ['DatosLiquidacion'=>$listado_final,"ubicacion"=>$lugar,"nombre_persona"=>$nombre_persona, "direcc_cont"=>$direcc_cont]);

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
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage(), "error"=>true];

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
                ->select('e.id','ci_ruc as Titpr_RUC_CI',DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS nombres"),DB::raw("CONCAT(e.ciudad, '-', e.direccion) AS direccion"))
                ->where('p.num_predio',$matricula)
                ->where('pp.estado','A')
                ->get();

            }else if($tipo==2){
                $clave=$request->valor;
                $dataContribuyente=DB::connection('pgsql')->table('sgm_app.cat_predio as p')
                ->leftJoin('sgm_app.cat_predio_propietario as pp','pp.predio','p.id')
                ->leftJoin('sgm_app.cat_ente as e','e.id','pp.ente')
                ->select('e.id','ci_ruc as Titpr_RUC_CI',DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS nombres"),DB::raw("CONCAT(e.ciudad, '-', e.direccion) AS direccion"))
                ->where('p.clave_cat',$clave)
                ->where('pp.estado','A')
                ->get();
            }else {
                $valor=$request->valor;
               
                $dataContribuyente=DB::connection('pgsql')->table('sgm_app.cat_ente as e')
                ->select('id','ci_ruc as Titpr_RUC_CI',DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS nombres"),DB::raw("CONCAT(e.ciudad, '-', e.direccion) AS direccion"))
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
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th->getMessage(),'error'=>true]); 
        } 
    }

    public function consultarTitulosUrb($cedula, $notifica=1){
        try {
            $predios_contribuyente= DB::connection('pgsql')->table('sgm_app.cat_ente as e')
            ->join('sgm_app.cat_predio_propietario as pp', 'pp.ente', '=', 'e.id')
            ->where('pp.estado','A')
            ->where('e.ci_ruc',$cedula)
            ->pluck('pp.predio')
            ->toArray();
            
            $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
            ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
            ->join('sgm_app.cat_predio_propietario', 'sgm_app.cat_predio_propietario.predio', '=', 'sgm_app.cat_predio.id')
           
            ->leftJoin('sgm_app.cat_ente', 'sgm_app.cat_predio_propietario.ente', '=', 'sgm_app.cat_ente.id')
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
            ->where('sgm_app.cat_predio_propietario.estado','A')
            ->whereNotIN('estado_liquidacion',[1,3,4,5])
            ->orderby('clave_cat','desc')
            ->orderBy('anio', 'asc')
            ->distinct('num_titulo','clave_cat','anio')
            ->get();
            $total_valor=0;
            foreach($liquidacionUrbana as $key=>$data){
                $total_valor=$total_valor+$data->total_pagar;
            }

            if(!is_null($notifica)){
                $buscarNotificacion=$this->notificacionContribuyente($cedula,'U', null);
                if($buscarNotificacion["error"]==true){
                    return ["mensaje"=>$buscarNotificacion["mensaje"], "error"=>true];
                }

                return ["resultado"=>$liquidacionUrbana, 
                    "total_valor"=>number_format($total_valor,2),
                    "exoneracion_3era_edad"=>0,
                    "exoneracion_discapacidad"=>0,
                    "error"=>false,
                    "notificaciones"=>$buscarNotificacion["resultado"]
                ];
            }
      
            return ["resultado"=>$liquidacionUrbana, 
                    "total_valor"=>number_format($total_valor,2),
                    "exoneracion_3era_edad"=>0,
                    "exoneracion_discapacidad"=>0,
                    "error"=>false,
                    "aplica_remision"=>0
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    private function notificacionContribuyente($cedula, $lugar, $persona){
        try{
            if($lugar=='U'){
                $notificacion = DB::connection('pgsql')->table('sgm_coactiva.notificacion as n')
                ->Join('sgm_app.cat_ente as e', 'e.ci_ruc', '=', 'n.cedula_ruc')
                ->select(DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS nombres"),'e.ci_ruc','n.created_at',
                'n.correo','n.archivo','n.idusuario','n.lugar','n.id')
                ->where('n.estado', 'A')
                ->where('n.cedula_ruc', $cedula)
                ->orderBy('n.created_at','desc')
                ->get();
                foreach($notificacion as $key=> $data){
                    $usuarioRegistra=DB::connection('mysql')->table('personas as p')
                    ->where('p.id',$data->idusuario)
                    ->select('p.nombres','p.apellidos','p.cedula')
                    ->first();
                    if(is_null($usuarioRegistra)){
                        $notificacion[$key]->nombre_usuario=$data->usuario;
                    }else{
                        $notificacion[$key]->nombre_usuario=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;
                        $notificacion[$key]->cedula_usuario=$usuarioRegistra->cedula;
                    }
                }
            }else{
               
                $notificacion = DB::connection('pgsql')->table('sgm_coactiva.notificacion as n')
                ->select('n.created_at','n.correo','n.archivo','n.idusuario','n.id','n.lugar')
                ->where('n.estado', 'A')
                ->where('n.cedula_ruc', $cedula)
                ->orderBy('n.created_at','desc')
                ->get();
                foreach($notificacion as $key=> $data){
                    $usuarioRegistra=DB::connection('mysql')->table('personas as p')
                    ->where('p.id',$data->idusuario)
                    ->select('p.nombres','p.apellidos','p.cedula')
                    ->first();
                    if(is_null($usuarioRegistra)){
                        $notificacion[$key]->nombre_usuario=$data->usuario;
                    }else{
                        $notificacion[$key]->nombre_usuario=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;
                        $notificacion[$key]->cedula_usuario=$usuarioRegistra->cedula;
                    }
                    $notificacion[$key]->nombres=$persona;
                    $notificacion[$key]->ci_ruc=$cedula;
                }
                return ["resultado"=>$notificacion,"error"=>false];
             }
           
            return ["resultado"=>$notificacion,
                    "error"=>false
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function notificaContribuyente(Request $request){
        try{
          
            $archivosInfo = [];
            $archivosAdjuntos=[];
            if ($request->hasFile('archivo_notifica')) {
                foreach ($request->file('archivo_notifica') as $archivo) {
                  
                    $nombreOriginal = $archivo->getClientOriginalName();
                    $nombreSinExtension = pathinfo($nombreOriginal, PATHINFO_FILENAME);
                    
                    $extension = pathinfo($archivo->getClientOriginalName(), PATHINFO_EXTENSION);
                    $nombreLimpio="Doc".time().".".$extension;
                   
                    Storage::disk('disksDocumentoCoactiva')->putFileAs('', $archivo, $nombreLimpio);

                    $archivosInfo[] = [
                        'nombre' => $nombreLimpio
                    ];

                    $archivosAdjuntos[] = [
                        'ruta'   => Storage::disk('disksDocumentoCoactiva')->path($nombreLimpio),
                        'nombre' => $nombreLimpio
                    ];
                }
            }

            $correos = array_filter(
                array_map('trim', explode(';', $request->correos_notifica))
            );
            
            $envioExitoso = false; // 
            $resultadoCorreos = [];
            foreach ($correos as $correo) {

                try {

                    Mail::send('email_documentos.notificacion_liquidacion', [], function ($message) use ($correo, $archivosAdjuntos) {

                        $message->to($correo)
                                ->subject('Notificación de deuda pendiente de pago');

                        foreach ($archivosAdjuntos as $adjunto) {
                            $message->attach($adjunto['ruta'], [
                                'as' => $adjunto['nombre']
                            ]);
                        }
                    });

                    $envioExitoso = true;
                   
                    array_push($resultadoCorreos, $correo);
                   
                } catch (\Exception $e) {
                   
                }
            }
    
           
            $guardaNotificacion=new NotificacionCoactiva();
            $guardaNotificacion->cedula_ruc=$request->ci_ruc_notifica;
            $guardaNotificacion->correo = json_encode($resultadoCorreos);
            $guardaNotificacion->archivo=json_encode($archivosInfo);
            $guardaNotificacion->estado='A';
            $guardaNotificacion->envia_correo=$envioExitoso ? 'S' : 'N';
            $guardaNotificacion->idusuario=auth()->user()->idpersona;
            $guardaNotificacion->lugar=$request->lugar_not;
            $guardaNotificacion->save();

            $notifica=$this->notificacionContribuyente($guardaNotificacion->cedula_ruc, $guardaNotificacion->lugar,$request->nombres_notifica);
            if($notifica["error"]!=true){
                return ["resultado"=>$notifica["resultado"], "error"=>false];
            }     
            return ["mensaje"=>$notifica["mensaje"], "error"=>true];  

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function descargarArchivo($id, $archivoIndex){
        $notifica = NotificacionCoactiva::find($id);
       
       
        // Verificar que la actividad exista y tenga un archivo
        if (!$notifica || !$notifica->archivo) {
            return response()->json(['error' => 'Archivo no encontrado'], 404);
        }

        // Decodificar la lista de archivos de la actividad
        $archivos = json_decode($notifica->archivo, true);

        $archivo = $archivos[$archivoIndex];

        // Construir ruta física completa en public/archivos
        $ruta = storage_path('app/documentosCoactiva/' . $archivo['nombre']);
        
        // Verificar existencia del archivo
        if (!file_exists($ruta)) {
            return response()->json(['error' => 'Archivo no encontrado en el servidor'], 404);
        }

        // Generar nombre limpio para descarga
        $nombreDescarga = Str::slug($archivo['nombre']);
        // dd($nombreDescarga);
        $nombreDescarga="";

        // Descargar el archivo directamente desde public
        return response()->download($ruta, $nombreDescarga);
    }

    public function vistaActualizacion()
    {
        return view('liquidacion.actualizacion');
    }

     public function consultarPrediosUrb($cedula, $notifica=1){
        try {
            $predios_contribuyente= DB::connection('pgsql')->table('sgm_app.cat_ente as e')
            ->join('sgm_app.cat_predio_propietario as pp', 'pp.ente', '=', 'e.id')
            ->where('pp.estado','A')
            ->where('e.ci_ruc',$cedula)
            ->select('e.id','pp.predio')
            ->get();

            $id_predio=[];
            $id_cont="";
            foreach($predios_contribuyente as $data){
                array_push($id_predio, $data->predio);
                $id_cont=$data->id;
            }
            
            $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
            ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
            ->join('sgm_app.cat_predio_propietario', 'sgm_app.cat_predio_propietario.predio', '=', 'sgm_app.cat_predio.id')
            ->leftJoin('sgm_app.cat_ente', 'sgm_app.cat_predio_propietario.ente', '=', 'sgm_app.cat_ente.id')
            ->select('sgm_financiero.ren_liquidacion.id',
            'sgm_app.cat_predio.clave_cat as clave',
            'sgm_app.cat_ente.apellidos',
            'sgm_app.cat_ente.nombres',
            DB::raw("CONCAT(sgm_app.cat_ente.apellidos, ' ', sgm_app.cat_ente.nombres) AS nombre_contr1"),
            'sgm_app.cat_ente.ci_ruc',
            'sgm_app.cat_predio.num_predio',
            DB::raw("CONCAT(sgm_app.cat_predio.calle, ' y ', sgm_app.cat_predio.calle_s) AS direcc_cont"))            
            ->whereIn('sgm_financiero.ren_liquidacion.predio',$id_predio)
            ->where('sgm_app.cat_predio.estado','A')
            ->where('sgm_app.cat_predio_propietario.estado','A')
            ->whereNotIN('estado_liquidacion',[1,3,4,5])
            ->orderby('clave_cat','desc')            
            ->distinct('clave_cat')
            ->get();
            
            return ["resultado"=>$liquidacionUrbana, "id_cont"=>$id_cont,                    
                    "error"=>false,
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getLine(), "error"=>true];
        }
    }

     public function consultarPrediosRurales($cedula){
        try {
            $prediosRurales=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->where(function ($query) use($cedula){
                $query->where('CarVe_CI',$cedula)
                ->orWhere('CarVe_RUC',$cedula);
            })
            ->whereIn('cv.CarVe_Estado',['E'])
            ->pluck('Pre_CodigoCatastral')
            ->toArray();
            
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select(
                'cv.Pre_CodigoCatastral as clave',
                DB::raw("
                    CASE 
                        WHEN cv.CarVe_CI > 0 THEN cv.CarVe_CI 
                        ELSE cv.CarVe_RUC 
                    END AS num_ident
                "),
                'cv.CarVe_Nombres as nombre_per',
                'cv.CarVe_Calle as direcc_cont'
            )
            ->whereIN('cv.Pre_CodigoCatastral',$prediosRurales)
            ->whereIn('cv.CarVe_Estado',['E']) //E=Emitidos, N=Nueva Emision
            ->orderby('cv.Pre_CodigoCatastral','asc')            
            ->distinct('cv.Pre_CodigoCatastral')
            ->get();
           
                                   
            $liquidacionActual=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral as clave','tp.Titpr_RUC_CI as num_ident','tp.TitPr_Nombres as nombre_per','TitPr_DireccionCont as direcc_cont')
            ->where('tp.Titpr_RUC_CI',$cedula)            
            ->whereIn('tp.TitPr_Estado',['E','N'])
            ->orderby('tp.Pre_CodigoCatastral','asc')   
            ->distinct('tp.Pre_CodigoCatastral')         
            ->get();            
            
            $resultado = $liquidacionRural
            ->merge($liquidacionActual)
            ->unique('clave')
            ->sortBy([
                ['clave', 'desc'],
                ['num_titulo', 'asc'],
            ])
            ->values();
                      
            return ["resultado"=>$resultado,"id_cont"=>$cedula, 
                    "error"=>false
            ];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function actualizaContribuyente(Request $request){
        $transaction=DB::transaction(function() use($request){
            try{
                if($request->lugar_not=='Urbano'){
                    $ci_ruc=$request->ci_ruc_contribuyente;
                    $verificaContrib=PsqlEnte::where(function($query)use($ci_ruc){
                        $query->where('ci_ruc', $ci_ruc)
                        ->orWhere('ci_ruc', substr($ci_ruc, 0, 10));
                    })
                    ->where('id','!=',$request->id_cont)
                    ->first();
                    if(!is_null($verificaContrib)){                   
                        return ["mensaje"=>"El numero de cedula o ruc ya existe en otra persona => ".$verificaContrib->nombres." ".$verificaContrib->apellidos, "error"=>true];  
                    }
                    $actualizaContrib=PsqlEnte::find($request->id_cont);
                    $actualizaContrib->ci_ruc=$request->ci_ruc_contribuyente;
                    $actualizaContrib->nombres=$request->nombre_contribuyente;
                    $actualizaContrib->apellidos=$request->apellido_contribuyente;
                    $actualizaContrib->direccion=$request->direccion_contribuyente;
                    $actualizaContrib->ciudad=$request->ciudad_contribuyente;
                    $actualizaContrib->save();
                
                    $verificaTlfo=PsqlEnteTelefono::where('ente',$actualizaContrib->id)->delete();
                
                    $verificaCorreo=PsqlEnteCorreo::where('ente',$actualizaContrib->id)->delete();
                    foreach($request->correo as $correo){
                    
                        $guardaCorreo=new PsqlEnteCorreo();
                        $guardaCorreo->email=$correo;
                        $guardaCorreo->ente=$actualizaContrib->id;
                        $guardaCorreo->save();

                    }

                    foreach($request->telefono as $telefono){
                        $guardaTlfo=new PsqlEnteTelefono();
                        $guardaTlfo->telefono=$telefono;
                        $guardaTlfo->ente=$actualizaContrib->id;
                        $guardaTlfo->save();
                    }
                        
                return ["mensaje"=>"Datos actualizados exitosamente", "error"=>false];  

                }else{
                    $cedula=$request->id_cont;
                                                
                    $prediosRurales=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
                    ->where(function ($query) use($cedula){
                        $query->where('CarVe_CI',$cedula)
                        ->orWhere('CarVe_RUC',$cedula);
                    })
                    ->whereIn('cv.CarVe_Estado',['E'])
                    ->distinct()
                    ->pluck('Pre_CodigoCatastral')
                    ->toArray();
                
                    $titulo=DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
                    ->where('Carve_Estado','E')
                    ->whereIn('Pre_CodigoCatastral',$prediosRurales)
                    ->where(function ($query) use($cedula){
                        $query->where('CarVe_CI',$cedula)
                        ->orWhere('CarVe_RUC',$cedula);
                    })
                    ->get();
                    
                    foreach($titulo as $data){
                        
                        if($data->CarVe_CI>0){
                        
                            $actualiza=DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
                            ->where('Carve_Estado','E')
                            ->where('Pre_CodigoCatastral',$data->Pre_CodigoCatastral)
                            ->where('CarVe_NumTitulo',$data->CarVe_NumTitulo)
                            ->where('CarVe_CI',$data->CarVe_CI)
                            ->update([
                                "CarVe_Nombres" => $request->apellido_contribuyente." - ".$request->nombre_contribuyente,
                                "CarVe_CI" => $request->ci_ruc_contribuyente                      
                            ]);

                           
                            
                        }else if($data->CarVe_RUC>0){
                        
                            /*$actualiza=DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
                            ->where('Carve_Estado','E')
                            ->where('Pre_CodigoCatastral',$data->Pre_CodigoCatastral)
                            ->where('CarVe_NumTitulo',$data->CarVe_NumTitulo)
                            ->where('CarVe_RUC',$data->CarVe_RUC)
                            ->update([
                                "CarVe_RUC" => $request->ci_ruc_contribuyente                      
                            ]);*/

                           
                        }
                                            
                    }

                   

                    if(strlen($cedula)==10){

                        $predio_titulo=DB::connection('sqlsrv')->table('TITULOS_PREDIO')
                        ->whereIN('TitPr_Estado',['E','N'])
                        ->whereIn('Pre_CodigoCatastral',$prediosRurales)
                        ->where('Titpr_RUC_CI',$cedula)
                        ->update([
                            "TitPr_Nombres" => $request->apellido_contribuyente." - ".$request->nombre_contribuyente,
                            "Titpr_RUC_CI" => $request->ci_ruc_contribuyente                      
                        ]);

                        $ciudadano=DB::connection('sqlsrv')->table('CIUDADANO')
                        ->where('Ciu_Cedula',$cedula)
                        ->update([
                            "Ciu_Cedula" => $request->ci_ruc_contribuyente,
                            "Ciu_Apellidos" => $request->apellido_contribuyente,
                            "Ciu_Nombres" => $request->nombre_contribuyente
                        ]);

                        $domicilio=DB::connection('sqlsrv')->table('PROPIETARIO')
                        ->where('Ciu_Cedula',$cedula)
                        ->update([
                            "Ciu_Cedula" => $request->ci_ruc_contribuyente,
                            "Pro_CiudadDomicilio" => $request->ciudad_contribuyente,
                            "Pro_DireccionDomicilio" => $request->direccion_contribuyente
                        ]);
                    }else{
                        /*$predio_titulo=DB::connection('sqlsrv')->table('TITULOS_PREDIO')
                        ->whereIN('TitPr_Estado',['E','N'])
                        ->whereIn('Pre_CodigoCatastral',$prediosRurales)
                        ->where('Titpr_RUC_CI',$cedula)
                        ->update([
                            "Titpr_RUC_CI" => $request->ci_ruc_contribuyente                      
                        ]);

                        $institucion=DB::connection('sqlsrv')->table('INSTITUCION')
                        ->where('Ins_Ruc',$cedula)
                        ->update([
                            "Ins_Ruc" => $request->ci_ruc_contribuyente,
                            "Ins_Nombre" => $request->nombre_contribuyente
                            
                        ]);*/

                        $domicilio=DB::connection('sqlsrv')->table('PROPIETARIO')
                        ->where('Ins_Ruc',$cedula)
                        ->update([
                           /* "Ins_Ruc" => $request->ci_ruc_contribuyente,*/
                            "Pro_CiudadDomicilio" => $request->ciudad_contribuyente,
                            "Pro_DireccionDomicilio" => $request->direccion_contribuyente
                        ]);
                    }

                    $verificaTlfo=RuralEnteTelefono::where('cedula_ruc',$cedula)->delete();              
                    $verificaCorreo=RuralEnteCorreo::where('cedula_ruc',$cedula)->delete();

                    foreach($request->correo as $correo){
                    
                        $guardaCorreo=new RuralEnteCorreo();
                        $guardaCorreo->correo=$correo;
                        $guardaCorreo->cedula_ruc=$request->ci_ruc_contribuyente;
                        $guardaCorreo->save();

                    }

                    foreach($request->telefono as $telefono){
                        $guardaTlfo=new RuralEnteTelefono();
                        $guardaTlfo->telefono=$telefono;
                        $guardaTlfo->cedula_ruc=$request->ci_ruc_contribuyente;
                        $guardaTlfo->save();
                    }
                    
                    return ["mensaje"=>"Datos actualizados exitosamente", "error"=>false];  
                }
                
            
            } catch (\Exception $e) {
                DB::Rollback();
                return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage(), "error"=>true];
            }
        });
        return $transaction;
    }
}
