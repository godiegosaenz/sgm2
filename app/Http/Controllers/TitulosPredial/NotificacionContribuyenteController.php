<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\TituloRural;
use DB;
use Mail;
use Storage;
class NotificacionContribuyenteController extends Controller
{
    public function index()
    {
        
        $consultaData=DB::connection('pgsql')->table('sgm_app.ente_correo as corr')
        ->Join('sgm_app.cat_ente as e','e.id','corr.ente')
        ->select('e.id','e.nombres','corr.email','e.ci_ruc')
        ->where('e.estado','A')
        ->where('e.id',35724)
        ->limit(100)
        ->get();

        foreach($consultaData as $data){

            $generaDocumento=$this->pdfLiquidacion($data->ci_ruc,1);
            $correo_envio=$data->email;
            // dd($correo_envio);
            if($generaDocumento['error']==false){

                $archivo=Storage::disk('public')->get($generaDocumento['pdf']);
                $nombrearchivo=$generaDocumento['pdf'];
                
                // $correo_envio="juan.roland@outlook.es";
            
                Mail::send('email_documentos.notificacion_liquidacion', [], function ($m) use ($correo_envio,$archivo, $nombrearchivo) {
                    $m->to($correo_envio)
                    ->subject("Notificacion de deuda pendiente de pago")
                    
                    ->attachData($archivo, $nombrearchivo, [
                        'mime' => 'application/pdf',
                    ]);
                
                }); 
            }
            
        }

        return [$consultaData];
        // dd($consultaData);
        // return view('liquidacion_urbano.index');
    }

     public function pdfLiquidacion($cedula, $lugar){
        try{
            $tipo_agrupado="";
            if($lugar==1){
                $consulta=$this->consultarTitulosUrb($cedula);
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
                $consulta=$this->consultarTitulos($cedula);
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

            // return $pdf->download('reporteLiquidacion.pdf');
            $pdf->setPaper("A4", "landscape");
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

        } catch (\Exception $e) {
            //\Log::error($e);
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

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
                    "error"=>false,
            ];
        } catch (\Exception $e) {
             //\Log::error($e);
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    
}
