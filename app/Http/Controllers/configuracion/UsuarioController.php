<?php

namespace App\Http\Controllers\configuracion;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use App\Models\User;

class UsuarioController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('configuraciones.usuarioListar');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('configuraciones.usuarioCrear');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $r)
    {
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

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }

    public function datatables(Request $r){
        $User = User::orderBy("id", "desc")->get();
        return Datatables($User)
                ->addColumn('action', function ($User) {
                    $botonesCita = '';
                    $botonesCita .= '<a href="'.route('show.remision',$User->id).'" class="btn btn-primary btn-sm"><i class="bi bi-eye"></i></a> ';

                    return $botonesCita;
                })
                ->rawColumns(['action'])
                ->make(true);
    }
}
