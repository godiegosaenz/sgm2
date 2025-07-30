<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;
use App\Models\JefeArea;
use App\Models\Area;
use App\Models\User;
use Exception;

class AreaController extends Controller
{
    public function index(){
        $area = Area::where('estado','A')->get();
        $usuario = User::with('persona')->where('status',1)->get();
        return view('area.index',compact('area','usuario'));
    }

    public function datatable(Request $r)
    {
        if($r->ajax()){
            $lista_jefe = JefeArea::with('area','usuario')
            ->where('estado','A')
            ->get();
            return Datatables($lista_jefe)
            ->addColumn('cc_ruc', function ($lista_jefe) {
                return $lista_jefe->usuario->persona->cedula;
            })
            ->addColumn('empleado', function ($lista_jefe) {
                return $lista_jefe->usuario->persona->nombres.' '.$lista_jefe->usuario->persona->apellidos;
            })
            ->addColumn('area', function ($lista_jefe) {
                return $lista_jefe->area->descripcion;
            })
           
            ->addColumn('action', function ($lista_jefe) {
                return'
                <a class="btn btn-primary btn-sm" onclick="editar(\''.$lista_jefe->id_jefe_area.'\')">Editar</a>
                <a class="btn btn-danger btn-sm" onclick="eliminar(\''.$lista_jefe->id_jefe_area.'\')">Eliminar</a>';
            })

            ->rawColumns(['action','contribuyente','vehiculo'])
            ->make(true);
        }
    }

    public function mantenimiento(Request $request)
    {
        DB::beginTransaction(); // Iniciar la transacción

        try {

            $id_jefe_area=$request->jefe_area_id;
            $id_area=$request->area_id;
            $id_usuario=$request->user_id;

            $area_jefe=JefeArea::where('id_area',$id_area)
            ->where('estado','A')
            ->first();

            if(!is_null($area_jefe)){
                return ["mensaje"=>"El area ya se encuentra asignada a otro empleado", "error"=>true];
            }

            $usuario_jefe=JefeArea::where('id_usuario',$id_usuario)
            ->where('estado','A')
            ->first();

            if(!is_null($usuario_jefe)){
                return ["mensaje"=>"El empleado ya tiene asignado una area", "error"=>true];
            }

            if(!is_null($id_jefe_area)){
                $verifica=JefeArea::where('id_area',$id_area)
                ->where('id_usuario',$id_usuario)
                ->where('id_jefe_area','!=',$id_jefe_area)
                ->where('estado','A')
                ->first();

                if(!is_null($verifica)){
                    return ["mensaje"=>"El area ya se encuentra asignada a otro empleado", "error"=>true];
                }

                $actualiza=JefeArea::where('id_jefe_area',$id_jefe_area)->first();
                $actualiza->id_area=$id_area;
                $actualiza->id_usuario=$id_usuario;
                $actualiza->estado='A';
                $actualiza->save();

                DB::commit(); // Confirmar los cambios en la BD

                return [
                    'error' => false,
                    'mensaje' => 'Informacion actualizada exitosamente.',
                ];
            }

            $verifica=JefeArea::where('id_area',$id_area)
            ->where('id_usuario',$id_usuario)
            ->first();

            if(!is_null($verifica)){
                if($verifica->estado=="A"){
                    return ["mensaje"=>"El area ya se encuentra asignada a otro empleado", "error"=>true];
                }

                $verifica->id_area=$id_area;
                $verifica->id_usuario=$id_usuario;
                $verifica->estado='A';
                $verifica->save();

                DB::commit(); // Confirmar los cambios en la BD

                return [
                    'error' => false,
                    'mensaje' => 'Informacion actualizada exitosamente.',
                ];
            }

            $nuevo=new JefeArea();
            $nuevo->id_area=$id_area;
            $nuevo->id_usuario=$id_usuario;
            $nuevo->estado='A';
            $nuevo->save();
            
            DB::commit(); // Confirmar los cambios en la BD

            return [
                'error' => false,
                'mensaje' => 'Informacion registrada exitosamente.',
            ];

        } catch (\Exception $e) {
            DB::rollback(); // Revertir cambios en caso de error
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al generar la patente: ' . $e->getMessage()
            ];
        }
    }

    public function editar($id_jefe_area){
        try{    
            $jefe_area=JefeArea::where('id_jefe_area',$id_jefe_area)->first();
             return [
                'error' => false,
                'resultado' => $jefe_area
            ];
        } catch (\Exception $e) {
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al generar la patente: ' . $e->getMessage()
            ];
        }
    }

    public function eliminar($id_jefe_area){

        try{    
           
            $jefe_area=JefeArea::where('id_jefe_area',$id_jefe_area)->first();
            // dd($jefe_area);
            $jefe_area->estado='I';
            $jefe_area->save();

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

}