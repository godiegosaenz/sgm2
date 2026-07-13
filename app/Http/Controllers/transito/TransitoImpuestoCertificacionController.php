<?php

namespace App\Http\Controllers\transito;

use App\Http\Controllers\Controller;
use App\Models\PsqlEnte;
use Illuminate\Http\Request;
use App\Models\TransitoImpuesto;
use App\Models\TransitoImpuestoConcepto;
use App\Models\TransitoMarca;
use App\Models\TransitoTarifaAnual;
use App\Models\TransitoTipoVehiculo;
use App\Models\TransitoVehiculo;
use App\Models\TransitoYearImpuesto;
use App\Models\TransitoConcepto;

class TransitoImpuestoCertificacionController extends Controller
{
    public function index(){
        $vehiculos = TransitoVehiculo::all();
        $conceptos = TransitoConcepto::whereNull('anio')->orderby('orden','asc')->WHERE('estado','A')->get();
        $year = TransitoYearImpuesto::all();
        $marcas = TransitoMarca::where('estado','A')->get();
        $tipo_vehiculo = TransitoTipoVehiculo::where('estado','A')->get();
        $rangos = TransitoTarifaAnual::all();
        return view('transito.impuestos_index_cert', compact('vehiculos', 'conceptos','year','tipo_vehiculo','marcas','rangos'));
    }

    public function llenarCertVehicular($id){
         try{
            $conceptos = TransitoConcepto::where('id',$id)->first();
            return["error"=>false, "resultado"=>$conceptos];

        } catch (Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde']);
        }
    }
    

}