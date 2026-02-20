<?php

namespace App\Http\Controllers\Coactiva;

use App\Http\Controllers\Controller;
use App\Models\Coactiva\DataNotifica;
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

            $generarPdf=$this->Liquidaciones->pagoVoluntario($cedula, $lugar, 1);
            if($generarPdf['error']==true){
                return ["mensaje"=>$generarPdf['mensaje'], "error"=>true];
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

            $guardaDataNotificacion->documento=$generarPdf['pdf'];
            $guardaDataNotificacion->save();

            return ["mensaje"=>'Notificacion registrada exitosamente', "error"=>false, "pdf"=>$generarPdf['pdf']];

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

}