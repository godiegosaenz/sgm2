<?php

namespace App\Http\Controllers\coactiva;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlYearDeclaracion;
use App\Models\PsqlLiquidacion;
use App\Models\CoactTitulo;
use App\Models\CoactListadoTitulo;
use Carbon\Carbon;
use DB;
use Illuminate\Support\Facades\Gate;

class NotificacionesController extends Controller
{
    /**
     * Display a listing of the resource.
     */
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

}