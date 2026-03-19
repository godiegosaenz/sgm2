<?php

namespace App\Http\Controllers\Convenio;

use App\Http\Controllers\Controller;
use App\Models\Coactiva\Convenio;
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
}