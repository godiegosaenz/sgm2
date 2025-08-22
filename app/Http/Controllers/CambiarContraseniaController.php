<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;
use App\Models\FirmaElectronica;
use App\Models\User;
use Exception;
use GuzzleHttp\Client;
use Illuminate\Support\Facades\Validator;
use Hash;
use SimpleSoftwareIO\QrCode\Facades\QrCode;


class CambiarContraseniaController extends Controller
{
    public function index(){
        return view('contrasenia.index');
    }

    public function cambiar(Request $request){

         try {
            // dd($request->all());
            $validator = Validator::make($request->all(), [
                'password_act' => 'required|min:6|string|regex:/^[a-zA-Z0-9_\-@$&#.]{6,18}$/',
                'password_new' => 'required|min:6|string|regex:/^[a-zA-Z0-9_\-@$&#.]{6,18}$/',
            ]);
            if ($validator->fails()) {    
                return response()->json([
                    'error' => true, 
                    'mensaje' => 'Contraseña debe tener mínimo 6 caracteres'
                ]);

            }
            $usuario= auth()->User();

            if (Hash::check($request['password_act'], $usuario->password)){
            
                if($request['password_new']==$request['password_rep']){

                    if (Hash::check($request['password_new'], $usuario->password)){

                        return response()->json([
                            'error'=>true,
                            'mensaje' => 'La nueva contraseña no puede ser igual a la anterior'
                        ]);

                    }else{

                        $usuario->password=bcrypt($request['password_new']);
                        if($usuario->save()){
                            return response()->json(['error'=>false,'mensaje'=>'Contraseña actualizada exitosamente']);
                        }
                        else{
                            return response()->json(['error'=>true,'mensaje'=>'Error, inténtelo nuevamente']);
                        }

                    }
                    
                }else{
                    return response()->json(['error'=>true,'mensaje'=>'Las contraseñas no coinciden']);
                } 
            }else{
                return response()->json(['error'=>true,'mensaje'=>'La contraseña actual ingresada no es la correcta por favor verificar']);
            }

        } catch (\Throwable $th) {
            \Log::error('CambiarContraseniaController,CambiarContrasenia:' . $th->getMessage()); 
            return response()->json(['error'=>true,'mensaje'=>'Incovenientes al procesar la solicitud, intente nuevamente']);

        }

    }

}