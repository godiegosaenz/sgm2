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
            $datos=Convenio::with('notificacion','coactiva')
             ->whereHas('notificacion', function($query) use($data) { // Filtramos por la relación 'notificacion'
                $query->whereHas('ente', function($query) use($data) { // Filtro en la relación 'ente' dentro de 'notificacion'
                    $query->where('ci_ruc', '=',$data )
                    ->orwhere(DB::raw("CONCAT(nombres, ' ', apellidos)"), 'ilike', '%'.$data.'%')
                    ->orwhere(DB::raw("CONCAT(apellidos, ' ', nombres )"), 'ilike', '%'.$data.'%');
                })
                ->orwhere('num_ident','=',$data)
                ->orwhere('contribuyente', 'ilike', '%'.$data.'%');
            })
            ->get();
            
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }
}