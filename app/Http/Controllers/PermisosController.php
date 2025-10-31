<?php

namespace App\Http\Controllers;
use \Log;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Rol;
use App\Models\RolPermiso;
use DB;
use Carbon\Carbon;
use Illuminate\Support\Facades\Gate;

class PermisosController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        //Gate::authorize('index', PsqlPaPatente::class);
         $permisos=DB::table('permissions')
        ->get();
        return view('permisos.index',[
            "permisos"=>$permisos
        ]);
    }

    public function guardarPermiso(Request $request){
        
        $transaction=DB::transaction(function() use($request){
            try{ 
                $ExistePermiso = RolPermiso::where('role_id', $request->id_rol_selecc)
                ->where('permission_id', $request->permisos)
                ->first();

                if(!is_null($ExistePermiso)) {
                    return response()->json([
                        'error' => true,
                        'mensaje' => 'Ya existe el permiso para este rol.'
                    ]);
                    
                }
                
                $guardaPermiso=new RolPermiso();
                $guardaPermiso->permission_id=$request->permisos;
                $guardaPermiso->role_id=$request->id_rol_selecc;
                $guardaPermiso->save();

                return response()->json([
                    'error'=>false,
                    'mensaje'=>'Permiso Agregado Exitosamente'
                ]);

            }catch (\Throwable $e) {
               
                DB::Rollback();
                Log::error('RolController => guardarPermiso => mensaje => '.$e->getMessage().' linea => '.$e->getLine());
                return response()->json([
                    'error'=>true,
                    'mensaje'=>'Ocurrió un error'
                ]);
                
            }
        });
        return $transaction;
    }

    public function listar(){
        try{
           
            $roles =Rol::with('permiso_rol')->get();

            return response()->json([
                'error'=>false,
                'resultado'=>$roles
            ]);
        }catch (\Throwable $e) {
            Log::error('RolController => listar => mensaje => '.$e->getMessage());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error '.$e
            ]);
            
        }
    }

    public function verPermisos($id){
        try{
           
            $roles =Rol::with('permiso_rol')
            ->where('id',$id)
            ->get();

            return response()->json([
                'error'=>false,
                'resultado'=>$roles[0]
            ]);
        }catch (\Throwable $e) {
            Log::error('RolController => verPermisos => mensaje => '.$e->getMessage());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'
            ]);
            
        }
    }

    public function eliminarPermiso($idpermiso,$idrol){
        try{
           
            $roles=DB::table('role_has_permissions')
            ->where('permission_id', $idpermiso)
            ->where('role_id', $idrol)
            ->delete();

            return response()->json([
                'error'=>false,
                'mensaje'=>'Informacion eliminada exitosamente'
            ]);
        }catch (\Throwable $e) {
            Log::error('RolController => eliminarPermiso => mensaje => '.$e->getMessage());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'
            ]);
            
        }
    }
}
