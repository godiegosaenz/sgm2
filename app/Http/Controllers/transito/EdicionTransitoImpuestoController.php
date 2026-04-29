<?php

namespace App\Http\Controllers\transito;

use App\Http\Controllers\Controller;
use App\Models\TransitoClaseTipo;
use App\Models\TransitoConcepto;
use App\Models\TransitoEnte;
use App\Models\TransitoImpuesto;
use App\Models\TransitoImpuestoConcepto;
use App\Models\TransitoMarca;
use App\Models\TransitoTarifaAnual;
use App\Models\TransitoTipoVehiculo;
use App\Models\TransitoVehiculo;
use App\Models\TransitoYearImpuesto;
use App\Models\ClaseVehiculo;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use Exception;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Support\Facades\Auth;
use GuzzleHttp\Client;
use Illuminate\Support\Facades\Gate;
use App\BSrE_PDF_Signer_Cli;
use Illuminate\Support\Facades\Crypt;
use App\Models\PsqlEnte;
use Endroid\QrCode\Builder\Builder;
use Endroid\QrCode\Writer\PngWriter;


class EdicionTransitoImpuestoController extends Controller
{

    public function index(){
        if(!Auth()->user()->hasPermissionTo('Edicion impuestos transito'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        $vehiculos = TransitoVehiculo::all();
        $conceptos = TransitoConcepto::where('anio',date('Y'))->orderby('orden','asc')->WHERE('estado','A')->get();
        $year = TransitoYearImpuesto::all();
        $marcas = TransitoMarca::where('estado','A')->get();
        $tipo_vehiculo = TransitoTipoVehiculo::where('estado','A')->get();
        $rangos = TransitoTarifaAnual::all();
        return view('transito.edicion',compact('vehiculos', 'conceptos','year','tipo_vehiculo','marcas','rangos'));
    }

    public function datatableEdicion(Request $r)
    {
        if($r->ajax()){
            $listaimpuesto = TransitoImpuesto::with('cliente')->orderBy('id','desc')
            ->whereIN('estado',[1])//generado
            ->where('created_at', '>=', now()->subDays(3))
            ->get();
            return Datatables($listaimpuesto)
            ->addColumn('cc_ruc', function ($listaimpuesto) {
                return $listaimpuesto->cliente->ci_ruc;
            })
            ->addColumn('contribuyente', function ($listaimpuesto) {
                return $listaimpuesto->cliente->nombres.' '.$listaimpuesto->cliente->apellidos;
            })
            ->addColumn('vehiculo', function ($listaimpuesto) {
                return $listaimpuesto->vehiculo->placa_cpn_ramv;
            })
            ->editColumn('created_at', function ($item) {
                // Formato: día-mes-año hora:minuto:segundo
                return \Carbon\Carbon::parse($item->created_at)->format('d-m-Y H:i:s');
            })
            
            ->addColumn('action', function ($listaimpuesto) {
                $disabled="";
                if($listaimpuesto->estado==1 || $listaimpuesto->estado==3){
                   
                    $btn2='<a class="btn btn-success btn-sm" onclick="cobrarTitulo(\''.$listaimpuesto->id.'\')">Visualizar</a>';
                    $disabled="disabled";

                  
                }else if($listaimpuesto->estado==3){
                    
                }
                return $btn2;;
            })

            ->rawColumns(['action','contribuyente','vehiculo'])
            ->make(true);
        }
    }

    public function editar($id){
        try{
            $impuesto = TransitoImpuesto::with('cliente','vehiculo','conceptos')
            ->where('id',$id)
            ->wherein('estado',[1])
            ->first();

            return["error"=>false, "resultado"=>$impuesto];

        } catch (Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde']);
        }
    }

    public function actualizar(Request $request)
    {   
      
        DB::beginTransaction();
        try {
            
            $vehiculo = TransitoVehiculo::where('id',$request->input('vehiculo_id_2'))->first();
            $aplica_recargo=0;
            $fin = date('Y');
            if($vehiculo->tipo_identif=="PLACA"){
                $placa=$vehiculo->placa_cpn_ramv;
                $lastChar = substr($placa, -1);
                $mes = date("n");

                $lastChar=(int)$lastChar;
                $valor=$lastChar+1;
               
                if($valor<$mes){
                    $aplica_recargo=1;
                    $fin=$fin+1;
                }
            }
                     
            $inicio = $request->last_year_declaracion;
            if($inicio<date('Y')){
                $inicio=$inicio+1;
            }
            $cadena=null;
            if($inicio!=date('Y')){                
                $anios = range($inicio, $fin - 1);
                $ultimo_5_anio=date('Y')-5;
                if($request->last_year_declaracion<$ultimo_5_anio){
                    $ultimosCinco = array_slice($anios, -5);
                }else{
                    $ultimosCinco = $anios;
                }
                
                $cadena = implode(', ', $ultimosCinco);
                
            }
            if($cadena==null){
                $cadena=date('Y');
            }
            if($request->solo_dupli!="no"){
                $cadena=null;
            }
            
            $qr_Rentas = DB::connection('mysql')
                        ->table('area as a')
                        ->leftJoin('jefe_area as ja', 'ja.id_area', '=', 'a.id_area')
                        ->leftJoin('archivo_p12 as pdoce', 'pdoce.id_usuario', '=', 'ja.id_usuario')
                        ->leftJoin('users as u', 'u.id', '=', 'ja.id_usuario')
                        ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                        ->select(DB::raw("CONCAT(nombres,' ',apellidos) AS nombre"),'pdoce.archivo', 'pdoce.password')
                        ->where('a.descripcion', 'Rentas')
                        ->where('a.estado', 'A')
                        ->where('ja.estado', 'A')
                        ->where('pdoce.estado', 'A')
                        ->first();
                    
            if(is_null($qr_Rentas)) {
                
                // return [
                //     'error' => true,
                //     'mensaje' => "No existe un Certificado Vigente para el encargado de Rentas"
                // ];
            }


            $qr_Tesoreria = DB::connection('mysql')
                    ->table('area as a')
                    ->leftJoin('jefe_area as ja', 'ja.id_area', '=', 'a.id_area')
                    ->leftJoin('archivo_p12 as pdoce', 'pdoce.id_usuario', '=', 'ja.id_usuario')
                    ->leftJoin('users as u', 'u.id', '=', 'ja.id_usuario')
                    ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                    ->select(DB::raw("CONCAT(nombres,' ',apellidos) AS nombre"),'pdoce.archivo', 'pdoce.password')
                    ->where('a.descripcion', 'Rentas')
                    ->where('a.estado', 'A')
                    ->where('ja.estado', 'A')
                    ->where('pdoce.estado', 'A')
                    ->first();

            if(is_null($qr_Tesoreria)) {
                // return [
                //     'error' => true,
                //     'mensaje' => "No existe un Certificado Vigente para el encargado de Tesoreria"
                // ];
            }


            $qr_Recaudador = DB::connection('mysql')
                    ->table('archivo_p12 as pdoce')
                    ->leftJoin('users as u', 'u.id', '=', 'pdoce.id_usuario')
                    ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                    ->select(DB::raw("CONCAT(nombres,' ',apellidos) AS nombre"),'pdoce.archivo', 'pdoce.password')
                    ->where('pdoce.id_usuario', auth()->user()->id)
                    ->where('pdoce.estado', 'A')
                    ->first();
            if(is_null($qr_Recaudador)) {
                // return [
                //     'error' => true,
                //     'mensaje' => "No existe un Certificado Vigente para el encargado de Recaudacion"
                // ];
            }

            $verificaTitulo=TransitoImpuesto::where('id',$request->id_impuesto_editar)
            ->first();

            if(!is_null($verificaTitulo)){
                if($verificaTitulo->estado==2){
                    return (['error' => true, 'mensaje'=>'El titulo ya fue dado de baja y no se puede actualizar']);
                }else if($verificaTitulo->estado==3){
                    return (['error' => true, 'mensaje'=>'El titulo ya fue cobrado y no se puede actualizar']);
                }else if($verificaTitulo->estado==4){
                    return (['error' => true, 'mensaje'=>'El titulo ya fue eliminado y no se puede actualizar']);
                }
                
            }
           
            $validator = Validator::make($request->all(), [
                'conceptos' => 'required|array|min:1',
                'conceptos.*.id' => 'required',
                'conceptos.*.valor' => 'required|numeric|min:0',
                'vehiculo_id_2' => 'required',
                'cliente_id_2' => 'required',
                'year_declaracion' => 'required',
                'last_year_declaracion'=>'required'
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'errors' => $validator->errors()
                ], 422);
            }

            $TransitoImpuesto = TransitoImpuesto::find($request->id_impuesto_editar);
            $TransitoImpuesto->cat_ente_id = $request->cliente_id_2;
            $TransitoImpuesto->vehiculo_id = $request->vehiculo_id_2;
            $TransitoImpuesto->usuario_actualiza = Auth()->user()->name; 
            $TransitoImpuesto->fecha_actualiza=date('Y-m-d H:i:s');
            $TransitoImpuesto->calendarizacion = $cadena;
            $TransitoImpuesto->recargo_anio_actual = $aplica_recargo;
            $TransitoImpuesto->last_year_declaracion = $request->last_year_declaracion;
            $TransitoImpuesto->year_impuesto = $request->year_declaracion;
            $TransitoImpuesto->solo_duplicado = $request->solo_dupli;
            $TransitoImpuesto->save();

            $total = 0;

            $eliminaConcepto=TransitoImpuestoConcepto::where('impuesto_matriculacion_id',$TransitoImpuesto->id)
            ->delete();

            foreach ($request->conceptos as $concepto) {
                TransitoImpuestoConcepto::create([
                    'concepto_id' => $concepto['id'],
                    'impuesto_matriculacion_id' => $TransitoImpuesto->id,
                    'valor' => $concepto['valor'],
                ]);
                $total += $concepto['valor'];
            }


            $TransitoImpuesto->total_pagar = $total;
            $TransitoImpuesto->estado = 1; //estado 1 = emitido
            $TransitoImpuesto->save();
            DB::commit();

            return response()->json(['success' => true, 'id' => $TransitoImpuesto->id]);
        } catch (Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde' .$e]);
        }
    }
    

}