<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;
use App\Models\FirmaElectronica;
use App\Models\Area;
use Illuminate\Support\Facades\Crypt;
use App\Models\User;
use Exception;
use GuzzleHttp\Client;
use SimpleSoftwareIO\QrCode\Facades\QrCode;


class ParteDiarioController extends Controller
{
    private $clienteFirmador = null;

    public function __construct(){
        try{
           
            $ip2="http://192.168.0.77:82/";

            $this->clienteFirmador = new Client([
                'base_uri' =>$ip2,
                'verify' => false,
            ]);

        }catch(Exception $e){
            Log::error($e->getMessage());
        }
    }

    public function index(){
        return view('parte_diario.index');
    }

    public function consultar($fecha){
        $response = $this->clienteFirmador->request('GET', "/soporte-desarrollo/pdf-parte-diario/{$fecha}", [
            'headers' => [
                'Accept' => 'application/pdf',
            ],
        ]);

        $responseBody = json_decode($response->getBody(), true);
        if($responseBody['error']==true){
            return ['error' => true, 'mensaje' => $responseBody['mensaje']];
        }

        $response = $this->clienteFirmador->request('GET', "/soporte-desarrollo/documento-parte/{$responseBody['pdf']}", [
            'headers' => [
                'Accept' => 'application/pdf',
            ],
        ]);

        if ($response->getStatusCode() === 200) {
            $contenidoPDF = $response->getBody()->getContents();

            // Guardar en disco local del Proyecto A
            \Storage::disk('local')->put("public/{$responseBody['pdf']}", $contenidoPDF);
            
            return ['error' => false, 'mensaje' => 'Archivo generado exitosamente.', 'pdf'=>$responseBody['pdf']];
        } else {
            return ['error' => true, 'mensaje' => 'Error al obtener el PDF.'];
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
}