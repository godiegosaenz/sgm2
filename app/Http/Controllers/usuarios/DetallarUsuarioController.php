<?php

namespace App\Http\Controllers\usuarios;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\User;
use App\models\Persona;

class DetallarUsuarioController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function index(Request $r, $id,$id2){
        $usuario = User::find($id);
        $persona = Persona::find($id2);

        return view('auth.detallar',['usuario' => $usuario, 'persona' => $persona]);
    }
}
