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
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use Exception;

class TransitoImpuestoCertificacionController extends Controller
{
    public function index(){
        $vehiculos = TransitoVehiculo::all();
        $conceptos = TransitoConcepto::whereNull('anio')->orderby('concepto','asc')->WHERE('estado','A')->get();
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

    public function store(Request $request){
        // dd($request->all());
        DB::beginTransaction();
        try {
            $validator = Validator::make($request->all(), [
                'idsCambio' => 'required|array|min:1',
                
                'vehiculo_id_2' => 'required',
                'cliente_id_2' => 'required'
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'errors' => $validator->errors()
                ], 422);
            }

            $TransitoImpuesto = new TransitoImpuesto();
            $TransitoImpuesto->cat_ente_id = $request->cliente_id_2;
            $TransitoImpuesto->vehiculo_id = $request->vehiculo_id_2;
            $TransitoImpuesto->usuario = Auth()->user()->name; //estado 1 = pagado
            $TransitoImpuesto->idusuario_registra = Auth()->user()->id;
            $TransitoImpuesto->certificacion='S';
            $TransitoImpuesto->year_impuesto=date('Y');
            $TransitoImpuesto->save();

            $valor=$request->valorCambio;
            $descr=$request->descripcionesCambio;
            // dd($descr);
            $total = 0;
            foreach ($request->idsCambio as $i=> $concepto) {
                TransitoImpuestoConcepto::create([
                    'concepto_id' => $concepto,
                    'impuesto_matriculacion_id' => $TransitoImpuesto->id,
                    'valor' => $valor[$i] ?? 0,
                    'descripcion' => $descr[$i] ?? '',
                ]);
                $total += $valor[$i] ?? 0;
            }

            $verificaNum=TransitoImpuesto::whereNotNull('numero_titulo')
            ->select('numero_titulo')
            ->where('year_impuesto',date('Y'))
            ->where('certificacion','S')
            ->orderBy('id','desc')->first();

            $num=0;
            if(is_null($verificaNum)){
                $num=1;
            }else{
                $solo_numero=explode("-",$verificaNum->numero_titulo);
                $num = (int)$solo_numero[2] + 1;
            }

            $TransitoImpuesto->numero_titulo = 'TR-CERT-'.str_pad($num, 5, '0', STR_PAD_LEFT).'-'.$TransitoImpuesto->year_impuesto;

            $TransitoImpuesto->total_pagar = $total;
            $TransitoImpuesto->estado = 1; //estado 1 = pagado
            $TransitoImpuesto->save();
            DB::commit();
            return response()->json(['success' => true, 'id' => $TransitoImpuesto->id]);
        } catch (Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde' .$e]);
        }
    }
    

}