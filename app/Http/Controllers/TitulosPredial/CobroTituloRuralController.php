<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use App\Models\TransitoVehiculo;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use DB;
use PDF;
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

    public function buscarDeudaPredio(Request $request){ 
        try{
            $tipo=$request->tipo;
            $tipo_per=$request->tipo_per;
            $valor=$request->valor;

            $liquidacionRuralAct=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            ->Join('PREDIO as p', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral','tp.Titpr_Nombres as nombres','tp.Titpr_RUC_CI','p.Pre_NombrePredio','tp.TitPr_DireccionCont','tp.TitPr_Estado as ruc')
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
            ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral','cv.CarVe_Nombres as nombres','cv.CarVe_CI as Titpr_RUC_CI'
            ,'p.Pre_NombrePredio','cv.CarVe_Calle as TitPr_DireccionCont','cv.carVe_RUC as ruc')
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
    public function consultarTitulos($clave,$cedula){
        try {
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave','cv.CarVe_FechaEmision as fecha_emi','cv.CarVe_NumTitulo as num_titulo','cv.CarVe_CI as num_ident','cv.CarVe_Estado','cv.CarVe_Nombres as nombre_per','cv.CarVe_ValorEmitido as valor_emitido','cv.CarVe_TasaAdministrativa as tasa','CarVe_Calle as direcc_cont','cv.Carve_Recargo as recargo','cv.Carve_Descuento as descuento')
            ->where('cv.Pre_CodigoCatastral', '=', $clave)
            ->where(function ($query) use($cedula){
                $query->where('carVe_CI',$cedula)
                ->orWhere('carVe_RUC',$cedula);
            })

            ->whereIn('cv.CarVe_Estado',['E']) //E=Emitidos, N=Nueva Emision
            // ->where('Pre_Tipo','Rural')
            ->orderby('CarVe_NumTitulo','asc')
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
            ->where('tp.Pre_CodigoCatastral', '=', $clave)
            ->where('tp.Titpr_RUC_CI',$cedula)
            
            ->whereIn('tp.TitPr_Estado',['E','N'])
            // ->where('Pre_Tipo','Rural')
            ->orderby('TitPr_NumTitulo','asc')
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
            $resultado = $liquidacionRural->merge($liquidacionActual);

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

    public function pagaRuralTitulo(Request $request){
        
        $transaction=DB::transaction(function() use($request){ 
            try{ 
                if($request->chequeadoRemision==1 && date('m')>=7){
                    return ["mensaje"=>"Ocurrio un error, con respecto a la aplicacion de remision", "error"=>true];
                }  

                $obtenerRecaudador=auth()->user()->persona->cedula;
                $es_tesoreria=DB::connection('sqlsrv')->table('USUARIOSIC')
                ->where('USU_CEDULA', $obtenerRecaudador)
                ->where('USU_ACTIVO',1)
                ->select('USU_PERFIL','USU_NICK')
                ->first();

                if(is_null($es_tesoreria)){
                    return ["mensaje"=>"El usuario actual no esta registrado en el SISTEMA AME", "error"=>true];
                }
                
                if($es_tesoreria->USU_PERFIL!="TESORERIA"){
                    return ["mensaje"=>"El usuario actual no tiene asignado el perfil TESORERIA en el SISTEMA AME", "error"=>true];
                }
               
                
                $cont=0;
                $valor_cobrado=$request->valorCobrado;
                $valor_interes=$request->valorInteres;
                $valor_descuento=$request->valorDescuento;
                $valor_recarga=$request->valorRecarga;

                foreach($request->numTitulosSeleccionados as $key=>$tit){
                                    
                    $solo_anio=explode("-", $tit);
                    if (date('Y') == (int)$solo_anio[0]) {
                        $es_nueva_emi=DB::connection('sqlsrv')->table('TITULOS_PREDIO')
                        ->where('TitPr_NumTitulo',$tit)
                        ->whereIn('TitPr_Estado',['E','N'])
                        ->select('TitPr_Estado')
                        ->first();
                      
                        $estado_pagado='C';
                        $estado_actual='E';
                        if($es_nueva_emi->TitPr_Estado=='N'){
                            $estado_pagado='Q';
                            $estado_actual='N';
                        }
                       
                        $titulo=DB::connection('sqlsrv')->table('TITULOS_PREDIO')
                        ->where('TitPr_NumTitulo',$tit)
                        ->where('TitPr_Estado',$estado_actual)
                        ->update([
                            "TitPr_FechaRecaudacion" => date('Y-d-m 00:00:00.000'), // ✔ Fecha automática correcta
                            "TitPr_Estado" => $estado_pagado,
                            "Usu_usuario" => $es_tesoreria->USU_NICK,
                            "TitPr_ValorTCobrado" => round($valor_cobrado[$key], 2),
                            "TitPr_Interes" => round($valor_interes[$key], 2),
                            "TitPr_Recargo" => round($valor_recarga[$key], 2),
                            "TitPr_Descuento" => round($valor_descuento[$key], 2) // ✔ formato numérico válido
                        ]);
                        $cont=$cont+1;
                        
                    }else{                    
                        //cartera vencida
                        $carteraVencida=DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
                        ->where('CarVe_NumTitulo',$tit)
                        ->where('Carve_Estado','E')
                        ->update([
                            "CarVe_FechaRecaudacion" => date('Y-d-m 00:00:00.000'), // ✔ Fecha automática correcta
                            "Carve_Estado" => "C",
                            "Usu_usuario" => $es_tesoreria->USU_NICK,
                            "CarVe_ValorTCobrado" => round($valor_cobrado[$key], 2),
                            "CarVe_Interes" => round($valor_interes[$key], 2),
                            "CarVe_Recargo" => round($valor_recarga[$key], 2),
                            "CarVe_Descuento" => round($valor_descuento[$key], 2) // ✔ formato numérico válido
                        ]);
                        $cont=$cont+1;
                    }                    
                    
                }
                if($cont>0){
                    $crearPdf=$this->pdfTitulo($request->numTitulosSeleccionados,'nocopia');
                    if($crearPdf['error']==true){
                        DB::Rollback();
                        return ["mensaje"=>$crearPdf["mensaje"], "error"=>true];
                    }
                    return ["mensaje"=>"Pago procesado exitosamente", "error"=>false, "pdf"=>$crearPdf['pdf']];
                }

                return ["mensaje"=>"No se pudo procesar el/los pago(s)", "error"=>true];

            }catch (\Exception $e) {
                DB::Rollback();
                return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage(), "error"=>true];
            }
         });
        return $transaction;
    }

    public function pdfTitulo($titulos, $copia){
        try{
            //$titulos=['2018-000001-PR','2019-000001-PR','2025-000001-PR','2024-003798-PR'];
            $anio_actual=[''];
            $vencido=[''];
            foreach($titulos as $item){
                $solo_anio=explode("-",$item);
                if($solo_anio[0]==date('Y')){
                    $anio_actual=[];
                    array_push($anio_actual,$item);
                }else{
                    // $vencido=[];
                    array_push($vencido,$item);
                }

            }
            // dd($vencido);
            $liquidacionRural=[];
           
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            // ->Join('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave'
            ,'cv.CarVe_FechaEmision as fecha_emi',
            'cv.CarVe_NumTitulo as num_titulo',
            'cv.CarVe_CI as num_ident',
            'cv.CarVe_Nombres as nombres',
            // 'c.Ciu_Apellidos as apellidos',
            // 'c.Ciu_Nombres as nombres',
            'cv.CarVe_ValorEmitido as valor_emitido',
            'cv.Carve_Recargo as recargo',
            'cv.Carve_Descuento as descuento',
            'cv.CarVe_ValorTCobrado as valor_cobrado',
            'cv.CarVe_Interes as interes',
            'CarVe_direccPropietario as direccion',
            'cv.CarVe_Calle as calle',
            'cv.CarVe_ValTotalTerrPredio as total_terreno_predio',
            'cv.CarVe_ValTotalEdifPredio as valorEdifPredio',
            'cv.CarVe_ValOtrasInver as valor_otras_inv',
            'cv.CarVe_ValComerPredio as valor_comer_predio',
            'cv.CarVe_RebajaHipotec as valor_rebaja',
            'cv.CarVe_BaseImponible as base_imp',
            'cv.CarVe_IPU as ipu',
            'cv.CarVe_TasaAdministrativa as tasa_adm',
            'cv.CarVe_Bomberos as bomberos',
            'cv.Carve_Valor1 as seguridad',
            DB::raw("FORMAT(cv.CarVe_FechaEmision,'dd/MM/yyyy') as fecha_emi"),
            DB::raw("FORMAT(cv.CarVe_FechaRecaudacion,'dd/MM/yyyy') as fecha_recaudacion"))
            ->whereIn('cv.CarVe_NumTitulo', $vencido)                    
            ->where('cv.CarVe_Estado','C')
            ->orderby('CarVe_NumTitulo','asc')
            ->get();
            //dd($anio_actual);
           
            $actual=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            // ->Join('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'tp.Titpr_RUC_CI')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral as clave',
            'tp.TitPr_NumTitulo as num_titulo',
            'tp.TitPr_FechaEmision as fecha_emi',           
            'tp.Titpr_RUC_CI as num_ident',
            'tp.TitPr_Nombres as nombres',
            // 'c.Ciu_Apellidos as apellidos',
            // 'c.Ciu_Nombres as nombres',
            'tp.TitPr_ValorEmitido as valor_emitido',
            'tp.TitPr_Recargo as recargo',
            'tp.TitPr_Descuento as descuento',
            'tp.TitPr_ValorTCobrado as valor_cobrado',
            'tp.TitPr_Interes as interes',
            'tp.TitPr_FechaRecaudacion as fecha_recaudacion',
            'tp.TitPr_DireccionCont as direccion',
            'tp.TitPr_DireccionCont as calle',
            'tp.TitPr_ValTotalTerrPredio as total_terreno_predio',
            'tp.TitPr_ValTotalEdifPredio as valorEdifPredio',
            'tp.TitPr_ValOtrasInver as valor_otras_inv',
            'tp.TitPr_ValComerPredio as valor_comer_predio',
            'tp.TitPr_RebajaHipotec as valor_rebaja',
            'tp.TitPr_BaseImponible as base_imp',
            'tp.TitPr_IPU as ipu',
            'tp.TitPr_TasaAdministrativa as tasa_adm',
            'tp.TitPr_Bomberos as bomberos',
            'tp.TitPr_Valor1 as seguridad',
            DB::raw("FORMAT(tp.TitPr_FechaEmision,'dd/MM/yyyy') as fecha_emi"),
            DB::raw("FORMAT(tp.TitPr_FechaRecaudacion,'dd/MM/yyyy') as fecha_recaudacion"))
            ->whereIn('tp.TitPr_NumTitulo', [$anio_actual])            
            ->whereIn('tp.TitPr_Estado',['C','Q'])
            ->orderby('TitPr_NumTitulo','asc')
            ->get();

            $resultado = $liquidacionRural->merge($actual);


            $nombrePDF="TituloRural".date('YmdHis').".pdf";
            // dd($copia);
            $pdf = PDF::loadView('reportes.TituloRural',["liquidacionRural"=>$resultado, "copia"=>$copia]);

            // return $pdf->stream($nombrePDF);

            $estadoarch = $pdf->stream();

            //lo guardamos en el disco temporal
            \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk('public')->exists($nombrePDF); 
            if($exists_destino){ 
                return [
                    'error'=>false,
                    'pdf'=>$nombrePDF
                ];
            }else{
                return [
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ];
            }

        }catch (\Exception $e) {
            
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function verTituloRuralSinPagar(Request $request){
        try{
            $crearPdf=$this->pdfTituloPrevio($request->numTitulosSeleccionados,'nocopia');
            if($crearPdf['error']==true){
                DB::Rollback();
                return ["mensaje"=>$crearPdf["mensaje"], "error"=>true];
            }
            return ["mensaje"=>"Pago procesado exitosamente", "error"=>false, "pdf"=>$crearPdf['pdf']];
        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }
    public function pdfTituloPrevio($titulos, $copia){
        try{
            // $titulos=['2018-000001-PR','2019-000001-PR','2025-000001-PR','2024-003798-PR'];
            $anio_actual=[''];
            $vencido=[''];
            foreach($titulos as $item){
                $solo_anio=explode("-",$item);
                if($solo_anio[0]==date('Y')){
                    $anio_actual=[];
                    array_push($anio_actual,$item);
                }else{
                    // $vencido=[];
                    array_push($vencido,$item);
                }

            }
            // dd($vencido);
            $liquidacionRural=[];
           
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            // ->Join('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave'
            ,'cv.CarVe_FechaEmision as fecha_emi',
            'cv.CarVe_NumTitulo as num_titulo',
            'cv.CarVe_CI as num_ident',
            'cv.CarVe_Nombres as nombres',
            // 'c.Ciu_Apellidos as apellidos',
            // 'c.Ciu_Nombres as nombres',
            'cv.CarVe_ValorEmitido as valor_emitido',
            'cv.Carve_Recargo as recargo',
            'cv.Carve_Descuento as descuento',
            'cv.CarVe_ValorTCobrado as valor_cobrado',
            // 'cv.CarVe_Interes as interes',
            'cv.CarVe_TasaAdministrativa as tasa',
            'CarVe_direccPropietario as direccion',
            'cv.CarVe_Calle as calle',
            'cv.CarVe_ValTotalTerrPredio as total_terreno_predio',
            'cv.CarVe_ValTotalEdifPredio as valorEdifPredio',
            'cv.CarVe_ValOtrasInver as valor_otras_inv',
            'cv.CarVe_ValComerPredio as valor_comer_predio',
            'cv.CarVe_RebajaHipotec as valor_rebaja',
            'cv.CarVe_BaseImponible as base_imp',
            'cv.CarVe_IPU as ipu',
            'cv.CarVe_TasaAdministrativa as tasa_adm',
            'cv.CarVe_Bomberos as bomberos',
            'cv.Carve_Valor1 as seguridad',
            DB::raw("FORMAT(cv.CarVe_FechaEmision,'dd/MM/yyyy') as fecha_emi"),
            DB::raw("FORMAT(cv.CarVe_FechaRecaudacion,'dd/MM/yyyy') as fecha_recaudacion"))
            ->whereIn('cv.CarVe_NumTitulo', $vencido)                    
            // ->where('cv.CarVe_Estado','C')
            ->orderby('CarVe_NumTitulo','asc')
            ->get();

            $mes_Actual=date('m');
            $aplica_remision=0;
            if($mes_Actual<7){
                $aplica_remision=1;
            }
            $total_valor=0;
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
                // $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                $liquidacionRural[$key]->intereses=$valor;

                $total_pago=($valor + $data->valor_emitido + $data->recargo) - $data->descuento;
                $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);

                $total_valor=$total_valor+$total_pago;
                $total_valor=number_format($total_valor,2);
            }
            //dd($anio_actual);
           
            $actual=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            // ->Join('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'tp.Titpr_RUC_CI')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral as clave',
            'tp.TitPr_NumTitulo as num_titulo',
            'tp.TitPr_FechaEmision as fecha_emi',           
            'tp.Titpr_RUC_CI as num_ident',
            'tp.TitPr_Nombres as nombres',
            // 'c.Ciu_Apellidos as apellidos',
            // 'c.Ciu_Nombres as nombres',
            'tp.TitPr_ValorEmitido as valor_emitido',
            'tp.TitPr_Recargo as recargo',
            'tp.TitPr_Descuento as descuento',
            'tp.TitPr_ValorTCobrado as valor_cobrado',
            'tp.TitPr_Interes as interes',
            'tp.TitPr_FechaRecaudacion as fecha_recaudacion',
            'tp.TitPr_DireccionCont as direccion',
            'tp.TitPr_DireccionCont as calle',
            'tp.TitPr_ValTotalTerrPredio as total_terreno_predio',
            'tp.TitPr_ValTotalEdifPredio as valorEdifPredio',
            'tp.TitPr_ValOtrasInver as valor_otras_inv',
            'tp.TitPr_ValComerPredio as valor_comer_predio',
            'tp.TitPr_RebajaHipotec as valor_rebaja',
            'tp.TitPr_BaseImponible as base_imp',
            'tp.TitPr_IPU as ipu',
            'tp.TitPr_TasaAdministrativa as tasa_adm',
            'tp.TitPr_Bomberos as bomberos',
            'tp.TitPr_Valor1 as seguridad',
            DB::raw("FORMAT(tp.TitPr_FechaEmision,'dd/MM/yyyy') as fecha_emi"),
            DB::raw("FORMAT(tp.TitPr_FechaRecaudacion,'dd/MM/yyyy') as fecha_recaudacion"))
            ->whereIn('tp.TitPr_NumTitulo', [$anio_actual])            
            // ->whereIn('tp.TitPr_Estado',['C','Q'])
            ->orderby('TitPr_NumTitulo','asc')
            ->get();

            foreach($actual as $key=> $data){
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

                    $actual[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                    $actual[$key]->intereses=$valor;

                    $total_pago=($valor +$data->valor_emitido + $data->recargo) - $data->descuento;
                    $actual[$key]->total_pagar=number_format($total_pago,2);
                }else{
                    $cero=0;
                    $actual[$key]->porcentaje_intereses=number_format($cero,2);
                    $actual[$key]->intereses=number_format($cero,2);

                    $total_pago=($valor +$data->valor_emitido+ $data->recargo) - $data->descuento;
                    $actual[$key]->total_pagar=number_format($total_pago,2);
                }
                $actual[$key]->subtotal_emi=$subtotal;

                $total_valor=$total_valor+$total_pago;
                $total_valor=number_format($total_valor,2);
            }

            $resultado = $liquidacionRural->merge($actual);


            $nombrePDF="PrevioTituloRural".date('YmdHis').".pdf";
            // dd($copia);
            $pdf = PDF::loadView('reportes.PrevioTituloRural',["liquidacionRural"=>$resultado, "copia"=>$copia]);

            // return $pdf->stream($nombrePDF);

            $estadoarch = $pdf->stream();

            //lo guardamos en el disco temporal
            \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk('public')->exists($nombrePDF); 
            if($exists_destino){ 
                return [
                    'error'=>false,
                    'pdf'=>$nombrePDF
                ];
            }else{
                return [
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ];
            }

        }catch (\Exception $e) {
            
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function descargarPdfTitulo($archivo){
        try{

            $exists_destino = \Storage::disk('public')->exists($archivo);

            if($exists_destino){
                return response()->download( storage_path('app/public/'.$archivo))->deleteFileAfterSend(true);
            }else{
                dd("a");
                return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
            }

        } catch (\Throwable $th) {
            dd($th);
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
        }
    }

    public function verDocumento($documentName){
        try {
            $info = new \SplFileInfo($documentName);
            $extension = strtolower($info->getExtension());

            // Si NO es PDF → descargar normalmente
            if ($extension != "pdf") {
                return \Storage::disk('public')->download($documentName);
            }

            // SI ES PDF → devolverlo como archivo real, no base64, no vista
            $pdfPath = \Storage::disk('public')->path($documentName);

            if (!file_exists($pdfPath)) {
                abort(404, "Archivo no encontrado");
            }

            return response()->file($pdfPath, [
                'Content-Type'        => 'application/pdf',
                'Content-Disposition' => 'inline; filename="'.$documentName.'"',
            ]);

        } catch (\Throwable $th) {
            abort(404);
        }
    }

    public function vistaCobrados(){
        if(!Auth()->user()->hasPermissionTo('Titulos Rurales Cobrados'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('cobroTituloRural.cobrados');
    }

    public function consultarTitulosCobrados($clave,$cedula)
    {
        try {
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            // ->Join('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave',
            'cv.CarVe_FechaEmision as fecha_emi',
            'cv.CarVe_NumTitulo as num_titulo',
            'cv.CarVe_CI as num_ident',
            'cv.CarVe_Estado',
            // 'c.Ciu_Apellidos',
            // 'c.Ciu_Nombres',
            'cv.CarVe_Nombres as nombre_per',
            DB::raw("FORMAT(cv.CarVe_FechaRecaudacion,'dd/MM/yyyy') as fecha_recaudacion"),
            'cv.CarVe_TasaAdministrativa as tasa',
            'CarVe_Calle as direcc_cont',
            DB::raw("FORMAT(cv.CarVe_ValorTCobrado, 'N2') as total_cobrado"))
            ->where('cv.Pre_CodigoCatastral', '=', $clave)  
            ->where(function ($query) use($cedula){
                $query->where('carVe_CI',$cedula)
                ->orWhere('carVe_RUC',$cedula);
            })          
            ->whereIn('cv.CarVe_Estado',['C'])
            ->orderby('CarVe_NumTitulo','asc')
            ->get();
            
            $liquidacionActual=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            // ->Join('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'tp.Titpr_RUC_CI')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral as clave',
            'tp.TitPr_FechaEmision as fecha_emi',
            'tp.TitPr_NumTitulo as num_titulo',
            'tp.Titpr_RUC_CI as num_ident',
            'tp.TitPr_Estado',
            // 'c.Ciu_Apellidos',
            DB::raw("FORMAT(tp.TitPr_FechaRecaudacion,'dd/MM/yyyy') as fecha_recaudacion"),
            // 'c.Ciu_Nombres',
            'tp.TitPr_Nombres as nombre_per',
            'TitPr_DireccionCont as direcc_cont',
            DB::raw("FORMAT(tp.TitPr_ValorTCobrado, 'N2') as total_cobrado"))
            ->where('tp.Pre_CodigoCatastral', '=', $clave)     
            ->where('tp.Titpr_RUC_CI',$cedula)       
            ->whereIn('tp.TitPr_Estado',['C','Q'])
            ->orderby('TitPr_NumTitulo','asc')
            ->get();

            $resultado = $liquidacionRural->merge($liquidacionActual);

            return ["resultado"=>$resultado, "error"=>false];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function descargarTitulosRural(Request $request){
        
        $transaction=DB::transaction(function() use($request){ 
            try{ 
                $crearPdf=$this->pdfTitulo($request->numTitulosSeleccionados,'copia');
                if($crearPdf['error']==true){
                    DB::Rollback();
                    return ["mensaje"=>$crearPdf["mensaje"], "error"=>true];
                }
                return ["mensaje"=>"Pago procesado exitosamente", "error"=>false, "pdf"=>$crearPdf['pdf']];               

            }catch (\Exception $e) {
                DB::Rollback();
                return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage(), "error"=>true];
            }
         });
        return $transaction;
    }

     public function buscarPagosPredio(Request $request){ 
        try{
            $tipo=$request->tipo;
            $tipo_per=$request->tipo_per;
            $valor=$request->valor;

            $liquidacionRuralAct=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
            ->Join('PREDIO as p', 'p.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
            ->select('tp.Pre_CodigoCatastral','tp.Titpr_Nombres as nombres','tp.Titpr_RUC_CI','p.Pre_NombrePredio','tp.TitPr_DireccionCont','tp.TitPr_Estado as ruc')
            ->where(function($query)use($tipo,$valor, $tipo_per) {
                if($tipo==1){
                    $query->where('Titpr_RUC_CI', '=', $valor);
                }else if($tipo==2){
                    $query->where('tp.Pre_CodigoCatastral', '=', $valor);
                }else{
                    $query->where('tp.Titpr_Nombres', 'LIKE', "%$valor%");
                }
                
            })            
            ->whereIn('tp.TitPr_Estado',['C','Q']) //E=Emitidos, C=Cancelado, Q=Cancelado Nueva Emitido, N=Nueva Emision
            ->distinct()
            ->limit(10)
            ->get();
            
            if(sizeof($liquidacionRuralAct)>0){
                return (['data'=>$liquidacionRuralAct,'error'=>false]); 
            }
           
            $liquidacionRuralAct=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral','cv.CarVe_Nombres as nombres','cv.CarVe_CI as Titpr_RUC_CI'
            ,'p.Pre_NombrePredio','cv.CarVe_Calle as TitPr_DireccionCont','cv.carVe_RUC as ruc')
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
            ->whereIn('cv.CarVe_Estado',['C']) //E=Emitidos, C=Cancelado, Q=Cancelado Nueva Emitido, N=Nueva Emision
            ->distinct()
            ->limit(10)
            ->get();            
               
            return (['data'=>$liquidacionRuralAct,'error'=>false]); 


        } catch (\Throwable $th) {
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th,'error'=>true]); 
        } 
    }
}