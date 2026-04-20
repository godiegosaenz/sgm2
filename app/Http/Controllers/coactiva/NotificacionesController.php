<?php

namespace App\Http\Controllers\Coactiva;

use App\Http\Controllers\Controller;
use App\Models\Coactiva\Convenio;
use App\Models\Coactiva\CuotaConvenio;
use App\Models\Coactiva\DataCoa;
use App\Models\Coactiva\DataNotifica;
use App\Models\Coactiva\InfoCoa;
use App\Models\Coactiva\InfoNotifica;
use App\Models\Coactiva\Pago;
use App\Models\Coactiva\Secuencial;
use Illuminate\Http\Request;
use App\Models\PsqlYearDeclaracion;
use App\Models\PsqlLiquidacion;
use App\Models\CoactTitulo;
use App\Models\CoactListadoTitulo;
use Carbon\Carbon;
use DB;
use Illuminate\Support\Facades\Gate;
use App\Http\Controllers\TitulosPredial\LiquidacionesController;

class NotificacionesController extends Controller
{   
    private $Liquidaciones= null;
    public function __construct()
    {
        $this->Liquidaciones=new LiquidacionesController();
    }

    public function index()
    {
        // if(!Auth()->user()->hasPermissionTo('Notificacion'))
        // {
        //     abort(403, 'No tienes acceso a esta seccion.');
        // }
        return view('coactiva.notificacion');
    }

    public function notificar(Request $request){
       
        DB::beginTransaction();
        try{
            $id_liquidacion=$request->id_liquidacion;
            // Convertir a array de números:
            $ids = explode(',', $id_liquidacion);
            $ids = array_map('trim', $ids);   // quitar espacios
            $ids = array_map('intval', $ids); // convertir a enteros
            
            $verifica=PsqlLiquidacion::whereIn('id',$ids)
            ->where('notifica_coact',null)
            ->where('estado_liquidacion',2)
            ->get();
          
            if(sizeof($verifica)==0){
                return ["mensaje"=>' Las emisiones ya no se encuentran disponibles de notificar', "error"=>true];
            }

            $documento=$request->archivo;

            $creaNotificacion=new CoactTitulo();   
            $creaNotificacion->observacion_notificacion=$request->observacion; 
            $creaNotificacion->ente=$request->idcontr_selecc;  

            if(!is_null($documento)){
                $extension = pathinfo($documento->getClientOriginalName(), PATHINFO_EXTENSION);
                $nombreDocumento = "documento_coact_"."-" . date('Ymd') . '-' . time();
                $creaNotificacion->documento_notif = $nombreDocumento . "." . $extension;
            }
            $creaNotificacion->estado="N";
            $creaNotificacion->id_usuario_notif=auth()->user()->id;
            $creaNotificacion->fecha_registro_notif=date('Y-m-d H:i:s');
            
            if($creaNotificacion->save()){

               
                $contador=0;
                $liquidacion=PsqlLiquidacion::whereIn('id',$ids)
                ->where('estado_liquidacion',2)
                ->where('notifica_coact',null)
                ->get();    
                foreach($liquidacion as $emision){
                    $NotificaCoactivoTitulo=new CoactListadoTitulo();
                    $NotificaCoactivoTitulo->id_coact_listado_titulos=$creaNotificacion->id;
                    $NotificaCoactivoTitulo->id_ren_liquidacion=$emision->id;
                    $NotificaCoactivoTitulo->estado="N";
                    $NotificaCoactivoTitulo->save();

                    // $emision->coactiva=true;
                    $emision->notifica_coact="S";
                    $emision->save();
                    $contador++;

                    if(!is_null($documento)){
                        \Storage::disk('disksDocumentoRenta')->put($nombreDocumento . "." . $extension, \File::get($documento));
                       
                    }
                }                
            
                DB::commit();
                if($contador==0){
                    DB::rollBack();
                    return ["mensaje"=>' No se Notifico ningun titulo', "error"=>true];
                }else{
                    
                    return ["mensaje"=>"Notificacion realizado exitosamente", "error"=>false];
                }                    
            
            }

            return ["mensaje"=>' No se Notifico ningun titulo22', "error"=>true];

        }catch (\Exception $e) {
            DB::rollBack();
            return ["mensaje"=>'Ocurrio un error, intentelo mas tarde '.$e, "error"=>true];
        }
    }

    public function vistaNotificar(){
        if(!Auth()->user()->hasPermissionTo('Listado Notificaciones'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('coactiva.notificar');
    }

    public function notificacionPagoVoluntario2($cedula, $lugar){
       
        DB::connection('pgsql')->beginTransaction();
        try{
            if($lugar==1){
                
                $generarPdf=$this->Liquidaciones->pagoVoluntario($cedula, $lugar, 1);
                
                if($generarPdf['error']==true){
                    return ["mensaje"=>$generarPdf['mensaje'], "error"=>true];
                }

                $verificaNoti=InfoNotifica::where('id_persona',$generarPdf['listado_final'][0]->idpersona)
                ->whereIn('estado',['Notificado','Coactivado'])
                ->first();

                if(!is_null($verificaNoti)){
                    return ["mensaje"=>'El contribuyente ya tiene registrado una notificacion', "error"=>true];
                }

                $id_persona=DB::connection('pgsql')->table('sgm_app.cat_ente')
                ->where('ci_ruc',$cedula)->select('id')->first();

                $guardaDataNotificacion=new InfoNotifica();
                $guardaDataNotificacion->id_persona=$id_persona->id;
                $guardaDataNotificacion->save();
                
                $subtotal_emi=0;
                $intereses_emi=0;
                $descuento_emi=0;
                $recargo_emi=0;
                $total_emi=0;
                foreach($generarPdf['listado_final'] as $data){
                   
                    $subtotal  = $data->subtotal_emi ? str_replace(',', '', $data->subtotal_emi) : 0;
                    $interes   = $data->intereses ? str_replace(',', '', $data->intereses) : 0;
                    $recargo   = $data->recargo ? str_replace(',', '', $data->recargo) : 0;
                    $descuento = $data->descuento ? str_replace(',', '', $data->descuento) : 0;
                    $total     = $data->total_pagar ? str_replace(',', '', $data->total_pagar) : 0;

                    $guardaData= new DataNotifica();
                    $guardaData->id_info_notifica=$guardaDataNotificacion->id;
                    $guardaData->id_liquidacion=$data->id;
                    $guardaData->subtotal=$subtotal;
                    $guardaData->interes=$interes;
                    $guardaData->recargo=$recargo ;
                    $guardaData->descuento=$descuento;
                    $guardaData->total=$total;
                    $guardaData->estado='Notificado';
                    $guardaData->save();

                    $subtotal_emi=$subtotal_emi + $guardaData->subtotal;
                
                    $intereses_emi=$intereses_emi + $guardaData->interes;
                    
                    $recargo_emi=$intereses_emi + $guardaData->recargo;
                    
                    $descuento_emi=$descuento_emi + $guardaData->descuento;
                    
                    $total_emi=$total_emi + $guardaData->total;
                    
                }

                $guardaDataNotificacion->estado="Notificado";
                $guardaDataNotificacion->id_usuario_registra=auth()->user()->id;
                $guardaDataNotificacion->fecha_registra=date('Y-m-d H:i:s');
                $guardaDataNotificacion->subtotal_notificado=(float)$subtotal_emi;
                $guardaDataNotificacion->interes_notificado=(float)$intereses_emi;
                $guardaDataNotificacion->descuento_notificado=(float)$descuento_emi;
                $guardaDataNotificacion->recargo_notificado=(float)$recargo_emi;
                $guardaDataNotificacion->total_notificado=(float)$total_emi;
                $guardaDataNotificacion->predio='Urbano';
                $guardaDataNotificacion->documento=$generarPdf['pdf'];
                $guardaDataNotificacion->save();
                DB::connection('pgsql')->commit();
                return ["mensaje"=>'Notificacion registrada exitosamente', "error"=>false, "pdf"=>$generarPdf['pdf']];
            }else{
                $generarPdf=$this->Liquidaciones->pagoVoluntario($cedula, $lugar, 1);
                if($generarPdf['error']==true){
                    return ["mensaje"=>$generarPdf['mensaje'], "error"=>true];
                }
               
                $guardaDataNotificacion=new InfoNotifica();
                $guardaDataNotificacion->num_ident=$generarPdf['listado_final'][0]->num_ident;
                $guardaDataNotificacion->contribuyente=$generarPdf['listado_final'][0]->nombre_per;
                $guardaDataNotificacion->save();
                
                $subtotal_emi=0;
                $intereses_emi=0;
                $descuento_emi=0;
                $recargo_emi=0;
                $total_emi=0;
                foreach($generarPdf['listado_final'] as $data){

                    $subtotal  = $data->subtotal_emi ? str_replace(',', '', $data->subtotal_emi) : 0;
                    $interes   = $data->intereses ? str_replace(',', '', $data->intereses) : 0;
                    $recargo   = $data->recargo ? str_replace(',', '', $data->recargo) : 0;
                    $descuento = $data->descuento ? str_replace(',', '', $data->descuento) : 0;
                    $total     = $data->total_pagar ? str_replace(',', '', $data->total_pagar) : 0;
                    
                    $guardaData= new DataNotifica();
                    $guardaData->id_info_notifica=$guardaDataNotificacion->id;
                    $guardaData->num_titulo=$data->num_titulo;
                    
                    $guardaData->subtotal=$subtotal;
                    $guardaData->interes=$interes;
                    $guardaData->recargo=$recargo ;
                    $guardaData->descuento=$descuento;
                    $guardaData->total=$total;

                    $guardaData->estado='Notificado';
                    $guardaData->save();

                    $subtotal_emi=$subtotal_emi + $guardaData->subtotal;
                
                    $intereses_emi=$intereses_emi + $guardaData->interes;
                    
                    $recargo_emi=$intereses_emi + $guardaData->recargo;
                    
                    $descuento_emi=$descuento_emi + $guardaData->descuento;
                    
                    $total_emi=$total_emi + $guardaData->total;
                    
                }

                $guardaDataNotificacion->estado="Notificado";
                $guardaDataNotificacion->id_usuario_registra=auth()->user()->id;
                $guardaDataNotificacion->fecha_registra=date('Y-m-d H:i:s');
                $guardaDataNotificacion->subtotal_notificado=(float)$subtotal_emi;
                $guardaDataNotificacion->interes_notificado=(float)$intereses_emi;
                $guardaDataNotificacion->descuento_notificado=(float)$descuento_emi;
                $guardaDataNotificacion->recargo_notificado=(float)$recargo_emi;
                $guardaDataNotificacion->total_notificado=(float)$total_emi;
                $guardaDataNotificacion->predio='Rural';
                $guardaDataNotificacion->documento=$generarPdf['pdf'];
                $guardaDataNotificacion->save();
                DB::connection('pgsql')->commit();
                return ["mensaje"=>'Notificacion registrada exitosamente', "error"=>false, "pdf"=>$generarPdf['pdf']];
            }

        }catch (\Exception $e) {
            DB::connection('pgsql')->rollBack();
            return ["mensaje"=>'Ocurrio un error, intentelo mas tarde '.$e->getMessage()." Linea=>".$e->getLine(), "error"=>true];
        }
    }

    public function notificacionPagoVoluntario($cedula, $lugar){
       
        DB::connection('pgsql')->beginTransaction();
        try{
                           
            $generarPdf=$this->Liquidaciones->pagoVoluntario($cedula, $lugar, 1);
            
            if($generarPdf['error']==true){
                return ["mensaje"=>$generarPdf['mensaje'], "error"=>true];
            }

            $verificaNoti=InfoNotifica::where('num_ident',$cedula)
            ->whereIn('estado',['Notificado','Coactivado'])
            ->first();

            if(!is_null($verificaNoti)){
                return ["mensaje"=>'El contribuyente ya tiene registrado una notificacion', "error"=>true];
            }

            $id_persona=DB::connection('pgsql')->table('sgm_app.cat_ente')
            ->where('ci_ruc',$cedula)->select('id')->first();               

            $guardaDataNotificacion=new InfoNotifica();
            
            if(!is_null($id_persona)){$guardaDataNotificacion->id_persona=$id_persona->id;}
            $guardaDataNotificacion->num_ident=$cedula;
            $guardaDataNotificacion->contribuyente=$generarPdf['listado_final'][0]->nombre_per;
            $guardaDataNotificacion->save();
            
            $subtotal_emi=0;
            $intereses_emi=0;
            $descuento_emi=0;
            $recargo_emi=0;
            $total_emi=0;
            foreach($generarPdf['listado_final'] as $data){
                
                $subtotal  = $data->subtotal_emi ? str_replace(',', '', $data->subtotal_emi) : 0;
                $interes   = $data->intereses ? str_replace(',', '', $data->intereses) : 0;
                $recargo   = $data->recargo ? str_replace(',', '', $data->recargo) : 0;
                $descuento = $data->descuento ? str_replace(',', '', $data->descuento) : 0;
                $total     = $data->total_pagar ? str_replace(',', '', $data->total_pagar) : 0;

                $guardaData= new DataNotifica();
                $guardaData->id_info_notifica=$guardaDataNotificacion->id;
                if(isset($data->id)){$guardaData->id_liquidacion=$data->id;}
                if(isset($data->num_titulo)){$guardaData->num_titulo=$data->num_titulo;}
                
                
                $guardaData->subtotal=$subtotal;
                $guardaData->interes=$interes;
                $guardaData->recargo=$recargo ;
                $guardaData->descuento=$descuento;
                $guardaData->total=$total;
                $guardaData->estado='Notificado';
                $guardaData->save();

                $subtotal_emi=$subtotal_emi + $guardaData->subtotal;
            
                $intereses_emi=$intereses_emi + $guardaData->interes;
                
                $recargo_emi=$intereses_emi + $guardaData->recargo;
                
                $descuento_emi=$descuento_emi + $guardaData->descuento;
                
                $total_emi=$total_emi + $guardaData->total;
                
            }

            $guardaDataNotificacion->estado="Notificado";
            $guardaDataNotificacion->id_usuario_registra=auth()->user()->id;
            $guardaDataNotificacion->fecha_registra=date('Y-m-d H:i:s');
            $guardaDataNotificacion->subtotal_notificado=(float)$subtotal_emi;
            $guardaDataNotificacion->interes_notificado=(float)$intereses_emi;
            $guardaDataNotificacion->descuento_notificado=(float)$descuento_emi;
            $guardaDataNotificacion->recargo_notificado=(float)$recargo_emi;
            $guardaDataNotificacion->total_notificado=(float)$total_emi;
            
            $guardaDataNotificacion->documento=$generarPdf['pdf'];
            $guardaDataNotificacion->save();
            DB::connection('pgsql')->commit();
            return ["mensaje"=>'Notificacion registrada exitosamente', "error"=>false, "pdf"=>$generarPdf['pdf']];
           

        }catch (\Exception $e) {
            DB::connection('pgsql')->rollBack();
            return ["mensaje"=>'Ocurrio un error, intentelo mas tarde '.$e->getMessage()." Linea=>".$e->getLine(), "error"=>true];
        }
    }


    public function verDocumento($documentName){
        try {
            $info = new \SplFileInfo($documentName);
            $extension = strtolower($info->getExtension());

            // Si NO es PDF → descargar normalmente
            if ($extension != "pdf") {
                return \Storage::disk('disksCoactiva')->download($documentName);
            }

            // SI ES PDF → devolverlo como archivo real, no base64, no vista
            $pdfPath = \Storage::disk('disksCoactiva')->path($documentName);

            if (!file_exists($pdfPath)) {
                abort(404, "Archivo no encontrado");
            }

            return response()->file($pdfPath, [
                'Content-Type'        => 'application/pdf',
                'Content-Disposition' => 'inline; filename="'.$documentName.'"',
            ]);

        } catch (\Throwable $th) {
            dd($th);
            abort(404);
        }
    }

    public function descargarArchivo($archivo){
        try{

            $exists_destino = \Storage::disk('disksCoactiva')->exists($archivo);
            if($exists_destino){
                $filePath = \Storage::disk('disksCoactiva')->path($archivo);

                // Devuelve la respuesta para descargar el archivo
                return response()->download($filePath);
            }else{
                // Log::error("DocumentosController =>descargarArchivo =>sms => Documento no encontrado");
                return back()->with(['mensaje'=>'Ocurrió un error','error'=>'danger']);
            }

        } catch (\Throwable $th) {
            dd($th);
            // Log::error("DocumentosController =>descargarArchivo =>sms => ".$th->getMessage());
            return back()->with(['mensaje'=>'Ocurrió un error','error'=>'danger']);
        }

    }

    public function vistaListaNotificacion(){
        if(!Auth()->user()->hasPermissionTo('Listado Notificaciones'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('coactiva.lista_notificacion');
    }


    public function tablaNotificacion($periodo){
        try{
           
            $mes = $periodo; 
            $inicio = $mes . '-01';
            $fin = date("Y-m-t", strtotime($inicio)); // último día del mes

            $datos=InfoNotifica::with('data','ente')->whereIn('estado',['Notificado','Coactivado'])
            ->whereBetween('fecha_registra', [$inicio.' 00:00:00', $fin.' 23:59:59'])
            ->select('*')
            ->selectRaw("CURRENT_DATE - DATE(fecha_registra) AS dias_transcurridos")
            ->get();        

            foreach($datos as $key=> $data){
               
                $usuarioRegistra=DB::connection('mysql')->table('users as u')
                ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                ->where('u.id',$data->id_usuario_registra)
                ->select('p.nombres','p.apellidos','p.cedula')
                ->first();
                $datos[$key]->profesional=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;

            }

           
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function detalleNotificacion($id){
        try{
           
            $datos=InfoNotifica::with('data','ente')->whereIn('estado',['Notificado','Coactivado'])
            ->where('id',$id)
            ->select('*')
            ->selectRaw("CURRENT_DATE - DATE(fecha_registra) AS dias_transcurridos")
            ->get();

        
            foreach($datos as $key=> $data){
               
                $usuarioRegistra=DB::connection('mysql')->table('users as u')
                ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                ->where('u.id',$data->id_usuario_registra)
                ->select('p.nombres','p.apellidos','p.cedula')
                ->first();
                $datos[$key]->profesional=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;

                $cedula=$data->num_ident;
                
                foreach($data->data as $key2=> $info){
                    if(is_null($info->id_liquidacion)){
                        $anio=$anio=explode("-",$info->num_titulo);  
                        
                        if($anio[0]==date('Y')){
                            $contr=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
                            ->whereIn('tp.TitPr_Estado',['E','N'])
                            ->where('Titpr_RUC_CI',$cedula)
                            ->where('TitPr_NumTitulo',$info->num_titulo)
                            ->select('Pre_CodigoCatastral','TitPr_Nombres')
                            ->first();
                            
                            $data->data[$key2]->clave_cat=$contr->Pre_CodigoCatastral;
                            $data->data[$key2]->contrib=$contr->TitPr_Nombres;
                            $data->data[$key2]->anio=$anio[0];
                        }else{
                            $contr=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
                            ->whereIn('cv.CarVe_Estado',['E'])
                            ->where('CarVe_NumTitulo',$info->num_titulo)
                            ->select('Pre_CodigoCatastral','CarVe_Nombres','CarVe_NumTitulo')
                            ->first();
                        
                            $data->data[$key2]->clave_cat=$contr->Pre_CodigoCatastral;
                            $data->data[$key2]->contrib=$contr->CarVe_Nombres;
                            $data->data[$key2]->anio=$anio[0];
                        } 
                    }          
                        
                }
               
            }
            $datosCoa=[];
            if($datos[0]->estado=='Coactivado'){
                $datosCoact=InfoCoa::with('data')
                ->where('id_info_notifica',$id)
                ->select('*')
                ->selectRaw("CURRENT_DATE - DATE(fecha_registra) AS dias_transcurridos")
                ->get();

                foreach($datosCoact as $key=> $data2){
                                  
                    $usuarioRegistra=DB::connection('mysql')->table('users as u')
                    ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                    ->where('u.id',$data2->id_usuario_registra)
                    ->select('p.nombres','p.apellidos','p.cedula')
                    ->first();
                    $datosCoact[$key]->profesional=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;
                   
                    foreach($data2->data as $key2=> $info){
                        if(is_null($info->id_liquidacion)){
                            $anio=$anio=explode("-",$info->num_titulo);  
                            if($anio[0]==date('Y')){
                                $contr=DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
                                ->whereIn('tp.TitPr_Estado',['E','N'])
                                ->where('Titpr_RUC_CI',$cedula)
                                ->where('TitPr_NumTitulo',$info->num_titulo)
                                ->select('Pre_CodigoCatastral','TitPr_Nombres')
                                ->first();
                                
                                $data2->data[$key2]->clave_cat=$contr->Pre_CodigoCatastral;
                                $data2->data[$key2]->contrib=$contr->TitPr_Nombres;
                                $data2->data[$key2]->anio=$anio[0];
                            }else{
                                $contr=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
                                ->whereIn('cv.CarVe_Estado',['E'])
                                ->where('CarVe_NumTitulo',$info->num_titulo)
                                ->select('Pre_CodigoCatastral','CarVe_Nombres','CarVe_NumTitulo')
                                ->first();
                            
                                $data2->data[$key2]->clave_cat=$contr->Pre_CodigoCatastral;
                                $data2->data[$key2]->contrib=$contr->CarVe_Nombres;
                                $data2->data[$key2]->anio=$anio[0];
                            }          
                        } 
                    }
                   
                }
                return ["resultado"=>$datos[0],"datosCoa"=>$datosCoact[0], "error"=>false];

            }

            return ["resultado"=>$datos[0],"datosCoa"=>$datosCoa, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage(). "Linea => ".$e->getLine() , "error"=>true];
        }
    }

    public function detalleNotificacionProceso($id,){
        try{
            
            $datos=$this->consultarTitulosUrb($id);
            if($datos['error']==true){
                return ["mensaje"=>$datos['mensaje'], "error"=>true];
            }
           
            $datos1=$this->consultarTitulos($id);
            if($datos1['error']==true){
                return ["mensaje"=>$datos['mensaje'], "error"=>true];
            }
        
            $todo = $datos["resultado"]->merge($datos1["resultado"]);
                          
            // return ["resultado"=>$datos['resultado'], "total"=>$datos['total_valor'], "error"=>false];
            $total1 = (float) str_replace(',', '', $datos['total_valor']);
            $total2 = (float) str_replace(',', '', $datos1['total_valor']);

            $total = $total1 + $total2;
            return ["resultado"=>$todo, "total"=>$total, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function consultarTitulosUrb($id){
        try {
            
            /*foreach($actualizar as $info){
                $liquidaciones=PsqlLiquidacion::where('id', $info->id_liquidacion)
                ->where('estado_liquidacion',2)
                ->select('id_liquidacion','id') 
                ->first();
               
                // validar que exista
                if ($liquidaciones) {
                    $info->num_titulo = $liquidaciones->id_liquidacion;
                    $info->save();
                }
            }*/
         
            $liquidaciones=DataNotifica::where('id_info_notifica',$id)
            ->pluck('num_titulo')
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
            'sgm_app.cat_predio.coordx',
            'sgm_app.cat_predio.coordy',
            'sgm_app.cat_ente.nombres',
            'sgm_app.cat_ente.apellidos',
            DB::raw("CONCAT(sgm_app.cat_ente.apellidos, ' ', sgm_app.cat_ente.nombres) AS nombre_contr1"),
            'sgm_app.cat_ente.ci_ruc',
            'sgm_app.cat_ente.id as idpersona',
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
                                                ROUND(
                                                    COALESCE((
                                                        SELECT SUM(d.valor)
                                                        FROM sgm_financiero.ren_det_liquidacion d
                                                        WHERE d.liquidacion = ren_liquidacion.id
                                                        AND d.rubro = 2
                                                    ),0)
                                                    * (
                                                        SELECT porcentaje
                                                        FROM sgm_app.ctlg_descuento_emision
                                                        WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                                        AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                                        LIMIT 1
                                                    ) / 100
                                                , 2) * (-1)
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
                                                ROUND(COALESCE((
                                                    SELECT SUM(d.valor)
                                                    FROM sgm_financiero.ren_det_liquidacion d
                                                    WHERE d.liquidacion = ren_liquidacion.id
                                                    AND d.rubro = 2
                                                ),0) * 0.10, 2)
                                            WHEN ren_liquidacion.anio < EXTRACT(YEAR FROM NOW()) THEN
                                                ROUND(COALESCE((
                                                    SELECT SUM(d.valor)
                                                    FROM sgm_financiero.ren_det_liquidacion d
                                                    WHERE d.liquidacion = ren_liquidacion.id
                                                    AND d.rubro = 2
                                                ),0) * 0.10, 2)
                                            ELSE 0
                                        END
                                    ), 0)

                                ), 2)
                        ) AS total_pagar
                    '),
                     DB::raw("
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
                            WHERE d.liquidacion = ren_liquidacion.id
                             
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
            
           
            ->whereIn('sgm_financiero.ren_liquidacion.id_liquidacion',$liquidaciones)
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
           
            return ["resultado"=>$liquidacionUrbana, 
                    "total_valor"=>number_format($total_valor,2),
                    "liquidaciones"=>$liquidaciones,
                    "id"=>$id,
                    "error"=>false,
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

     public function consultarTitulos($id){
        try {

            $liquidaciones=DataNotifica::where('id_info_notifica',$id)
           ->pluck('num_titulo')
            ->toArray();

            
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave','cv.CarVe_FechaEmision as fecha_emi','cv.CarVe_NumTitulo as num_titulo','cv.CarVe_CI as num_ident','cv.CarVe_Estado','cv.CarVe_Nombres as nombre_per','cv.CarVe_ValorEmitido as valor_emitido','cv.CarVe_TasaAdministrativa as tasa','CarVe_Calle as direcc_cont','cv.Carve_Recargo as recargo','cv.Carve_Descuento as descuento')
            ->where('P.Pre_Tipo','Rural')
            ->whereIN('cv.CarVe_NumTitulo',$liquidaciones)
            ->whereIn('cv.CarVe_Estado',['E']) //E=Emitidos, N=Nueva Emision
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
                    $valor=number_format($valor,2);
                    $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                }else{
                    $cero=0;
                    $valor=number_format($cero,2);
                    $liquidacionRural[$key]->porcentaje_intereses=number_format($cero,2);
                }
                $liquidacionRural[$key]->subtotal_emi=$subtotal;
                
                $liquidacionRural[$key]->intereses=$valor;
                $liquidacionRural[$key]->interes=$valor;

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
            ->where('P.Pre_Tipo','Rural')
            ->whereIn('tp.TitPr_NumTitulo',$liquidaciones)            
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
                    $liquidacionActual[$key]->interes=$valor;

                    $total_pago=($valor +$data->valor_emitido + $data->recargo) - $data->descuento;
                    $liquidacionActual[$key]->total_pagar=number_format($total_pago,2);
                }else{
                    $cero=0;
                    $liquidacionActual[$key]->porcentaje_intereses=number_format($cero,2);
                    $liquidacionActual[$key]->intereses=number_format($cero,2);
                    $liquidacionActual[$key]->interes=number_format($cero,2);

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
           
            return ["resultado"=>$resultado, 
                    "total_valor"=>number_format($total_valor,2),
                    "liquidaciones"=>$liquidaciones,
                    "error"=>false,
                    
            ];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tardexx ".$e->getLine(), "error"=>true];
        }
    }

    public function subirArchivoFirmado(Request $request){
        
        try{
            $noti=InfoNotifica::find($request->idnoti);
            $nombre_geneado=$noti->documento;
            $solo_nombre=explode(".",$nombre_geneado);
            $nombre_firmado=$solo_nombre[0]."_firmado";
            $documento=$request->archivo;
            $extension = pathinfo($documento->getClientOriginalName(), PATHINFO_EXTENSION);
            if(!is_null($documento)){
                \Storage::disk('disksCoactiva')->put($nombre_firmado . "." . $extension, \File::get($documento));
                $noti->documento_subido=$nombre_firmado.".".$extension;
                $noti->save();

                return ["mensaje"=>"Documento subido exitosamente ", "error"=>false, "archivo"=>$noti->documento_subido];
            }
         } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    
    }

    public function subirArchivoFirmadoCoact(Request $request){
        
        try{
            $coa=InfoCoa::find($request->idcoa);
            $nombre_geneado=$coa->documento;
            $solo_nombre=explode(".",$nombre_geneado);
            $nombre_firmado=$solo_nombre[0]."_firmado";
            $documento=$request->archivo;
            $extension = pathinfo($documento->getClientOriginalName(), PATHINFO_EXTENSION);
            if(!is_null($documento)){
                \Storage::disk('disksCoactiva')->put($nombre_firmado . "." . $extension, \File::get($documento));
                $coa->documento_subido=$nombre_firmado.".".$extension;
                $coa->save();

                return ["mensaje"=>"Documento subido exitosamente.. ", "error"=>false, "archivo"=>$coa->documento_subido];
            }
         } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    
    }

    public function iniciaProcesoCoactiva($id){

        DB::connection('pgsql')->beginTransaction();

        try{
            set_time_limit(0);
            ini_set("memory_limit",-1);
            ini_set('max_execution_time', 0);

            //*****VALIDACIONES RESPECTIVAS***********************************************/

            $noti=InfoNotifica::where('estado','Notificado')
            ->where('id',$id)
            ->first();
            if(is_null($noti)){
                return ["mensaje"=>"La notificacion ya no esta disponible para coactivar", "error"=>true];
            }

            $notiConvenio=Convenio::where('id_info_notifica',$id)
            ->where('estado','Activo')
            ->first();
            if(!is_null($notiConvenio)){
                return ["mensaje"=>"La notificacion voluntaria tiene un convenio activo", "error"=>true];
            }

            $pagoConvenio=Pago::where('id_info_notifica',$id)
            ->where('estado','Activo')
            ->first();
            if(!is_null($pagoConvenio)){
                return ["mensaje"=>"La notificacion voluntaria tiene un pago activo", "error"=>true];
            }
           
            $guardaCoa=new InfoCoa();
            $guardaCoa->id_info_notifica=$id;
            $guardaCoa->save();
            
                
            // TITULOS URBANOS
            $consulta=$this->consultarTitulosUrb($id);
            if($consulta['error']==true){
                DB::connection('pgsql')->rollBack();
                return ["mensaje"=>$consulta['mensaje'], "error"=>true];
                
            }
            
            // TITULOS RURALES
            $consulta1=$this->consultarTitulos($id);
            if($consulta1['error']==true){
                DB::connection('pgsql')->rollBack();
                return ["mensaje"=>$consulta1['mensaje'], "error"=>true];
                
            }

            //UNIMOS AMBAS DATA (URBANA Y RURAL)
            $todo = $consulta["resultado"]->merge($consulta1["resultado"]);
            
            $listado_final=[];

            $subtotal_emi=0;
            $intereses_emi=0;
            $descuento_emi=0;
            $recargo_emi=0;
            $total_emi=0;
            
            //RECORREMOS LA DATA PARA GUARDAR LOS DETALLES DE LA DATA
            foreach ($todo as $key => $item){ 
                
                $subtotal  = $item->subtotal_emi ? str_replace(',', '', $item->subtotal_emi) : 0;
                $interes   = $item->intereses ? str_replace(',', '', $item->intereses) : 0;
                $recargo   = $item->recargo ? str_replace(',', '', $item->recargo) : 0;
                $descuento = $item->descuento ? str_replace(',', '', $item->descuento) : 0;
                $total     = $item->total_pagar ? str_replace(',', '', $item->total_pagar) : 0;

                $guardaData= new DataCoa();
                $guardaData->id_info_coact=$guardaCoa->id;
                if(isset($item->id)){$guardaData->id_liquidacion=$item->id;}
                if(isset($item->num_titulo)){$guardaData->num_titulo=$item->num_titulo;}
                
                $guardaData->subtotal=$subtotal;
                $guardaData->interes=$interes;
                $guardaData->recargo=$recargo;
                $guardaData->descuento=$descuento;
                $guardaData->total=$total;
                $guardaData->estado='A';
                $guardaData->save();

                $subtotal_emi=$subtotal_emi + $guardaData->subtotal;
            
                $intereses_emi=$intereses_emi + $guardaData->interes;
                
                $recargo_emi=$intereses_emi + $guardaData->recargo;
                
                $descuento_emi=$descuento_emi + $guardaData->descuento;
                
                $total_emi=$total_emi + $guardaData->total; 

                $anios[] = $item->anio;  
                if(isset($item->num_predio)){    // SI VIENE NUM_PREDIO ES URBANA Y AGRUPAMOS POR ESO (MATRICULA INMOBILIARIA)         
                    if(!isset($listado_final[$item->num_predio])) {
                        $listado_final[$item->num_predio]=array($item);
                
                    }else{
                        array_push($listado_final[$item->num_predio], $item);
                    }
                }else{ // CASO CONTRARIO ES RURAL AGRUPAMOS POR CLAVE CATASTRAL
                    if(!isset($listado_final[$item->clave])) {
                        $listado_final[$item->clave]=array($item);
            
                    }else{
                        array_push($listado_final[$item->clave], $item);
                    }
                }

                $nombre_persona=$item->nombre_per;
                $direcc_cont=$item->direcc_cont;
                $ci_ruc=$noti->num_ident;
                if(is_null($item->nombre_per)){
                    $nombre_persona=$item->nombre_contr1;
                }
            }            
            
            //OBTENEMOS EL TITULO MENOR EN AÑOS ASI COMO EL MAYOR EN AÑOS
            $anio_min = min($anios);
            $anio_max = max($anios);

            $rango='DESDE EL '.($anio_min . ' HASTA EL EJERCICIO FISCAL ' . $anio_max);

            //DATA PARA EL PDF DE LOS FUNCIONARIOS 
            $funcionarios=DB::connection('pgsql')
            ->table('sgm_coactiva.parametro_coactiva')
            ->selectRaw("
                MAX(CASE WHEN codigo = 'TESO' THEN valor END) AS tesorera,
                MAX(CASE WHEN codigo = 'JUEZ_COACT' THEN valor END) AS juez_coactiva,
                MAX(CASE WHEN codigo = 'SECRETARIO' THEN valor END) AS secretario
            ")
            ->whereIn('codigo', ['TESO','JUEZ_COACT','SECRETARIO'])
            ->where('estado','A')
            ->first();

            $datosUrbano = collect();
            $datosRural  = collect();

            // OBTENEMOS LOS NUM_TITULOS DE LA DATA NOTIFICADA PARA BUSCAR LOS TITULOS PREDIALES DE LA BD URBANA
            $dataNoti=DataNotifica::where('id_info_notifica',$noti->id)
                ->whereNotNull('id_liquidacion')
                ->pluck('num_titulo')
                ->toArray();

            if(count($dataNoti)> 0){
                $generarTitulo=$this->tituloCreditoUrb($dataNoti);
                if($generarTitulo['error']==true){
                    DB::connection('pgsql')->rollBack();
                    return ["mensaje"=>$generarTitulo['mensaje'], "error"=>true];
                }
                $datosUrbano = collect($generarTitulo['data']['DatosLiquidaciones'] ?? []);
               

            }
           
            // OBTENEMOS LOS NUM_TITULOS DE LA DATA NOTIFICADA PARA BUSCAR LOS TITULOS PREDIALES DE LA BD RURAL
            $dataNoti=DataNotifica::where('id_info_notifica',$noti->id)
            ->whereNull('id_liquidacion')
            ->pluck('num_titulo')
            ->toArray();

            if(count($dataNoti)> 0){    
                $generarTitulo1=$this->tituloCreditoRural($dataNoti);
            
                if($generarTitulo1['error']==true){
                    DB::connection('pgsql')->rollBack();
                    return ["mensaje"=>$generarTitulo1['mensaje'], "error"=>true];
                }
                $datosRural = collect($generarTitulo1['data']['DatosLiquidaciones'] ?? []);
            }
            
            // UNIMOS AMBAS DATA
            $todo_cons = $datosUrbano->merge($datosRural);            
            
            //SECUENCIAL PARA LOS INFORMES
            $ultimo_sec = InfoCoa::whereYear('fecha_registra', date('Y'))
                ->whereNotNull('num_proceso')
                ->orderByDesc('num_proceso')
                ->first();
             
            if (is_null($ultimo_sec)) {

                $secuencial = Secuencial::where('descripcion', 'Proceso')
                    ->where('anio', date('Y'))
                    ->where('estado', 'A')
                    ->lockForUpdate()
                    ->first();

                if (is_null($secuencial)) {

                    $num_proceso = 1;

                    Secuencial::create([
                        'secuencia' => 1,
                        'descripcion' => 'Proceso',
                        'estado' => 'A',
                        'anio' => date('Y')
                    ]);

                } else {

                    $secuencial->increment('secuencia');
                    $num_proceso = $secuencial->secuencia;
                }

            } else {

                $num_proceso = $ultimo_sec->num_proceso + 1;
            }
            
            // PROCEDEMOS AL REGISTRO EN LA BD
            $nombrePDF="ProcesoCoactiva".date('YmdHis').".pdf";  
            $guardaCoa->estado_proceso=1;
            $guardaCoa->id_usuario_registra=auth()->user()->id;
            $guardaCoa->fecha_registra=date('Y-m-d H:i:s');
            $guardaCoa->subtotal_pago_inmediato=number_format(($subtotal_emi),2,'.', '');
            $guardaCoa->interes_pago_inmediato=number_format(($intereses_emi),2,'.', '');
            $guardaCoa->descuento_pago_inmediato=number_format(($descuento_emi),2,'.', '');
            $guardaCoa->recargo_pago_inmediato=number_format(($recargo_emi),2,'.', '');
            $guardaCoa->valor_pago_inmediato=number_format(($total_emi),2,'.', '');
            $guardaCoa->documento=$nombrePDF;
            $guardaCoa->estado_pago='Debe';
            $guardaCoa->num_proceso=$num_proceso;
            $guardaCoa->save();

            $fecha_hoy=date('Y-m-d');
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
            $fecha_timestamp = strtotime($fecha_hoy);    
            $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);
            
            // CREAMOS EL DOCUMENTO RESPECTIVO                             
            $pdf = \PDF::loadView('reportes.procesoCoactiva', [
                'DatosLiquidacion'=>$listado_final,
                "nombre_persona"=>$nombre_persona, 
                "direcc_cont"=>$direcc_cont,
                "ci_ruc"=>$ci_ruc,
                "rango"=>$rango, 
                "funcionarios"=>$funcionarios, 
                'DatosLiquidaciones'=>$todo_cons, 
                'fecha_formateada'=>$fecha_formateada,
                "lugar_predio"=>$noti->predio, 
                "num_proceso"=>$num_proceso, 
                "liquidacionUrb"=>$datosUrbano, 
                "liquidacionRural"=>$datosRural]);           
            $estadoarch = $pdf->stream();

            // LO GUARDAMOS EN EL DISCO
            $disco="disksCoactiva";
            \Storage::disk($disco)->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk($disco)->exists($nombrePDF);
            if($exists_destino){

                $noti->estado='Coactivado';
                $noti->save();
                DB::connection('pgsql')->commit();
                return ["mensaje"=>'Proceso iniciado exitosamente', "error"=>false, "pdf"=>$nombrePDF];
            }else{
                DB::connection('pgsql')->rollBack();
                return [
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ];
            }


        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage(). 'Linea=> '.$e->getLine(), "error"=>true];

        }
    }
    public function obtenerTitulosConvenio($id, $lugar, $noti_proc){
        try{
           
            // Definir variables dinámicas
            $esVoluntario = $noti_proc == "Voluntario";
            $esUrbano = $lugar == "Urbano";

            $campoInfo = $esVoluntario ? 'id_info_notifica' : 'id_info_coact';
            $modeloData = $esVoluntario ? DataNotifica::class : DataCoa::class;
            $campoPluck = $esUrbano ? 'id_liquidacion' : 'num_titulo';

            // Obtener convenio
            $obtenerId = Convenio::where('id', $id)
                ->where('estado', 'Activo')
                ->select($campoInfo)
                ->first();

            if (is_null($obtenerId)) {
                return ["mensaje" => "El convenio ya no se encuentra activo", "error" => true];
            }
            

            // Obtener liquidaciones
            $obtenerLiquidaciones = $modeloData::where($campoInfo, $obtenerId->$campoInfo)
                ->pluck($campoPluck)
                ->toArray();

            $metodo = $lugar == "Urbano" ? 'tituloCreditoUrbAux' : 'tituloCreditoRuralAux';

            $consulta = $this->$metodo($obtenerLiquidaciones);

            if ($consulta['error'] == true) {
                return [
                    "mensaje" => $consulta['mensaje'],
                    "error" => true
                ];
            }

            return['resultado'=>$consulta['data']['DatosLiquidaciones'],"error"=>false];


        }catch(\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function pdfMedidas($id){
        try{
            $noti=InfoNotifica::where('id',$id)
            ->first();

            if($noti->predio=="Urbano"){
                $consulta=$this->consultarTitulosUrb($id);
                if($consulta['error']==true){
                    return ["mensaje"=>$consulta['mensaje'], "error"=>true];
                }
            
                $listado_final=[];

                foreach ($consulta["resultado"] as $key => $item){ 

                    $anios[] = $item->anio;              
                    if(!isset($listado_final[$item->num_predio])) {
                        $listado_final[$item->num_predio]=array($item);
                
                    }else{
                        array_push($listado_final[$item->num_predio], $item);
                    }

                    $nombre_persona=$item->nombre_per;
                    $direcc_cont=$item->direcc_cont;
                    $ci_ruc=$item->ci_ruc;
                    if(is_null($item->nombre_per)){
                        $nombre_persona=$item->nombre_contr1;
                    }
                } 
            
            }else{
                $consulta=$this->consultarTitulos($id);
                if($consulta['error']==true){
                    return ["mensaje"=>$consulta['mensaje'], "error"=>true];
                }
                
                $listado_final=[];

                foreach ($consulta["resultado"] as $key => $item){ 
                   
                    $anios[] = $item->anio;              
                    if(!isset($listado_final[$item->clave])) {
                        $listado_final[$item->clave]=array($item);
                
                    }else{
                        array_push($listado_final[$item->clave], $item);
                    }

                    $nombre_persona=$item->nombre_per;
                    $direcc_cont=$item->direcc_cont;
                    $ci_ruc=$item->num_ident;
                    if(is_null($item->nombre_per)){
                        $nombre_persona=$item->nombre_contr1;
                    }
                } 
            }

            $anio_min = min($anios);
            $anio_max = max($anios);

            $rango='DESDE EL '.($anio_min . ' HASTA EL EJERCICIO FISCAL ' . $anio_max);
        
            $funcionarios=DB::connection('pgsql')
            ->table('sgm_coactiva.parametro_coactiva')
            ->selectRaw("
                MAX(CASE WHEN codigo = 'TESO' THEN valor END) AS tesorera,
                MAX(CASE WHEN codigo = 'JUEZ_COACT' THEN valor END) AS juez_coactiva,
                MAX(CASE WHEN codigo = 'SECRETARIO' THEN valor END) AS secretario
            ")
            ->whereIn('codigo', ['TESO','JUEZ_COACT','SECRETARIO'])
            ->where('estado','A')
            ->first();

            $secr=DB::connection('pgsql')
            ->table('sgm_coactiva.parametro_coactiva')
            ->select('valor2')
            ->where('codigo','SECRETARIO')
            ->where('estado','A')
            ->first();

          
            $nombrePDF="MedidasCoactiva".date('YmdHis').".pdf";                               
            $pdf = \PDF::loadView('reportes.medidasCoact', ['DatosLiquidacion'=>$listado_final,"nombre_persona"=>$nombre_persona, "direcc_cont"=>$direcc_cont, "ci_ruc"=>$ci_ruc, "rango"=>$rango, "funcionarios"=>$funcionarios, "lugar_predio"=>$noti->predio,"secr"=>$secr->valor2]);

            return $pdf->stream($nombrePDF);

        }catch(\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function pdfProcesoCoactiva($id,$lugar){
        try{
            $consulta=$this->consultarTitulosUrb($id);
            if($consulta['error']==true){
                return ["mensaje"=>$consulta['mensaje'], "error"=>true];
            }

            $listado_final=[];
            
            foreach ($consulta["resultado"] as $key => $item){   
                $anios[] = $item->anio;              
                if(!isset($listado_final[$item->num_predio])) {
                    $listado_final[$item->num_predio]=array($item);
            
                }else{
                    array_push($listado_final[$item->num_predio], $item);
                }

                $nombre_persona=$item->nombre_per;
                $direcc_cont=$item->direcc_cont;
                $ci_ruc=$item->ci_ruc;
                if(is_null($item->nombre_per)){
                    $nombre_persona=$item->nombre_contr1;
                }
            } 

            $consulta=$this->consultarTitulos($id);
            if($consulta['error']==true){
                return ["mensaje"=>$consulta['mensaje'], "error"=>true];
            }
                

            $anio_min = min($anios);
            $anio_max = max($anios);

            $rango='DESDE EL '.($anio_min . ' HASTA EL EJERCICIO FISCAL ' . $anio_max);
        
            $funcionarios=DB::connection('pgsql')
            ->table('sgm_coactiva.parametro_coactiva')
            ->selectRaw("
                MAX(CASE WHEN codigo = 'TESO' THEN valor END) AS tesorera,
                MAX(CASE WHEN codigo = 'JUEZ_COACT' THEN valor END) AS juez_coactiva,
                MAX(CASE WHEN codigo = 'SECRETARIO' THEN valor END) AS secretario
            ")
            ->whereIn('codigo', ['TESO','JUEZ_COACT','SECRETARIO'])
            ->where('estado','A')
            ->first();

            $generarTitulo=$this->tituloCreditoUrb($consulta["liquidaciones"]);
            if($generarTitulo['error']==true){
                return ["mensaje"=>$generarTitulo['mensaje'], "error"=>true];
            }
            
            $nombrePDF="ProcesoCoactiva".date('YmdHis').".pdf";                               
            $pdf = \PDF::loadView('reportes.procesoCoactiva', ['DatosLiquidacion'=>$listado_final,"ubicacion"=>$lugar,"nombre_persona"=>$nombre_persona, "direcc_cont"=>$direcc_cont, "ci_ruc"=>$ci_ruc, "rango"=>$rango, "funcionarios"=>$funcionarios, 'DatosLiquidaciones'=>$generarTitulo['data']['DatosLiquidaciones'], 'fecha_formateada'=>$generarTitulo['data']['fecha_formateada']]);

            return $pdf->stream($nombrePDF);
        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function tituloCreditoUrbAux($idliquidaciones){
        try{
            $dataArray = array();
            foreach($idliquidaciones as $clave => $valor){
                $liquidacion = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as liq')

                // ->leftJoin('sgm_app.cat_ente as en', 'en.id', '=', 'liq.comprador')
                ->leftJoin('sgm_app.cat_predio as pre', 'pre.id', '=', 'liq.predio')
                ->join('sgm_app.cat_predio_propietario as pp', 'pp.predio', '=', 'pre.id')           
                ->leftJoin('sgm_app.cat_ente as en', 'pp.ente', '=', 'en.id')
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
                    'liq.anio',
                    'en.direccion',
                    'en.ci_ruc as cedula',
                    'liq.estado_liquidacion',
                    DB::raw("
                        CASE
                            WHEN liq.comprador IS NULL THEN liq.nombre_comprador
                            ELSE CASE en.es_persona
                                WHEN TRUE THEN COALESCE(en.apellidos, '') || ' ' || COALESCE(en.nombres, '')
                                ELSE COALESCE(en.razon_social, '')
                            END
                        END AS nombres
                    "),
                   
                    DB::raw("cdla.nombre || ' MZ: ' || pre.urb_mz || ' SL: ' || pre.urb_solarnew AS direccion1"),
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
                            WHERE d.liquidacion = liq.id
                            AND d.rubro = 2
                        ) AS valor_complemento
                    "),

                    DB::raw("
                            (
                                SELECT
                                    CASE
                                        WHEN (liq.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
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
                                WHERE d.liquidacion = liq.id AND d.rubro = 2
                                LIMIT 1
                            ) AS desc
                        "),
                        
                        DB::raw("
                            (
                                SELECT
                                    CASE
                                        WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                            ROUND((liq.saldo * (
                                                SELECT ROUND((porcentaje / 100), 2) 
                                                FROM sgm_financiero.ren_intereses i
                                                WHERE i.anio = liq.anio
                                                LIMIT 1
                                            )), 2)
                                        ELSE
                                            0.00
                                        END
                                FROM sgm_financiero.ren_det_liquidacion d
                                WHERE d.liquidacion = liq.id 
                               
                                LIMIT 1
                            ) AS interes
                        "),

                        DB::raw("
                            (
                                SELECT
                                    CASE
                                        WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                            ROUND((d.valor * 0.10), 2)
                                        WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                            ROUND((d.valor * 0.10), 2)
                                        ELSE
                                            0.00
                                    END
                                FROM sgm_financiero.ren_det_liquidacion d
                                WHERE d.liquidacion = liq.id AND d.rubro = 2
                                LIMIT 1
                            ) AS recargos
                        "),

                         DB::raw('
                        (
                            SELECT
                                ROUND((
                                    COALESCE(liq.saldo, 0)

                                    +
                                    COALESCE((
                                        CASE
                                            WHEN (liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) < 7) THEN
                                                ROUND(
                                                    COALESCE((
                                                        SELECT SUM(d.valor)
                                                        FROM sgm_financiero.ren_det_liquidacion d
                                                        WHERE d.liquidacion = liq.id
                                                        AND d.rubro = 2
                                                    ),0)
                                                    * (
                                                        SELECT porcentaje
                                                        FROM sgm_app.ctlg_descuento_emision
                                                        WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                                        AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                                        LIMIT 1
                                                    ) / 100
                                                , 2) * (-1)
                                            ELSE 0
                                        END
                                    ), 0)

                                    +
                                    COALESCE((
                                        CASE
                                            WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN
                                                ROUND((liq.saldo * (
                                                    SELECT ROUND((porcentaje / 100), 2)
                                                    FROM sgm_financiero.ren_intereses i
                                                    WHERE i.anio = liq.anio
                                                    LIMIT 1
                                                )), 2)
                                            ELSE 0
                                        END
                                    ), 0)

                                    +
                                    COALESCE((
                                        CASE
                                            WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                                ROUND(COALESCE((
                                                    SELECT SUM(d.valor)
                                                    FROM sgm_financiero.ren_det_liquidacion d
                                                    WHERE d.liquidacion = liq.id
                                                    AND d.rubro = 2
                                                ),0) * 0.10, 2)
                                            WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                                ROUND(COALESCE((
                                                    SELECT SUM(d.valor)
                                                    FROM sgm_financiero.ren_det_liquidacion d
                                                    WHERE d.liquidacion = liq.id
                                                    AND d.rubro = 2
                                                ),0) * 0.10, 2)
                                            ELSE 0
                                        END
                                    ), 0)

                                ), 2)
                        ) AS total_complemento
                    '),

                    'liq.id_liquidacion'
                )
                ->where('liq.id', $valor)
                ->where('pp.estado','A')
                ->whereIn('liq.estado_liquidacion',[1,2])
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
                'DatosLiquidaciones' => $dataArray,
                'fecha_formateada'=>$fecha_formateada
            ];

            return ["data"=>$data, "error"=>false];
        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

     public function tituloCreditoUrb($idliquidaciones){
        try{
            
            $dataArray = array();
            foreach($idliquidaciones as $clave => $valor){
                if(!is_null($valor)){
                   
                    $liquidacion = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as liq')

                    // ->leftJoin('sgm_app.cat_ente as en', 'en.id', '=', 'liq.comprador')
                    ->leftJoin('sgm_app.cat_predio as pre', 'pre.id', '=', 'liq.predio')
                    ->join('sgm_app.cat_predio_propietario as pp', 'pp.predio', '=', 'pre.id')           
                    ->leftJoin('sgm_app.cat_ente as en', 'pp.ente', '=', 'en.id')
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
                        'liq.anio',
                        'en.direccion',
                        'en.ci_ruc as cedula',
                        'liq.estado_liquidacion',
                        DB::raw("
                            CASE
                                WHEN liq.comprador IS NULL THEN liq.nombre_comprador
                                ELSE CASE en.es_persona
                                    WHEN TRUE THEN COALESCE(en.apellidos, '') || ' ' || COALESCE(en.nombres, '')
                                    ELSE COALESCE(en.razon_social, '')
                                END
                            END AS nombres
                        "),
                       
                        DB::raw("cdla.nombre || ' MZ: ' || pre.urb_mz || ' SL: ' || pre.urb_solarnew AS direccion1"),
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
                                WHERE d.liquidacion = liq.id
                                AND d.rubro = 2
                            ) AS valor_complemento
                        "),

                        DB::raw("
                                (
                                    SELECT
                                        CASE
                                            WHEN (liq.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
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
                                    WHERE d.liquidacion = liq.id AND d.rubro = 2
                                    LIMIT 1
                                ) AS desc
                            "),
                            
                            DB::raw("
                                (
                                    SELECT
                                        CASE
                                            WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                                ROUND((liq.saldo * (
                                                    SELECT ROUND((porcentaje / 100), 2) 
                                                    FROM sgm_financiero.ren_intereses i
                                                    WHERE i.anio = liq.anio
                                                    LIMIT 1
                                                )), 2)
                                            ELSE
                                                0.00
                                            END
                                    FROM sgm_financiero.ren_det_liquidacion d
                                    WHERE d.liquidacion = liq.id 
                                
                                    LIMIT 1
                                ) AS interes
                            "),

                            DB::raw("
                                (
                                    SELECT
                                        CASE
                                            WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                                ROUND((d.valor * 0.10), 2)
                                            WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                                ROUND((d.valor * 0.10), 2)
                                            ELSE
                                                0.00
                                        END
                                    FROM sgm_financiero.ren_det_liquidacion d
                                    WHERE d.liquidacion = liq.id AND d.rubro = 2
                                    LIMIT 1
                                ) AS recargos
                            "),

                            DB::raw('
                            (
                                SELECT
                                    ROUND((
                                        COALESCE(liq.saldo, 0)

                                        +
                                        COALESCE((
                                            CASE
                                                WHEN (liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) < 7) THEN
                                                    ROUND(
                                                        COALESCE((
                                                            SELECT SUM(d.valor)
                                                            FROM sgm_financiero.ren_det_liquidacion d
                                                            WHERE d.liquidacion = liq.id
                                                            AND d.rubro = 2
                                                        ),0)
                                                        * (
                                                            SELECT porcentaje
                                                            FROM sgm_app.ctlg_descuento_emision
                                                            WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                                            AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                                            LIMIT 1
                                                        ) / 100
                                                    , 2) * (-1)
                                                ELSE 0
                                            END
                                        ), 0)

                                        +
                                        COALESCE((
                                            CASE
                                                WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN
                                                    ROUND((liq.saldo * (
                                                        SELECT ROUND((porcentaje / 100), 2)
                                                        FROM sgm_financiero.ren_intereses i
                                                        WHERE i.anio = liq.anio
                                                        LIMIT 1
                                                    )), 2)
                                                ELSE 0
                                            END
                                        ), 0)

                                        +
                                        COALESCE((
                                            CASE
                                                WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                                    ROUND(COALESCE((
                                                        SELECT SUM(d.valor)
                                                        FROM sgm_financiero.ren_det_liquidacion d
                                                        WHERE d.liquidacion = liq.id
                                                        AND d.rubro = 2
                                                    ),0) * 0.10, 2)
                                                WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                                    ROUND(COALESCE((
                                                        SELECT SUM(d.valor)
                                                        FROM sgm_financiero.ren_det_liquidacion d
                                                        WHERE d.liquidacion = liq.id
                                                        AND d.rubro = 2
                                                    ),0) * 0.10, 2)
                                                ELSE 0
                                            END
                                        ), 0)

                                    ), 2)
                            ) AS total_complemento
                        '),

                        'liq.id_liquidacion'
                    )
                
                    ->where('liq.id_liquidacion', $valor)
                    ->where('pp.estado','A')
                    ->whereIn('liq.estado_liquidacion',[2])
                    ->get();

                    //dd($liquidacion[0]->id);
                
                    $fecha_hoy=date('Y-m-d');
                    setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
                    $fecha_timestamp = strtotime($fecha_hoy);    
                    $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);
            

                    $rubros = DB::connection('pgsql')->table('sgm_financiero.ren_det_liquidacion as rdl')
                                                         ->join('sgm_financiero.ren_rubros_liquidacion as rrl', 'rdl.rubro', '=', 'rrl.id')
                                                         ->select('rdl.id', 'rdl.liquidacion', 'rdl.rubro', 'rdl.valor', 'rdl.estado', 'rrl.descripcion')
                                                        // ->where('rdl.liquidacion', $valor)
                                                        ->where('rdl.liquidacion', $liquidacion[0]->id)

                                                         ->get();
                                                    
                    $liquidacion['rubros'] = $rubros;

                    array_push($dataArray, $liquidacion);
                }
            }
            // dd($dataArray);
            $data = [
                'title' => 'Reporte de liquidacion',
                'date' => date('m/d/Y'),
                'DatosLiquidaciones' => $dataArray,
                'fecha_formateada'=>$fecha_formateada
            ];

            return ["data"=>$data, "error"=>false];
        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function tituloCreditoRural($idliquidaciones){
        try{
            $dataArray = array();
            
            $existe=0;
            foreach($idliquidaciones as $clave => $valor_num){
               
                
                $solo_anio=explode("-", $valor_num);
                if (date('Y') == (int)$solo_anio[0]) {
                    $existe=1;
                    $liquidacionActual=\DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
                    ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'tp.Titpr_RUC_CI')
                    ->Join('PREDIO as P', 'P.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
                    ->select('tp.Pre_CodigoCatastral',
                    'tp.TitPr_FechaEmision as CarVe_FechaEmision',
                    'tp.TitPr_NumTitulo as CarVe_NumTitulo',
                    'tp.Titpr_RUC_CI as CarVe_CI',
                    'tp.TitPr_Estado as CarVe_Estado',
                    'c.Ciu_Apellidos',
                    'c.Ciu_Nombres',
                    'P.Pre_NombrePredio',
                    'tp.TitPr_ValTotalTerrPredio as CarVe_ValTotalTerrPredio',
                    'tp.TitPr_ValTotalEdifPredio as CarVe_ValTotalEdifPredio',
                    'tp.TitPr_ValOtrasInver as CarVe_ValOtrasInver',
                    'tp.TitPr_ValComerPredio as CarVe_ValComerPredio',
                    'tp.TitPr_RebajaHipotec as CarVe_RebajaHipotec',
                    'tp.TitPr_BaseImponible as CarVe_BaseImponible',
                    'tp.TitPr_IPU as CarVe_IPU',
                    'tp.TitPr_TasaAdministrativa as CarVe_TasaAdministrativa',
                    'tp.TitPr_Bomberos as CarVe_Bomberos', 
                    'tp.TitPr_ValorEmitido as CarVe_ValorEmitido',              
                    'tp.TitPr_DireccionCont as Pro_DireccionDomicilio',
                    'tp.TitPr_Recargo as recargo',
                    'P.Ubi_Codigo',)
                    ->where('tp.TitPr_NumTitulo', '=', $valor_num)            
                    ->whereIn('tp.TitPr_Estado',['E','N'])
                    ->orderby('TitPr_NumTitulo','asc')
                    ->get();

                    foreach($liquidacionActual as $key=> $data){
                        $subtotal=0;
                        $subtotal=number_format($data->CarVe_ValorEmitido,2);
                        $valor=0;
                        $anio=explode("-",$data->CarVe_NumTitulo);
                        $consultaInteresMora=\DB::connection('sqlsrv')->table('INTERES_MORA as im')
                        ->where('IntMo_Año',$anio)
                        ->select('IntMo_Valor')
                        ->first();
                        
                        if(!is_null($consultaInteresMora)){
                            $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->CarVe_ValorEmitido + $data->recargo - $data->CarVe_TasaAdministrativa));
                            
                            $valor=number_format($valor,2);

                            $liquidacionActual[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                            $liquidacionActual[$key]->intereses=$valor;

                            $total_pago=$valor +$data->CarVe_ValorEmitido;
                            $liquidacionActual[$key]->total_pagar=number_format($total_pago,2);
                        }else{
                            $cero=0;
                            $liquidacionActual[$key]->porcentaje_intereses=number_format($cero,2);
                            $liquidacionActual[$key]->intereses=number_format($cero,2);

                            $total_pago=$valor +$data->CarVe_ValorEmitido;
                            $liquidacionActual[$key]->total_pagar=number_format($total_pago,2);
                        }

                        $sitioBarrio=\DB::connection('sqlsrv')
                        ->table('UBICACION as hijo')
                        ->join('UBICACION as padre', 'padre.Ubi_Codigo', '=', 'hijo.Ubi_CodigoPadre')
                        ->where('hijo.Ubi_Codigo', $data->Ubi_Codigo)
                        ->value('padre.Ubi_Descripcion');
                        $liquidacionActual[$key]->nombre_sitio=$sitioBarrio;
                        
                    }
                    array_push($dataArray, $liquidacionActual);
                   
                }else{
                    $liquidacionRural=\DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
                    ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
                    ->leftJoin('PROPIETARIO as pr', 'pr.Ciu_Cedula', '=', 'cv.CarVe_CI')
                    ->leftJoin('PREDIO as P', 'P.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
                    ->select('cv.Pre_CodigoCatastral',
                    'cv.CarVe_FechaEmision',
                    'cv.CarVe_NumTitulo',
                    'cv.CarVe_CI',
                    'cv.CarVe_Estado',
                    'c.Ciu_Apellidos',
                    'c.Ciu_Nombres',
                    'P.Pre_NombrePredio',
                    'cv.CarVe_ValTotalTerrPredio',
                    'cv.CarVe_ValTotalEdifPredio',
                    'cv.CarVe_ValOtrasInver',
                    'cv.CarVe_ValComerPredio',
                    'cv.CarVe_RebajaHipotec',
                    'cv.CarVe_BaseImponible',
                    'cv.CarVe_IPU',
                    'cv.CarVe_TasaAdministrativa',
                    'cv.CarVe_Bomberos'
                    ,'cv.CarVe_ValorEmitido',
                    'cv.CarVe_direccPropietario as Pro_DireccionDomicilio',
                    'cv.Carve_Recargo as recargo',
                    'P.Ubi_Codigo',)
                    ->where('CarVe_NumTitulo', '=', $valor_num)
                    ->get();
                   
                    //volver
                    $mes_Actual=date('m');
           
                    $aplica_remision=0;
                    if($mes_Actual<7){
                        $aplica_remision=1;
                    }
                    $aplica_remision=0;
                    foreach($liquidacionRural as $key=> $data){
                        $anio=explode("-",$data->CarVe_NumTitulo);
                        $valor=0;
                        if($aplica_remision==0){
                            $consultaInteresMora=\DB::connection('sqlsrv')->table('INTERES_MORA as im')
                            ->where('IntMo_Año',$anio)
                            ->select('IntMo_Valor')
                            ->first();
                            if(!is_null($consultaInteresMora)){
                                
                                $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->CarVe_ValorEmitido +$data->recargo - $data->CarVe_TasaAdministrativa));
                            
                                $valor=number_format($valor,2);

                                $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                                
                            }else{
                                $cero=0;
                                $liquidacionRural[$key]->porcentaje_intereses=number_format($cero,2);
                            }
                        }else  {
                            $cero=0;
                            $valor=number_format($cero,2);
                            $liquidacionRural[$key]->porcentaje_intereses=number_format($cero,2);
                            
                        }  
                        
                        $liquidacionRural[$key]->intereses=$valor;
                        $total_pago=$valor +$data->CarVe_ValorEmitido;
                        $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);

                        $sitioBarrio=\DB::connection('sqlsrv')
                        ->table('UBICACION as hijo')
                        ->join('UBICACION as padre', 'padre.Ubi_Codigo', '=', 'hijo.Ubi_CodigoPadre')
                        ->where('hijo.Ubi_Codigo', $data->Ubi_Codigo)
                        ->value('padre.Ubi_Descripcion');
                        $liquidacionRural[$key]->nombre_sitio=$sitioBarrio;
                    }
                    array_push($dataArray, $liquidacionRural);
                }

            }
            
            $fecha_hoy=date('Y-m-d');
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
            $fecha_timestamp = strtotime($fecha_hoy);
            $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);
            $data = [
                'title' => 'Reporte de liquidacion',
                'date' => date('m/d/Y'),
                // 'DatosLiquidacion' => $data->resultado,
                'DatosLiquidaciones' => $dataArray,
                'fecha_formateada'=>$fecha_formateada
            ];
            return ["data"=>$data, "error"=>false];
        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

     public function tituloCreditoRuralAux($idliquidaciones){
        try{
            $dataArray = array();
            
            $existe=0;
            foreach($idliquidaciones as $clave => $valor_num){
               
                
                $solo_anio=explode("-", $valor_num);
                if (date('Y') == (int)$solo_anio[0]) {
                    $existe=1;
                    $liquidacionActual=\DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
                    ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'tp.Titpr_RUC_CI')
                    ->Join('PREDIO as P', 'P.Pre_CodigoCatastral', '=', 'tp.Pre_CodigoCatastral')
                    ->select('tp.Pre_CodigoCatastral',
                    'tp.TitPr_FechaEmision as CarVe_FechaEmision',
                    'tp.TitPr_NumTitulo as CarVe_NumTitulo',
                    'tp.Titpr_RUC_CI as CarVe_CI',
                    'tp.TitPr_Estado as CarVe_Estado',
                    'c.Ciu_Apellidos',
                    'c.Ciu_Nombres',
                    'P.Pre_NombrePredio',
                    'tp.TitPr_ValTotalTerrPredio as CarVe_ValTotalTerrPredio',
                    'tp.TitPr_ValTotalEdifPredio as CarVe_ValTotalEdifPredio',
                    'tp.TitPr_ValOtrasInver as CarVe_ValOtrasInver',
                    'tp.TitPr_ValComerPredio as CarVe_ValComerPredio',
                    'tp.TitPr_RebajaHipotec as CarVe_RebajaHipotec',
                    'tp.TitPr_BaseImponible as CarVe_BaseImponible',
                    'tp.TitPr_IPU as CarVe_IPU',
                    'tp.TitPr_TasaAdministrativa as CarVe_TasaAdministrativa',
                    'tp.TitPr_Bomberos as CarVe_Bomberos', 
                    'tp.TitPr_ValorEmitido as CarVe_ValorEmitido',              
                    'tp.TitPr_DireccionCont as Pro_DireccionDomicilio',
                    'tp.TitPr_Recargo as recargo',
                    'P.Ubi_Codigo',)
                    ->where('tp.TitPr_NumTitulo', '=', $valor_num)            
                    ->whereIn('tp.TitPr_Estado',['E','N','C'])
                    ->orderby('TitPr_NumTitulo','asc')
                    ->get();

                    foreach($liquidacionActual as $key=> $data){
                        $subtotal=0;
                        $subtotal=number_format($data->CarVe_ValorEmitido,2);
                        $valor=0;
                        $anio=explode("-",$data->CarVe_NumTitulo);
                        $consultaInteresMora=\DB::connection('sqlsrv')->table('INTERES_MORA as im')
                        ->where('IntMo_Año',$anio)
                        ->select('IntMo_Valor')
                        ->first();
                        
                        if(!is_null($consultaInteresMora)){
                            $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->CarVe_ValorEmitido + $data->recargo - $data->CarVe_TasaAdministrativa));
                            
                            $valor=number_format($valor,2);

                            $liquidacionActual[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                            $liquidacionActual[$key]->intereses=$valor;

                            $total_pago=$valor +$data->CarVe_ValorEmitido;
                            $liquidacionActual[$key]->total_pagar=number_format($total_pago,2);
                        }else{
                            $cero=0;
                            $liquidacionActual[$key]->porcentaje_intereses=number_format($cero,2);
                            $liquidacionActual[$key]->intereses=number_format($cero,2);

                            $total_pago=$valor +$data->CarVe_ValorEmitido;
                            $liquidacionActual[$key]->total_pagar=number_format($total_pago,2);
                        }

                        $sitioBarrio=\DB::connection('sqlsrv')
                        ->table('UBICACION as hijo')
                        ->join('UBICACION as padre', 'padre.Ubi_Codigo', '=', 'hijo.Ubi_CodigoPadre')
                        ->where('hijo.Ubi_Codigo', $data->Ubi_Codigo)
                        ->value('padre.Ubi_Descripcion');
                        $liquidacionActual[$key]->nombre_sitio=$sitioBarrio;
                        
                    }
                    array_push($dataArray, $liquidacionActual);
                   
                }else{
                    $liquidacionRural=\DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
                    ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
                    ->leftJoin('PROPIETARIO as pr', 'pr.Ciu_Cedula', '=', 'cv.CarVe_CI')
                    ->leftJoin('PREDIO as P', 'P.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
                    ->select('cv.Pre_CodigoCatastral',
                    'cv.CarVe_FechaEmision',
                    'cv.CarVe_NumTitulo',
                    'cv.CarVe_CI',
                    'cv.CarVe_Estado',
                    'c.Ciu_Apellidos',
                    'c.Ciu_Nombres',
                    'P.Pre_NombrePredio',
                    'cv.CarVe_ValTotalTerrPredio',
                    'cv.CarVe_ValTotalEdifPredio',
                    'cv.CarVe_ValOtrasInver',
                    'cv.CarVe_ValComerPredio',
                    'cv.CarVe_RebajaHipotec',
                    'cv.CarVe_BaseImponible',
                    'cv.CarVe_IPU',
                    'cv.CarVe_TasaAdministrativa',
                    'cv.CarVe_Bomberos'
                    ,'cv.CarVe_ValorEmitido',
                    'cv.CarVe_direccPropietario as Pro_DireccionDomicilio',
                    'cv.Carve_Recargo as recargo',
                    'P.Ubi_Codigo',)
                    ->where('CarVe_NumTitulo', '=', $valor_num)
                    ->get();
                   
                    //volver
                    $mes_Actual=date('m');
           
                    $aplica_remision=0;
                    if($mes_Actual<7){
                        $aplica_remision=1;
                    }
                    $aplica_remision=0;
                    foreach($liquidacionRural as $key=> $data){
                        $anio=explode("-",$data->CarVe_NumTitulo);
                        $valor=0;
                        if($aplica_remision==0){
                            $consultaInteresMora=\DB::connection('sqlsrv')->table('INTERES_MORA as im')
                            ->where('IntMo_Año',$anio)
                            ->select('IntMo_Valor')
                            ->first();
                            if(!is_null($consultaInteresMora)){
                                
                                $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->CarVe_ValorEmitido +$data->recargo - $data->CarVe_TasaAdministrativa));
                            
                                $valor=number_format($valor,2);

                                $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
                                
                            }else{
                                $cero=0;
                                $liquidacionRural[$key]->porcentaje_intereses=number_format($cero,2);
                            }
                        }else  {
                            $cero=0;
                            $valor=number_format($cero,2);
                            $liquidacionRural[$key]->porcentaje_intereses=number_format($cero,2);
                            
                        }  
                        
                        $liquidacionRural[$key]->intereses=$valor;
                        $total_pago=$valor +$data->CarVe_ValorEmitido;
                        $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);

                        $sitioBarrio=\DB::connection('sqlsrv')
                        ->table('UBICACION as hijo')
                        ->join('UBICACION as padre', 'padre.Ubi_Codigo', '=', 'hijo.Ubi_CodigoPadre')
                        ->where('hijo.Ubi_Codigo', $data->Ubi_Codigo)
                        ->value('padre.Ubi_Descripcion');
                        $liquidacionRural[$key]->nombre_sitio=$sitioBarrio;
                    }
                    array_push($dataArray, $liquidacionRural);
                }

            }
            
            $fecha_hoy=date('Y-m-d');
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
            $fecha_timestamp = strtotime($fecha_hoy);
            $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);
            $data = [
                'title' => 'Reporte de liquidacion',
                'date' => date('m/d/Y'),
                // 'DatosLiquidacion' => $data->resultado,
                'DatosLiquidaciones' => $dataArray,
                'fecha_formateada'=>$fecha_formateada
            ];
            return ["data"=>$data, "error"=>false];
        }catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function tablaConvenioNot($id){
        try{
           
            $datos=Convenio::where('id_info_notifica',$id)
            ->get();
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

   

    public function guardarConvenioNot(Request $request){
        try{

            //***VALIDACIONES DE QUE NO HAYA OTRO CONVENIO O NO ESTE EN PROCESO COACTIVA****** */

            $verifica=Convenio::where('id_info_notifica',$request->idnot_conv)
            ->where('estado','Activo')
            ->first();
            if(!is_null($verifica)){
                return ["mensaje"=>"Ya existe un convenio activo", "error"=>true];
            }

            $esta_coact=InfoCoa::where('id_info_notifica',$request->idnot_conv)
            ->first();
            if(!is_null($esta_coact)){
                return ["mensaje"=>"Ya existe un proceso de coactiva inicializado", "error"=>true];
            }
            
        
            $total_deuda=0;
            
            //******BUSQUEDA DE DEUDAS PREDIALES URBANAS********************************* */

            $dataNoti=DataNotifica::where('id_info_notifica',$request->idnot_conv)
            ->whereNotNull('id_liquidacion') // SI TENGO GUARDADO EL CAMPO ID_LIQUIDACION
            ->pluck('num_titulo')                
            ->toArray();
                
            if(count($dataNoti)> 0){
                $total=$this->tituloCreditoUrb($dataNoti);
            
                foreach($total['data']['DatosLiquidaciones'] as $data){
                    $total_deuda=$total_deuda + $data[0]->total_complemento;
                }
            }
                    
            //******BUSQUEDA DE DEUDAS PREDIALES RURALES********************************* */

            $dataNoti=DataNotifica::where('id_info_notifica',$request->idnot_conv)
            ->whereNull('id_liquidacion') // SI NO REGISTRE EL CAMPO ID_LIQUIDACION
            ->pluck('num_titulo')
            
            ->toArray();
            if(count($dataNoti)> 0){
                $total=$this->tituloCreditoRural($dataNoti);

                foreach($total['data']['DatosLiquidaciones'] as $data){
                    $total = $data[0]->total_pagar ? str_replace(',', '', $data[0]->total_pagar) : 0;
                    $total_deuda=$total_deuda + $total;
                }
            }          

            //*****COMPROBACION DEL QUE ELVALOR NOTIFICADO NO HAYA CAMBIADDO AL INICCIARLIZAR EL PROCESO DE COACTICA */

            $total_deuda = (float) $total_deuda;
            $valorAdeudado = (float) $request->valor_adeudado;
            if(round($total_deuda,2) != round($valorAdeudado,2)){
                return [
                    "mensaje" => "El valor adeudado actual ha cambiado a $" . number_format($total_deuda,2),
                    "error" => true
                ];
            }

            //**********REGISTRO DEL CONVENIO************************************** */

            $guarda=new Convenio();
            $guarda->id_info_notifica=$request->idnot_conv;
            $guarda->valor_adeudado = (float) $request->valor_adeudado; // Convertir a float
            $guarda->cuota_inicial = (float) $request->cuota_inicial; // Convertir a float
            $guarda->numero_cuotas=$request->num_cuotas;
            $guarda->f_inicio=$request->f_ini;
            $guarda->f_fin=$request->f_fin;
            $guarda->usuario_registra=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $guarda->fecha_registra=date('Y-m-d H:i:s');
            $guarda->estado='Activo';
            $guarda->estado_pago='Debe';
            $guarda->valor_cancelado=0;
            $guarda->save();

            $fecha_ini = Carbon::parse($request->fecha_ini);
            for($i=0; $i<$request->num_cuotas; $i++){
                if($i==0){
                    $cuotas=new CuotaConvenio();
                    $cuotas->fecha=$guarda->f_inicio;
                    $cuotas->valor_cuota=(float) $guarda->cuota_inicial ;
                    $cuotas->estado='Pendiente';
                    $cuotas->id_convenio=$guarda->id;
                    $cuotas->cuota_inicial=true;
                    $cuotas->save();
                }
                $fechaCuota = $fecha_ini->copy()->addMonthsNoOverflow($i+1);
                $cuotas=new CuotaConvenio();
                $cuotas->fecha=$fechaCuota;
                $cuotas->valor_cuota=(float) $request->valor_cuotas;
                $cuotas->estado='Pendiente';
                $cuotas->id_convenio=$guarda->id;
                $cuotas->save();
            }

            return ["mensaje"=>"Informacion registrada exitosamente", "error"=>false];
            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage(). " Linea => ".$e->getLine(), "error"=>true];
        }
    }

    public function guardarPagoNot(Request $request){
        try{
            $verifica=Pago::where('id_info_notifica',$request->idnot_pago)
            ->where('estado','Activo')
            ->first();
            if(!is_null($verifica)){
                return ["mensaje"=>"Ya existe un pago activo", "error"=>true];
            }

            $esta_coact=InfoCoa::where('id_info_notifica',$request->idnot_pago)
            ->first();
            if(!is_null($esta_coact)){
                return ["mensaje"=>"Ya existe un proceso de coactiva inicializado", "error"=>true];
            }

            $guarda=new Pago();
            $guarda->id_info_notifica=$request->idnot_pago;
            $guarda->valor_cancelado = (float) $request->valor_cancelado; // Convertir a float
            $guarda->usuario_registra=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $guarda->fecha_registra=date('Y-m-d H:i:s');
            $guarda->estado='Activo';
            $guarda->save();

            $actualizaPagoNot=InfoNotifica::where('id',$request->idnot_pago)
            ->first();
            $actualizaPagoNot->valor_cancelado=(float) $request->valor_cancelado; 
            $actualizaPagoNot->save();

            
            return ["mensaje"=>"Informacion registrada exitosamente", "error"=>false];
            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function anularNotificacion(Request $request){
        try{
            
            $verifica=Convenio::where('id_info_notifica',$request->idnot_elimina)
            ->where('estado','Activo')
            ->first();
            if(!is_null($verifica)){
                return ["mensaje"=>"Ya existe un convenio activo, por lo que no se puede anular la notificacion", "error"=>true];
            }
            $verifica=Pago::where('id_info_notifica',$request->idnot_elimina)
            ->where('estado','Activo')
            ->first();
            if(!is_null($verifica)){
                return ["mensaje"=>"Ya existe un pago activo, por lo que no se puede anular la notificacion", "error"=>true];
            }

            $esta_coact=InfoCoa::where('id_info_notifica',$request->idnot_elimina)
            ->first();
            if(!is_null($esta_coact)){
                return ["mensaje"=>"Ya existe un proceso de coactiva inicializado, por lo que no se puede anular la notificacion", "error"=>true];
            }

            $anula=InfoNotifica::where('id',$request->idnot_elimina)->first();
            if($anula->estado=="Coactivado"){
                return ["mensaje"=>"Ya existe un proceso de coactiva inicializado, por lo que no se puede anular la notificacion", "error"=>true];
            }else if($anula->estado=="Anulado"){
                return ["mensaje"=>"La notificacion ya se encuentra anulada ", "error"=>true];
            }else{
                $anula->estado="Anulado";
                $anula->usuario_elimina=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
                $anula->fecha_elimina=date('Y-m-d H:i:s');
                $anula->motivo_anula=$request->motivo_anula;
                $anula->save();

                return ["mensaje"=>"Informacion anulada exitosamente", "error"=>false];
            }
            
            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function tablaPagosNot($id){
        try{
           
            $datos=Pago::where('id_info_notifica',$id)
            ->get();
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function deudasContribuyente($cedula){
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
            ->select(
            // 'sgm_financiero.ren_liquidacion.id',
            'sgm_financiero.ren_liquidacion.id_liquidacion as num_titulo',
            // 'sgm_financiero.ren_liquidacion.total_pago',
            // 'sgm_financiero.ren_liquidacion.estado_liquidacion',
            'sgm_financiero.ren_liquidacion.predio',
            'sgm_financiero.ren_liquidacion.anio',
            'sgm_financiero.ren_liquidacion.nombre_comprador AS nombre_per',
            'sgm_app.cat_predio.clave_cat as clave',
            'sgm_app.cat_predio.coordx',
            'sgm_app.cat_predio.coordy',
            'sgm_app.cat_ente.nombres',
            'sgm_app.cat_ente.apellidos',
            
            DB::raw("
                CASE 
                    WHEN sgm_app.cat_ente.tipo_documento = 606 
                        THEN sgm_app.cat_ente.razon_social
                    ELSE CONCAT(sgm_app.cat_ente.apellidos, ' ', sgm_app.cat_ente.nombres)
                END AS nombre_contr1
            "),
            'sgm_app.cat_ente.ci_ruc',
            'sgm_app.cat_ente.id as idpersona',
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
                                                ROUND(
                                                    COALESCE((
                                                        SELECT SUM(d.valor)
                                                        FROM sgm_financiero.ren_det_liquidacion d
                                                        WHERE d.liquidacion = ren_liquidacion.id
                                                        AND d.rubro = 2
                                                    ),0)
                                                    * (
                                                        SELECT porcentaje
                                                        FROM sgm_app.ctlg_descuento_emision
                                                        WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                                        AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                                        LIMIT 1
                                                    ) / 100
                                                , 2) * (-1)
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
                                                ROUND(COALESCE((
                                                    SELECT SUM(d.valor)
                                                    FROM sgm_financiero.ren_det_liquidacion d
                                                    WHERE d.liquidacion = ren_liquidacion.id
                                                    AND d.rubro = 2
                                                ),0) * 0.10, 2)
                                            WHEN ren_liquidacion.anio < EXTRACT(YEAR FROM NOW()) THEN
                                                ROUND(COALESCE((
                                                    SELECT SUM(d.valor)
                                                    FROM sgm_financiero.ren_det_liquidacion d
                                                    WHERE d.liquidacion = ren_liquidacion.id
                                                    AND d.rubro = 2
                                                ),0) * 0.10, 2)
                                            ELSE 0
                                        END
                                    ), 0)

                                ), 2)
                        ) AS total_pagar
                    '),
                     DB::raw("
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
                            WHERE d.liquidacion = ren_liquidacion.id
                             
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

            // return ["resultado"=>$liquidacionUrbana, 
            //         "total_valor"=>number_format($total_valor,2),
            //         "exoneracion_3era_edad"=>0,
            //         "exoneracion_discapacidad"=>0,
            //         "error"=>false,
            //         "aplica_remision"=>0
            // ];

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
                'cv.CarVe_FechaEmision as fecha_emi',
                'cv.CarVe_NumTitulo as num_titulo',
                'cv.CarVe_CI as num_ident',
                'cv.CarVe_Estado',
                'cv.CarVe_Nombres as nombre_per',
                'cv.CarVe_ValorEmitido as valor_emitido',
                'cv.CarVe_TasaAdministrativa as tasa',
                'CarVe_Calle as direcc_cont',
                'cv.Carve_Recargo as recargo',
                'cv.Carve_Descuento as descuento')
            ->where('P.Pre_Tipo','Rural')
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
            $aplica_remision=0;
            // $total_valor=0;
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
          
            ->where('P.Pre_Tipo','Rural')
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
            ->merge($liquidacionUrbana)
            ->sortBy([
                ['clave', 'desc'],
                ['num_titulo', 'asc'],
            ])
            ->values();
           
           
            return ["resultado"=>$resultado, 
                    "total_valor"=>number_format($total_valor,2),
                    "exoneracion_3era_edad"=>$exoneracion_3era_edad,
                    "exoneracion_discapacidad"=>$exoneracion_discapacidad,
                    "error"=>false,
                    "aplica_remision"=>$aplica_remision
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

}