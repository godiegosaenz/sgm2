<?php

namespace App\Http\Controllers\usuarios;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Validator;
use Illuminate\Validation\Rule;
use App\User;


class CrearUsuarioController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function index(){

        return view('auth.crear');
    }

    public function guardar(Request $r){
        $messages = [
            'required' => 'El campo :attribute es requerido.',
            'unique' => 'El dato del campo :attribute ya existe',
            'size' => 'El campo :attribute debe tener exactamente :size caracteres',
            'max' => 'El campo :attribute no debe exceder los :max caracteres',
            'confirmed' => 'El campo :attribute no coincide la contraseña'
        ];
        $reglas = [
            'name' => 'bail|required|max:250',
            'email' => 'bail|required|max:250|unique:users,email',
            'idpersona' => 'bail|required|unique:users,idpersona',
            'password' => 'bail|required|min:6|max:250|confirmed',
            'password_confirmation' => 'bail|required|min:6|max:250'
        ];
        $validator = Validator::make($r->all(), $reglas,$messages);//->validate();
        $validator->after(function ($validator) {
            if($validator->errors()->has('idpersona')){
                $validator->errors()->add('txtpersonanombre', 'Seleccione a una persona');
            }
        });


        if ($validator->fails()) {
            return redirect('/api/usuario/crear')
                        ->withErrors($validator)
                        ->withInput();
        }

        $u = new User;
        $u->name = $r->name;
        $u->email = $r->email;
        $u->password = bcrypt($r->password);
        $u->idpersona = $r->idpersona;
        $u->save();
        return redirect('/api/usuario/detallar/'.$u->id.'/persona/'.$r->idpersona);
    }

    public function verificarUsuario(Request $r){
        if($r->ajax()){
            $verificar = User::where('idpersona',$r->idpersona)->exists();
            if($verificar == true){
                echo 'SI';
            }else{
                echo 'NO';
            }

        }
    }
}
