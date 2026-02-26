<?php

namespace App\Http\Controllers\Coactiva;

use App\Http\Controllers\Controller;
use App\Models\Coactiva\Convenio;
use App\Models\Coactiva\Pago;
use App\Models\Coactiva\DataCoa;
use App\Models\Coactiva\DataNotifica;
use App\Models\Coactiva\InfoCoa;
use App\Models\Coactiva\InfoNotifica;
use App\Models\Coactiva\Medidas;
use Illuminate\Http\Request;
use App\Models\PsqlYearDeclaracion;
use App\Models\PsqlLiquidacion;
use App\Models\CoactTitulo;
use App\Models\CoactListadoTitulo;
use Carbon\Carbon;
use DB;
use Illuminate\Support\Facades\Gate;
use App\Http\Controllers\TitulosPredial\LiquidacionesController;

class CoactivaController extends Controller
{  
    public function index()
    {
        return view('coactiva.lista_coactiva');
    }

    public function tablaCoactiva($periodo){
        try{
           
            $mes = $periodo; 
            $inicio = $mes . '-01';
            $fin = date("Y-m-t", strtotime($inicio)); // último día del mes

            $datos=InfoCoa::with('data','notificacion')
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
            // DD($datos);              

            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardarConvenio(Request $request){
        try{
            $verifica=Convenio::where('id_info_coact',$request->idcoa_conv)
            ->where('estado','Activo')
            ->first();
            if(!is_null($verifica)){
                return ["mensaje"=>"Ya existe un convenio activo", "error"=>true];
            }

            $actualizaEstado=$this->actualizarEstadoCoa($request->idcoa_conv,'Conv','A');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }
            
            $guarda=new Convenio();
            $guarda->id_info_coact=$request->idcoa_conv;
            $guarda->valor_adeudado = (float) $request->valor_adeudado; // Convertir a float
            $guarda->cuota_inicial = (float) $request->cuota_inicial; // Convertir a float
            $guarda->numero_cuotas=$request->num_cuotas;
            $guarda->f_inicio=$request->f_ini;
            $guarda->f_fin=$request->f_fin;
            $guarda->usuario_registra=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $guarda->fecha_registra=date('Y-m-d H:i:s');
            $guarda->estado='Activo';
            $guarda->save();

            return ["mensaje"=>"Informacion registrada exitosamente", "error"=>false];
            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function tablaConvenio($id){
        try{
           
            $datos=Convenio::where('id_info_coact',$id)
            ->get();
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function inactivarConvenio($id){
        try{
            
            $inactiva=Convenio::where('id',$id)
            ->first();
            $inactiva->usuario_elimina=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $inactiva->fecha_elimina=date('Y-m-d H:i:s');
            $inactiva->estado='Inactivo';
            $inactiva->save();

            $actualizaEstado=$this->actualizarEstadoCoa($inactiva->id_info_coact,'Conv','I');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }
         
            return ["mensaje"=>'Informacion eliminada exitosamente', "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardarMedidas(Request $request){
        try{
            $verifica=Medidas::where('id_info_coact',$request->idcoa_medida)
            ->where('estado','Activo')
            ->first();
            if(!is_null($verifica)){
                return ["mensaje"=>"Ya existe una medida activa", "error"=>true];
            }

            $actualizaEstado=$this->actualizarEstadoCoa($request->idcoa_medida,'Medi','A');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }

            $guarda=new Medidas();
            $guarda->id_info_coact=$request->idcoa_medida;
            $guarda->total_deuda = (float) $request->total_valor_deuda; // Convertir a float
            $guarda->medidas = $request->medidas_txt; 
            $guarda->usuario_registra=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $guarda->fecha_registra=date('Y-m-d H:i:s');
            $guarda->estado='Activo';
            $guarda->save();

            return ["mensaje"=>"Informacion registrada exitosamente", "error"=>false];
            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function tablaMedidas($id){
        try{
           
            $datos=Medidas::where('id_info_coact',$id)
            ->get();
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function inactivarMedidas($id){
        try{
           
            $inactiva=Medidas::where('id',$id)
            ->first();
            $inactiva->usuario_elimina=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $inactiva->fecha_elimina=date('Y-m-d H:i:s');
            $inactiva->estado='Inactivo';
            $inactiva->save();

            $actualizaEstado=$this->actualizarEstadoCoa($inactiva->id_info_coact,'Medi','I');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }
         
            return ["mensaje"=>'Informacion eliminada exitosamente', "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardarPago(Request $request){
        try{
            $verifica=Pago::where('id_info_coact',$request->idcoa_pago)
            ->where('estado','Activo')
            ->first();
            if(!is_null($verifica)){
                return ["mensaje"=>"Ya existe un pago activo", "error"=>true];
            }

            $actualizaEstado=$this->actualizarEstadoCoa($request->idcoa_pago,'Pago','A');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }

            $guarda=new Pago();
            $guarda->id_info_coact=$request->idcoa_pago;
            $guarda->valor_cancelado = (float) $request->valor_cancelado; // Convertir a float
            $guarda->usuario_registra=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $guarda->fecha_registra=date('Y-m-d H:i:s');
            $guarda->estado='Activo';
            $guarda->save();

            return ["mensaje"=>"Informacion registrada exitosamente", "error"=>false];
            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function tablaPagos($id){
        try{
           
            $datos=Pago::where('id_info_coact',$id)
            ->get();
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function inactivarPagos($id){
        try{
           
            $inactiva=Pago::where('id',$id)
            ->first();
            $inactiva->usuario_elimina=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $inactiva->fecha_elimina=date('Y-m-d H:i:s');
            $inactiva->estado='Inactivo';
            $inactiva->save();

            $actualizaEstado=$this->actualizarEstadoCoa($inactiva->id_info_coact,'Pago','I');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }
         
            return ["mensaje"=>'Informacion eliminada exitosamente', "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function actualizarEstadoCoa($id,$tipo,$estado){
         try{
            if($estado=="A"){
                if($tipo=="Conv"){
                    $actualizaCoac=InfoCoa::where('id',$id)->first();
                    if(!is_null($actualizaCoac)){
                        $actualizaCoac->estado_proceso=4;
                        $actualizaCoac->save();
                    }
                    
                    $inactivaMedidas=Medidas::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaMedidas)){
                        $inactivaMedidas->estado='Inactivo';
                        $inactivaMedidas->save();
                    }

                    $inactivaCancelado=Pago::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaMedidas)){
                        $inactivaCancelado->estado='Inactivo';
                        $inactivaCancelado->save();
                    }
                   

                }else if($tipo=="Medi"){
                    $actualizaCoac=InfoCoa::where('id',$id)
                    ->first();
                    if(!is_null($actualizaCoac)){
                       
                        $actualizaCoac->estado_proceso=2;
                        $actualizaCoac->save();
                    }
                    
                    $inactivaConv=Convenio::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaConv)){
                        $inactivaConv->estado='Inactivo';
                        $inactivaConv->save();
                    }

                    $inactivaCancelado=Pago::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaCancelado)){
                        $inactivaCancelado->estado='Inactivo';
                        $inactivaCancelado->save();
                    }
                   
                }else if($tipo=="Pago"){
                    $actualizaCoac=InfoCoa::where('id',$id)
                    ->first();
                    if(!is_null($actualizaCoac)){
                        $actualizaCoac->estado_proceso=3;
                        $actualizaCoac->save();
                    }
                    
                    $inactivaMedidas=Medidas::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaMedidas)){
                        $inactivaMedidas->estado='Inactivo';
                        $inactivaMedidas->save();
                    }

                    $inactivaConv=Convenio::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaConv)){
                        $inactivaConv->estado='Inactivo';
                        $inactivaConv->save();
                    }
                   
                }
            }else{
                $actualizaCoac=InfoCoa::where('id',$id)->first();
                if(!is_null($actualizaCoac)){
                    $actualizaCoac->estado_proceso=1;
                    $actualizaCoac->save();
                    
                }

            }
          
            return ["mensaje"=>'Informacion eliminada exitosamente', "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

}