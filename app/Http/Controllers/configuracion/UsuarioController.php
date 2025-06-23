<?php

namespace App\Http\Controllers\configuracion;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\DB;
use App\Models\User;
use Exception;
use Illuminate\Console\View\Components\Warn;
use Spatie\Permission\Models\Permission;
use Spatie\Permission\Models\Role;
use Illuminate\Support\Facades\Gate;

class UsuarioController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        Gate::authorize('lista_usuario', User::class);
        return view('configuraciones.usuarioListar');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        Gate::authorize('crear_usuario', User::class);
        return view('configuraciones.usuarioCrear');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $r)
    {
        try {
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
                'persona_id' => 'bail|required|unique:users,idpersona',
                'password' => 'bail|required|min:6|max:250|confirmed',
                'password_confirmation' => 'bail|required|min:6|max:250',
                'status' => 'bail|required'
            ];
            $validator = Validator::make($r->all(), $reglas,$messages);//->validate();
            $validator->after(function ($validator) {
                if($validator->errors()->has('persona_id')){
                    $validator->errors()->add('inputNombresPersona', 'Seleccione a una persona');
                }
            });


            if ($validator->fails()) {
                return redirect('/usuario')
                            ->withErrors($validator)
                            ->withInput();
            }

            $u = new User;
            $u->name = $r->name;
            $u->email = $r->email;
            $u->password = bcrypt($r->password);
            $u->idpersona = $r->persona_id;
            $u->status = $r->status;
            $u->save();
            return redirect()->route('create.usuario')->with('success', 'Se creo el usuario correctamente');
        } catch (Exception $e) {
            return redirect()->route('create.usuario')->with('error', 'Hubo un problema al crear el usuario');
        }
    }

    /**
     * Display the specified resource.
     */
    public function show(User $user)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Request $r, String $id)
    {
        $User = User::find($id);
        $permissions = Permission::orderBy('id', 'asc')->get();
        $roles = Role::orderBy('id', 'asc')->get();
        $rolPermissionuser = $User->getPermissionsViaRoles()->pluck('name','id');
        $roluser = $User->getRoleNames()->first();
        $permissionsuser = $User->permissions->pluck('name','id');
        return view('configuraciones.usuarioEditar', compact('User','permissions','roles','roluser','rolPermissionuser','permissionsuser'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $r, String $id)
    {
        try {
            $User = User::find($id);
            $messages = [
                'required' => 'El campo :attribute es requerido.',
                'unique' => 'El dato del campo :attribute ya existe',
                'size' => 'El campo :attribute debe tener exactamente :size caracteres',
                'max' => 'El campo :attribute no debe exceder los :max caracteres',
                'confirmed' => 'El campo :attribute no coincide la contraseña'
            ];
            $reglas = [
                'name' => 'bail|required|max:250',
                'email' => ['bail','required','max:250',Rule::unique('users', 'email')->ignore($id)],
                'persona_id' => ['bail','required',Rule::unique('users', 'idpersona')->ignore($User->idpersona)],
                'status' => 'bail|required',

            ];
            $validator = Validator::make($r->all(), $reglas,$messages);//->validate();
            $validator->after(function ($validator) {
                if($validator->errors()->has('persona_id')){
                    $validator->errors()->add('inputNombresPersona', 'Seleccione a una persona');
                }
            });


            if ($validator->fails()) {
                return redirect('usuario/'.$id.'/edit')
                            ->withErrors($validator)
                            ->withInput();
            }

            $User->name = $r->name;
            $User->email = $r->email;
            $User->password = bcrypt($r->password);
            $User->idpersona = $r->persona_id;
            $User->status = $r->status;
            $User->save();
            return redirect()->route('edit.usuario',$id)->with('success', 'Se actualizo el usuario correctamente');
        } catch (Exception $e) {
            return redirect()->route('edit.usuario',$id)->with('error', 'Hubo un problema al actualizar el usuario : '.$e->getMessage());
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {

    }

    public function datatables(Request $r){
        $User = User::orderBy("id", "desc")->get();
        return Datatables($User)
                ->editColumn('status', function($User){
                    if($User->status == 1){
                        return '<span class="badge text-bg-success">Activo</span>';
                    }else{
                        return '<span class="badge text-bg-danger">Inactivo</span>';
                    }
                })
                ->addColumn('action', function ($User) {
                    $botonesCita = '';
                    $botonesCita .= '<a href="'.route('show.remision',$User->id).'" class="btn btn-primary btn-sm"><i class="bi bi-eye"></i></a> ';
                    $botonesCita .= '<a href="'.route('edit.usuario',$User->id).'" class="btn btn-warning btn-sm"><i class="bi bi-pencil-square"></i></a> ';

                    return $botonesCita;
                })
                ->rawColumns(['action','status'])
                ->make(true);
    }

    public function rolusuario(Request $request)
    {
        $user = User::findOrFail($request->usuarioId);
        $user->syncRoles($request->rol); // Reemplaza todos los roles previos con este
        return response()->json([
            'guardado' => 'ok'
        ]);
    }

    public function Permisousuario(Request $request)
    {
        $user = User::findOrFail($request->usuarioId);
        $user->syncPermissions($request->permisos); // <-- importante
        return response()->json([
            'guardado' => 'ok'
        ]);
    }
}
