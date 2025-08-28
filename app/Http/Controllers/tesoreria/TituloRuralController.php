<?php

namespace App\Http\Controllers\tesoreria;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Exception;
use Barryvdh\DomPDF\Facade\Pdf;
use GuzzleHttp\Client;
use Illuminate\Support\Facades\Gate;
use App\Models\PsqlLiquidacion;

class TituloRuralController extends Controller
{
     private $clientMunicipio = null;

    public function __construct(){

        try{

            $this->clientMunicipio = new Client([
                // 'base_uri' =>'http://192.168.0.77:81/sgm-api/api/',
                'base_uri' =>'http://192.168.0.77:81/sgm-api/api/',
                'verify' => false,
            ]);

        }catch(Exception $e){
            dd($e);
        }
    }
    public function index(){
        //Gate::authorize('impresion_titulos_rur', PsqlLiquidacion::class);
        $num_predio = 0;
        return view('tesoreria.TitulosRural',compact('num_predio'));
    }

    public function consultaTitulos($tipo,$valor)
    {
        try {

            $listaTitulo = $this->clientMunicipio->request('GET', "buscar-titulo-rural/{$tipo}/{$valor}",[
                'headers' => [
                    'Authorization' => ''
                ] ,
                'connect_timeout' => 30,
                'timeout' => 30
            ]);


            $info= json_decode((string) $listaTitulo->getBody());
            if($info->error==true){
                return [
                    'error'=>true,
                    'mensaje'=>'Ocurrió un error al consultar la informacion'
                ];
            }
            return ["resultado"=>$info->resultado, "error"=>false];

            // $liquidacionRural=\DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            // ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            // ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            // ->select('cv.Pre_CodigoCatastral','cv.CarVe_FechaEmision','cv.CarVe_NumTitulo','cv.CarVe_CI'
            // ,'cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','cv.CarVe_Nombres','cv.CarVe_ValorEmitido'
            // ,'cv.CarVe_TasaAdministrativa')
            // ->where(function($query)use($tipo,$valor) {
            //     if($tipo==1){
            //         $query->where('CarVe_CI', '=', $valor);
            //     }else{
            //         $query->where('cv.Pre_CodigoCatastral', '=', $valor);
            //     }
                
            // })
            
            // ->whereIn('cv.CarVe_Estado',['E'])
            // // ->where('Pre_Tipo','Rural')
            // ->orderby('CarVe_NumTitulo','desc')
            // ->get();

            // foreach($liquidacionRural as $key=> $data){
            //     $anio=explode("-",$data->CarVe_NumTitulo);
            //     $consultaInteresMora=\DB::connection('sqlsrv')->table('INTERES_MORA as im')
            //     ->where('IntMo_Año',$anio)
            //     ->select('IntMo_Valor')
            //     ->first();

            //     $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->CarVe_ValorEmitido - $data->CarVe_TasaAdministrativa));
                
            //     $valor=number_format($valor,2);

            //     $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
            //     $liquidacionRural[$key]->intereses=$valor;

            //     $total_pago=$valor +$data->CarVe_ValorEmitido;
            //     $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);
            // }

            // return ["resultado"=>$liquidacionRural, "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function reportetest(Request $r){
        try{

            $data = $this->clientMunicipio->request('POST', "tituloscoactivarural/imprimir",[
                'headers' => [
                    'Content-Type' => 'application/json'
                ],
                'body' => json_encode($r->all())
            ]);

            $data=json_decode((string) $data->getBody());

            if($data->error==true){
                return [
                    'error'=>true,
                    'mensaje'=>'Ocurrió un error al consultar la informacion'
                ];
            }

            // $dataArray = array();
            // foreach($r->checkLiquidacion as $clave => $valor){
            //     $liquidacionRural=\DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            //     ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            //     ->leftJoin('PROPIETARIO as pr', 'pr.Ciu_Cedula', '=', 'cv.CarVe_CI')
            //     ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            //     ->select('cv.Pre_CodigoCatastral','cv.CarVe_FechaEmision','cv.CarVe_NumTitulo','cv.CarVe_CI'
            //     ,'cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','p.Pre_NombrePredio','cv.CarVe_ValTotalTerrPredio'
            //     ,'cv.CarVe_ValTotalEdifPredio','cv.CarVe_ValOtrasInver','cv.CarVe_ValComerPredio','cv.CarVe_RebajaHipotec'
            //     ,'cv.CarVe_BaseImponible','cv.CarVe_IPU','cv.CarVe_TasaAdministrativa','cv.CarVe_Bomberos'
            //     ,'cv.CarVe_ValorEmitido','pr.Pro_DireccionDomicilio' ,'cv.CarVe_TasaAdministrativa')
            //     ->where('CarVe_NumTitulo', '=', $valor)
            //     ->get();

            //     foreach($liquidacionRural as $key=> $data){
            //         $anio=explode("-",$data->CarVe_NumTitulo);
            //         $consultaInteresMora=\DB::connection('sqlsrv')->table('INTERES_MORA as im')
            //         ->where('IntMo_Año',$anio)
            //         ->select('IntMo_Valor')
            //         ->first();

            //         $valor=(($consultaInteresMora->IntMo_Valor/100) * ($data->CarVe_ValorEmitido - $data->CarVe_TasaAdministrativa));
                    
            //         $valor=number_format($valor,2);

            //         $liquidacionRural[$key]->porcentaje_intereses=$consultaInteresMora->IntMo_Valor;
            //         $liquidacionRural[$key]->intereses=$valor;

            //         $total_pago=$valor +$data->CarVe_ValorEmitido;
            //         $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);
            //     }
                         
            //     array_push($dataArray, $liquidacionRural);
            // }

            $fecha_hoy=date('Y-m-d');
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
            $fecha_timestamp = strtotime($fecha_hoy);
            $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);
            $data = [
                'title' => 'Reporte de liquidacion',
                'date' => date('m/d/Y'),
                'DatosLiquidacion' => $data->resultado,
                'fecha_formateada'=>$fecha_formateada
            ];

            $nombrePDF="reporteTituloRural.pdf";

            $pdf = PDF::loadView('reportes.reporteTitulosRural',$data);

            $estadoarch = $pdf->stream();

            \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk('public')->exists($nombrePDF);
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


        }catch (\Throwable $e) {

            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'.$e
            ]);

        }
    }

     public function descargarPdf($archivo){
        try{

            $exists_destino = \Storage::disk('public')->exists($archivo);

            if($exists_destino){
                return response()->download( storage_path('app/public/'.$archivo))->deleteFileAfterSend(true);
            }else{
                return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
            }

        } catch (\Throwable $th) {
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
        }
    }

    public function buscarContribuyenteRural(Request $request){

        // $data = $this->clientMunicipio->request('POST', "tituloscoactivarural/buscarContribuyente",[
        //     'headers' => [
        //         'Content-Type' => 'application/json'
        //     ],
        //     'body' => json_encode($request->all())
        // ]);

        // $data=json_decode((string) $data->getBody());

        // // dd($data);

        // if($data->error==true){
        //     return [
        //         'error'=>true,
        //         'mensaje'=>'Ocurrió un error al consultar la informacion'
        //     ];
        // }

        $data = [];
        if($request->has('q')){
            $search = $request->q;
            $data = \DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw('UPPER(cv.CarVe_Nombres) LIKE UPPER(?)', ['%' . $search . '%'])
            ->select('Pre_CodigoCatastral','CarVe_Nombres','CarVe_CI')
            ->distinct('Pre_CodigoCatastral')
            ->take(10)->get();

        }
        // return ["resultado"=>$data, "error"=>false];

        return response()->json($data);
    }

}
