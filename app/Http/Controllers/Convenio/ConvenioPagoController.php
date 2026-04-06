<?php

namespace App\Http\Controllers\Convenio;

use App\Http\Controllers\Coactiva\NotificacionesController;
use App\Http\Controllers\Controller;
use App\Models\Coactiva\Convenio;
use App\Models\Coactiva\CuotaConvenio;
use App\Models\Coactiva\DataCoa;
use App\Models\Coactiva\DataNotifica;
use App\Models\PsqlPredio;
use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;

class ConvenioPagoController extends Controller
{   
    private $coactiva= null;
    public function __construct() {
        $this->coactiva = new NotificacionesController;
    }
    public function index(){
        return view('convenio.pago');
    } 
    
    public function tablaConvenioFiltra(Request $request){
        try{
            $data=$request->data;
            $datos = Convenio::with('notificacion', 'coactiva')
            ->where('estado', 'Activo') // Aseguramos que este filtro se aplique primero
            ->where(function ($query) use ($data) {
                $query->whereHas('notificacion', function ($query) use ($data) {
                    $query->whereHas('ente', function ($query) use ($data) {
                        $query->where('ci_ruc', '=', $data)
                            ->orWhere(DB::raw("CONCAT(nombres, ' ', apellidos)"), 'ilike', '%' . $data . '%')
                            ->orWhere(DB::raw("CONCAT(apellidos, ' ', nombres )"), 'ilike', '%' . $data . '%')
                            ->orWhere(DB::raw("razon_social"), 'ilike', '%' . $data . '%');
                    })
                    ->orWhere('num_ident', '=', $data)
                    ->orWhere('contribuyente', 'ilike', '%' . $data . '%');
                })
                ->orWhereHas('coactiva', function ($query) use ($data) {
                    $query->whereHas('notificacion', function ($query) use ($data) {
                        $query->whereHas('ente', function ($query) use ($data) {
                            $query->where('ci_ruc', '=', $data)
                                ->orWhere(DB::raw("CONCAT(nombres, ' ', apellidos)"), 'ilike', '%' . $data . '%')
                                ->orWhere(DB::raw("CONCAT(apellidos, ' ', nombres )"), 'ilike', '%' . $data . '%')
                                ->orWhere(DB::raw("razon_social"), 'ilike', '%' . $data . '%');
                        })
                        ->orWhere('num_ident', '=', $data)
                        ->orWhere('contribuyente', 'ilike', '%' . $data . '%');
                    });
                });
            })
            ->limit(10)
            ->get();
                    
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function pagaCuota(Request $request)
    {
       
        DB::connection('pgsql')->beginTransaction();

        try {

            $cuotas=CuotaConvenio::where('id_convenio',$request->IdConvenio)
            ->where('estado','!=','Pagada')
            ->orderBy('id','asc')
            ->get();
            
            if($cuotas->isEmpty()){
                return ["mensaje" => "No existen cuotas pendientes de pago", "error" => true];
            }
            
            $valor_recibido=$request->total_recibido;
            foreach ($cuotas as $key=> $info) {
                 
                $cuota_o_abono = $info->estado == "Abonada"
                ? $info->saldo_abono
                : $info->valor_cuota;
                
                $valor_pagar=$info->valor_cuota;
               
                if (round((float)$valor_recibido, 2) >= round((float)$cuota_o_abono, 2)) {
                    if($info->estado == "Abonada"){
                        $valor_pagar=(float) $info->valor_cuota -  (float) $info->saldo_abono;
                        $info->saldo_abono=null;
                       
                    }
                    $info->valor_cobrado=(float) $info->valor_cuota;
                    $info->estado='Pagada';
                    
                    
                }else{
                    if($valor_recibido>0){
                        $info->saldo_abono=(float) $valor_recibido;
                        $info->valor_cobrado=(float) $info->saldo_abono;
                        $info->estado='Abonada';
                    }else{
                        break;
                    }
                }

                $info->usuario_cobra=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
                $info->fecha_cobro=date('Y-m-d H:i:s');
                $info->save();

                $valor_recibido=(float) $valor_recibido - (float) $valor_pagar;
            }
            
            $actualizaConvenio=$this->actualizaConvenio($request->IdConvenio);
            if($actualizaConvenio['error']==true){
                DB::connection('pgsql')->rollBack();

                return [
                    "mensaje" => $actualizaConvenio['mensaje'],
                    "error" => true
                ];
            }

            $crearPdf=$this->pdfPagoCuota($request->idCuota[0],$request->Urbano_Rural,$request->Noti_o_Proceso);
            if($crearPdf['error']==true){
                DB::connection('pgsql')->rollBack();

                return [
                    "mensaje" => $crearPdf['mensaje'],
                    "error" => true
                ];
            }
            DB::connection('pgsql')->commit();

            return ["mensaje" => "Información registrada exitosamente", "error" => false, "pdf"=>$crearPdf['pdf']];
            // return ["mensaje" => "Información registrada exitosamente", "error" => false];

        } catch (\Exception $e) {

            DB::connection('pgsql')->rollBack();

            return [
                "mensaje" => "Ocurrió un error: " . $e->getMessage(). $e->getLine(),
                "error" => true
            ];
        }
    }

    public function actualizaConvenio($id)
    {
        DB::connection('pgsql')->beginTransaction();

        try {
            $totalCuotaConvenio = CuotaConvenio::where('id_convenio', $id)
            ->whereIn('estado',['Pagada','Abonada'])
                ->sum('valor_cobrado');

            $totalCuotaConvenio = round($totalCuotaConvenio, 2);

            $convenio=Convenio::where('estado','Activo')
            ->where('id','=', $id) 
            ->first();

            $convenio->valor_cancelado=$totalCuotaConvenio;
            $convenio->save();

            $obtenerAdeuadado = round($convenio->valor_adeudado, 2);

            if ($obtenerAdeuadado <= $totalCuotaConvenio) {
                $convenio->estado_pago = 'Pago Total';   // ✅ ya no debe nada
            } else {
                $convenio->estado_pago = 'Pago Parcial';   // ❌ aún debe
            }
            $convenio->save();


            DB::connection('pgsql')->commit();
            

            

            return [
                "mensaje" => "OK",
                "error" => false
            ];

        } catch (\Exception $e) {

            DB::connection('pgsql')->rollBack();

            return [
                "mensaje" => "Ocurrió un error: " . $e->getMessage(). $e->getLine(),
                "error" => true
            ];
        }
    }

    public function pdfPagoCuota($idcuota,$lugar,$noti_proc){       
        try {
            $nombrePDF="Convenio_Cuota_".$idcuota.".pdf";
            
            $disco="disksCoactiva";
            $exists_destino = \Storage::disk($disco)->exists($nombrePDF);
            if($exists_destino){
                return ["pdf"=>$nombrePDF, "error"=>false];
            }

            $dataConvenio=Convenio::with('notificacion','coactiva','cuotas')
            ->whereHas('cuotas', function($query) use($idcuota) { 
               $query->where('id',$idcuota);    
            })
            ->first();
                      
            $contr="";
            $cedula="";
            $idliqui=[''];
            $clave=[''];
            if($lugar=='Urbano'){
                if(!is_null($dataConvenio->notificacion)){
                    $contr=$dataConvenio->notificacion->ente->apellidos." ".$dataConvenio->notificacion->ente->nombres;
                    $cedula=$dataConvenio->notificacion->ente->ci_ruc;
                    
                    $idliqui=DataNotifica::with('liquidacion')
                    ->where('id_info_notifica',$dataConvenio->notificacion->id)                
                    ->get()
                    ->pluck('liquidacion.predio')
                    ->toArray();
                    
                    $clave=PsqlPredio::whereIN('id',$idliqui)
                    ->pluck('num_predio')
                    ->toArray();
                 

                }else{
                    $contr=$dataConvenio->coactiva->notificacion->ente->apellidos." ".$dataConvenio->coactiva->notificacion->ente->nombres;
                    $cedula=$dataConvenio->coactiva->notificacion->ente->ci_ruc;
                    $idliqui=DataCoa::with('liquidacion')
                    ->where('id_info_coact',$dataConvenio->coactiva->id)
                    ->get()
                    ->pluck('liquidacion.predio')
                    ->toArray();

                    $clave=PsqlPredio::whereIN('id',$idliqui)
                    ->pluck('num_predio')
                    ->toArray();
                }
            }else{
                if(!is_null($dataConvenio->notificacion)){
                    $contr=$dataConvenio->notificacion->contribuyente;
                    $cedula=$dataConvenio->notificacion->num_ident;
                    $num_titulo=DataNotifica::where('id_info_notifica',$dataConvenio->notificacion->id)
                    ->pluck('num_titulo')
                    ->toArray();
                }else{  
                   $contr=$dataConvenio->coactiva->contribuyente;
                    $cedula=$dataConvenio->coactiva->num_ident;
                    $num_titulo=DataCoa::where('id_info_coact',$dataConvenio->coactiva->id)
                    ->pluck('num_titulo')
                    ->toArray();
                }
               
                $clave = [];
                $clave_act = [];

                foreach ($num_titulo as $key => $data) {
                    $anio = explode("-", $data)[0];

                    if ($anio < date('Y')) {
                        $result = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
                            ->where('CarVe_NumTitulo', $data)
                            ->pluck('Pre_CodigoCatastral')
                            ->toArray();

                        $clave = array_merge($clave, $result);

                    } else {
                        $result = DB::connection('sqlsrv')->table('TITULOS_PREDIO as tp')
                            ->where('TitPr_NumTitulo', $data)
                            ->pluck('Pre_CodigoCatastral')
                            ->toArray();

                        $clave_act = array_merge($clave_act, $result);
                    }
                }

                // 🔥 unir ambos arrays en uno solo
                $claves_final = array_merge($clave, $clave_act);

                // opcional: quitar duplicados
                $claves_final = array_unique($claves_final);
                $clave=$claves_final;
               
            }
            
            $cuota = $dataConvenio->cuotas->firstWhere('id', $idcuota);
            // dd($cuota);
            $num_cuota="";
            foreach ($dataConvenio->cuotas as $i=> $cuota_data) {
                if($cuota_data->id==$idcuota){
                    $num_cuota=$cuota->cuota_inicial === true ? 'Inicial': $i; 
                }
            }
            // dd($cuota);
      
            $idConvenio=$dataConvenio->id;
           
            $dataTitulo=$this->coactiva->obtenerTitulosConvenio($idConvenio, $lugar, $noti_proc);
          
            $total=0;
            foreach($dataTitulo['resultado'] as $data){
                if($lugar=='Urbano'){
                    $total=$total + $data[0]->total_complemento;
                }else{
                    $total=$total + $data[0]->total_pagar;
                }
            }
            $interes=0;
            
            if (round((float)$dataConvenio->valor_adeudado, 2) < round((float)$total, 2)) {
                $interes=round((float)$total,2) - round((float)$dataConvenio->valor_adeudado,2);
                
            }
            
          
            $fecha_hoy=date('Y-m-d');
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
            $fecha_timestamp = strtotime($fecha_hoy);    
            $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);

            
            $pdf = \PDF::loadView('reportes.pago_cuota_convenio', 
                ["data"=>$dataConvenio,
                "interes"=>round((float)$interes,2), 
                "total"=>$total, 
                "cuota"=>$cuota, 
                "contr"=>$contr, 
                "cedula"=>$cedula,
                "clave"=>$clave, 
                "num_cuota"=>$num_cuota, 
                "fecha_formateada"=>$fecha_formateada
            ]);

            // return $pdf->stream($nombrePDF);

            $estadoarch = $pdf->stream();
            $disco="disksCoactiva";
            \Storage::disk($disco)->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk($disco)->exists($nombrePDF);
            if($exists_destino){
                return ["mensaje"=>'Documento generado exitosamente', "error"=>false, "pdf"=>$nombrePDF];
            }else{
                return [
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ];
            }

        }catch (\Exception $e) {
            return [
                'error'=>true,
                'mensaje'=>'Ocurrio un error '.$e->getLine().' Mensaje '.$e->getMessage(),
            ];
        }
    }

  

}