<?php

namespace App\Http\Controllers\Convenio;

use App\Http\Controllers\Controller;
use App\Models\Coactiva\Convenio;
use App\Models\Coactiva\CuotaConvenio;
use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;

class ConvenioPagoController extends Controller
{ 
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
            DB::connection('pgsql')->commit();

            return ["mensaje" => "Información registrada exitosamente", "error" => false];

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
            $obtenerValorPagado=CuotaConvenio::whereIn('estado',['Pagada','Abonada'])
            ->where('id_convenio','=', $id) 
            ->sum('valor_cobrado');
            $obtenerValorPagado = round($obtenerValorPagado, 2);
            if($obtenerValorPagado>0){
                $actualizaConvenio=Convenio::where('id',$id)
                ->where('estado','Activo')
                ->first();

                if(!is_null($actualizaConvenio)){
                    $totalConvenio = CuotaConvenio::where('id_convenio', $id)
                        ->sum('valor_cuota');

                    $totalConvenio = round($totalConvenio, 2);

                    if ($obtenerValorPagado >= $totalConvenio) {
                        $actualizaConvenio->estado_pago = 'Pago Total';   // ✅ ya no debe nada
                    } else {
                        $actualizaConvenio->estado_pago = 'Pago Parcial';   // ❌ aún debe
                    }

                    $actualizaConvenio->valor_cancelado=$obtenerValorPagado;
                    $actualizaConvenio->save();

                    DB::connection('pgsql')->commit();
                
                }

            }

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

    public function pdfPagoCuota($idcuota){
       
        try {
            $dataConvenio=Convenio::with('notificacion','coactiva','cuotas')
            ->whereHas('cuotas', function($query) use($idcuota) { 
               $query->where('id',$idcuota);    
            })
            ->first();
            // dd($dataConvenio);

            $fecha_hoy=date('Y-m-d');
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
            $fecha_timestamp = strtotime($fecha_hoy);    
            $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);

            $nombrePDF="Convenio_Cuota_".$idcuota.".pdf";
            $pdf = \PDF::loadView('reportes.pago_cuota_convenio', ["data"=>$dataConvenio, 
            "fecha_formateada"=>$fecha_formateada]);

            return $pdf->stream($nombrePDF);

            $estadoarch = $pdf->stream();
            $disco="public";
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
            dd($e);
        }
    }

}