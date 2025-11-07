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


class TransitoImpuestoController extends Controller
{
    private $clientNacional = null;
     private $clienteFirmador = null;

    public function __construct(){
        try{
            $ip="https://srienlinea.sri.gob.ec/movil-servicios/";

            $this->clientNacional = new Client([
                'base_uri' =>$ip,
                'verify' => false,
            ]);


            $ip2="http://192.168.0.42:82/";

            $this->clienteFirmador = new Client([
                'base_uri' =>$ip2,
                'verify' => false,
            ]);

        }catch(Exception $e){
            Log::error($e->getMessage());
        }
    }
    public function index()
    {
        if(!Auth()->user()->hasPermissionTo('Listar impuestos transito'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('transito.impuestos_index');
    }

    public function create(){
        
        if(!Auth()->user()->hasPermissionTo('Impuesto transitos'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        $vehiculos = TransitoVehiculo::all();
        $conceptos = TransitoConcepto::where('anio',date('Y'))->orderby('orden','asc')->WHERE('estado','A')->get();
        $year = TransitoYearImpuesto::all();
        $marcas = TransitoMarca::where('estado','A')->get();
        $tipo_vehiculo = TransitoTipoVehiculo::where('estado','A')->get();
        $rangos = TransitoTarifaAnual::all();
        return view('transito.impuestos', compact('vehiculos', 'conceptos','year','tipo_vehiculo','marcas','rangos'));
    }

    public function store(Request $request)
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
            // dd($cadena);
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

            $verificaTitulo=TransitoImpuesto::where('cat_ente_id',$request->cliente_id_2)
            ->where('vehiculo_id',$request->vehiculo_id_2)
            ->whereIn('estado',[1,3])
            ->where('year_impuesto',date('Y'))
            ->first();

            if(!is_null($verificaTitulo)){
                if($verificaTitulo->estado==1){
                    return (['error' => true, 'mensaje'=>'Ya existe un titulo pendiente de pago para este vehiculo en este año']);
                }else{
                    return (['error' => true, 'mensaje'=>'Ya existe un pago realizado para este vehiculo en este año']);
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

            $TransitoImpuesto = new TransitoImpuesto();
            $TransitoImpuesto->cat_ente_id = $request->cliente_id_2;
            $TransitoImpuesto->vehiculo_id = $request->vehiculo_id_2;
            $TransitoImpuesto->usuario = Auth()->user()->name; //estado 1 = pagado
            $TransitoImpuesto->idusuario_registra = Auth()->user()->id;
            $TransitoImpuesto->calendarizacion = $cadena;
            $TransitoImpuesto->recargo_anio_actual = $aplica_recargo;
            $TransitoImpuesto->last_year_declaracion = $request->last_year_declaracion;
            $TransitoImpuesto->year_impuesto = $request->year_declaracion;
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

            $verificaNum=TransitoImpuesto::whereNotNull('numero_titulo')
            ->select('numero_titulo')
            ->orderBy('id','desc')->first();

            $num=0;
            if(is_null($verificaNum)){
                $num=1;
            }else{
                $solo_numero=explode("-",$verificaNum->numero_titulo);
                $num = (int)$solo_numero[1] + 1;
            }

            $TransitoImpuesto->numero_titulo = 'TR-'.str_pad($num, 5, '0', STR_PAD_LEFT).'-'.$TransitoImpuesto->year_impuesto;

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

    public function comboMarca(){
        try{
            $marcas = TransitoMarca::where('estado','A')->get();
            return["error"=>false, "resultado"=>$marcas];

        } catch (Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde']);
        }
    }

    public function comboTipoVehiculo(){
        try{
            $tipo_vehiculo = TransitoTipoVehiculo::where('estado','A')->get();
            return["error"=>false, "resultado"=>$tipo_vehiculo];

        } catch (Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde']);
        }
    }

    public function comboClaseTipoVehiculo(){
        try{
            $tipo_vehiculo = ClaseVehiculo::where('estado','A')
            // ->where('clase_tipo_vehiculo_id',$id)
            ->get();
            return["error"=>false, "resultado"=>$tipo_vehiculo];

        } catch (Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde']);
        }
    }

    public function buscaTipoVehiculo($id){
        try{
            $tipo_vehiculo = \DB::connection('pgsql')
            ->table('sgm_transito.clase_vehiculo as cv')
            ->where('cv.estado','A')
            ->where('cv.id',$id)
            ->select('cv.clase_tipo_vehiculo_id')
            ->first();
            return["error"=>false, "resultado"=>$tipo_vehiculo];

        } catch (Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde '.$e]);
        }
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

    public function calcular1(Request $request){

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
                ->where('anio',$request->year)
                ->first();

            $clasetipo = TransitoClaseTipo::where('id', $vehiculo->tipo_clase_id)->first();
            $valortipoclase = $clasetipo->valor;
        }
        $tarifaAnual=0;
        if(!is_null($tarifa)){
            $tarifaAnual = $tarifa->valor;
        }

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

    public function calcular(Request $request){
        try{
            $conceptos = $request->input('conceptos', []);
            
            $vehiculo = TransitoVehiculo::where('id',$request->input('vehiculo_id'))->first();
            $aplica_recargo=0;
            $ultimo_4_anio=date('Y')-4;
            $anio_modelo=$vehiculo->year;
            $desmarca_rtv="";
            if($anio_modelo>$ultimo_4_anio){
                $desmarca_rtv='S';
            }          
           
            if($vehiculo->tipo_identif=="PLACA"){
                $placa=$vehiculo->placa_cpn_ramv;
                $lastChar = substr($placa, -1);
                $mes = date("n");

                $lastChar=(int)$lastChar;
                $valor=$lastChar+1;
                
                if($valor<$mes && $valor>1){
                    $aplica_recargo=1;
                }
                
            }
           
            $ultimo_anio_matriculacion=$request->last_year_declaracion;

            $ultimo_5_anio=date('Y')-5;
            if($ultimo_5_anio>=$ultimo_anio_matriculacion){
                $ultimo_anio_matriculacion=$ultimo_5_anio;
            }
        
            $diferencia=0;
            if($ultimo_anio_matriculacion < date('Y')){
                $diferencia=(date('Y')-1) - (int)$ultimo_anio_matriculacion;
            }
           
            $tarifa = null;
            $valortipoclase = null;
            if ($vehiculo) {
                $avaluo = $vehiculo->avaluo;
               
                $tarifa = TransitoTarifaAnual::where('desde', '<=', $avaluo)
                    ->where(function ($query) use ($avaluo) {
                        $query->where('hasta', '>=', $avaluo)
                            ->orWhereNull('hasta'); // Para el caso de "En adelante"
                    })
                    ->where('anio',$request->year)
                    ->first();
               
                $clasetipo = TransitoClaseTipo::where('id', $vehiculo->tipo_clase_id)->first();
                $valortipoclase = $clasetipo->valor;
                if($desmarca_rtv=='S'){
                    $valortipoclase=0;
                }
            }
            $tarifaAnual=0;
            if(!is_null($tarifa)){
                $tarifaAnual = $tarifa->valor;
            }

            $array=[];
            foreach($conceptos as $data){
                
                $concepto=TransitoConcepto::where('id',$data["id"])->first();
                if($concepto["codigo"]=="RTV"){
    
                    array_push($array,["id"=>$data["id"], "nuevo_valor"=> (float)$valortipoclase, "codigo"=>"RTV"]);
                   
                }else if($concepto["codigo"]=="IAV"){
                    array_push($array,["id"=>$data["id"], "nuevo_valor"=>(float)$tarifaAnual, "codigo"=>"IAV"]);
                }else if($concepto["codigo"]=="TSA"){
                    array_push($array,["id"=>$data["id"], "nuevo_valor"=>(float)$concepto->valor, "codigo"=>"TSA"]);
                }else if($concepto["codigo"]=="SRV"){
                    array_push($array,["id"=>$data["id"], "nuevo_valor"=>(float)$concepto->valor, "codigo"=>"SRV"]);
                }else if($concepto["codigo"]=="DM"){
                    array_push($array,["id"=>$data["id"], "nuevo_valor"=>(float)$concepto->valor, "codigo"=>"DM"]);
                }else if($concepto["codigo"]=="DE"){
                    array_push($array,["id"=>$data["id"], "nuevo_valor"=>(float)$concepto->valor, "codigo"=>"DE"]);
                }else if($concepto["codigo"]=="REC"){
                    $valor_recargo=$concepto->valor;
                    $valor_recargo_ant=$valor_recargo * $diferencia;                   
                    if($aplica_recargo==0){
                        $valor_recargo=0;
                    }
                    $valor_recargo=$valor_recargo + $valor_recargo_ant;
                    array_push($array,["id"=>$data["id"], "nuevo_valor"=>(float)$valor_recargo, "codigo"=>"REC"]);
                }

            }
            
            $total =1;
            return [
                'conceptos' => $array,
                'total' => round($total, 2),
                'anio_modelo'=>$anio_modelo,
                'desmarca_rtv'=>$desmarca_rtv
            ];
        } catch (\Throwable $th) {
            return ['mensaje'=>'Ocurrió un error '.$th,'error'=>true];
        }

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

        return $pdf->download('reporte_titulo_impuesto'.$r->id.'.pdf');
    }

    public function datatable(Request $r)
    {
        if($r->ajax()){
            $listaimpuesto = TransitoImpuesto::with('cliente')->orderBy('id','desc')
            ->whereIN('estado',[1,3])//generado, pagado
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
            
            ->addColumn('action', function ($listaimpuesto) {
                $disabled="";
                if($listaimpuesto->estado==1){
                    $btn='<a class="btn btn-success btn-sm" onclick="cobrarTitulo(\''.$listaimpuesto->id.'\')">Cobrar</a>';
                    $disabled="disabled";

                    $btn_pdf='<button class="btn btn-primary btn-sm" onclick="verpdf(\''.$listaimpuesto->documento_firmado.'\')" disabled>Titulo</button>';

                }else if($listaimpuesto->estado==3){
                    $btn='<a class="btn btn-danger btn-sm" onclick="eliminarTitulo(\''.$listaimpuesto->id.'\')">Dar Baja</a>';
                   
                    $btn_pdf=' <a class="btn btn-primary btn-sm" onclick="generarPdf(\''.$listaimpuesto->id.'\')" >Titulo</a>';
                }
                return $btn_pdf.' '.$btn;
            })

            ->rawColumns(['action','contribuyente','vehiculo'])
            ->make(true);
        }
    }

    public function realizarCobro($id){
        try {
            $realizarCobro= TransitoImpuesto::find($id);
            if($realizarCobro->estado==2){
                return ["mensaje"=>"La informacion ha sido eliminada y no se puede cobrar", "error"=>true];   
            }else if($realizarCobro->estado==3){
                return ["mensaje"=>"La informacion ya sido cobrada y no se puede volver a cobrar", "error"=>true];
            }
            $realizarCobro->estado=3;
            $realizarCobro->id_usuario_cobra=auth()->user()->id;
            $realizarCobro->fecha_cobro=date('Y-m-d H:i:s');
            $realizarCobro->save();

            // $generarDocumento=$this->pdfTransito($id,'');
            //queme la G para que no firme electronicamente
            $generarDocumento=$this->pdfTransito($id,'');
            // dd($generarDocumento);

            if($generarDocumento['error']==true){
                return ["mensaje"=>$generarDocumento['mensaje'], "error"=>true];
            }
            
            //change LOCALES
            return ["mensaje"=>"Cobro registrado exitosamente", "error"=>false, 'pdf'=>$generarDocumento['pdf']];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function anularCobro($id){
        try {
            $anularCobro= TransitoImpuesto::find($id);
            if($anularCobro->estado==2){
                return ["mensaje"=>"La informacion ya ha sido eliminada y no se puede volver a eliminar", "error"=>true];   
            }else if($anularCobro->estado==3){
                return ["mensaje"=>"La informacion ya sido cobrada y no se puede anular", "error"=>true];
            }
            $anularCobro->estado=3;
            $anularCobro->idusuario_anula=auth()->user()->id;
            $anularCobro->fecha_anula=date('Y-m-d H:i:s');
            $anularCobro->save();

            return ["mensaje"=>"Cobro anulado exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

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
            $guardaRango->anio=date('Y');
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
    public function tablaClaseTipo(){
        try {

            $info=DB::connection('pgsql')->table('sgm_transito.clase_vehiculo as cv')
            ->leftJoin('sgm_transito.clase_tipo_vehiculo as tv','tv.id','=','cv.clase_tipo_vehiculo_id')
            ->select('cv.id','cv.descripcion as descripcion_clase','tv.descripcion as descripcion_tipo','tv.id as idtipo')
            ->where('cv.estado','A')
            ->get();

            return ["resultado"=>$info, "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

     public function tablaConcepto(){
        try {

            $info=DB::connection('pgsql')->table('sgm_transito.conceptos')
            ->where('estado','A')
            ->where('anio',date('Y'))
            ->whereNotIn('codigo',['IAV','RTV'])
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
            ->where('estado','A')
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

    public function guardarClaseTipo(Request $request){
        try {
            $valida=ClaseVehiculo::where('descripcion',$request->descripcion_clase)
            ->first();
            if(!is_null($valida)){
                if($valida->estado=="A"){
                    return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
                }else{
                    $valida->descripcion=$request->descripcion_clase;
                    $valida->estado='A';
                    $valida->clase_tipo_vehiculo_id=$request->id_tipo;
                    $valida->save();
                    return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];
                }
            }

            $guardaTipo= new ClaseVehiculo;
            $guardaTipo->descripcion=$request->descripcion_clase;
            $guardaTipo->clase_tipo_vehiculo_id=$request->id_tipo;
            $guardaTipo->estado='A';
            $guardaTipo->save();

            return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

     public function actualizarClaseTipo(Request $request, $id){
        try {

            $valida=ClaseVehiculo::where('descripcion',$request->descripcion_clase)
            ->where('estado','A')
            ->where('id','!=',$id)
            ->first();
            if(!is_null($valida)){
                return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
            }

            $actualizaTipo= ClaseVehiculo::find($id);
            $actualizaTipo->descripcion=$request->descripcion_clase;
            $actualizaTipo->clase_tipo_vehiculo_id=$request->id_tipo;
            $actualizaTipo->estado='A';
            $actualizaTipo->save();

            return ["mensaje"=>"Informacion actualizada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function eliminaClaseTipo($id){
        try {
            $eliminarTipo= ClaseVehiculo::find($id);
            $eliminarTipo->estado='I';
            $eliminarTipo->save();

            return ["mensaje"=>"Informacion eliminada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function infoVehiculo($placa_cpn_ramv){
        try{
            $buscaVehiculo=\DB::connection('pgsql')
            ->table('sgm_transito.vehiculo as v')
            ->where('v.placa_cpn_ramv',$placa_cpn_ramv  )
            ->first();
            $id_vehiculo=0;
            if(!is_null($buscaVehiculo)){
                $id_vehiculo=$buscaVehiculo->id;
            }
            return ['data'=>$buscaVehiculo, 'error'=>false, 'id_vehiculo'=>$id_vehiculo];

        } catch (Exception $e) {
            dd($e);
            return [
                'error'=>true,
                'mensaje'=>'Ocurrio un error',
            ];

        }
    }

    public function infoPersona($cedula){
        try {
            $data = [];
            $id_persona=0;
            $validaPersona=PsqlEnte::where('ci_ruc',$cedula)
            ->first();
            
            $id="";
            $nombres="";
            $apellidos="";
            $direccion="";
            $f_nacimiento="";
            $correo="";
            $telefono="";
            if(!is_null($validaPersona)){
                $ultimoTlFn=\DB::connection('pgsql')->table('sgm_app.ente_telefono')
                ->where('ente',$validaPersona->id)
                ->orderBy('id','desc')
                ->first();

                $ultimoCorreo=\DB::connection('pgsql')->table('sgm_app.ente_correo')
                ->where('ente',$validaPersona->id)
                ->orderBy('id','desc')
                ->first();

                $id_persona=$validaPersona->id;
                $id=$id_persona;
                $nombres=$validaPersona->nombres;
                $apellidos=$validaPersona->apellidos;
                $direccion=$validaPersona->direccion;
                $date = new \DateTime($validaPersona->fecha_nacimiento);

                $f_nacimiento= $date->format('Y-m-d');;
                
                if(!is_null($ultimoTlFn)){
                    $telefono=$ultimoTlFn->telefono;
                }
                if(!is_null($ultimoCorreo)){
                    $correo=$ultimoCorreo->email;
                }
            }
            if($cedula){

                $response = $this->clientNacional->request('GET', "api/v1.0/deudas/porIdentificacion1/{$cedula}",[
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
                    'direccion' => $direccion,
                    'f_nacimiento' => $f_nacimiento,
                    'telefono' => $telefono,
                    'correo' => $correo,
                    
                ];
            }
           
            // Siempre se devuelve la variable $data, esté llena o vacía
            return ['data'=>$data, 'error'=>false , 'id_persona'=>$id_persona];

         } catch (Exception $e) {
            // dd($e);
            $response = $e->getResponse();
            $responseBody = json_decode($response->getBody(), true);

            $data[] = [
                'id' => $id,
                'nombre' => $nombres,
                'apellido' => $apellidos,
                'direccion' => $direccion,
                'f_nacimiento' => $f_nacimiento,
                'telefono' => $telefono,
                'correo' => $correo,
            ];

            return [
                'error'=>true,
                'mensaje'=>$responseBody,
                'id_persona'=>$id_persona,
                'data'=>$data
                
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

    public function generar_firma_qr($nombre_firma,$nombre_img){
        //$nombre_firma="RAFAEL ANTONIO ESPINOZA CASTRO";
        //$nombre_img="juridico";       
        // $fecha = "2025-10-07 17:30:50";
        // $textoQR = "FIRMADO POR: $nombre_firma\nRAZON: \nLOCALIZACION: \nFECHA: $fecha \nVALIDAR CON: https://www.firmadigital.gob.ec \nFirmado digitalmente con FirmaEC 4.0.1 Windows 11 10.0";

        $fecha = date('Y-m-d H:i:s');       
        $textoQR = "GADM SAN VICENTE\nFirmado por: $nombre_firma\nFecha: $fecha";

        // 1. Generar el QR
        $qr = Builder::create()
            ->writer(new PngWriter())
            ->data($textoQR)
            ->size(1000)
            ->margin(0)
            ->build();

        // 2. Obtener imagen desde QR generado
        $qrImage = imagecreatefromstring($qr->getString());

        $dimensiones = 1000;
        $ancho_adicional = 2000;
        $nueva_imagen = imagecreatetruecolor($dimensiones + $ancho_adicional, $dimensiones);
        $color_blanco = imagecolorallocate($nueva_imagen, 255, 255, 255);
        imagefill($nueva_imagen, 0, 0, $color_blanco);

        imagecopy($nueva_imagen, $qrImage, 0, 0, 0, 0, $dimensiones, $dimensiones);

        // Separar nombre
        $nombres = $this->separarNombre($nombre_firma); // [apellido, nombre]
        $texto1 = "Firmado electrónicamente por:\n";
        $texto2 = "{$nombres[1]}\n";
        $texto3 = $nombres[0];
       

        $text_color = imagecolorallocate($nueva_imagen, 0, 0, 0); // negro

        
        $font_path1 = public_path('fonts/cour.ttf');
        $font_path2 = public_path('fonts/Courier_BOLD.ttf');

        $font_size1 = 80;
        $font_size2 = 145;
        $text_x = 1002;
        $text_y1 = 400;
        $text_y2 = $text_y1 + 145;
        $text_y3 = $text_y2 + 145;

        imagettftext($nueva_imagen, $font_size1, 0, $text_x, $text_y1, $text_color, $font_path1, $texto1);
        imagettftext($nueva_imagen, $font_size2, 0, $text_x, $text_y2, $text_color, $font_path2, $texto2);
        imagettftext($nueva_imagen, $font_size2, 0, $text_x, $text_y3, $text_color, $font_path2, $texto3);
        // imagettftext($nueva_imagen, $font_size2, 0, $text_x, $text_y3, $text_color, $font_path2, $texto4);

        // Guardar imagen
        // $nombre_img = uniqid('qr_');
        $ruta = public_path("qrfirma/{$nombre_img}.png");
        imagepng($nueva_imagen, $ruta);

        imagedestroy($nueva_imagen);

        // return $nombre_img . '.png'; // o retorna la ruta o response()->download($ruta);

        return ['error'=>false, 'img'=>$nombre_img . '.png'];

    }
    public function pdfTransito($id,$tipo)
    {
        $dataArray = array();
        $TransitoImpuesto = TransitoImpuesto::with('cliente','vehiculo')->find($id);
       
        if($TransitoImpuesto->estado==3 && $tipo=='C'){
            return [
                'error'=>false,
                'pdf'=>$TransitoImpuesto->documento_firmado
            ];
        }
      
        $vehiculo =  $TransitoImpuesto->vehiculo;
        $cliente = $TransitoImpuesto->cliente;
        $transitoimpuestoconcepto = $TransitoImpuesto->conceptos;
        // $clase=\DB::connection('psql')->table('clase_vehiculo')-

        foreach($transitoimpuestoconcepto as $key => $data){
           
            if($data->codigo=='RTV'){
                $obtener= DB::connection('pgsql')
                ->table('sgm_transito.clase_tipo_vehiculo')
                ->where('valor', $data->pivot->valor)
                ->pluck('descripcion')
                ->toArray();
                
                $transitoimpuestoconcepto[$key]->agrupado=$obtener;
            }           

        }
       
        $fecha_documento=$TransitoImpuesto->created_at;
        $fecha_hoy=date('d-m-Y', strtotime($fecha_documento));

        setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
        $fecha_timestamp = strtotime($fecha_hoy);
        $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);

        $liquidacion['TransitoImpuesto'] = $TransitoImpuesto;
        $liquidacion['vehiculo'] = $vehiculo;
        $liquidacion['cliente'] = $cliente;
        $liquidacion['transitoimpuestoconcepto'] = $transitoimpuestoconcepto;
        array_push($dataArray, $liquidacion);

        // $qr_Rentas = DB::connection('mysql')
        //             ->table('area as a')
        //             ->leftJoin('jefe_area as ja', 'ja.id_area', '=', 'a.id_area')
        //             ->leftJoin('archivo_p12 as pdoce', 'pdoce.id_usuario', '=', 'ja.id_usuario')
        //             ->leftJoin('users as u', 'u.id', '=', 'ja.id_usuario')
        //             ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
        //             ->select(DB::raw("CONCAT(nombres,' ',apellidos) AS nombre"),'pdoce.archivo', 'pdoce.password')
        //             ->where('a.descripcion', 'Rentas')
        //             ->where('a.estado', 'A')
        //             ->where('ja.estado', 'A')
        //             ->where('pdoce.estado', 'A')
        //             ->first();
                   
        // if(is_null($qr_Rentas)) {
            
        //     return [
        //         'error' => true,
        //         'mensaje' => "No existe un Certificado Vigente para el encargado de Rentas"
        //     ];
        // }

        // $qr_Tesoreria = DB::connection('mysql')
        //             ->table('area as a')
        //             ->leftJoin('jefe_area as ja', 'ja.id_area', '=', 'a.id_area')
        //             ->leftJoin('archivo_p12 as pdoce', 'pdoce.id_usuario', '=', 'ja.id_usuario')
        //             ->leftJoin('users as u', 'u.id', '=', 'ja.id_usuario')
        //             ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
        //             ->select(DB::raw("CONCAT(nombres,' ',apellidos) AS nombre"),'pdoce.archivo', 'pdoce.password')
        //             ->where('a.descripcion', 'Rentas')
        //             ->where('a.estado', 'A')
        //             ->where('ja.estado', 'A')
        //             ->where('pdoce.estado', 'A')
        //             ->first();

        // if(is_null($qr_Tesoreria)) {
        //     return [
        //         'error' => true,
        //         'mensaje' => "No existe un Certificado Vigente para el encargado de Tesoreria"
        //     ];
        // }
      
        // $nombreRentas=$qr_Rentas->nombre;
        // $archivoFirmaRentas=$qr_Rentas->archivo;
        // $claveFirmaRentas=$qr_Rentas->password;
        

        // $imagenRentas="";
        // $imagenRentas=$this->generar_firma_qr($nombreRentas, 'Rentas');
        // if($imagenRentas['error']==true){
        //     return [
        //         'error' => true,
        //         'mensaje' => $imagenRentas['error']
        //     ];
        // }

        // $qr_Tesoreria = DB::connection('mysql')
        //             ->table('area as a')
        //             ->leftJoin('jefe_area as ja', 'ja.id_area', '=', 'a.id_area')
        //             ->leftJoin('archivo_p12 as pdoce', 'pdoce.id_usuario', '=', 'ja.id_usuario')
        //             ->leftJoin('users as u', 'u.id', '=', 'ja.id_usuario')
        //             ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
        //             ->select(DB::raw("CONCAT(nombres,' ',apellidos) AS nombre"),'pdoce.archivo', 'pdoce.password')
        //             ->where('a.descripcion', 'Rentas')
        //             ->where('a.estado', 'A')
        //             ->where('ja.estado', 'A')
        //             ->where('pdoce.estado', 'A')
        //             ->first();

        // if(is_null($qr_Tesoreria)) {
        //     return [
        //         'error' => true,
        //         'mensaje' => "No existe un Certificado Vigente para el encargado de Tesoreria"
        //     ];
        // }
        // $nombreTesoreria=$qr_Tesoreria->nombre;
        // $archivoFirmaTesoreria=$qr_Tesoreria->archivo;
        // $claveFirmaTesoreria=$qr_Tesoreria->password;

        // $imagenTesoreria="";
        // $imagenTesoreria=$this->generar_firma_qr($nombreTesoreria, 'Tesoreria');
        // if($imagenTesoreria['error']==true){
        //     return [
        //         'error' => true,
        //         'mensaje' => $imagenTesoreria['error']
        //     ];
        // }

        // $qr_Recaudador = DB::connection('mysql')
        //             ->table('archivo_p12 as pdoce')
        //             ->leftJoin('users as u', 'u.id', '=', 'pdoce.id_usuario')
        //             ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
        //             ->select(DB::raw("CONCAT(nombres,' ',apellidos) AS nombre"),'pdoce.archivo', 'pdoce.password')
        //             ->where('pdoce.id_usuario', auth()->user()->id)
        //             ->where('pdoce.estado', 'A')
        //             ->first();
        // if(is_null($qr_Recaudador)) {
        //     return [
        //         'error' => true,
        //         'mensaje' => "No existe un Certificado Vigente para el encargado de Recaudacion"
        //     ];
        // }

        // $nombreRecaudador=$qr_Recaudador->nombre;
        // $archivoFirmaRecaudador=$qr_Recaudador->archivo;
        // $claveFirmaRecaudador=$qr_Recaudador->password;

        // $imagenTesoreria="";
        // $imagenTesoreria=$this->generar_firma_qr($nombreRecaudador, 'Recaudador');
        // if($imagenTesoreria['error']==true){
        //     return [
        //         'error' => true,
        //         'mensaje' => $imagenTesoreria['error']
        //     ];
        // }

        
        $data = [
            'title' => 'Reporte de liquidacion',
            'date' => date('m/d/Y'),
            'datosTitulo' => $dataArray,
            'fecha_formateada'=>$fecha_formateada
        ];
        

        $nombrePDF='reporte_titulo_impuesto'.$id.'.pdf';

        $pdf = PDF::loadView('transito.reporteTitulosTransito', $data);

        // return $pdf->stream('a.pdf');
        $estadoarch = $pdf->stream();

        \Storage::disk('disksDocumentoRenta')->put(str_replace("", "",$nombrePDF), $estadoarch);
        $exists_destino = \Storage::disk('disksDocumentoRenta')->exists($nombrePDF);
        if($exists_destino){
           return [
                'error'=>false,
                'pdf'=>$nombrePDF
            ];
            // if($tipo=='G'){
            //     return [
            //         'error'=>false,
            //         'pdf'=>$nombrePDF
            //     ];
            // }
            
            // $procesaFirma=$this->firmarDocumento($nombrePDF,$archivoFirmaRentas,$claveFirmaRentas, $archivoFirmaTesoreria, $claveFirmaTesoreria, $archivoFirmaRecaudador, $claveFirmaRecaudador);
           
            // if($procesaFirma['error']==false){
            //     $TransitoImpuesto->documento_firmado=$procesaFirma['pdf'];
            //     $TransitoImpuesto->save();
            //     return [
            //         'error'=>false,
            //         'pdf'=>$procesaFirma['pdf']
            //     ];
            // }

            // return[
            //     'error'=>true,
            //     'mensaje'=>$procesaFirma['mensaje']
            // ];
            

        }else{
            return[
                'error'=>true,
                'mensaje'=>'No se pudo crear el documento'
            ];
        }

    }

    public function bajaTituloTransito(Request $request){
        try {

            $baja=TransitoImpuesto::find($request->id_impuesto);
            $baja->observacion_baja=$request->motivo_baja;
            $baja->idusuariobaja=auth()->user()->id;
            $baja->fecha_baja=date('Y-m-d H:i:s');
            $baja->estado=2;
            $baja->save();

            return ["mensaje"=>"Informacion eliminada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function detalleTitulo($id)
    {
        $dataArray = array();
        $TransitoImpuesto = TransitoImpuesto::with('cliente','vehiculo')->find($id);

        $usuario=$TransitoImpuesto->idusuario_registra;

        $persona= DB::connection('mysql')
                ->table('personas as p')
                ->leftJoin('users as u', 'u.idpersona', '=', 'p.id')
                ->where('u.id', $usuario)
                ->select(DB::raw("CONCAT(p.apellidos, ' ', p.nombres) AS nombre"))
                ->first();
      
        $vehiculo =  $TransitoImpuesto->vehiculo;
        $cliente = $TransitoImpuesto->cliente;
        $transitoimpuestoconcepto = $TransitoImpuesto->conceptos;

        foreach($transitoimpuestoconcepto as $key => $data){
           
            if($data->codigo=='RTV'){
                $obtener= DB::connection('pgsql')
                ->table('sgm_transito.clase_tipo_vehiculo')
                ->where('valor', $data->pivot->valor)
                ->pluck('descripcion')
                ->toArray();
                
                $transitoimpuestoconcepto[$key]->agrupado=$obtener;
            }           

        }
       
        $fecha_documento=$TransitoImpuesto->created_at;
        $fecha_hoy=date('d-m-Y', strtotime($fecha_documento));

        // $fecha_hoy=date('Y-m-d');
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
        
        return ['error'=>false, 'data'=> $transitoimpuestoconcepto,'info'=>$dataArray, 'persona'=>$persona];

    }

    public function guardarConcepto(Request $request){
        try {

            $valida=TransitoConcepto::where('concepto',$request->txt_concepto)
            ->where('anio',date('Y'))
            ->first();
            if(!is_null($valida)){
                if($valida->estado=="A"){
                    return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
                }else{
                    $valida->concepto=$request->txt_concepto;
                    $valida->estado='A';
                    $valida->valor=$request->valor_concepto;
                    $valida->save();
                    return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];
                }
            }

            $guardaConcepto= new TransitoConcepto;
            $guardaConcepto->concepto=$request->txt_concepto;
            $guardaConcepto->valor=$request->valor_concepto;
            $guardaConcepto->estado='A';
            $guardaConcepto->anio=date('Y');
            $guardaConcepto->save();

            return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function actualizarConcepto(Request $request, $id){
        try {

            $valida=TransitoConcepto::where('concepto',$request->txt_concepto)
            // ->where('estado','A')
            ->where('anio',date('Y'))
            ->where('id','!=',$id)
            ->first();
            if(!is_null($valida)){
                return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
            }

            $actualizarConcepto= TransitoConcepto::find($id);
            // dd($actualizarConcepto);
            $actualizarConcepto->concepto=$request->txt_concepto;
            $actualizarConcepto->valor=$request->valor_concepto;
            $actualizarConcepto->estado='A';
            $actualizarConcepto->save();

            return ["mensaje"=>"Informacion actualizada exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function verificaArchivo($tipo){
        try{
            $certificado = null;

            if ($tipo === "Recaudador") {
                $certificado = DB::connection('mysql')
                    ->table('archivo_p12 as pdoce')
                    ->select('archivo', 'password')
                    ->where('pdoce.id_usuario', auth()->user()->id)
                    ->where('pdoce.estado', 'A')
                    ->first();
            } else {
                $certificado = DB::connection('mysql')
                    ->table('area as a')
                    ->leftJoin('jefe_area as ja', 'ja.id_area', '=', 'a.id_area')
                    ->leftJoin('archivo_p12 as pdoce', 'pdoce.id_usuario', '=', 'ja.id_usuario')
                    ->select('archivo', 'password')
                    ->where('a.descripcion', $tipo)
                    ->where('a.estado', 'A')
                    ->where('ja.estado', 'A')
                    ->where('pdoce.estado', 'A')
                    ->first();
            }

            if (is_null($certificado)) {
                // return [
                //     'error' => true,
                //     'mensaje' => "No existe un Certificado Vigente para el encargado de $tipo"
                // ];
            }

            return [
                'error' => false,
                'resultado' => $certificado
            ];


        }catch(\Throwable $th){
            \Log::error("TransitoImpuestoController =>verificaArchivo =>sms => ".$th->getMessage());
            return [
                "error"=>true,
                "mensaje"=>'Ocurrió un error, al generar el documento'
            ];
        }
    }

    public function firmarDocumento($nombre_documento_firmar, $archivoFirmaRentas, $claveFirmaRentas, $archivoFirmaTesoreria, $claveFirmaTesoreria, $archivoFirmaRecaudador, $claveFirmaRecaudador){

        // $consultaCertRenta=$this->verificaArchivo('Tesoreria');
        // if($consultaCertRenta['error']==true){
        //     return ['error' => true, 'mensaje' => $consultaCertRenta['mensaje']];
        // }
        // $desencriptado = Crypt::decrypt($consultaCertRenta['resultado']->password);
        
       
        $nombre_archivo_certificado=$archivoFirmaRentas;
        $desencriptado = Crypt::decrypt($claveFirmaRentas);
        $clave_certificado=$desencriptado;
        $prefijofinal="_firmado";
        //firma rentas
        $response = $this->clienteFirmador->request('POST', '/tics-soporte/api/firma-p12', [
            'multipart' => [
                [
                    'name'     => 'certificado',
                    'contents' => fopen(storage_path('app/documentosFirmar/'.$nombre_archivo_certificado), 'r'),
                    'filename' => $nombre_archivo_certificado
                ],
                [
                    'name'     => 'documento',
                    'contents' => fopen(storage_path('app/documentosRentas/'.$nombre_documento_firmar), 'r'),
                    'filename' => $nombre_documento_firmar
                ],
                [
                    'name'     => 'clave',
                    'contents' => $clave_certificado
                ],
              
                [
                    'name'     => 'nombre_pdf',
                    'contents' =>  $nombre_documento_firmar
                ],
                [
                    'name'     => 'nombre_p12',
                    'contents' =>  $nombre_archivo_certificado
                ],

                [
                    'name'     => 'tipo',
                    'contents' =>  'rentas'
                ]
            ],
        ]);

        $responseBody = json_decode($response->getBody(), true);
        
        if($responseBody["error"]!=true){
            //firma  tesoreria   

            // $consultaCertRenta=$this->verificaArchivo('Tesoreria');
            // if($consultaCertRenta['error']==true){
            //     return ['error' => true, 'mensaje' => $consultaCertRenta['mensaje']];
            // }
            $desencriptado = Crypt::decrypt($claveFirmaTesoreria);
       
            $nombre_archivo_certificado=$archivoFirmaTesoreria;
            $clave_certificado=$desencriptado;

            $nombreSinExtension = explode('.', $nombre_documento_firmar)[0];   
            $archivo_tesoreria=$nombreSinExtension."".$prefijofinal.".pdf";      
            $response = $this->clienteFirmador->request('POST', '/tics-soporte/api/firma-p12', [
                'multipart' => [
                    [
                        'name'     => 'nombre_pdf',
                        'contents' => $archivo_tesoreria,
                    ],
                    [
                        'name'     => 'nombre_p12',
                        'contents' =>  $nombre_archivo_certificado
                    ],
                    [
                        'name'     => 'clave',
                        'contents' => $clave_certificado
                    ],
                    [
                        'name'     => 'tipo',
                        'contents' =>  'tesoreria'
                    ]
                ],
            ]);

            $responseBody = json_decode($response->getBody(), true);

            if($responseBody["error"]!=true){
                $nombreSinExtension = explode('.', $archivo_tesoreria)[0];   
                $archivo_recaudador=$nombreSinExtension."".$prefijofinal.".pdf";     
                //firma recaudador   
                
                // $consultaCertRenta=$this->verificaArchivo('Recaudador');
                // if($consultaCertRenta['error']==true){
                //     return ['error' => true, 'mensaje' => $consultaCertRenta['mensaje']];
                // }
                $desencriptado = Crypt::decrypt($claveFirmaRecaudador);
        
                $nombre_archivo_certificado=$archivoFirmaRecaudador;
                $clave_certificado=$desencriptado;


                $response = $this->clienteFirmador->request('POST', '/tics-soporte/api/firma-p12', [
                    'multipart' => [
                        [
                            'name'     => 'nombre_pdf',
                            'contents' =>  $archivo_recaudador
                        ],
                        [
                            'name'     => 'nombre_p12',
                            'contents' =>  $nombre_archivo_certificado
                        ],
                        [
                            'name'     => 'clave',
                            'contents' => $clave_certificado
                        ],
                        [
                            'name'     => 'tipo',
                            'contents' =>  'recaudador'
                        ]
                    ],
                ]);

                $responseBody = json_decode($response->getBody(), true);

                if($responseBody["error"]!=true){
                    $archivo=$archivo_recaudador;
                    $response = $this->clienteFirmador->request('GET', "/tics-soporte/api/documento/{$archivo}", [
                        'headers' => [
                            'Accept' => 'application/pdf',
                        ],
                    ]);

                    if ($response->getStatusCode() === 200) {
                        $contenidoPDF = $response->getBody()->getContents();

                        // Guardar en disco local del Proyecto A
                        \Storage::disk('local')->put("documentosRentas/{$archivo}", $contenidoPDF);
                        
                        return ['error' => false, 'mensaje' => 'Archivo generado exitosamente.', 'pdf'=>$archivo];
                    } else {
                        return ['error' => true, 'mensaje' => 'Error al obtener el PDF.'];
                    }

                    
                }else{
                    return ["mensaje"=>"Ocurrio un error al realizar la firma de RECAUDADOR ", "error"=>true];
                }

            }else{
                return ["mensaje"=>"Ocurrio un error al realizar la firma de TESORERIA ", "error"=>true];
            }
        }else{
            return ["mensaje"=>"Ocurrio un error al realizar la firma de RENTAS ", "error"=>true];
        }

       

    }



}
