<?php

namespace App\Http\Controllers\Coactiva;

use App\Http\Controllers\Controller;
use App\Models\Coactiva\DataCoa;
use App\Models\Coactiva\DataNotifica;
use App\Models\Coactiva\InfoCoa;
use App\Models\Coactiva\InfoNotifica;
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
        return view('coactiva.notificar');
    }

    public function notificacionPagoVoluntario($cedula, $lugar){
       
        DB::beginTransaction();
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

                $guardaDataNotificacion=new InfoNotifica();
                $guardaDataNotificacion->id_persona=$generarPdf['listado_final'][0]->idpersona;
                $guardaDataNotificacion->save();
                
                $subtotal_emi=0;
                $intereses_emi=0;
                $descuento_emi=0;
                $recargo_emi=0;
                $total_emi=0;
                foreach($generarPdf['listado_final'] as $data){
                    $guardaData= new DataNotifica();
                    $guardaData->id_info_notifica=$guardaDataNotificacion->id;
                    $guardaData->id_liquidacion=$data->id;
                    $guardaData->subtotal=$data->subtotal_emi;
                    $guardaData->interes=$data->intereses;
                    $guardaData->recargo=$data->recargo;
                    $guardaData->descuento=$data->descuento;
                    $guardaData->total=$data->total_pagar;
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
                $guardaDataNotificacion->subtotal_notificado=number_format(($subtotal_emi),2,'.', '');
                $guardaDataNotificacion->interes_notificado=number_format(($intereses_emi),2,'.', '');
                $guardaDataNotificacion->descuento_notificado=number_format(($descuento_emi),2,'.', '');
                $guardaDataNotificacion->recargo_notificado=number_format(($recargo_emi),2,'.', '');
                $guardaDataNotificacion->total_notificado=number_format(($total_emi),2,'.', '');
                $guardaDataNotificacion->predio='Urbano';
                $guardaDataNotificacion->documento=$generarPdf['pdf'];
                $guardaDataNotificacion->save();

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
                    $guardaData= new DataNotifica();
                    $guardaData->id_info_notifica=$guardaDataNotificacion->id;
                    $guardaData->num_titulo=$data->num_titulo;
                    $guardaData->subtotal=$data->subtotal_emi;
                    $guardaData->interes=$data->intereses;
                    $guardaData->recargo=$data->recargo;
                    $guardaData->descuento=$data->descuento;
                    $guardaData->total=$data->total_pagar;
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
                $guardaDataNotificacion->subtotal_notificado=number_format(($subtotal_emi),2,'.', '');
                $guardaDataNotificacion->interes_notificado=number_format(($intereses_emi),2,'.', '');
                $guardaDataNotificacion->descuento_notificado=number_format(($descuento_emi),2,'.', '');
                $guardaDataNotificacion->recargo_notificado=number_format(($recargo_emi),2,'.', '');
                $guardaDataNotificacion->total_notificado=number_format(($total_emi),2,'.', '');
                $guardaDataNotificacion->predio='Rural';
                $guardaDataNotificacion->documento=$generarPdf['pdf'];
                $guardaDataNotificacion->save();

                return ["mensaje"=>'Notificacion registrada exitosamente', "error"=>false, "pdf"=>$generarPdf['pdf']];
            }

        }catch (\Exception $e) {
            DB::rollBack();
            return ["mensaje"=>'Ocurrio un error, intentelo mas tarde '.$e, "error"=>true];
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

                }
                return ["resultado"=>$datos[0],"datosCoa"=>$datosCoact[0], "error"=>false];

            }

            return ["resultado"=>$datos[0],"datosCoa"=>$datosCoa, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function detalleNotificacionProceso($id){
        try{
            $datos=$this->consultarTitulosUrb($id);
            if($datos['error']==true){
                return ["mensaje"=>$datos['mensaje'], "error"=>true];
            }
           
            return ["resultado"=>$datos['resultado'], "total"=>$datos['total_valor'], "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

     public function consultarTitulosUrb($id){
        try {
            $liquidaciones=DataNotifica::where('id_info_notifica',$id)
           ->pluck('id_liquidacion')
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
            
            // ->whereIn('sgm_financiero.ren_liquidacion.predio',$predios_contribuyente)
            ->whereIn('sgm_financiero.ren_liquidacion.id',$liquidaciones)
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
                    "error"=>false,
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

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
        try{
            $noti=InfoNotifica::where('estado','Notificado')
            ->where('id',$id)
            ->first();
            if(is_null($noti)){
                return ["mensaje"=>"La notificacion ya no esta disponible para coactivar", "error"=>true];
            }
           
            $consulta=$this->consultarTitulosUrb($id);
            if($consulta['error']==true){
                return ["mensaje"=>$consulta['mensaje'], "error"=>true];
            }
           
            $listado_final=[];

            $guardaCoa=new InfoCoa();
            $guardaCoa->id_info_notifica=$id;
            $guardaCoa->save();

            $subtotal_emi=0;
            $intereses_emi=0;
            $descuento_emi=0;
            $recargo_emi=0;
            $total_emi=0;
            
            foreach ($consulta["resultado"] as $key => $item){ 
                $guardaData= new DataCoa();
                $guardaData->id_info_coact=$guardaCoa->id;
                $guardaData->id_liquidacion=$item->id;
                $guardaData->subtotal=$item->subtotal_emi;
                $guardaData->interes=$item->intereses;
                $guardaData->recargo=$item->recargo;
                $guardaData->descuento=$item->descuento;
                $guardaData->total=$item->total_pagar;
                $guardaData->estado='A';
                $guardaData->save();

                $subtotal_emi=$subtotal_emi + $guardaData->subtotal;
            
                $intereses_emi=$intereses_emi + $guardaData->interes;
                
                $recargo_emi=$intereses_emi + $guardaData->recargo;
                
                $descuento_emi=$descuento_emi + $guardaData->descuento;
                
                $total_emi=$total_emi + $guardaData->total; 

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
            $pdf = \PDF::loadView('reportes.procesoCoactiva', ['DatosLiquidacion'=>$listado_final,"nombre_persona"=>$nombre_persona, "direcc_cont"=>$direcc_cont, "ci_ruc"=>$ci_ruc, "rango"=>$rango, "funcionarios"=>$funcionarios, 'DatosLiquidaciones'=>$generarTitulo['data']['DatosLiquidaciones'], 'fecha_formateada'=>$generarTitulo['data']['fecha_formateada']]);

            $estadoarch = $pdf->stream();
            $disco="disksCoactiva";
            \Storage::disk($disco)->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk($disco)->exists($nombrePDF);
            if($exists_destino){
                $guardaCoa->estado_proceso=1;
                $guardaCoa->id_usuario_registra=auth()->user()->id;
                $guardaCoa->fecha_registra=date('Y-m-d H:i:s');
                $guardaCoa->subtotal_pago_inmediato=number_format(($subtotal_emi),2,'.', '');
                $guardaCoa->interes_pago_inmediato=number_format(($intereses_emi),2,'.', '');
                $guardaCoa->descuento_pago_inmediato=number_format(($descuento_emi),2,'.', '');
                $guardaCoa->recargo_pago_inmediato=number_format(($recargo_emi),2,'.', '');
                $guardaCoa->valor_pago_inmediato=number_format(($total_emi),2,'.', '');
                $guardaCoa->documento=$nombrePDF;
                $guardaCoa->documento=$nombrePDF;
                $guardaCoa->save();

                $noti->estado='Coactivado';
                $noti->save();

                return ["mensaje"=>'Notificacion registrada exitosamente', "error"=>false, "pdf"=>$nombrePDF];
            }else{
                return [
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ];
            }

       
            // return $pdf->stream($nombrePDF);

           

        }catch (\Exception $e) {
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

    public function tituloCreditoUrb($idliquidaciones){
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
                    DB::raw("
                        CASE
                            WHEN liq.comprador IS NULL THEN liq.nombre_comprador
                            ELSE CASE en.es_persona
                                WHEN TRUE THEN COALESCE(en.apellidos, '') || ' ' || COALESCE(en.nombres, '')
                                ELSE COALESCE(en.razon_social, '')
                            END
                        END AS nombres
                    "),
                    // DB::raw("
                    //     CASE
                    //         WHEN liq.comprador IS NULL THEN 'S/N'
                    //         ELSE (SELECT ci_ruc FROM sgm_app.cat_ente WHERE cat_ente.id = liq.comprador)
                    //     END AS cedula
                    // "),
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
                ->get();
                //dd($liquidacion);
            

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

}