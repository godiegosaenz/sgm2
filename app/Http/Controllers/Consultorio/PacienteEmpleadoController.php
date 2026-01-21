<?php

namespace App\Http\Controllers\Consultorio;
use App\Models\Consultorio\EmpleadoPaciente;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use DB;
use App\models\Cita;

class PacienteEmpleadoController extends Controller
{
    public function index(){
        return view('consultorio.registro_paciente');
    }

    public function guardaEmpleadoPaciente(Request $request){
        try{
            // dd($request->all());
            $buscaPaciente=EmpleadoPaciente::where('cedula',$request->cedula_empleado)
            ->first();
            if(!is_null($buscaPaciente)){
             
                $buscaPaciente->cedula=$request->cedula_empleado;
                $buscaPaciente->primer_apellido=$request->primer_apellido;
                $buscaPaciente->segundo_apellido=$request->segundo_apellido;
                $buscaPaciente->primer_nombre=$request->primer_nombre;
                $buscaPaciente->segundo_nombre=$request->segundo_nombre;
                $buscaPaciente->fecha_nacimiento=$request->fecha_nacimiento;
                $buscaPaciente->correo=$request->correo;
                $buscaPaciente->telefono=$request->telefono;
                $buscaPaciente->sexo=$request->sexo;
                $buscaPaciente->grupo_sanguinedad=$request->grupo_sanguineo;
                $buscaPaciente->lateralidad=$request->lateridad;
                $buscaPaciente->estado='A';
                $buscaPaciente->idusuario_actualiza=auth()->user()->persona->id;
                $buscaPaciente->fecha_actualiza=date('Y-m-d H:i:s');
                $buscaPaciente->save();
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false];
            }else{
                $nuevoPaciente= new EmpleadoPaciente();
                $nuevoPaciente->cedula=$request->cedula_empleado;
                $nuevoPaciente->primer_apellido=$request->primer_apellido;
                $nuevoPaciente->segundo_apellido=$request->segundo_apellido;
                $nuevoPaciente->primer_nombre=$request->primer_nombre;
                $nuevoPaciente->segundo_nombre=$request->segundo_nombre;
                $nuevoPaciente->fecha_nacimiento=$request->fecha_nacimiento;
                $nuevoPaciente->correo=$request->correo;
                $nuevoPaciente->telefono=$request->telefono;
                $nuevoPaciente->sexo=$request->sexo;
                $nuevoPaciente->grupo_sanguinedad=$request->grupo_sanguineo;
                $nuevoPaciente->lateralidad=$request->lateridad;
                $nuevoPaciente->estado='A';
                $nuevoPaciente->idusuario_registra=auth()->user()->persona->id;
                $nuevoPaciente->fecha_registro=date('Y-m-d H:i:s');
                $nuevoPaciente->save();
                return ["mensaje"=>"Informacion regsitrada exitosamente ", "error"=>false];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function infoEmpleadoPaciente($id){
        try{
            $info=EmpleadoPaciente::where('cedula',$id)
            ->where('estado','A')->first();
          
            return ["resultado"=>$info, "error"=>false];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function evolucionEmpleadoPaciente($id){
        try{
            $info=EmpleadoPaciente::where('id',$id)
            ->where('estado','A')->first();
          
            return ["resultado"=>$info, "error"=>false];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function dataEmpleadoPaciente(Request $request){
        try{
            $tipo=$request->tipo_busq;
            $valor=$request->valor;
           
            $data=DB::connection('pgsql')->table('sgm_consultorio.empleado')
            ->where(function($query)use($valor, $tipo){
                if($tipo==2){
                    $query->where(DB::raw("CONCAT(primer_nombre, ' ', segundo_nombre, ' ',primer_apellido, ' ', segundo_apellido)"), 'ilike', '%'.$valor.'%')
                    ->orwhere(DB::raw("CONCAT(primer_apellido, ' ', segundo_apellido, ' ', primer_nombre, ' ', segundo_nombre )"), 'ilike', '%'.$valor.'%');
                }else{
                    $query->where('estado','A')->get();
                }
            })            
            ->select('id','cedula',DB::raw("CONCAT(primer_apellido, ' ', segundo_apellido, ' ', primer_nombre, ' ', segundo_nombre) AS nombre"),'telefono','correo')
            ->take(50)->get();
            
          
            return ["resultado"=>$data, "error"=>false];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }
}