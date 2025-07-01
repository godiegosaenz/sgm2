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
                // 'base_uri' =>'http://192.168.0.68:81/sgm-api/api/',
                'base_uri' =>'http://192.168.0.68:81/sgm-api/api/',
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
                'connect_timeout' => 10,
                'timeout' => 10
            ]);


            $info= json_decode((string) $listaTitulo->getBody());
            if($info->error==true){
                return [
                    'error'=>true,
                    'mensaje'=>'Ocurrió un error al consultar la informacion'
                ];
            }

            return ["resultado"=>$info->resultado, "error"=>false];

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

        $data = $this->clientMunicipio->request('POST', "tituloscoactivarural/buscarContribuyente",[
            'headers' => [
                'Content-Type' => 'application/json'
            ],
            'body' => json_encode($request->all())
        ]);

        $data=json_decode((string) $data->getBody());

        // dd($data);

        if($data->error==true){
            return [
                'error'=>true,
                'mensaje'=>'Ocurrió un error al consultar la informacion'
            ];
        }


        return response()->json($data->resultado);
    }

}
