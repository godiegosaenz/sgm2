<?php

namespace App\Http\Controllers\Consultorio;
use App\Models\Consultorio\ActividadesExpo;
use App\Models\Consultorio\ActividadesExtras;
use App\Models\Consultorio\Antecedentes;
use App\Models\Consultorio\AntecedentesEmpleo;
use App\Models\Consultorio\AptitudMedica;
use App\Models\Consultorio\CabeceraAtencion;
use App\Models\Consultorio\ConstantesVitales;
use App\Models\Consultorio\Diagnostico;
use App\Models\Consultorio\DiagnosticoEmpleado;
use App\Models\Consultorio\EmpleadoPaciente;
use App\Http\Controllers\Controller;
use App\Models\Consultorio\Enfermedad;
use App\Models\Consultorio\EstiloActividad;
use App\Models\Consultorio\ExamenFisicoRegional;
use App\Models\Consultorio\MedicacionHabitual;
use App\Models\Consultorio\MedidasPreventivas;
use App\Models\Consultorio\MotivoConsulta;
use App\Models\Consultorio\ObservResultadosExamen;
use App\Models\Consultorio\Puesto;
use App\Models\Consultorio\Examenes;
use App\Models\Consultorio\RecomendacionTratamiento;
use App\Models\Consultorio\ResultadosExamen;
use App\Models\Consultorio\Retiro;
use Illuminate\Http\Request;
use DB;
use App\models\Cita;
use PHPUnit\Framework\Constraint\IsFalse;
use PHPUnit\Logging\TestDox\TestFailedSubscriber;
use function PHPUnit\Framework\returnArgument;

class PacienteEmpleadoController extends Controller
{
    public function index(){
        if(!Auth()->user()->hasPermissionTo('Evaluaciones Ocupacionales'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('consultorio.registro_paciente');
    }

    public function guardaEmpleadoPaciente(Request $request){
       
        try{

            $embarazada = $request->has('embarazada') ? true : false;
            $discapacidad = $request->has('discapacidad') ? true : false;
            $ecatastrofica = $request->has('ecatastrofica') ? true : false;
            $lactancia = $request->has('lactancia') ? true : false;
            $mayor_edad = $request->has('mayor_edad') ? true : false;

            $buscaPaciente=EmpleadoPaciente::where('cedula',$request->cedula_empleado)
            ->first();
            if(!is_null($buscaPaciente)){
             
                $buscaPaciente->cedula=$request->cedula_empleado;
                $buscaPaciente->primer_apellido=$request->primer_apellido;
                $buscaPaciente->segundo_apellido=$request->segundo_apellido;
                $buscaPaciente->primer_nombre=$request->primer_nombre;
                $buscaPaciente->segundo_nombre=$request->segundo_nombre;
                $buscaPaciente->fecha_nacimiento=$request->fecha_nacimiento;
                if(isset($request->correo)){
                    $buscaPaciente->correo=$request->correo;
                }
                if(isset($request->telefono)){
                    $buscaPaciente->telefono=$request->telefono;
                }
                $buscaPaciente->sexo=$request->sexo;
                $buscaPaciente->grupo_sanguinedad=$request->grupo_sanguineo;
                $buscaPaciente->lateralidad=$request->lateridad;

                $buscaPaciente->embarazada=$embarazada;
                $buscaPaciente->discapacidad=$discapacidad;
                $buscaPaciente->ecatastrofica=$ecatastrofica;
                $buscaPaciente->lactancia=$lactancia;
                $buscaPaciente->mayor_edad=$mayor_edad;

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
                 if(isset($request->correo)){
                    $nuevoPaciente->correo=$request->correo;
                }
                if(isset($request->telefono)){
                    $nuevoPaciente->telefono=$request->telefono;
                }
                
                $nuevoPaciente->sexo=$request->sexo;
                $nuevoPaciente->grupo_sanguinedad=$request->grupo_sanguineo;
                $nuevoPaciente->lateralidad=$request->lateridad;

                $nuevoPaciente->embarazada=$embarazada;
                $nuevoPaciente->discapacidad=$discapacidad;
                $nuevoPaciente->ecatastrofica=$ecatastrofica;
                $nuevoPaciente->lactancia=$lactancia;
                $nuevoPaciente->mayor_edad=$mayor_edad;

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

    public function actualizaSeccionA(Request $request){
        try{

            $embarazada = $request->has('embarazada') ? true : false;
            $discapacidad = $request->has('discapacidad') ? true : false;
            $ecatastrofica = $request->has('ecatastrofica') ? true : false;
            $lactancia = $request->has('lactancia') ? true : false;
            $mayor_edad = $request->has('mayor_edad') ? true : false;

            $buscaPaciente=EmpleadoPaciente::where('id',$request->id_empleado)
            ->first();
            if(!is_null($buscaPaciente)){
             
                $buscaPaciente->cedula=$request->num_cedula_empleado;
                $buscaPaciente->primer_apellido=$request->primer_apellido_empleado;
                $buscaPaciente->segundo_apellido=$request->segundo_apellido_empleado;
                $buscaPaciente->primer_nombre=$request->primer_nombre_empleado;
                $buscaPaciente->segundo_nombre=$request->segundo_nombre_empleado;
                $buscaPaciente->fecha_nacimiento=$request->fecha_nacimiento_empleado;
                $buscaPaciente->sexo=$request->sexo_empleado;
                $buscaPaciente->grupo_sanguinedad=$request->grupo_sanguineo_empleado;
                $buscaPaciente->lateralidad=$request->lateralidad_empleado;

                $buscaPaciente->embarazada=$embarazada;
                $buscaPaciente->discapacidad=$discapacidad;
                $buscaPaciente->ecatastrofica=$ecatastrofica;
                $buscaPaciente->lactancia=$lactancia;
                $buscaPaciente->mayor_edad=$mayor_edad;

                $buscaPaciente->estado='A';
                $buscaPaciente->idusuario_actualiza=auth()->user()->persona->id;
                $buscaPaciente->fecha_actualiza=date('Y-m-d H:i:s');
                $buscaPaciente->save();
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false];
            }else{
                return ["mensaje"=>"No se encontro el empleado", "error"=>true];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaPuesto(Request $request){
        try{
           
            $buscaPuesto=Puesto::where('descripcion',$request->nombre_puesto)
            ->first();
            if(!is_null($buscaPuesto)){
             
                $buscaPuesto->descripcion=$request->nombre_puesto;
                $buscaPuesto->estado='A';
                $buscaPuesto->save();
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false];
            }else{
                $nuevoPuesto=new Puesto();
                $nuevoPuesto->descripcion=$request->nombre_puesto;
                $nuevoPuesto->estado='A';
                $nuevoPuesto->save();
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaMotivo(Request $request){
        try{

            $cabeceraAtencion=new CabeceraAtencion();
            $cabeceraAtencion->id_usuario_registra=auth()->user()->id;
            $cabeceraAtencion->fecha_registro=date('Y-m-d H:i:s');
            $cabeceraAtencion->estado='Pendiente';
            $cabeceraAtencion->id_empleado=$request->id_empleado;
            $cabeceraAtencion->save();
           
            $buscaMotivo=MotivoConsulta::where('fecha_atencion',$request->fecha_atencion)
            ->where('estado','Borrador')
            ->where('id_cabecera_atencion',$cabeceraAtencion->id)
            ->orderBy('id','desc')
            ->first();
            if(!is_null($buscaMotivo)){             
                $buscaMotivo->id_puesto=$request->puesto_cmb;
                $buscaMotivo->fecha_atencion=$request->fecha_atencion;
                $buscaMotivo->fecha_ingreso_trabajo=$request->fecha_ingreso;
                $buscaMotivo->fecha_reintegro=$request->fecha_reingreso;
                $buscaMotivo->fecha_ultimo_dia_laboral=$request->fecha_ultimo_dia;
                $buscaMotivo->tipo_atencion=$request->tipo_atencion;
                $buscaMotivo->motivo=$request->motivo;
                $buscaMotivo->id_empleado=$request->id_empleado;
                $buscaMotivo->idusuario_actualiza=auth()->user()->persona->id;
                $buscaMotivo->fecha_actualiza=date('Y-m-d H:i:s');
                $buscaMotivo->id_cabecera_atencion=$cabeceraAtencion->id;
                $buscaMotivo->save();
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false, "idcabecera"=>$cabeceraAtencion->id];
            }else{
                $nuevoMotivo=new MotivoConsulta();
                $nuevoMotivo->id_puesto=$request->puesto_cmb;
                $nuevoMotivo->fecha_atencion=$request->fecha_atencion;
                $nuevoMotivo->fecha_ingreso_trabajo=$request->fecha_ingreso;
                $nuevoMotivo->fecha_reintegro=$request->fecha_reingreso;
                $nuevoMotivo->fecha_ultimo_dia_laboral=$request->fecha_ultimo_dia;
                $nuevoMotivo->tipo_atencion=$request->tipo_atencion;
                $nuevoMotivo->motivo=$request->motivo;
                $nuevoMotivo->estado='Borrador';
                $nuevoMotivo->id_empleado=$request->id_empleado;
                $nuevoMotivo->idusuario_registra=auth()->user()->persona->id;
                $nuevoMotivo->fecha_registro=date('Y-m-d H:i:s');
                $nuevoMotivo->id_cabecera_atencion=$cabeceraAtencion->id;
                $nuevoMotivo->save();
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false, "idcabecera"=>$cabeceraAtencion->id];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaAntecedentes(Request $request){
        try{
            // $verificaSeccionMotivo= MotivoConsulta::where('fecha_atencion',date('Y-m-d'))
            // ->where('estado','Borrador')
            // ->orderBy('id','desc')
            // ->first();
            // if(is_null($verificaSeccionMotivo)){
            //     return ["mensaje"=>"Debe completar primero la seccion B. MOTIVO DE CONSULTA", "error"=>true];
            // }
           
            $buscaAntecedentes=Antecedentes::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();
            if(!is_null($buscaAntecedentes)){             
                $buscaAntecedentes->id_cabecera_atencion=$request->IdCabeceraAtencion;
                $buscaAntecedentes->antecedentes_cq=$request->antecedente_cq;
                $buscaAntecedentes->antecedentes_familiares=$request->antecedente_familiares;
                $buscaAntecedentes->autoriza_transfusion=$request->autoriza_transfusion;
                $buscaAntecedentes->tratamiento_hormonal=$request->tratamiento_ormonal;                
                $buscaAntecedentes->observacion=$request->observacion_antecedentes;
                $buscaAntecedentes->id_empleado=$request->id_empleado;
                $buscaAntecedentes->idusuario_actualiza=auth()->user()->persona->id;
                $buscaAntecedentes->fecha_actualiza=date('Y-m-d H:i:s');
                $buscaAntecedentes->save();
                if($request->hombre_mujer=='Hombre'){
                    $buscaAntecedentes->planificacion_fam_masc=$request->metodo_planificacion_masculino;
                    $buscaAntecedentes->txt_planificacion_fam_masc=$request->txt_metodo_planificcion_familiar_masculino;
                    $eliminaExamenes=Examenes::where('id_seccion_antecedentes',$buscaAntecedentes->id)->delete();
                    if(sizeof($request->examenes_masculino)>0){
                        $tiempo_exa=$request->tiempo_masculino;
                        $resultado_exa=$request->resultado_masculino;
                        foreach($request->examenes_masculino as $key => $data){
                            if(!is_null($data)){
                                $examenes= new Examenes();
                                $examenes->id_seccion_antecedentes=$buscaAntecedentes->id;
                                $examenes->examen=$data;
                                $examenes->tiempo=$tiempo_exa[$key];
                                $examenes->resultado=$resultado_exa[$key];
                                $examenes->estado='Borrador';
                                $examenes->sexo='Hombre';
                                $examenes->save();
                            }
                        }
                    }
                                           
                    $buscaAntecedentes->planificacion_fam_fem=$request->metodo_planificacion;
                    $buscaAntecedentes->txt_planificacion_fem=$request->txt_metodo_planificcion_familiar;
                    $buscaAntecedentes->save();
                }else{

                    $buscaAntecedentes->planificacion_fam_masc=$request->metodo_planificacion_masculino;
                    $buscaAntecedentes->txt_planificacion_fam_masc=$request->txt_metodo_planificcion_familiar_masculino;
                    $eliminaExamenes=Examenes::where('id_seccion_antecedentes',$buscaAntecedentes->id)->delete();
                    if(sizeof($request->examenes)>0){
                        $tiempo_exa=$request->tiempo;
                        $resultado_exa=$request->resultado;
                        foreach($request->examenes as $key => $data){
                            if(!is_null($data)){
                                $examenes= new Examenes();
                                $examenes->id_seccion_antecedentes=$buscaAntecedentes->id;
                                $examenes->examen=$data;
                                $examenes->tiempo=$tiempo_exa[$key];
                                $examenes->resultado=$resultado_exa[$key];
                                $examenes->estado='Borrador';
                                $examenes->sexo='Mujer';
                                $examenes->save();
                            }
                        }
                    }
                    $buscaAntecedentes->planificacion_fam_fem=$request->metodo_planificacion;
                    $buscaAntecedentes->txt_planificacion_fem=$request->txt_metodo_planificcion_familiar;
                    $buscaAntecedentes->fecha_ultima_menstruacion=$request->fecha_ultima_menstruacion;
                    if(isset($request->gestas)){
                        $buscaAntecedentes->gestas=true;
                    }
                    if(isset($request->partos)){
                        $buscaAntecedentes->partos=true;
                    }
                    if(isset($request->cesareas)){
                        $buscaAntecedentes->cesareas=true;
                    }
                    if(isset($request->abortos)){
                         $buscaAntecedentes->abortos=true;
                    }
                    $buscaAntecedentes->save();

                }

                $eliminaEstiloVida=EstiloActividad::where('id_seccion_antecedentes',$buscaAntecedentes->id)->delete();
                $tiempo_act=$request->tiempo;
                foreach($request->activida_fisica as $key => $data){
                    if(!is_null($data)){
                        $estilo=new EstiloActividad();
                        $estilo->id_seccion_antecedentes=$buscaAntecedentes->id;
                        $estilo->cual=$data;
                        $estilo->estado='Borrador';
                        $estilo->tiempo=$tiempo_act[$key];
                        $estilo->save();
                    }
                }

                $eliminaMedicacion=MedicacionHabitual::where('id_seccion_antecedentes',$buscaAntecedentes->id)->delete();

                $tiempo_medicacion=$request->tiempo_medicacion;
                foreach($request->activida_fisica as $key => $data){
                    if(!is_null($data)){
                        $medicacion=new MedicacionHabitual();
                        $medicacion->id_seccion_antecedentes=$buscaAntecedentes->id;
                        $medicacion->cual=$data;
                        $medicacion->estado='Borrador';
                        $medicacion->tiempo=$tiempo_medicacion[$key];
                        $medicacion->save();
                    }
                }
               
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>IsFalse];
            }else{

                $nuevoAntecedentes=new Antecedentes();
                $nuevoAntecedentes->id_cabecera_atencion=$request->IdCabeceraAtencion;
                $nuevoAntecedentes->antecedentes_cq=$request->antecedente_cq;
                $nuevoAntecedentes->antecedentes_familiares=$request->antecedente_familiares;
                $nuevoAntecedentes->autoriza_transfusion=$request->autoriza_transfusion;
                $nuevoAntecedentes->tratamiento_hormonal=$request->tratamiento_ormonal;                
                $nuevoAntecedentes->observacion=$request->observacion_antecedentes;
                $nuevoAntecedentes->id_empleado=$request->id_empleado;
                $nuevoAntecedentes->estado='Borrador';
                $nuevoAntecedentes->idusuario_registra=auth()->user()->persona->id;
                $nuevoAntecedentes->fecha_registro=date('Y-m-d H:i:s');
                $nuevoAntecedentes->save();
                if($request->hombre_mujer=='Hombre'){
                    $nuevoAntecedentes->planificacion_fam_masc=$request->metodo_planificacion_masculino;
                    $nuevoAntecedentes->txt_planificacion_fam_masc=$request->txt_metodo_planificcion_familiar_masculino;
                    if(sizeof($request->examenes_masculino)>0){
                        $tiempo_exa=$request->tiempo_masculino;
                        $resultado_exa=$request->resultado_masculino;
                        foreach($request->examenes_masculino as $key => $data){
                            if(!is_null($data)){
                                $examenes= new Examenes();
                                $examenes->id_seccion_antecedentes=$nuevoAntecedentes->id;
                                $examenes->examen=$data;
                                $examenes->tiempo=$tiempo_exa[$key];
                                $examenes->resultado=$resultado_exa[$key];
                                $examenes->estado='Borrador';
                                $examenes->sexo='Hombre';
                                $examenes->save();
                            }
                        }
                    }

                    $nuevoAntecedentes->planificacion_fam_fem=$request->metodo_planificacion;
                    $nuevoAntecedentes->txt_planificacion_fem=$request->txt_metodo_planificcion_familiar;
                    $nuevoAntecedentes->save();
                }else{

                    $nuevoAntecedentes->planificacion_fam_masc=$request->metodo_planificacion_masculino;
                    $nuevoAntecedentes->txt_planificacion_fam_masc=$request->txt_metodo_planificcion_familiar_masculino;
                    if(sizeof($request->examenes)>0){
                        $tiempo_exa=$request->tiempo_exa;
                        $resultado_exa=$request->resultado;
                        foreach($request->examenes as $key => $data){
                            if(!is_null($data)){
                                $examenes= new Examenes();
                                $examenes->id_seccion_antecedentes=$nuevoAntecedentes->id;
                                $examenes->examen=$data;
                                $examenes->tiempo=$tiempo_exa[$key];
                                $examenes->resultado=$resultado_exa[$key];
                                $examenes->estado='Borrador';
                                $examenes->sexo='Mujer';
                                $examenes->save();
                            }
                        }
                    }
                    $nuevoAntecedentes->planificacion_fam_fem=$request->metodo_planificacion;
                    $nuevoAntecedentes->txt_planificacion_fem=$request->txt_metodo_planificcion_familiar;
                    $nuevoAntecedentes->fecha_ultima_menstruacion=$request->fecha_ultima_menstruacion;
                    $nuevoAntecedentes->fecha_ultima_menstruacion=$request->fecha_ultima_menstruacion;
                    if(isset($request->gestas)){
                        $nuevoAntecedentes->gestas=true;
                    }
                    if(isset($request->partos)){
                        $nuevoAntecedentes->partos=true;
                    }
                    if(isset($request->cesareas)){
                        $nuevoAntecedentes->cesareas=true;
                    }
                    if(isset($request->abortos)){
                        $nuevoAntecedentes->abortos=true;
                    }
                    $nuevoAntecedentes->save();

                }

                $tiempo_act=$request->tiempo;
                foreach($request->activida_fisica as $key => $data){
                    if(!is_null($data)){
                        $estilo=new EstiloActividad();
                        $estilo->id_seccion_antecedentes=$nuevoAntecedentes->id;
                        $estilo->cual=$data;
                        $estilo->estado='Borrador';
                        $estilo->tiempo=$tiempo_act[$key];
                        $estilo->save();
                    }
                }

                $tiempo_medicacion=$request->tiempo_medicacion;
                foreach($request->medicacion as $key => $data){
                    if(!is_null($data)){
                        $medicacion=new MedicacionHabitual();
                        $medicacion->id_seccion_antecedentes=$nuevoAntecedentes->id;
                        $medicacion->cual=$data;
                        $medicacion->estado='Borrador';
                        $medicacion->tiempo=$tiempo_medicacion[$key];
                        $medicacion->save();
                    }
                }
               
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage(), "error"=>true];
        }
    }

    public function guardaEnfemedadProblemaActual(Request $request){
        try{
            // $verificaAntecedente= Antecedentes::where('id',$request->IdAntecedentesRegistrado)
            // ->where('estado','Borrador')
            // ->orderBy('id','desc')
            // ->first();
            // if(is_null($verificaAntecedente)){
            //     return ["mensaje"=>"Debe completar primero la seccion C. ANTECEDENTES PERSONALES", "error"=>true];
            // }

            $buscaEnfermedad=Enfermedad::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();
            if(!is_null($buscaEnfermedad)){             
                $buscaEnfermedad->enfermedad_problema=$request->enfermedad_problema_actual;
                $buscaEnfermedad->id_empleado=$request->id_empleado;
                $buscaEnfermedad->idusuario_actualiza=auth()->user()->persona->id;
                $buscaEnfermedad->fecha_actualiza=date('Y-m-d H:i:s');
                $buscaEnfermedad->save();
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false];
            }else{
                $nuevaEnfermedad=new Enfermedad();
                $nuevaEnfermedad->id_cabecera_atencion=$request->IdCabeceraAtencion;
                $nuevaEnfermedad->enfermedad_problema=$request->enfermedad_problema_actual;
                $nuevaEnfermedad->estado='Borrador';
                $nuevaEnfermedad->id_empleado=$request->id_empleado;
                $nuevaEnfermedad->idusuario_registra=auth()->user()->persona->id;
                $nuevaEnfermedad->fecha_registro=date('Y-m-d H:i:s');
                $nuevaEnfermedad->save();
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaConstantesVitales(Request $request){
        try{
            // $verificaEnfermedad= Enfermedad::where('id',$request->IdEnfermedadregistrada)
            // ->where('estado','Borrador')
            // ->orderBy('id','desc')
            // ->first();
           
            // if(is_null($verificaEnfermedad)){
            //     return ["mensaje"=>"Debe completar primero la seccion D. ENFERMEDAD O PROBLEMA ACTUAL", "error"=>true];
            // }

            $buscaConstante=ConstantesVitales::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();
            if(!is_null($buscaConstante)){             
                $buscaConstante->temperatura=$request->temperatura;
                $buscaConstante->id_empleado=$request->id_empleado;
                $buscaConstante->presion_arterial=$request->presion_arterial;
                $buscaConstante->frecuencia_cardiaca=$request->frecuencia_cardiaca;
                $buscaConstante->frecuencia_respiratoria=$request->frecuencia_respiratoria;
                $buscaConstante->saturacion=$request->saturacion;
                $buscaConstante->peso=$request->peso;
                $buscaConstante->talla=$request->talla;
                $buscaConstante->imc=$request->imc;
                $buscaConstante->perimetro_abdominal=$request->perimetro_abdominal;
                $buscaConstante->idusuario_actualiza=auth()->user()->persona->id;
                $buscaConstante->fecha_actualiza=date('Y-m-d H:i:s');
                $buscaConstante->save();
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false];
            }else{
                $nuevaConstante=new ConstantesVitales();
                $nuevaConstante->id_cabecera_atencion=$request->IdCabeceraAtencion;
                $nuevaConstante->temperatura=$request->temperatura;
                $nuevaConstante->id_empleado=$request->id_empleado;
                $nuevaConstante->presion_arterial=$request->presion_arterial;
                $nuevaConstante->frecuencia_cardiaca=$request->frecuencia_cardiaca;
                $nuevaConstante->frecuencia_respiratoria=$request->frecuencia_respiratoria;
                $nuevaConstante->saturacion=$request->saturacion;
                $nuevaConstante->peso=$request->peso;
                $nuevaConstante->talla=$request->talla;
                $nuevaConstante->imc=$request->imc;
                $nuevaConstante->perimetro_abdominal=$request->perimetro_abdominal;
                $nuevaConstante->estado='Borrador';
                $nuevaConstante->idusuario_registra=auth()->user()->persona->id;
                $nuevaConstante->fecha_registro=date('Y-m-d H:i:s');
                $nuevaConstante->save();
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaExamenFisico(Request $request){
        try{
            // $verificaConstante= ConstantesVitales::where('id',$request->IdConstantesVitale)
            // ->where('estado','Borrador')
            // ->orderBy('id','desc')
            // ->first();
           
            // if(is_null($verificaConstante)){
            //     return ["mensaje"=>"Debe completar primero la seccion E. CONSTANTES VITALES Y ANTROPOMETRIA", "error"=>true];
            // }

            $cicatrices = $request->has('cicatrices') ? true : false;
            $piel_fanera = $request->has('piel_fanera') ? true : false;

            $parpados = $request->has('parpados') ? true : false;
            $conjuntiva = $request->has('conjuntiva') ? true : false;
            $pupila = $request->has('pupila') ? true : false;
            $corneas = $request->has('corneas') ? true : false;
            $motilidad = $request->has('motilidad') ? true : false;

            $auditivo_externo = $request->has('auditvo_externo') ? true : false;
            $pabellon = $request->has('pabellon') ? true : false;
            $timpano = $request->has('timpano') ? true : false;

            $labios = $request->has('labios') ? true : false;
            $lengua = $request->has('lengua') ? true : false;
            $faringe = $request->has('faringe') ? true : false;
            $amigdalas = $request->has('amigdalas') ? true : false;
            $dentadura = $request->has('dentadura') ? true : false;

            $tabique = $request->has('tabique') ? true : false;
            $cornete = $request->has('cornete') ? true : false;
            $mucosas = $request->has('mucosas') ? true : false;
            $senos_paranasales = $request->has('senos_paranasales') ? true : false;

            $tiroides = $request->has('tiroides') ? true : false;
            $movilidad = $request->has('movilidad') ? true : false;

            $mamas = $request->has('mamas') ? true : false;

            $pulmones = $request->has('pulmones') ? true : false;
            $corazon = $request->has('corazon') ? true : false;
            $parilla_costal = $request->has('parilla_costal') ? true : false;

            $visceras = $request->has('visceras') ? true : false;
            $pared_abdominal = $request->has('pared_abdominal') ? true : false;

            $flexibilidad = $request->has('flexibilidad') ? true : false;
            $desviacion = $request->has('desviacion') ? true : false;
            $dolor = $request->has('dolor') ? true : false;

            $pelvis = $request->has('pelvis') ? true : false;
            $genitales = $request->has('genitales') ? true : false;

            
            $vascular = $request->has('vascular') ? true : false;
            $miembros_superiores = $request->has('miembros_superiores') ? true : false;
            $miembros_inferiores = $request->has('miembros_inferiores') ? true : false;

            $fuerza = $request->has('fuerza') ? true : false;
            $sensibilidad = $request->has('sensibilidad') ? true : false;
            $marcha = $request->has('marcha') ? true : false;
            $reflejos = $request->has('reflejos') ? true : false;
     

            $buscaExamenFisico=ExamenFisicoRegional::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();

            if(!is_null($buscaExamenFisico)){ 
                $buscaExamenFisico->cicatrices=$cicatrices;
                $buscaExamenFisico->piel_fanera=$piel_fanera;
                $buscaExamenFisico->parpados=$parpados;
                $buscaExamenFisico->conjuntiva=$conjuntiva;
                $buscaExamenFisico->pupila=$pupila;
                $buscaExamenFisico->corneas=$corneas;
                $buscaExamenFisico->motilidad=$motilidad;
                $buscaExamenFisico->auditivo_externo=$auditivo_externo;
                $buscaExamenFisico->pabellon=$pabellon;
                $buscaExamenFisico->timpano=$timpano;

                $buscaExamenFisico->labios=$labios;
                $buscaExamenFisico->lengua=$lengua;
                $buscaExamenFisico->faringe=$faringe;
                $buscaExamenFisico->amigdalas=$amigdalas;
                $buscaExamenFisico->dentadura=$dentadura;
                $buscaExamenFisico->tabique=$tabique;
                $buscaExamenFisico->cornete=$cornete;
                $buscaExamenFisico->mucosas=$mucosas;
                $buscaExamenFisico->senos_paranasales=$senos_paranasales;
                $buscaExamenFisico->tiroides=$tiroides;
                $buscaExamenFisico->movilidad=$movilidad;

                $buscaExamenFisico->mamas=$mamas;
                $buscaExamenFisico->pulmones=$pulmones;
                $buscaExamenFisico->corazon=$corazon;
                $buscaExamenFisico->parilla_costal=$parilla_costal;
                $buscaExamenFisico->visceras=$visceras;
                $buscaExamenFisico->pared_abdominal=$pared_abdominal;
                $buscaExamenFisico->flexibilidad=$flexibilidad;
                $buscaExamenFisico->desviacion=$desviacion;
                $buscaExamenFisico->dolor=$dolor;
                $buscaExamenFisico->pelvis=$pelvis;
                $buscaExamenFisico->genitales=$genitales;

                $buscaExamenFisico->vascular=$vascular;
                $buscaExamenFisico->miembros_superiores=$miembros_superiores;
                $buscaExamenFisico->miembros_inferiores=$miembros_inferiores;
                $buscaExamenFisico->fuerza=$fuerza;
                $buscaExamenFisico->sensibilidad=$sensibilidad;
                $buscaExamenFisico->marcha=$marcha;
                $buscaExamenFisico->reflejos=$reflejos;
                
                $buscaExamenFisico->observacion=$request->motivo_examen;
                $buscaExamenFisico->idusuario_actualiza=auth()->user()->persona->id;
                $buscaExamenFisico->fecha_actualiza=date('Y-m-d H:i:s');
                $buscaExamenFisico->save();

                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false, "idexamenfisico"=>$buscaExamenFisico->id];

            }else{
                $nuevoExamen=new ExamenFisicoRegional();
                $nuevoExamen->id_cabecera_atencion=$request->IdCabeceraAtencion;
                $nuevoExamen->id_empleado=$request->id_empleado;
                $nuevoExamen->cicatrices=$cicatrices;
                $nuevoExamen->piel_fanera=$piel_fanera;
                $nuevoExamen->parpados=$parpados;
                $nuevoExamen->conjuntiva=$conjuntiva;
                $nuevoExamen->pupila=$pupila;
                $nuevoExamen->corneas=$corneas;
                $nuevoExamen->motilidad=$motilidad;
                $nuevoExamen->auditivo_externo=$auditivo_externo;
                $nuevoExamen->pabellon=$pabellon;
                $nuevoExamen->timpano=$timpano;

                $nuevoExamen->labios=$labios;
                $nuevoExamen->lengua=$lengua;
                $nuevoExamen->faringe=$faringe;
                $nuevoExamen->amigdalas=$amigdalas;
                $nuevoExamen->dentadura=$dentadura;
                $nuevoExamen->tabique=$tabique;
                $nuevoExamen->cornete=$cornete;
                $nuevoExamen->mucosas=$mucosas;
                $nuevoExamen->senos_paranasales=$senos_paranasales;
                $nuevoExamen->tiroides=$tiroides;
                $nuevoExamen->movilidad=$movilidad;

                $nuevoExamen->mamas=$mamas;
                $nuevoExamen->pulmones=$pulmones;
                $nuevoExamen->corazon=$corazon;
                $nuevoExamen->parilla_costal=$parilla_costal;
                $nuevoExamen->visceras=$visceras;
                $nuevoExamen->pared_abdominal=$pared_abdominal;
                $nuevoExamen->flexibilidad=$flexibilidad;
                $nuevoExamen->desviacion=$desviacion;
                $nuevoExamen->dolor=$dolor;
                $nuevoExamen->pelvis=$pelvis;
                $nuevoExamen->genitales=$genitales;

                $nuevoExamen->vascular=$vascular;
                $nuevoExamen->miembros_superiores=$miembros_superiores;
                $nuevoExamen->miembros_inferiores=$miembros_inferiores;
                $nuevoExamen->fuerza=$fuerza;
                $nuevoExamen->sensibilidad=$sensibilidad;
                $nuevoExamen->marcha=$marcha;
                $nuevoExamen->reflejos=$reflejos;

                $nuevoExamen->observacion=$request->motivo_examen;
                $nuevoExamen->estado='Borrador';
                $nuevoExamen->idusuario_registra=auth()->user()->persona->id;
                $nuevoExamen->fecha_registro=date('Y-m-d H:i:s');
                $nuevoExamen->save();

                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false, "idconstante"=>$nuevoExamen->id];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardarFactoresRiesgo(Request $request){
        try{
           
            $existe=ActividadesExpo::where('id_actividad',$request->indice)
            ->where('id_expo',$request->expo)
            ->where('tipo',$request->tipo)
            ->where('codigo',$request->codigo)
            ->where('idpaciente',$request->paciente_id)
            ->where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->where('fecha_atencion',date('Y-m-d'))
            ->first();
            if(!is_null($existe)){
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false];
            }
            $guardaActExpo = new ActividadesExpo();
            $guardaActExpo->id_actividad=$request->indice;
            $guardaActExpo->id_expo=$request->expo;
            $guardaActExpo->tipo=$request->tipo;
            $guardaActExpo->codigo=$request->codigo;
            $guardaActExpo->idpaciente=$request->paciente_id;
            $guardaActExpo->fecha_atencion=date('Y-m-d');
            $guardaActExpo->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaActExpo->save();
            return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false];

            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }
    public function llenaTablaMedida($idempleado){
        try{
            $data=MedidasPreventivas::where('id_empleado',$idempleado)
            ->where('estado','P')
            ->where('fecha_atencion',date('Y-m-d'))
            ->get();

            return ["resultado"=>$data, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaMedidasPreventivas(Request $request){
        try{
           
            $existe=MedidasPreventivas::where('id_empleado',$request->id_empleado)
            ->where('charla_salud',$request->charla_salud_empl)
            ->where('controles_medicos_rutinarios',$request->controles_med_empl)
            ->where('uso_adecuado_prenda_prot',$request->prenda_proteccion_empl)
            ->where('fecha_atencion',date('Y-m-d'))
            // ->where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->first();
            if(!is_null($existe)){
                if($existe->estado=='P'){
                    return ["mensaje"=>"Informacion ya se encuentra registrada ", "error"=>true];
                }else{
                    $existe->id_usuario=auth()->user()->persona->id;
                    $existe->fecha_registro=date('Y-m-d H:i:s');
                    $existe->estado='P';
                    $existe->fecha_atencion=date('Y-m-d');
                    $existe->save();
                    return ["mensaje"=>"Informacion registrada exitosamenteq ", "error"=>true];
                }
                    
            }
            $guardaMedida = new MedidasPreventivas();
            $guardaMedida->id_empleado=$request->id_empleado;
            $guardaMedida->charla_salud=$request->charla_salud_empl;
            $guardaMedida->controles_medicos_rutinarios=$request->controles_med_empl;
            $guardaMedida->uso_adecuado_prenda_prot=$request->prenda_proteccion_empl;
            $guardaMedida->id_usuario=auth()->user()->persona->id;
            $guardaMedida->fecha_registro=date('Y-m-d H:i:s');
            $guardaMedida->fecha_atencion=date('Y-m-d');
            $guardaMedida->estado='P';
            // $guardaMedida->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaMedida->save();
         
            return ["mensaje"=>"Informacion registrada exitosamentew ", "error"=>false];

            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaActividadLaboral(Request $request){
        try{
           
            $existe=AntecedentesEmpleo::where('id_empleado',$request->id_empleado)
            ->where('centro_trabajo',$request->centro_trab_empl)
            ->where('actividad_desempenia',$request->actividades_desempeniaba)
            ->where('trabajo',$request->trabajo)
            ->where('tiempo_trabajo',$request->tiempo_trab_empl)
            ->where('incidente',$request->incidente_empl)
            ->where('accidente',$request->accidente_empl)
            ->where('enfermedad_profesional',$request->enfermedad_prof_empl)
            ->where('calificado_iess',$request->califica_iess)
            ->where('fecha_calificacion',$request->fecha_califica)
            ->where('especificar',$request->especificar_calif_empl)
            ->where('observaciones',$request->observaciones_calif_empl)
            ->where('fecha_atencion',date('Y-m-d'))
            ->first();
            if(!is_null($existe)){
                if($existe->estado=='P'){
                    return ["mensaje"=>"Informacion ya se encuentra registrada ", "error"=>true];
                }else{
                    $existe->id_usuario=auth()->user()->persona->id;
                    $existe->fecha_registro=date('Y-m-d H:i:s');
                    $existe->estado='P';
                    $existe->fecha_atencion=date('Y-m-d');
                    $existe->save();
                    return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>true];
                }
                    
            }
            $guardaAntecedente = new AntecedentesEmpleo();
            $guardaAntecedente->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaAntecedente->id_empleado=$request->id_empleado;
            $guardaAntecedente->centro_trabajo=$request->centro_trab_empl;
            $guardaAntecedente->actividad_desempenia=$request->actividades_desempeniaba;
            $guardaAntecedente->trabajo=$request->trabajo;

            $guardaAntecedente->tiempo_trabajo=$request->tiempo_trab_empl;
            $guardaAntecedente->incidente=$request->incidente_empl;
            $guardaAntecedente->accidente=$request->accidente_empl;
            $guardaAntecedente->enfermedad_profesional=$request->enfermedad_prof_empl;

            $guardaAntecedente->calificado_iess=$request->califica_iess;
            $guardaAntecedente->fecha_calificacion=$request->fecha_califica;
            $guardaAntecedente->especificar=$request->especificar_calif_empl;
            $guardaAntecedente->observaciones=$request->observaciones_calif_empl;

            $guardaAntecedente->id_usuario=auth()->user()->persona->id;
            $guardaAntecedente->fecha_registro=date('Y-m-d H:i:s');
            $guardaAntecedente->fecha_atencion=date('Y-m-d');
            $guardaAntecedente->estado='P';
            $guardaAntecedente->save();
         
            return ["mensaje"=>"Informacion registrada exitosamentew ", "error"=>false];

            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function llenaTablaActividad($idempleado){
        try{
            $data=AntecedentesEmpleo::where('id_empleado',$idempleado)
            ->where('estado','P')
            ->where('fecha_atencion',date('Y-m-d'))
            ->get();

            return ["resultado"=>$data, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaActividadLabExtra(Request $request){
        try{
           
            $existe=ActividadesExtras::where('id_empleado',$request->id_empleado)
            ->where('actividad',$request->act_extra)
            ->where('fecha',$request->fecha_act_extra)
            ->where('fecha_atencion',date('Y-m-d'))
            ->first();
            if(!is_null($existe)){
                if($existe->estado=='P'){
                    return ["mensaje"=>"Informacion ya se encuentra registrada ", "error"=>true];
                }else{
                    $existe->id_usuario_registra=auth()->user()->persona->id;
                    $existe->fecha_registra=date('Y-m-d H:i:s');
                    $existe->estado='P';
                    $existe->fecha_atencion=date('Y-m-d');
                    $existe->save();
                    return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>true];
                }
                    
            }
            $guardaActExtra = new ActividadesExtras();
            $guardaActExtra->id_empleado=$request->id_empleado;
            $guardaActExtra->actividad=$request->act_extra;
            $guardaActExtra->fecha=$request->fecha_act_extra;
            $guardaActExtra->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaActExtra->estado='P';
            $guardaActExtra->id_usuario_registra=auth()->user()->persona->id;
            $guardaActExtra->fecha_registra=date('Y-m-d H:i:s');
            $guardaActExtra->fecha_atencion=date('Y-m-d');
            $guardaActExtra->save();
         
            return ["mensaje"=>"Informacion registrada exitosamentew ", "error"=>false];

            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function llenaTablaActividadExtra($idempleado){
        try{
            $data=ActividadesExtras::where('id_empleado',$idempleado)
            ->where('estado','P')
            ->where('fecha_atencion',date('Y-m-d'))
            ->get();

            return ["resultado"=>$data, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaResultadosExamen(Request $request){
        try{
           
            $existe=ResultadosExamen::where('id_empleado',$request->id_empleado)
            ->where('examen',$request->nombre_examen)
            ->where('fecha',$request->fecha_examen)
            ->where('resultado',$request->resultados_exam)
            ->where('fecha_atencion',date('Y-m-d'))
            ->first();
            if(!is_null($existe)){
                if($existe->estado=='P'){
                    return ["mensaje"=>"Informacion ya se encuentra registrada ", "error"=>true];
                }else{
                    $existe->id_usuario_registra=auth()->user()->persona->id;
                    $existe->fecha_registra=date('Y-m-d H:i:s');
                    $existe->fecha_atencion=date('Y-m-d');
                    $existe->estado='P';
                    $existe->save();
                    return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>true];
                }
                    
            }
            $guardaActExtra = new ResultadosExamen();
            $guardaActExtra->id_empleado=$request->id_empleado;
            $guardaActExtra->examen=$request->nombre_examen;
            $guardaActExtra->fecha=$request->fecha_examen;
            $guardaActExtra->resultado=$request->resultados_exam;
            $guardaActExtra->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaActExtra->estado='P';
            $guardaActExtra->id_usuario_registra=auth()->user()->persona->id;
            $guardaActExtra->fecha_registra=date('Y-m-d H:i:s');
            $guardaActExtra->fecha_atencion=date('Y-m-d');
            $guardaActExtra->save();
         
            return ["mensaje"=>"Informacion registrada exitosamentew ", "error"=>false];

            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function llenaTablaResultadoExamen($idempleado){
        try{
            $data=ResultadosExamen::where('id_empleado',$idempleado)
            ->where('estado','P')
            ->where('fecha_atencion',date('Y-m-d'))
            ->get();

            return ["resultado"=>$data, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaObservacionExamen(Request $request){
        try{
           
            $existe=ObservResultadosExamen::where('id_empleado',$request->id_empleado)
            ->where('observacion',$request->observacion_resultados)
            ->where('fecha_atencion',date('Y-m-d'))
            ->where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->first();
            if(!is_null($existe)){
                if($existe->estado=='P'){
                    return ["mensaje"=>"Informacion ya se encuentra registrada ", "error"=>true];
                }else{
                    $existe->estado='P';
                    $existe->fecha_atencion=date('Y-m-d');
                    $existe->save();
                    return ["mensaje"=>"Informacion registrada exitosamenteq ", "error"=>true];
                }
                    
            }
            $guardaObservacion = new ObservResultadosExamen();
            $guardaObservacion->id_empleado=$request->id_empleado;
            $guardaObservacion->observacion=$request->observacion_resultados;
            $guardaObservacion->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaObservacion->fecha_atencion=date('Y-m-d');
            $guardaObservacion->estado='P';
            $guardaObservacion->save();
         
            return ["mensaje"=>"Informacion registrada exitosamentew ", "error"=>false];

            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function buscarCie10(Request $request){
        $data = [];
        if($request->has('q')){
            $search = $request->q;
            $data=DB::connection('pgsql')->table('sgm_consultorio.cie10')
            ->where(function($query)use($search){
                $query->where('codigo', 'ilike', '%'.$search.'%')
                ->orwhere('descripcion', 'ilike', '%'.$search.'%');
            })
            ->where('estado','A')
            ->select('id',DB::raw("CONCAT(codigo,' - ',descripcion) AS nombre"))
            ->take(10)->get();

        }
        return response()->json($data);
    }


    public function eliminaFactoresRiesgo(Request $request){
        try{
           
            $existe=ActividadesExpo::where('id_actividad',$request->indice)
            ->where('id_expo',$request->expo)
            ->where('tipo',$request->tipo)
            ->where('codigo',$request->codigo)
            ->where('idpaciente',$request->paciente_id)
            ->first();
            $existe->delete();
            return ["mensaje"=>"Informacion eliminada exitosamente ", "error"=>false];

            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }


    public function comboPuesto(){
        try{
            $marcas = Puesto::where('estado','A')
            ->orderBy('descripcion','asc')
            ->get();
            return["error"=>false, "resultado"=>$marcas];

        } catch (\Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde']);
        }
    }

    public function calculaEdad($fecha){
        try{
           
            $fechaNacimiento = new \DateTime($fecha);

            // Obtener la fecha actual
            $fechaActual = new \DateTime();

            // Calcular la diferencia entre la fecha actual y la fecha de nacimiento
            $edad = $fechaActual->diff($fechaNacimiento)->y;

            return ["edad"=>$edad, "error"=>false];
           
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

    public function limpiarDataBorrador($idpaciente){

        try{
            $actualizaFactoreRiego=ActividadesExpo::where('fecha_atencion',date('Y-m-d'))
            ->where('idpaciente',$idpaciente)
            ->where('estado','!=','Atendido')
            ->delete();

            $actualizaMedida=MedidasPreventivas::where('fecha_atencion',date('Y-m-d'))
            ->where('id_empleado',$idpaciente)
            ->where('estado','!=','Atendido')
            ->delete();               

            $actualizaAntecedenteEmpleo=AntecedentesEmpleo::where('fecha_atencion',date('Y-m-d'))
            ->where('id_empleado',$idpaciente)
            ->where('estado','!=','Atendido')
            ->delete();  
                
            $actualizaActividadExtra=ActividadesExtras::where('fecha_atencion',date('Y-m-d'))
            ->where('id_empleado',$idpaciente)
            ->where('estado','!=','Atendido')
            ->delete();                

            $actualizaResultadoExam=ResultadosExamen::where('fecha_atencion',date('Y-m-d'))
            ->where('id_empleado',$idpaciente)
            ->where('estado','!=','Atendido')
            ->delete();  

            $actualizaDiagnostico=DiagnosticoEmpleado::where('fecha_atencion',date('Y-m-d'))
            ->where('id_empleado',$idpaciente)
            ->where('estado','!=','Atendido')
            ->delete(); 
            
            return ["mensaje"=>"ok", "error"=>false];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }

    }

     public function histoEvoEmpleadoPaciente($id){
        try{
            //$limpiaDataBorrador=$this->limpiarDataBorrador($id);
            $info = EmpleadoPaciente::where('id', $id)
            ->select(
                'cedula', 
                'primer_apellido', 
                'segundo_apellido', 
                'primer_nombre', 
                'segundo_nombre', 
                'fecha_nacimiento', 
                'sexo', 
                'grupo_sanguinedad', 
                'lateralidad',
                'embarazada',
                'discapacidad',
                'ecatastrofica',
                'lactancia',
                'mayor_edad',
                DB::raw('EXTRACT(YEAR FROM AGE(fecha_nacimiento)) AS edad')
            )
            ->where('estado', 'A')
            ->first();

            $atenciones=DB::connection('pgsql')->table('sgm_consultorio.cabecera_atencion as ca')
            ->leftJoin('sgm_consultorio.seccion_motivo_consulta as mot', 'mot.id_cabecera_atencion', '=', 'ca.id')
            ->select('ca.id as idcab','mot.motivo','ca.id_usuario_registra','ca.fecha_registro')
            ->where('ca.estado','Atendido')
            ->where('ca.id_empleado',$id)
            ->where('mot.estado','Atendido')
            ->get();

            foreach($atenciones as $key=> $data){
                $diag=DB::connection('pgsql')->table('sgm_consultorio.diagnostico_empleado as de')
                ->leftJoin('sgm_consultorio.cie10 as c', 'de.id_cie10', '=', 'c.id')
                ->where('de.estado','Atendido')
                ->where('id_cabecera_atencion',$data->idcab)
                ->select(DB::raw("CONCAT(c.codigo, ' - ', c.descripcion) AS nombre"))
                ->get();

                $usuarioRegistra=DB::connection('mysql')->table('users as u')
                ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                ->where('u.id',$data->id_usuario_registra)
                ->select('p.nombres','p.apellidos','p.cedula')
                ->first();
                $atenciones[$key]->profesional=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;

                $atenciones[$key]->diagnostico=$diag;
            }

            
            return ["resultado"=>$info, 
                "motivo"=>$atenciones,
                "error"=>false
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }


    public function evolucionEmpleadoPaciente($id){
        try{
            $limpiaDataBorrador=$this->limpiarDataBorrador($id);
            $info = EmpleadoPaciente::where('id', $id)
            ->select(
                'cedula', 
                'primer_apellido', 
                'segundo_apellido', 
                'primer_nombre', 
                'segundo_nombre', 
                'fecha_nacimiento', 
                'sexo', 
                'grupo_sanguinedad', 
                'lateralidad',
                'embarazada',
                'discapacidad',
                'ecatastrofica',
                'lactancia',
                'mayor_edad',
                DB::raw('EXTRACT(YEAR FROM AGE(fecha_nacimiento)) AS edad')
            )
            ->where('estado', 'A')
            ->first();

            $motivo=MotivoConsulta::where('id_empleado',$id)
            ->select('id_puesto',
            'fecha_atencion',
            'fecha_ingreso_trabajo',
            'fecha_reintegro',
            'fecha_ultimo_dia_laboral')
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();

            $fecha_atencion=date('Y-m-d');

            $factores_riesgo=ActividadesExpo::where('idpaciente',$id)
            ->where('fecha_atencion',date('Y-m-d'))
            ->get();
          
            return ["resultado"=>$info, 
                "motivo"=>$motivo,
                "fecha_atencion"=>$fecha_atencion,
                "factores_riesgo"=>$factores_riesgo,
                "error"=>false
            ];

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
                    $query->where('cedula',$valor);
                }
            })            
            ->select('id','cedula',DB::raw("CONCAT(primer_apellido, ' ', segundo_apellido, ' ', primer_nombre, ' ', segundo_nombre) AS nombre"),'telefono','correo')
            ->where('estado','A')
            ->take(10)->get();
            
          
            return ["resultado"=>$data, "error"=>false];
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaDiagnostico(Request $request){
        try{
           
            $existe=DiagnosticoEmpleado::where('id_empleado',$request->id_empleado)
            ->where('id_cie10',$request->cmb_diagnostico)
            ->where('prev_def',$request->prevent_defin)
            ->where('fecha_atencion',date('Y-m-d'))
            // ->where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->first();
            if(!is_null($existe)){
                if($existe->estado=='P'){
                    return ["mensaje"=>"Informacion ya se encuentra registrada ", "error"=>true];
                }else{
                    $existe->estado='P';
                    $existe->id_usuario_registra=auth()->user()->persona->id;
                    $existe->fecha_registra=date('Y-m-d H:i:s');
                    $existe->fecha_atencion=date('Y-m-d');                    
                    $existe->save();
                    return ["mensaje"=>"Informacion registrada exitosamenteq ", "error"=>true];
                }
                    
            }
            $guardaDiagnostico = new DiagnosticoEmpleado();
            $guardaDiagnostico->id_empleado=$request->id_empleado;
            // $guardaDiagnostico->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaDiagnostico->id_cie10=$request->cmb_diagnostico;
            $guardaDiagnostico->fecha_atencion=date('Y-m-d');
            $guardaDiagnostico->prev_def=$request->prevent_defin;
            $guardaDiagnostico->estado='P';
            $guardaDiagnostico->id_usuario_registra=auth()->user()->persona->id;
            $guardaDiagnostico->fecha_registra=date('Y-m-d H:i:s');
            $guardaDiagnostico->save();
         
            return ["mensaje"=>"Informacion registrada exitosamentew ", "error"=>false];

            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

     public function llenaTablaDiagnostico($idempleado){
        try{
            $data=DiagnosticoEmpleado::with('cie10')
            ->where('id_empleado',$idempleado)
            ->where('estado','P')
            ->where('fecha_atencion',date('Y-m-d'))
            ->get();

            return ["resultado"=>$data, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaAptitudesMedicas(Request $request){
        try{
            
            $apto = $request->has('apto') ? true : false;
            $apto_observ = $request->has('apto_observ') ? true : false;
            $apto_con_limitacion = $request->has('apto_con_limitacion') ? true : false;
            $no_apto = $request->has('no_apto') ? true : false;
                       
            $existe=AptitudMedica::where('id_empleado',$request->id_empleado)
            ->where('apto',$apto)
            ->where('apto_observ',$apto_observ)
            ->where('apto_limitacion',$apto_con_limitacion)
            ->where('no_apto',$no_apto)
            ->where('observacion',$request->observ_apt_med)
            ->where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->where('fecha_atencion',date('Y-m-d'))
            ->first();
            if(!is_null($existe)){
                if($existe->estado=='A'){
                    return ["mensaje"=>"Informacion ya se encuentra registrada ", "error"=>true];
                }else{
                    $existe->estado='A';
                    $existe->id_usuario_registra=auth()->user()->persona->id;
                    $existe->fecha_registra=date('Y-m-d H:i:s');
                    $existe->save();
                    return ["mensaje"=>"Informacion registrada exitosamenteq ", "error"=>true];
                }
                    
            }
            $guardaAptitudMed = new AptitudMedica();
            $guardaAptitudMed->id_empleado=$request->id_empleado;
            $guardaAptitudMed->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaAptitudMed->apto=$apto;
            $guardaAptitudMed->fecha_atencion=date('Y-m-d');
            $guardaAptitudMed->apto_observ=$apto_observ;
            $guardaAptitudMed->apto_limitacion=$apto_con_limitacion;
            $guardaAptitudMed->observacion=$request->observ_apt_med;
            $guardaAptitudMed->no_apto=$no_apto;
            $guardaAptitudMed->estado='A';
            $guardaAptitudMed->id_usuario_registra=auth()->user()->persona->id;
            $guardaAptitudMed->fecha_registra=date('Y-m-d H:i:s');
            $guardaAptitudMed->save();
         
            return ["mensaje"=>"Informacion registrada exitosamentew ", "error"=>false];

            
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaRecomendacionTratamiento(Request $request){
        try{
                       
            $existe=RecomendacionTratamiento::where('id_empleado',$request->id_empleado)
            ->where('recomendacion_tratamiento',$request->recomendacion_trata)
            ->where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->where('fecha_atencion',date('Y-m-d'))
            ->first();
            if(!is_null($existe)){
                if($existe->estado=='A'){
                    return ["mensaje"=>"Informacion ya se encuentra registrada ", "error"=>true];
                }else{
                    $existe->estado='A';
                    $existe->id_usuario_registra=auth()->user()->persona->id;
                    $existe->fecha_registra=date('Y-m-d H:i:s');
                    $existe->save();
                    return ["mensaje"=>"Informacion registrada exitosamenteq ", "error"=>true];
                }
                    
            }

            $guardaRecomendacionTrata = new RecomendacionTratamiento();
            $guardaRecomendacionTrata->id_empleado=$request->id_empleado;
            $guardaRecomendacionTrata->recomendacion_tratamiento=$request->recomendacion_trata;
            $guardaRecomendacionTrata->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaRecomendacionTrata->fecha_atencion=date('Y-m-d');
            $guardaRecomendacionTrata->estado='A';
            $guardaRecomendacionTrata->id_usuario_registra=auth()->user()->persona->id;
            $guardaRecomendacionTrata->fecha_registra=date('Y-m-d H:i:s');
            $guardaRecomendacionTrata->save();
         
            return ["mensaje"=>"Informacion registrada exitosamentew ", "error"=>false];


        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaRetiro(Request $request){
        try{
                       
            $si_realiza_eva = $request->has('si_realiza_eva') ? true : false;
            $no_realiza_eva = $request->has('no_realiza_eva') ? true : false;
            $si_condicion_salud = $request->has('si_condicion_salud') ? true : false;
            $no_condicion_salud = $request->has('no_condicion_salud') ? true : false;

            $existe=Retiro::where('id_empleado',$request->id_empleado)
            ->where('se_realiza_evaluacion',$si_realiza_eva)
            ->where('no_se_realiza_evaluacion',$no_realiza_eva)
            ->where('si_condicion_relacionada_trabajo',$si_condicion_salud)
            ->where('no_condicion_relacionada_trabajo',$no_condicion_salud)
            ->where('observacion_retiro',$request->observ_retiro)
            ->where('id_cabecera_atencion',$request->IdCabeceraAtencion)
            ->where('fecha_atencion',date('Y-m-d'))
            ->first();
            if(!is_null($existe)){
                if($existe->estado=='A'){
                    return ["mensaje"=>"Informacion ya se encuentra registrada ", "error"=>true];
                }else{
                    $existe->estado='A';
                    $existe->id_usuario_registra=auth()->user()->persona->id;
                    $existe->fecha_registra=date('Y-m-d H:i:s');
                    $existe->save();
                    return ["mensaje"=>"Informacion registrada exitosamenteq ", "error"=>true];
                }
                    
            }

            $guardaRetiro = new Retiro();
            $guardaRetiro->id_empleado=$request->id_empleado;
            $guardaRetiro->se_realiza_evaluacion=$si_realiza_eva;
            $guardaRetiro->no_se_realiza_evaluacion=$no_realiza_eva;
            $guardaRetiro->si_condicion_relacionada_trabajo=$si_condicion_salud;
            $guardaRetiro->no_condicion_relacionada_trabajo=$no_condicion_salud;
            $guardaRetiro->observacion_retiro=$request->observ_retiro;
            $guardaRetiro->id_cabecera_atencion=$request->IdCabeceraAtencion;
            $guardaRetiro->fecha_atencion=date('Y-m-d');
            $guardaRetiro->estado='A';
            $guardaRetiro->id_usuario_registra=auth()->user()->persona->id;
            $guardaRetiro->fecha_registra=date('Y-m-d H:i:s');
            if($guardaRetiro->save()){
                $actualiza=CabeceraAtencion::where('id',$request->IdCabeceraAtencion)
                ->first();
                $actualiza->estado='Atendido';
                $actualiza->save();

                $guardaRetiro->estado='Atendido';
                $guardaRetiro->save();

                $actualizaMotivo=MotivoConsulta::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
                ->first();
                $actualizaMotivo->estado='Atendido';
                $actualizaMotivo->save();

                $actualizaAntecedente=Antecedentes::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
                ->first();
                $actualizaAntecedente->estado='Atendido';
                $actualizaAntecedente->save();

                $actualizaEnfermedad=Enfermedad::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
                ->first();
                $actualizaEnfermedad->estado='Atendido';
                $actualizaEnfermedad->save();

                $actualizaConstante=ConstantesVitales::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
                ->first();
                $actualizaConstante->estado='Atendido';
                $actualizaConstante->save();

                $actualizaExamen=ExamenFisicoRegional::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
                ->first();
                $actualizaExamen->estado='Atendido';
                $actualizaExamen->save();

                $idpaciente=$request->id_empleado;

                $actualizaFactoreRiego=ActividadesExpo::where('fecha_atencion',date('Y-m-d'))
                ->where('idpaciente',$idpaciente)
                ->update(["estado"=>"Atendido", "id_cabecera_atencion"=>$request->IdCabeceraAtencion]);

                $actualizaMedida=MedidasPreventivas::where('fecha_atencion',date('Y-m-d'))
                ->where('id_empleado',$idpaciente)
                ->update(["estado"=>"Atendido", "id_cabecera_atencion"=>$request->IdCabeceraAtencion]);               

                $actualizaAntecedenteEmpleo=AntecedentesEmpleo::where('fecha_atencion',date('Y-m-d'))
                ->where('id_empleado',$idpaciente)
                ->update(["estado"=>"Atendido", "id_cabecera_atencion"=>$request->IdCabeceraAtencion]);
               
                $actualizaActividadExtra=ActividadesExtras::where('fecha_atencion',date('Y-m-d'))
                ->where('id_empleado',$idpaciente)
                ->update(["estado"=>"Atendido", "id_cabecera_atencion"=>$request->IdCabeceraAtencion]);               

                $actualizaResultadoExam=ResultadosExamen::where('fecha_atencion',date('Y-m-d'))
                ->where('id_empleado',$idpaciente)
                ->update(["estado"=>"Atendido","id_cabecera_atencion"=>$request->IdCabeceraAtencion]);
               
                $actualizaObservRes=ObservResultadosExamen::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
                ->first();
                $actualizaObservRes->estado='Atendido';
                $actualizaObservRes->save();

                $actualizaDiagnostico=DiagnosticoEmpleado::where('fecha_atencion',date('Y-m-d'))
                ->where('id_empleado',$idpaciente)
                ->update(["estado"=>"Atendido","id_cabecera_atencion"=>$request->IdCabeceraAtencion]);

                $actualizaAptitudMed=AptitudMedica::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
                ->first();
                $actualizaAptitudMed->estado='Atendido';
                $actualizaAptitudMed->save();

                $actualizaRecomend=RecomendacionTratamiento::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
                ->first();
                $actualizaRecomend->estado='Atendido';
                $actualizaRecomend->save();

                $actualizaRetiro=Retiro::where('id_cabecera_atencion',$request->IdCabeceraAtencion)
                ->first();
                $actualizaRetiro->estado='Atendido';
                $actualizaRetiro->save();


                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false];

            }
         
            return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false];


        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function pdfEvolucion($id){
        $nombrePDF="pdf_evolucion.pdf";

        $pdf=\PDF::LoadView('consultorio.pdf_evolucion',[]);
        $pdf->setPaper("A4", "portrait");
        return $pdf->stream($nombrePDF);
        // $estadoarch = $pdf->stream();

    }
}