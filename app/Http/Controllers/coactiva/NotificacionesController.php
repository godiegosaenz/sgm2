<?php

namespace App\Http\Controllers\Coactiva;

use App\Http\Controllers\Controller;
use App\Models\Coactiva\Convenio;
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

                if($data->predio=='Rural'){
                    $cedula=$data->num_ident;
                   
                    foreach($data->data as $key2=> $info){
                       
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
                            // ->where(function ($query) use($cedula){
                            //     $query->where('carVe_CI',$cedula)
                            //     ->orWhere('carVe_RUC',$cedula);
                            // })
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
                    if($data->predio=='Rural'){
                        foreach($data2->data as $key2=> $info){
                        
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
                                // ->where(function ($query) use($cedula){
                                //     $query->where('carVe_CI',$cedula)
                                //     ->orWhere('carVe_RUC',$cedula);
                                // })
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
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getLine(), "error"=>true];
        }
    }

    public function detalleNotificacionProceso($id,$lugar){
        try{
            if($lugar=='Urbano'){
                $datos=$this->consultarTitulosUrb($id);
                if($datos['error']==true){
                    return ["mensaje"=>$datos['mensaje'], "error"=>true];
                }
           
            }else{
                $datos=$this->consultarTitulos($id);
                if($datos['error']==true){
                    return ["mensaje"=>$datos['mensaje'], "error"=>true];
                }
           
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
            //dd($liquidacionActual);
           
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
        try{
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
            if($noti->predio=="Urbano"){
                $consulta=$this->consultarTitulosUrb($id);
                if($consulta['error']==true){
                    return ["mensaje"=>$consulta['mensaje'], "error"=>true];
                }
            
                $listado_final=[];


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
            
            }else{
                $consulta=$this->consultarTitulos($id);
                if($consulta['error']==true){
                    return ["mensaje"=>$consulta['mensaje'], "error"=>true];
                }
                
                $listado_final=[];

               

                $subtotal_emi=0;
                $intereses_emi=0;
                $descuento_emi=0;
                $recargo_emi=0;
                $total_emi=0;
                
                foreach ($consulta["resultado"] as $key => $item){ 
                   
                    $guardaData= new DataCoa();
                    $guardaData->id_info_coact=$guardaCoa->id;
                    $guardaData->num_titulo=$item->num_titulo;
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

            if($noti->predio=="Urbano"){
                $generarTitulo=$this->tituloCreditoUrb($consulta["liquidaciones"]);
                if($generarTitulo['error']==true){
                    return ["mensaje"=>$generarTitulo['mensaje'], "error"=>true];
                }
            }else{
                $generarTitulo=$this->tituloCreditoRural($consulta["liquidaciones"]);
                if($generarTitulo['error']==true){
                    return ["mensaje"=>$generarTitulo['mensaje'], "error"=>true];
                }
            }
            // dd($generarTitulo);
            
            $nombrePDF="ProcesoCoactiva".date('YmdHis').".pdf";                               
            $pdf = \PDF::loadView('reportes.procesoCoactiva', ['DatosLiquidacion'=>$listado_final,"nombre_persona"=>$nombre_persona, "direcc_cont"=>$direcc_cont, "ci_ruc"=>$ci_ruc, "rango"=>$rango, "funcionarios"=>$funcionarios, 'DatosLiquidaciones'=>$generarTitulo['data']['DatosLiquidaciones'], 'fecha_formateada'=>$generarTitulo['data']['fecha_formateada'], "lugar_predio"=>$noti->predio]);

            // return $pdf->stream('a.pdf');

            $estadoarch = $pdf->stream();
            $disco="disksCoactiva";
            \Storage::disk($disco)->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk($disco)->exists($nombrePDF);
            if($exists_destino){

                $ultimo_sec=DataCoa::whereYear('fecha_registra', date('Y'))
                ->whereNotNull('num_proceso')
                ->first();
                $num_proceso="";
                if(is_null($ultimo_sec)){
                    $secuencial=Secuencial::where('descripcion', 'Proceso')
                    ->where('anio',date('Y'))
                    ->where('estado','A')
                    ->select('secuencia')
                    ->first();
                    if(is_null($secuencial)){
                        $num_proceso=1;

                        $guardaSecuencia=new Secuencial();
                        $guardaSecuencia->secuencia=$num_proceso;
                        $guardaSecuencia->descripcion='Proceso';
                        $guardaSecuencia->estado='A';
                        $guardaSecuencia->anio=date('Y');
                        $guardaSecuencia->save();

                    }else{
                        $num_proceso=$secuencial->secuencia;
                    }

                    
                }else{
                    $num_proceso=$ultimo_sec->num_proceso;
                    $num_proceso=$num_proceso+1;
                }

                $guardaCoa->num_proceso=$num_proceso;
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

            // $secuencial=DB::connection('pgsql')
            // ->table('sgm_coactiva.parametro_secuencial')
            // ->selectRaw("
            //     MAX(CASE WHEN descripcion = 'Oficio' THEN secuencia END) AS oficio,
            //     MAX(CASE WHEN descripcion = 'Proceso' THEN secuencia END) AS proc
            // ")
            // ->whereIn('descripcion', ['Oficio','Proceso'])
            // ->where('anio',date('Y'))
            // ->where('estado','A')
            // ->first();

           

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
                        //  dd($liquidacionActual);
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
            //dd($liquidacionRural);
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
            $guarda->save();

            return ["mensaje"=>"Informacion registrada exitosamente", "error"=>false];
            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
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

     public function tablaPagosNot($id){
        try{
           
            $datos=Pago::where('id_info_notifica',$id)
            ->get();
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

}