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


class FirmaElectronicaController extends Controller
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
        return view('firma.index');
    }

    public function datatable(Request $r)
    {
        if($r->ajax()){
            $lista_archivo = FirmaElectronica::whereIn('estado',['A','I'])
            ->where('id_usuario',auth()->user()->id)
            ->get();
            return Datatables($lista_archivo)
            ->addColumn('f_emision', function ($lista_archivo) {
                return $lista_archivo->f_emision;
            })
            ->addColumn('f_expiracion', function ($lista_archivo) {
                return $lista_archivo->f_expiracion;
            })
            ->addColumn('created_at', function ($lista_archivo) {
                return $lista_archivo->created_at;
            })
            ->editColumn('estado', function($listaPatente){
                if($listaPatente->estado == 'A'){
                    return 'Vigente';
                }else{
                    return 'Caducado';
                }
            })
        
            ->addColumn('action', function ($lista_archivo) {
                return'
                <a class="btn btn-danger btn-sm" onclick="eliminar(\''.$lista_archivo->id.'\')">Eliminar</a>';
            })

            ->rawColumns(['action','contribuyente','vehiculo','estado'])
            ->make(true);
        }
    }

    public function mantenimiento(Request $request)
    {
        DB::beginTransaction(); // Iniciar la transacción

        try {

            $existe=FirmaElectronica::where('id_usuario',auth()->user()->id)
            ->where('estado','A')
            ->first();

            if(!is_null($existe)){
                return ["mensaje"=>"Usted ya tiene un archivo activo", "error"=>true];
            }

            
            if ($request->hasFile('p12') && $request->file('p12')->isValid()) {
            
                $archivo_certificado = $request->file('p12');
                $clave_certificado=$request->password;
                $clave_certificado = mb_convert_encoding($clave_certificado, 'UTF-8', 'UTF-8');
                $extension = pathinfo($archivo_certificado->getClientOriginalName(), PATHINFO_EXTENSION);
                if($extension != "p12"){ 
                    return ['error'=>true,'mensaje'=>"El archivo del certificado debe ser formato .p12"];
                }

                $nombre_archivo_certificado = $archivo_certificado->getClientOriginalName();
                
                $hash = md5($nombre_archivo_certificado); 
                $extension = $archivo_certificado->getClientOriginalExtension(); 
                $nombreP12 = $hash . '.' . $extension;  
               
                $path = $archivo_certificado->storeAs('documentosFirmar', $nombreP12);
                try{
                    $response = $this->clienteFirmador->request('POST', '/tics-soporte/api/verificar-p12', [
                        'multipart' => [
                            [
                                'name'     => 'certificado',
                                'contents' => fopen(storage_path('app/documentosFirmar/'.$nombreP12), 'r'),
                                'filename' => $nombreP12
                            ],
                        
                            [
                                'name'     => 'clave',
                                'contents' => $clave_certificado
                            ],
                            
                        ],
                    ]);
                    
                   
                    $responseBody = json_decode($response->getBody(), true);                  
                
                    if($responseBody["error"]==true){
                        return['error'=>true,'mensaje'=>$responseBody["mensaje"]];
                    }
                                        
                    $f_desde=$responseBody["info"]["fecha_de"];
                    $f_expiracion=$responseBody["info"]["fecha_hasta"];
                    $propietario=$responseBody["info"]["propietario"];

                    $guardaArchivo=new FirmaElectronica();
                    $guardaArchivo->archivo=$nombreP12;
                    $guardaArchivo->f_emision=$f_desde;
                    $guardaArchivo->f_expiracion=$f_expiracion;
                    $guardaArchivo->propietario=$propietario;
                    $guardaArchivo->estado='A';
                    $guardaArchivo->id_usuario=auth()->user()->id;
                    $guardaArchivo->password=Crypt::encrypt($clave_certificado);
                    $guardaArchivo->save();

                    DB::commit();
                    return['error'=>false,'mensaje'=>'El documento electronico fue subido exitosamente'];
                } catch (\GuzzleHttp\Exception\RequestException $e) {
                    // dd($e);
                // Capturar el error 500 de la API
                    if ($e->hasResponse()) {
                        $body = $e->getResponse()->getBody()->getContents();

                        // Opcional: intentar ver si hay JSON
                        $json = json_decode($body, true);
                        if (json_last_error() === JSON_ERROR_NONE) {
                            return ['error' => true, 'mensaje' => $json['mensaje'] ?? 'Error al validar el certificado'];
                        } else {
                            // Si es HTML (como en tu caso con error 500)
                            return ['error' => true, 'mensaje' => 'La API devolvió un error interno. Verifique la clave del certificado.'];
                        }
                    }

                    return ['error' => true, 'mensaje' => 'Error al comunicarse con la API: ' . $e->getMessage()];
                }
            }
        } catch (\Exception $e) {
            DB::rollback(); // Revertir cambios en caso de error
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al cargar archivo: ' . $e->getMessage()
            ];
        }
    }

    public function eliminar($id){

        try{    
           
            $firma=FirmaElectronica::where('id',$id)->first();
            $firma->estado='E';
            $firma->save();

            return [
                'error' => false,
                'mensaje' => 'Informacion eliminada exitosamente'
            ];

        } catch (\Exception $e) {

            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al generar la patente: ' . $e->getMessage()
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

    
}