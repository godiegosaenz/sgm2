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
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use Exception;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Support\Facades\Auth;
use GuzzleHttp\Client;

class TransitoImpuestoController extends Controller
{   
    private $clientNacional = null;

    public function __construct(){
        try{
            $ip="https://srienlinea.sri.gob.ec/movil-servicios/";
          
            $this->clientNacional = new Client([
                'base_uri' =>$ip,
                'verify' => false,
            ]);

        }catch(Exception $e){
            Log::error($e->getMessage());
        }
    }
    public function index()
    {
        return view('transito.impuestos_index');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create(){
        $entes = TransitoEnte::all();
        $vehiculos = TransitoVehiculo::all();
        $conceptos = TransitoConcepto::all();
        $year = TransitoYearImpuesto::all();
        $marcas = TransitoMarca::where('estado','A')->get();
        $tipo_vehiculo = TransitoTipoVehiculo::where('estado','A')->get();
        $rangos = TransitoTarifaAnual::all();
        return view('transito.impuestos', compact('entes', 'vehiculos', 'conceptos','year','tipo_vehiculo','marcas','rangos'));
    }

    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'conceptos' => 'required|array|min:1',
            'conceptos.*.id' => 'required',
            'conceptos.*.valor' => 'required|numeric|min:0',
            'vehiculo_id_2' => 'required',
            'cliente_id_2' => 'required',
            'year_declaracion' => 'required'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'errors' => $validator->errors()
            ], 422);
        }

        $TransitoImpuesto = new TransitoImpuesto();

        $TransitoImpuesto->year_impuesto = $request->year_declaracion;
        $TransitoImpuesto->cat_ente_id = $request->cliente_id_2;
        $TransitoImpuesto->vehiculo_id = $request->vehiculo_id_2;
        $TransitoImpuesto->estado = 1; //estado 1 = pagado
        $TransitoImpuesto->usuario = Auth()->user()->name; //estado 1 = pagado
        $TransitoImpuesto->save();

        $total = 0;
        foreach ($request->conceptos as $concepto) {
            TransitoImpuestoConcepto::create([
                'concepto_id' => $concepto['id'],
                'impuesto_matriculacion_id' => $TransitoImpuesto->id,
                'valor' => $concepto['valor'],
            ]);
            $total += $concepto['valor'];
        }

        // Ahora actualizas el total
        $TransitoImpuesto->numero_titulo = 'TR-'.str_pad($TransitoImpuesto->id, 5, '0', STR_PAD_LEFT).'-'.$TransitoImpuesto->year_impuesto;
        $TransitoImpuesto->total_pagar = $total;
        $TransitoImpuesto->save();

        return response()->json(['success' => true, 'id' => $TransitoImpuesto->id]);
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        $TransitoImpuesto = TransitoImpuesto::with('vehiculo','cliente')->find($id);
        $vehiculo =  $TransitoImpuesto->vehiculo;
        $cliente = $TransitoImpuesto->cliente;
        $transitoimpuestoconcepto = $TransitoImpuesto->conceptos;
        return view('transito.impuestos_show', compact('TransitoImpuesto','transitoimpuestoconcepto','vehiculo','cliente'));
    }

    public function calcular(Request $request){
        $conceptos = $request->input('conceptos', []);
        $vehiculo = TransitoVehiculo::where('id',$request->input('vehiculo_id'))->first();
        $DatosTasaAdministrativa = TransitoConcepto::find(5);
        $DatosStickerVehicular = TransitoConcepto::find(4);
        $DatosDuplicadoEspecie = TransitoConcepto::find(2);
        $tasaAdministrativa = $DatosTasaAdministrativa->valor;
        $tasaStickerVehicular = $DatosStickerVehicular->valor;
        $tasaDuplicadoEspecie = $DatosDuplicadoEspecie->valor;

        $tarifa = null;
        $valortipoclase = null;
        if ($vehiculo) {
            $avaluo = $vehiculo->avaluo;

            $tarifa = TransitoTarifaAnual::where('desde', '<=', $avaluo)
                ->where(function ($query) use ($avaluo) {
                    $query->where('hasta', '>=', $avaluo)
                          ->orWhereNull('hasta'); // Para el caso de "En adelante"
                })
                ->where('anio',date('Y'))
                ->first();
            // dd($tarifa);

            $clasetipo = TransitoClaseTipo::where('id', $vehiculo->tipo_clase_id)->first();
            $valortipoclase = $clasetipo->valor;
        }
        $tarifaAnual=0;
        // if(!is_null($tarifa)){
        //     $tarifaAnual = $tarifa->valor;
        // }
        //  $tarifaAnual = $tarifa->valor;
        // Simulación de cálculo: aquí puedes aplicar lógica propia
        $nuevosConceptos = collect($conceptos)->map(function ($item) use ($tarifaAnual,$valortipoclase,$tasaAdministrativa,$tasaStickerVehicular,$tasaDuplicadoEspecie) {
            // verificas cada concepto y colocas el valor
            $nuevoValor = 0;
            if($item['id'] == 1)
            {
                $nuevoValor = $tarifaAnual;
            }else if($item['id'] == 3)
            {
                $nuevoValor = $valortipoclase;
            }else if($item['id'] == 5)
            {
                $nuevoValor = $tasaAdministrativa;
            }
            else if($item['id'] == 4)
            {
                $nuevoValor = $tasaStickerVehicular;
            }
            else if($item['id'] == 2)
            {
                $nuevoValor = $tasaDuplicadoEspecie;
            }
            else
            {
                $nuevoValor = $item['valor'];
            }
            return [
                'id' => $item['id'],
                'nuevo_valor' => round($nuevoValor, 2)
            ];
        });

        $total = $nuevosConceptos->sum('nuevo_valor');

        return response()->json([
            'conceptos' => $nuevosConceptos,
            'total' => round($total, 2)
        ]);
    }

    public function reportetituloimpuesto(Request $r,string $id)
    {
        $dataArray = array();
        $TransitoImpuesto = TransitoImpuesto::with('cliente','vehiculo')->find($id);
        $vehiculo =  $TransitoImpuesto->vehiculo;
        $cliente = $TransitoImpuesto->cliente;
        $transitoimpuestoconcepto = $TransitoImpuesto->conceptos;

        $fecha_hoy=date('Y-m-d');
        setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
        $fecha_timestamp = strtotime($fecha_hoy);
        $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);

        $liquidacion['TransitoImpuesto'] = $TransitoImpuesto;
        $liquidacion['vehiculo'] = $vehiculo;
        $liquidacion['cliente'] = $cliente;
        $liquidacion['transitoimpuestoconcepto'] = $transitoimpuestoconcepto;
        array_push($dataArray, $liquidacion);

        $data = [
            'title' => 'Reporte de liquidacion',
            'date' => date('m/d/Y'),
            'datosTitulo' => $dataArray,
            'fecha_formateada'=>$fecha_formateada
        ];

        $pdf = PDF::loadView('transito.reporteTitulosTransito', $data);

        // return $pdf->stream("aa.pdf");

        return $pdf->download('reporte_titulo_impuesto'.$r->id.'.pdf');
    }

    public function datatable(Request $r)
    {
        if($r->ajax()){
            $listaimpuesto = TransitoImpuesto::with('cliente')->get();
            return Datatables($listaimpuesto)
            ->addColumn('cc_ruc', function ($listaimpuesto) {
                return $listaimpuesto->cliente->cc_ruc;
            })
            ->addColumn('contribuyente', function ($listaimpuesto) {
                return $listaimpuesto->cliente->nombres.' '.$listaimpuesto->cliente->apellidos;
            })
            ->addColumn('vehiculo', function ($listaimpuesto) {
                return $listaimpuesto->vehiculo->placa;
            })
            ->addColumn('action', function ($listaimpuesto) {
                return '<a href="' . route('show.transito', $listaimpuesto->id) . '" class="btn btn-primary btn-sm">Ver</a>';
            })
            ->rawColumns(['action','contribuyente','vehiculo'])
            ->make(true);
        }
    }

    public function tablaRango(){
        try {
        
            $info=DB::connection('pgsql')->table('sgm_transito.tarifa_anual')
            ->where('estado','A')
            ->where('anio',date('Y'))
            ->orderBy('desde','asc')
            ->get();

            return ["resultado"=>$info, "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function guardarRango(Request $request){
        try {

            $valida=TransitoTarifaAnual::where('desde',$request->desde_base)
            ->where('hasta',$request->hasta_base)
            ->where('valor',$request->valor_base)
            ->where('estado','A')
            ->first();
            if(!is_null($valida)){
                return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
            }

            $guardaRango=new TransitoTarifaAnual();
            $guardaRango->desde=$request->desde_base;
            $guardaRango->hasta=$request->hasta_base;
            $guardaRango->valor=$request->valor_base;
            $guardaRango->estado='A';
            $guardaRango->save();
            
            return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function actualizarRango(Request $request, $id){
        try {

            $valida=TransitoTarifaAnual::where('desde',$request->desde_base)
            ->where('hasta',$request->hasta_base)
            ->where('valor',$request->valor_base)
            ->where('estado','A')
            ->where('id','!=',$id)
            ->first();
            if(!is_null($valida)){
                return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
            }

            $guardaRango= TransitoTarifaAnual::find($id);
            $guardaRango->desde=$request->desde_base;
            $guardaRango->hasta=$request->hasta_base;
            $guardaRango->valor=$request->valor_base;
            $guardaRango->estado='A';
            $guardaRango->save();
            
            return ["mensaje"=>"Informacion actualizada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function tablaMarca(){
        try {
        
            $info=DB::connection('pgsql')->table('sgm_transito.marca_vehiculo')
            ->where('estado','A')
            ->get();

            return ["resultado"=>$info, "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function guardarMarca(Request $request){
        try {

            $valida=TransitoMarca::where('descripcion',$request->marca_vehi)
            ->first();
            if(!is_null($valida)){
                if($valida->estado=="A"){
                    return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
                }else{
                    $valida->descripcion=$request->marca_vehi;
                    $valida->estado='A';
                    $valida->save();
                    return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];
                }                
            }

            $guardaMarca= new TransitoMarca;
            $guardaMarca->descripcion=$request->marca_vehi;
            $guardaMarca->estado='A';
            $guardaMarca->save();
            
            return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function actualizarMarca(Request $request, $id){
        try {

            $valida=TransitoMarca::where('descripcion',$request->marca_vehi)
            // ->where('estado','A')
            ->where('id','!=',$id)
            ->first();
            if(!is_null($valida)){
                return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
            }

            $actualizarMarca= TransitoMarca::find($id);
            $actualizarMarca->descripcion=$request->marca_vehi;
            $actualizarMarca->estado='A';
            $actualizarMarca->save();
            
            return ["mensaje"=>"Informacion actualizada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function eliminaMarca($id){
        try {
            $eliminarMarca= TransitoMarca::find($id);
            $eliminarMarca->estado='I';
            $eliminarMarca->save();
            
            return ["mensaje"=>"Informacion eliminada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function tablaTipo(){
        try {
        
            $info=DB::connection('pgsql')->table('sgm_transito.clase_tipo_vehiculo')
            ->where('estado','A')
            ->get();

            return ["resultado"=>$info, "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

     public function guardarTipo(Request $request){
        try {

            $valida=TransitoTipoVehiculo::where('descripcion',$request->tipo_vehi)
            ->first();
            if(!is_null($valida)){
                if($valida->estado=="A"){
                    return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
                }else{
                    $valida->descripcion=$request->tipo_vehi;
                    $valida->estado='A';
                    $valida->valor=$request->tipo_valor;
                    $valida->save();
                    return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];
                }                
            }

            $guardaTipo= new TransitoTipoVehiculo;
            $guardaTipo->descripcion=$request->tipo_vehi;
            $guardaTipo->valor=$request->tipo_valor;
            $guardaTipo->estado='A';
            $guardaTipo->save();
            
            return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function actualizarTipo(Request $request, $id){
        try {

            $valida=TransitoTipoVehiculo::where('descripcion',$request->tipo_vehi)
            // ->where('estado','A')
            ->where('id','!=',$id)
            ->first();
            if(!is_null($valida)){
                return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
            }

            $actualizaTipo= TransitoTipoVehiculo::find($id);
            $actualizaTipo->descripcion=$request->tipo_vehi;
            $actualizaTipo->valor=$request->tipo_valor;
            $actualizaTipo->estado='A';
            $actualizaTipo->save();
            
            return ["mensaje"=>"Informacion actualizada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function eliminaTipo($id){
        try {
            $eliminarTipo= TransitoTipoVehiculo::find($id);
            $eliminarTipo->estado='I';
            $eliminarTipo->save();
            
            return ["mensaje"=>"Informacion eliminada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

     public function infoPersona($cedula){
        try {
            $data = [];
            if($cedula){
                    
                $response = $this->clientNacional->request('GET', "api/v1.0/deudas/porIdentificacion/{$cedula}",[
                    'headers' => [
                        // 'Authorization'=>'bearer '.$token,
                        'Content-Type' => 'application/json'
                    ],
                ]);
                
                $responseBody = json_decode($response->getBody(), true); 

                $separaNombre=$this->separarNombre($responseBody['contribuyente']['nombreComercial'] ?? 'Sin nombre');
             
               
                $data[] = [
                    'id' => $responseBody['contribuyente']['identificacion'] ?? null,
                    'nombre' => $separaNombre[0],
                    'apellido' => $separaNombre[1],
                ];
            }

            // Siempre se devuelve la variable $data, esté llena o vacía
            return ['data'=>$data, 'error'=>false];

         } catch (Exception $e) {
            
            $response = $e->getResponse();
            $responseBody = json_decode($response->getBody(), true);
                            
            return [
                'error'=>true,
                'mensaje'=>$responseBody['mensaje']
            ];
        
        }
    }

      public function separarNombre($full_name){

        $datos =  [];

        //$full_name ='MOREIRA CRUZ RAMONA';

        /* separar el nombre completo en espacios */
        $tokens = explode(' ', trim($full_name));
        /* arreglo donde se guardan las "palabras" del nombre */
        $names = array();
        /* palabras de apellidos (y nombres) compuetos */
        $special_tokens = array('da', 'de', 'del', 'la', 'las', 'los', 'mac', 'mc', 'van', 'von', 'y', 'i', 'san', 'santa');

        $prev = "";
        foreach($tokens as $token) {
            $_token = strtolower($token);
            if(in_array($_token, $special_tokens)) {
                $prev .= "$token ";
            } else {
                $names[] = $prev. $token;
                $prev = "";
            }
        }

        $num_nombres = count($names);
        $nombres = $apellidos = "";
        switch ($num_nombres) {
            case 0:
                $nombres = '';
                break;
            case 1:
                $nombres = $names[0];
                break;
            case 2:
                $nombres    = $names[0];
                $apellidos  = $names[1];
                break;
            case 3:
                $apellidos = $names[0] . ' ' . $names[1];
                $nombres   = $names[2];
            default:
                $apellidos = $names[0] . ' '. $names[1];
                unset($names[0]);
                unset($names[1]);

                $nombres = implode(' ', $names);
                break;
        }

        $datos =  [];
        $datos[0]=$nombres;
        $datos[1]=$apellidos;

        return $datos;


    }


    public function pdfTransito($id)
    {
        $dataArray = array();
        $TransitoImpuesto = TransitoImpuesto::with('cliente','vehiculo')->find($id);
        $vehiculo =  $TransitoImpuesto->vehiculo;
        $cliente = $TransitoImpuesto->cliente;
        $transitoimpuestoconcepto = $TransitoImpuesto->conceptos;

        $fecha_hoy=date('Y-m-d');
        setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
        $fecha_timestamp = strtotime($fecha_hoy);
        $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);

        $liquidacion['TransitoImpuesto'] = $TransitoImpuesto;
        $liquidacion['vehiculo'] = $vehiculo;
        $liquidacion['cliente'] = $cliente;
        $liquidacion['transitoimpuestoconcepto'] = $transitoimpuestoconcepto;
        array_push($dataArray, $liquidacion);

        $data = [
            'title' => 'Reporte de liquidacion',
            'date' => date('m/d/Y'),
            'datosTitulo' => $dataArray,
            'fecha_formateada'=>$fecha_formateada
        ];

        // $pdf = PDF::loadView('transito.reporteTitulosTransito', $data);

        $nombrePDF='reporte_titulo_impuesto'.$id.'.pdf';

        $pdf = PDF::loadView('transito.reporteTitulosTransito', $data);

        $estadoarch = $pdf->stream();

        \Storage::disk('disksDocumentoRenta')->put(str_replace("", "",$nombrePDF), $estadoarch);
        $exists_destino = \Storage::disk('disksDocumentoRenta')->exists($nombrePDF); 
        if($exists_destino){ 
            return [
                'error'=>false,
                'pdf'=>$nombrePDF
            ];
        }else{
            return[
                'error'=>true,
                'mensaje'=>'No se pudo crear el documento'
            ];
        }

        // return $pdf->stream("aa.pdf");

        // return $pdf->download('reporte_titulo_impuesto'.$r->id.'.pdf');
    }

    
}
