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
            ->where('estado','!=','Pagado')
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

                if ($valor_recibido >= $cuota_o_abono) {
                    $info->valor_cobrado=(float) $info->valor_cuota;
                    $info->estado='Pagada';
                    if($info->estado == "Abonada"){
                        $info->saldo_abono=null;
                    }
                }else{
                    if($valor_recibido>0){
                        $info->saldo_abono=(float) $valor_recibido;
                        $info->estado='Abonada';
                    }
                }

                $info->usuario_cobra=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
                $info->fecha_cobro=date('Y-m-d H:i:s');
                $info->save();

                $valor_recibido=$valor_recibido - $info->valor_cuota;
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

}