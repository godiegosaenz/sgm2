<?php

namespace App\Http\Controllers\coactiva;

use App\Http\Controllers\Controller;
use App\Models\Coactiva\Convenio;
use App\Models\Coactiva\CuotaConvenio;
use App\Models\Coactiva\Pago;
use App\Models\Coactiva\DataCoa;
use App\Models\Coactiva\DataNotifica;
use App\Models\Coactiva\InfoCoa;
use App\Models\Coactiva\InfoNotifica;
use App\Models\Coactiva\Medidas;
use App\Models\Coactiva\Secuencial;
use App\Models\PsqlLiquidacion;
use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Gate;
use App\Http\Controllers\TitulosPredial\LiquidacionesController;

class CoactivaController extends Controller
{  
    private $coactiva= null;
    public function __construct() {
        $this->coactiva = new NotificacionesController;
    }
    public function index()
    {
        if(!Auth()->user()->hasPermissionTo('Procesos Coactiva'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('coactiva.lista_coactiva');
    }

    public function tablaCoactiva($periodo){
        try{
           
            $mes = $periodo; 
            $inicio = $mes . '-01';
            $fin = date("Y-m-t", strtotime($inicio)); // último día del mes

            $datos=InfoCoa::with('data','notificacion')
            ->whereBetween('fecha_registra', [$inicio.' 00:00:00', $fin.' 23:59:59'])
            ->select('*')
            ->selectRaw("CURRENT_DATE - DATE(fecha_registra) AS dias_transcurridos")
            ->get();
         

            foreach($datos as $key=> $data){
               
                $usuarioRegistra=DB::connection('mysql')->table('users as u')
                ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                ->where('u.id',$data->id_usuario_registra)
                ->select('p.nombres','p.apellidos','p.cedula')
                ->first();
                $datos[$key]->profesional=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;

                $num_proceso=str_pad($data->num_proceso, 3, "0", STR_PAD_LEFT).'-'.date('Y');
                $datos[$key]->num_proceso=$num_proceso;


            }
            // DD($datos);              

            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardarConvenio(Request $request){
        try{
            
            //***VALIDACIONES DE QUE NO HAYA OTRO CONVENIO****** */

            $verifica=Convenio::where('id_info_coact',$request->idcoa_conv)
            ->where('estado','Activo')
            ->first();
            if(!is_null($verifica)){
                return ["mensaje"=>"Ya existe un convenio activo", "error"=>true];
            }

            

            $total_deuda=0;
            
            //******BUSQUEDA DE DEUDAS PREDIALES URBANAS********************************* */

            $verifica=DataNotifica::where('id_info_notifica',$request->idnotifica)
            ->select('num_titulo')
            ->first();
            if(is_null($verifica->num_titulo)){
               
                $obtenerLiquidacion=DataNotifica::where('id_info_notifica',$request->idnotifica)
                ->select('id', 'id_liquidacion')
                ->get();
                
                foreach($obtenerLiquidacion as $data){
                
                    $buscaLiquidacion=PsqlLiquidacion::where('id',$data->id_liquidacion)
                    ->select('id_liquidacion')
                    ->first();
                    
                    if(!is_null($buscaLiquidacion)){
                        $data->num_titulo=$buscaLiquidacion->id_liquidacion;
                        $data->save();
                    }
                }
            }
           
            $dataNoti=DataNotifica::where('id_info_notifica',$request->idnotifica)
            ->whereNotNull('id_liquidacion')
            ->pluck('num_titulo')
            ->toArray();
            
            
            if(count($dataNoti)> 0){
                $total=$this->coactiva->tituloCreditoUrb($dataNoti);  
              
                foreach($total['data']['DatosLiquidaciones'] as $data){
                    $total_deuda=$total_deuda + $data[0]->total_complemento;
                }
            }
             
            //******BUSQUEDA DE DEUDAS PREDIALES RURALES********************************* */

            $dataNoti=DataNotifica::where('id_info_notifica',$request->idnotifica)
            ->whereNull('id_liquidacion')
            ->pluck('num_titulo')
            ->toArray();
            
            if(count($dataNoti)> 0){
                $total=$this->coactiva->tituloCreditoRural($dataNoti);            
            
                foreach($total['data']['DatosLiquidaciones'] as $data){
                    $total = $data[0]->total_pagar ? str_replace(',', '', $data[0]->total_pagar) : 0;
                    $total_deuda=$total_deuda + $total;
                }
            }             
           
            $total_deuda = (float) $total_deuda;
            $valorAdeudado = (float) $request->valor_adeudado;
          
            if(round($total_deuda,2) != round($valorAdeudado,2)){
                return [
                    "mensaje" => "El valor adeudado actual ha cambiado a $" . number_format($total_deuda,2),
                    "error" => true
                ];
            }
            
            $actualizaEstado=$this->actualizarEstadoCoa($request->idcoa_conv,'Conv','A');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }
            
            $guarda=new Convenio();
            $guarda->id_info_coact=$request->idcoa_conv;
            $guarda->valor_adeudado = (float) $request->valor_adeudado; // Convertir a float
            $guarda->cuota_inicial = (float) $request->cuota_inicial; // Convertir a float
            $guarda->numero_cuotas=$request->num_cuotas;
            $guarda->f_inicio=$request->f_ini;
            $guarda->f_fin=$request->f_fin;
            $guarda->usuario_registra=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $guarda->fecha_registra=date('Y-m-d H:i:s');
            $guarda->estado='Activo';
            $guarda->estado_pago='Debe';
            $guarda->valor_cancelado=0;
            $guarda->save();

          
            $fecha_ini = Carbon::parse($request->fecha_ini);
            for($i=0; $i<$request->num_cuotas; $i++){
                if($i==0){
                    $cuotas=new CuotaConvenio();
                    $cuotas->fecha=$guarda->f_inicio;
                    $cuotas->valor_cuota=(float) $guarda->cuota_inicial ;
                    $cuotas->estado='Pendiente';
                    $cuotas->id_convenio=$guarda->id;
                    $cuotas->cuota_inicial=true;
                    $cuotas->save();
                }
                $fechaCuota = $fecha_ini->copy()->addMonthsNoOverflow($i+1);
                $cuotas=new CuotaConvenio();
                $cuotas->fecha=$fechaCuota;
                $cuotas->valor_cuota=(float) $request->valor_cuotas;
                $cuotas->estado='Pendiente';
                $cuotas->id_convenio=$guarda->id;
                $cuotas->save();
            }

            return ["mensaje"=>"Informacion registrada exitosamente", "error"=>false];
            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage()." Linea ".$e->getLine(), "error"=>true];
        }
    }

    public function tablaConvenio($id){
        try{
           
            $datos=Convenio::where('id_info_coact',$id)
            ->get();
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function inactivarConvenio($id){
        try{
            
            $inactiva=Convenio::where('id',$id)
            ->first();
            $inactiva->usuario_elimina=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $inactiva->fecha_elimina=date('Y-m-d H:i:s');
            $inactiva->estado='Inactivo';
            $inactiva->save();

            $actualizaEstado=$this->actualizarEstadoCoa($inactiva->id_info_coact,'Conv','I');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }
         
            return ["mensaje"=>'Informacion eliminada exitosamente', "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function detalleConvenio($id){
        try{
            
            $detalle=CuotaConvenio::with('convenio')->where('id_convenio',$id)
            ->orderBy('id','asc')
            ->get();
            
            // $fecha = new \DateTime();
            // $formato = new \IntlDateFormatter(
            //     'es_ES',
            //     \IntlDateFormatter::LONG,
            //     \IntlDateFormatter::NONE
            // );
            // dd($formato);

            return ["resultado"=>$detalle, 
                // "fechaFormateada"=>$formato->format($fecha), 
                "error"=>false
                ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }
   
    public function guardarMedidas_old(Request $request)
    {
        DB::connection('pgsql')->beginTransaction();

        try {

            $verifica = Medidas::where('id_info_coact', $request->idcoa_medida)
                ->where('estado', 'Activo')
                ->first();

            if (!is_null($verifica)) {
                return ["mensaje" => "Ya existe una medida activa", "error" => true];
            }

            $actualizaEstado = $this->actualizarEstadoCoa($request->idcoa_medida, 'Medi', 'A');
            if ($actualizaEstado['error'] == true) {
                DB::connection('pgsql')->rollBack();
                return ["mensaje" => $actualizaEstado['mensaje'], "error" => true];
            }

            $guarda = new Medidas();
            $guarda->id_info_coact = $request->idcoa_medida;
            $guarda->total_deuda = (float) $request->total_valor_deuda;
            $guarda->medidas = $request->medidas_txt;
            $guarda->usuario_registra = auth()->user()->persona->apellidos . " " . auth()->user()->persona->nombres;
            $guarda->fecha_registra = now();
            $guarda->estado = 'Activo';

            /* ================== SECUENCIAL ================== */

            $ultimo_sec = Medidas::whereYear('fecha_registra', date('Y'))
                ->whereNotNull('num_oficio1')
                ->orderByDesc('num_oficio1')
                ->first();

            if (is_null($ultimo_sec)) {

                $secuencial = Secuencial::where('descripcion', 'Oficio')
                    ->where('anio', date('Y'))
                    ->where('estado', 'A')
                    ->lockForUpdate()
                    ->first();

                if (is_null($secuencial)) {

                    $num_oficio1 = 1;

                    Secuencial::create([
                        'secuencia' => 1,
                        'descripcion' => 'Oficio',
                        'estado' => 'A',
                        'anio' => date('Y')
                    ]);

                } else {

                    $secuencial->increment('secuencia');
                    $num_oficio1 = $secuencial->secuencia;
                }

            } else {

                $num_oficio1 = $ultimo_sec->num_oficio3 + 1;
            }
          

            $nombrePDF = "MedidasCoactiva" . date('YmdHis') . ".pdf";

            $guarda->num_oficio1 = $num_oficio1;
            $guarda->num_oficio2 = $num_oficio1 + 1;
            $guarda->num_oficio3 = $num_oficio1 + 2;
            $guarda->documento = $nombrePDF;
            $guarda->save();

            /* ================== CONSULTAS ================== */
            
            $consulta = $request->predio == "Urbano"
                ? $this->coactiva->consultarTitulosUrb($request->IdNotificaSele)
                : $this->coactiva->consultarTitulos($request->IdNotificaSele);

            if ($consulta['error'] == true) {
                DB::connection('pgsql')->rollBack();
                return ["mensaje" => $consulta['mensaje'], "error" => true];
            }
        
            $listado_final = [];
            $anios = [];
            
            foreach ($consulta["resultado"] as $item) {

                $anios[] = $item->anio;

                $clave = $request->predio == "Urbano" ? $item->num_predio : $item->clave;

                if (!isset($listado_final[$clave])) {
                    $listado_final[$clave] = [$item];
                } else {
                    $listado_final[$clave][] = $item;
                }

                $nombre_persona = $item->nombre_per ?? $item->nombre_contr1;
                $direcc_cont = $item->direcc_cont;
                $ci_ruc = $request->predio == "Urbano" ? $item->ci_ruc : $item->num_ident;
            }
            

            $anio_min = min($anios);
            $anio_max = max($anios);
            $rango = 'DESDE EL ' . $anio_min . ' HASTA EL EJERCICIO FISCAL ' . $anio_max;

            /* ================== CONSULTAS db ================== */
            $secr=DB::connection('pgsql')
                ->table('sgm_coactiva.parametro_coactiva')
                ->select('valor2')
                ->where('codigo','SECRETARIO')
                ->where('estado','A')
                ->first();

            $funcionarios=DB::connection('pgsql')
                ->table('sgm_coactiva.parametro_coactiva')
                ->selectRaw("
                    MAX(CASE WHEN codigo = 'TESO' THEN valor END) AS tesorera,
                    MAX(CASE WHEN codigo = 'JUEZ_COACT' THEN valor END) AS juez_coactiva,
                    MAX(CASE WHEN codigo = 'SECRETARIO' THEN valor END) AS secretario
                ")
                ->whereIn('codigo', ['TESO','JUEZ_COACT','SECRETARIO'])
                ->where('estado','A')
                ->first();

            $secuencial_data=DB::connection('pgsql')
                ->table('sgm_coactiva.info_coact')
                ->where('id',$request->idcoa_medida)
                ->select('num_proceso')
                ->first();
        
            /* ================== GENERAR PDF ================== */

            $pdf = \PDF::loadView('reportes.medidasCoact', [
                'DatosLiquidacion' => $listado_final,
                "nombre_persona" => $nombre_persona,
                "direcc_cont" => $direcc_cont,
                "ci_ruc" => $ci_ruc,
                "rango" => $rango,
                "medidas" => $guarda,
                "lugar_predio" => $request->predio,
                "secuencial"=>$secuencial_data,
                "secr"=>$secr->valor2,
                "funcionarios"=>$funcionarios
            ]);

            $contenidoPDF = $pdf->output();

            $disco = "disksCoactiva";
            Storage::disk($disco)->put($nombrePDF, $contenidoPDF);

            if (!Storage::disk($disco)->exists($nombrePDF)) {
                DB::rollBack();
                return [
                    'error' => true,
                    'mensaje' => 'No se pudo crear el documento'
                ];
            }

            /* ================== COMMIT ================== */

            DB::connection('pgsql')->commit();

            return ["mensaje" => "Información registrada exitosamente", "error" => false];

        } catch (\Exception $e) {

            DB::connection('pgsql')->rollBack();

            // Si el PDF ya se creó, lo eliminamos
            if (isset($nombrePDF) && Storage::disk("disksCoactiva")->exists($nombrePDF)) {
                Storage::disk("disksCoactiva")->delete($nombrePDF);
            }

            return [
                "mensaje" => "Ocurrió un error: " . $e->getMessage(). $e->getLine(),
                "error" => true
            ];
        }
    }

     public function guardarMedidas(Request $request)
    {
        DB::connection('pgsql')->beginTransaction();

        try {

            $verifica = Medidas::where('id_info_coact', $request->idcoa_medida)
                ->where('estado', 'Activo')
                ->first();

            if (!is_null($verifica)) {
                return ["mensaje" => "Ya existe una medida activa", "error" => true];
            }

            $actualizaEstado = $this->actualizarEstadoCoa($request->idcoa_medida, 'Medi', 'A');
            if ($actualizaEstado['error'] == true) {
                DB::connection('pgsql')->rollBack();
                return ["mensaje" => $actualizaEstado['mensaje'], "error" => true];
            }

            $guarda = new Medidas();
            $guarda->id_info_coact = $request->idcoa_medida;
            $guarda->total_deuda = (float) $request->total_valor_deuda;
            $guarda->medidas = $request->medidas_txt;
            $guarda->usuario_registra = auth()->user()->persona->apellidos . " " . auth()->user()->persona->nombres;
            $guarda->fecha_registra = now();
            $guarda->estado = 'Activo';

            /* ================== SECUENCIAL ================== */

            $ultimo_sec = Medidas::whereYear('fecha_registra', date('Y'))
                ->whereNotNull('num_oficio1')
                ->orderByDesc('num_oficio1')
                ->first();

            if (is_null($ultimo_sec)) {

                $secuencial = Secuencial::where('descripcion', 'Oficio')
                    ->where('anio', date('Y'))
                    ->where('estado', 'A')
                    ->lockForUpdate()
                    ->first();

                if (is_null($secuencial)) {

                    $num_oficio1 = 1;

                    Secuencial::create([
                        'secuencia' => 1,
                        'descripcion' => 'Oficio',
                        'estado' => 'A',
                        'anio' => date('Y')
                    ]);

                } else {

                    $secuencial->increment('secuencia');
                    $num_oficio1 = $secuencial->secuencia;
                }

            } else {

                $num_oficio1 = $ultimo_sec->num_oficio3 + 1;
            }
          

            $nombrePDF = "MedidasCoactiva" . date('YmdHis') . ".pdf";

            $guarda->num_oficio1 = $num_oficio1;
            $guarda->num_oficio2 = $num_oficio1 + 1;
            $guarda->num_oficio3 = $num_oficio1 + 2;
            $guarda->documento = $nombrePDF;
            $guarda->save();

            /* ================== CONSULTAS ================== */

            $consulta=$this->coactiva->consultarTitulosUrb($request->IdNotificaSele);
            
            if($consulta['error']==true){
                DB::connection('pgsql')->rollBack();
                return ["mensaje"=>$consulta['mensaje'], "error"=>true];
                
            }
            
            $consulta1=$this->coactiva->consultarTitulos($request->IdNotificaSele);
            if($consulta1['error']==true){
                DB::connection('pgsql')->rollBack();
                return ["mensaje"=>$consulta1['mensaje'], "error"=>true];
                
            }

            $todo = $consulta["resultado"]->merge($consulta1["resultado"]);
           
            $listado_final = [];
            $anios = [];
            
            foreach ($todo as $key => $item){ 
                $anios[] = $item->anio;
                if(isset($item->num_predio)){            
                    if(!isset($listado_final[$item->num_predio])) {
                        $listado_final[$item->num_predio]=array($item);
                
                    }else{
                        array_push($listado_final[$item->num_predio], $item);
                    }
                }else{
                    if(!isset($listado_final[$item->clave])) {
                        $listado_final[$item->clave]=array($item);
            
                    }else{
                        array_push($listado_final[$item->clave], $item);
                    }
                }

                $nombre_persona=$item->nombre_per;
                $direcc_cont=$item->direcc_cont;
                if(isset($item->num_ident)){
                    $ci_ruc=$item->num_ident;
                }
                if(isset($item->ci_ruc)){
                    $ci_ruc=$item->ci_ruc;
                }
            }
                       

            $anio_min = min($anios);
            $anio_max = max($anios);
            $rango = 'DESDE EL ' . $anio_min . ' HASTA EL EJERCICIO FISCAL ' . $anio_max;

            /* ================== CONSULTAS db ================== */
            $secr=DB::connection('pgsql')
                ->table('sgm_coactiva.parametro_coactiva')
                ->select('valor2')
                ->where('codigo','SECRETARIO')
                ->where('estado','A')
                ->first();

            $funcionarios=DB::connection('pgsql')
                ->table('sgm_coactiva.parametro_coactiva')
                ->selectRaw("
                    MAX(CASE WHEN codigo = 'TESO' THEN valor END) AS tesorera,
                    MAX(CASE WHEN codigo = 'JUEZ_COACT' THEN valor END) AS juez_coactiva,
                    MAX(CASE WHEN codigo = 'SECRETARIO' THEN valor END) AS secretario
                ")
                ->whereIn('codigo', ['TESO','JUEZ_COACT','SECRETARIO'])
                ->where('estado','A')
                ->first();

            $secuencial_data=DB::connection('pgsql')
                ->table('sgm_coactiva.info_coact')
                ->where('id',$request->idcoa_medida)
                ->select('num_proceso')
                ->first();
        
            /* ================== GENERAR PDF ================== */

            $pdf = \PDF::loadView('reportes.medidasCoact', [
                'DatosLiquidacion' => $listado_final,
                "nombre_persona" => $nombre_persona,
                "direcc_cont" => $direcc_cont,
                "ci_ruc" => $ci_ruc,
                "rango" => $rango,
                "medidas" => $guarda,
                "lugar_predio" => $request->predio,
                "secuencial"=>$secuencial_data,
                "secr"=>$secr->valor2,
                "funcionarios"=>$funcionarios
            ]);

            $contenidoPDF = $pdf->output();

            $disco = "disksCoactiva";
            Storage::disk($disco)->put($nombrePDF, $contenidoPDF);

            if (!Storage::disk($disco)->exists($nombrePDF)) {
                DB::rollBack();
                return [
                    'error' => true,
                    'mensaje' => 'No se pudo crear el documento'
                ];
            }

            /* ================== COMMIT ================== */

            DB::connection('pgsql')->commit();

            return ["mensaje" => "Información registrada exitosamente", "error" => false];

        } catch (\Exception $e) {

            DB::connection('pgsql')->rollBack();

            // Si el PDF ya se creó, lo eliminamos
            if (isset($nombrePDF) && Storage::disk("disksCoactiva")->exists($nombrePDF)) {
                Storage::disk("disksCoactiva")->delete($nombrePDF);
            }

            return [
                "mensaje" => "Ocurrió un error: " . $e->getMessage().' Linea=> '. $e->getLine(),
                "error" => true
            ];
        }
    }

    public function tablaMedidas($id){
        try{
           
            $datos=Medidas::where('id_info_coact',$id)
            ->get();
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function inactivarMedidas($id){
        try{
           
            $inactiva=Medidas::where('id',$id)
            ->first();
            $inactiva->usuario_elimina=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $inactiva->fecha_elimina=date('Y-m-d H:i:s');
            $inactiva->estado='Inactivo';
            $inactiva->save();

            $actualizaEstado=$this->actualizarEstadoCoa($inactiva->id_info_coact,'Medi','I');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }
         
            return ["mensaje"=>'Informacion eliminada exitosamente', "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardarPago(Request $request){
        try{
            $verifica=Pago::where('id_info_coact',$request->idcoa_pago)
            ->where('estado','Activo')
            ->first();
            if(!is_null($verifica)){
                return ["mensaje"=>"Ya existe un pago activo", "error"=>true];
            }

            $actualizaEstado=$this->actualizarEstadoCoa($request->idcoa_pago,'Pago','A', $request->valor_cancelado);
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }

            $guarda=new Pago();
            $guarda->id_info_coact=$request->idcoa_pago;
            $guarda->valor_cancelado = (float) $request->valor_cancelado; // Convertir a float
            $guarda->usuario_registra=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $guarda->fecha_registra=date('Y-m-d H:i:s');
            $guarda->estado='Activo';
            $guarda->save();

            
            return ["mensaje"=>"Informacion registrada exitosamente", "error"=>false];
            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function tablaPagos($id){
        try{
           
            $datos=Pago::where('id_info_coact',$id)
            ->get();
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function inactivarPagos($id){
        try{
           
            $inactiva=Pago::where('id',$id)
            ->first();
            $inactiva->usuario_elimina=auth()->user()->persona->apellidos." ".auth()->user()->persona->nombres;
            $inactiva->fecha_elimina=date('Y-m-d H:i:s');
            $inactiva->estado='Inactivo';
            $inactiva->save();

            $actualizaEstado=$this->actualizarEstadoCoa($inactiva->id_info_coact,'Pago','I');
            if($actualizaEstado['error']==true){
                return ["mensaje"=>$actualizaEstado['mensaje'], "error"=>true];
            }
         
            return ["mensaje"=>'Informacion eliminada exitosamente', "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function actualizarEstadoCoa($id,$tipo,$estado, $valor=0){
         try{
            if($estado=="A"){
                if($tipo=="Conv"){
                    $actualizaCoac=InfoCoa::where('id',$id)->first();
                    if(!is_null($actualizaCoac)){
                        $actualizaCoac->estado_proceso=4;
                        $actualizaCoac->estado_pago='Debe';
                        $actualizaCoac->valor_cancelado=(float) $valor;
                        $actualizaCoac->save();

                        $actualizaPagoNot=InfoNotifica::where('id',$actualizaCoac->id_info_notifica)
                        ->first();
                        $actualizaPagoNot->valor_cancelado=(float) $actualizaCoac->valor_cancelado; 
                        $actualizaPagoNot->save();
                    }
                    
                    $inactivaMedidas=Medidas::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaMedidas)){
                        $inactivaMedidas->estado='Inactivo';
                        $inactivaMedidas->save();
                    }

                    $inactivaCancelado=Pago::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaCancelado)){
                        $inactivaCancelado->estado='Inactivo';
                        $inactivaCancelado->save();
                    }
                   

                }else if($tipo=="Medi"){
                    $actualizaCoac=InfoCoa::where('id',$id)
                    ->first();
                    if(!is_null($actualizaCoac)){
                       
                        $actualizaCoac->estado_proceso=2;
                        $actualizaCoac->estado_pago='Debe';
                        $actualizaCoac->valor_cancelado=(float) $valor;
                        $actualizaCoac->save();

                        $actualizaPagoNot=InfoNotifica::where('id',$actualizaCoac->id_info_notifica)
                        ->first();
                        $actualizaPagoNot->valor_cancelado=(float) $actualizaCoac->valor_cancelado; 
                        $actualizaPagoNot->save();
                    }
                    
                    $inactivaConv=Convenio::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaConv)){
                        $inactivaConv->estado='Inactivo';
                        $inactivaConv->save();
                    }

                    $inactivaCancelado=Pago::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaCancelado)){
                        $inactivaCancelado->estado='Inactivo';
                        $inactivaCancelado->save();
                    }
                   
                }else if($tipo=="Pago"){
                    $actualizaCoac=InfoCoa::where('id',$id)
                    ->first();
                    if(!is_null($actualizaCoac)){
                        $actualizaCoac->estado_proceso=3;
                        $actualizaCoac->estado_pago='Pagado';
                        $actualizaCoac->valor_cancelado=(float) $valor;
                        $actualizaCoac->save();

                        $actualizaPagoNot=InfoNotifica::where('id',$actualizaCoac->id_info_notifica)
                        ->first();
                        $actualizaPagoNot->valor_cancelado=(float) $actualizaCoac->valor_cancelado; 
                        $actualizaPagoNot->save();
                    }
                    
                    $inactivaMedidas=Medidas::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaMedidas)){
                        $inactivaMedidas->estado='Inactivo';
                        $inactivaMedidas->save();
                    }

                    $inactivaConv=Convenio::where('id_info_coact',$id)
                    ->where('estado','Activo')
                    ->first();
                    if(!is_null($inactivaConv)){
                        $inactivaConv->estado='Inactivo';
                        $inactivaConv->save();
                    }
                   
                }
            }else{
                $actualizaCoac=InfoCoa::where('id',$id)->first();
                if(!is_null($actualizaCoac)){
                    $actualizaCoac->estado_proceso=1;
                    $actualizaCoac->estado_pago='Debe';
                    $actualizaCoac->valor_cancelado=(float) $valor;
                    $actualizaCoac->save();

                    $actualizaPagoNot=InfoNotifica::where('id',$actualizaCoac->id_info_notifica)
                    ->first();
                    $actualizaPagoNot->valor_cancelado=(float) $actualizaCoac->valor_cancelado; 
                    $actualizaPagoNot->save();
                    
                }

            }
          
            return ["mensaje"=>'Informacion eliminada exitosamente', "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function vistaFiltra()
    {
        if(!Auth()->user()->hasPermissionTo('Busqueda Procesos'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('busqueda_coactiva.filtra_coactiva');
    }

    public function tablaCoactivaFiltra(Request $request){
        try{
            $data=$request->data;
            $datos=InfoCoa::with('data','notificacion')
            ->whereHas('notificacion', function($query) use($data) { // Filtramos por la relación 'notificacion'
                $query->whereHas('ente', function($query) use($data) { // Filtro en la relación 'ente' dentro de 'notificacion'
                    $query->where('ci_ruc', '=',$data )
                    ->orwhere(DB::raw("CONCAT(nombres, ' ', apellidos)"), 'ilike', '%'.$data.'%')
                    ->orwhere(DB::raw("CONCAT(apellidos, ' ', nombres )"), 'ilike', '%'.$data.'%');
                })
                ->orwhere('num_ident','=',$data)
                ->orwhere('contribuyente', 'ilike', '%'.$data.'%');
            })
            ->select('*')
            ->selectRaw("CURRENT_DATE - DATE(fecha_registra) AS dias_transcurridos")
            ->limit(10)
            ->get();
         

            foreach($datos as $key=> $data){
               
                $usuarioRegistra=DB::connection('mysql')->table('users as u')
                ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                ->where('u.id',$data->id_usuario_registra)
                ->select('p.nombres','p.apellidos','p.cedula')
                ->first();
                $datos[$key]->profesional=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;

            }
         
            return ["resultado"=>$datos, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }


}