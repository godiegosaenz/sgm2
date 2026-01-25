<?php

namespace App\Http\Controllers\Consultorio;
use App\Models\Consultorio\Antecedentes;
use App\Models\Consultorio\ConstantesVitales;
use App\Models\Consultorio\EmpleadoPaciente;
use App\Http\Controllers\Controller;
use App\Models\Consultorio\Enfermedad;
use App\Models\Consultorio\EstiloActividad;
use App\Models\Consultorio\ExamenFisicoRegional;
use App\Models\Consultorio\MedicacionHabitual;
use App\Models\Consultorio\MotivoConsulta;
use App\Models\Consultorio\Puesto;
use App\Models\Consultorio\Examenes;
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
           
            $buscaMotivo=MotivoConsulta::where('fecha_atencion',$request->fecha_atencion)
            ->where('estado','Borrador')
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
                $buscaMotivo->save();
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false];
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
                $nuevoMotivo->save();
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaAntecedentes(Request $request){
        try{
            $verificaSeccionMotivo= MotivoConsulta::where('fecha_atencion',date('Y-m-d'))
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();
            if(is_null($verificaSeccionMotivo)){
                return ["mensaje"=>"Debe completar primero la seccion B. MOTIVO DE CONSULTA", "error"=>true];
            }
           
            $buscaAntecedentes=Antecedentes::where('id_seccion_motivo',$verificaSeccionMotivo->id)
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();
            if(!is_null($buscaAntecedentes)){             
                $buscaAntecedentes->id_seccion_motivo=$verificaSeccionMotivo->id;
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
               
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false, "id_antecedentes"=>$buscaAntecedentes->id];
            }else{

                $nuevoAntecedentes=new Antecedentes();
                $nuevoAntecedentes->id_seccion_motivo=$verificaSeccionMotivo->id;
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
               
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false, "id_antecedentes"=>$nuevoAntecedentes->id];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e->getMessage(), "error"=>true];
        }
    }

    public function guardaEnfemedadProblemaActual(Request $request){
        try{
            $verificaAntecedente= Antecedentes::where('id',$request->IdAntecedentesRegistrado)
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();
            if(is_null($verificaAntecedente)){
                return ["mensaje"=>"Debe completar primero la seccion C. ANTECEDENTES PERSONALES", "error"=>true];
            }

            $buscaEnfermedad=Enfermedad::where('id_seccion_antecedentes',$request->IdAntecedentesRegistrado)
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();
            if(!is_null($buscaEnfermedad)){             
                $buscaEnfermedad->enfermedad_problema=$request->enfermedad_problema_actual;
                $buscaEnfermedad->id_empleado=$request->id_empleado;
                $buscaEnfermedad->idusuario_actualiza=auth()->user()->persona->id;
                $buscaEnfermedad->fecha_actualiza=date('Y-m-d H:i:s');
                $buscaEnfermedad->save();
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false, "idenfermedad"=>$buscaEnfermedad->id];
            }else{
                $nuevaEnfermedad=new Enfermedad();
                $nuevaEnfermedad->id_seccion_antecedentes=$request->IdAntecedentesRegistrado;
                $nuevaEnfermedad->enfermedad_problema=$request->enfermedad_problema_actual;
                $nuevaEnfermedad->estado='Borrador';
                $nuevaEnfermedad->id_empleado=$request->id_empleado;
                $nuevaEnfermedad->idusuario_registra=auth()->user()->persona->id;
                $nuevaEnfermedad->fecha_registro=date('Y-m-d H:i:s');
                $nuevaEnfermedad->save();
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false, "idenfermedad"=>$nuevaEnfermedad->id];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

    public function guardaConstantesVitales(Request $request){
        try{
            $verificaEnfermedad= Enfermedad::where('id',$request->IdEnfermedadregistrada)
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();
           
            if(is_null($verificaEnfermedad)){
                return ["mensaje"=>"Debe completar primero la seccion D. ENFERMEDAD O PROBLEMA ACTUAL", "error"=>true];
            }

            $buscaConstante=ConstantesVitales::where('id_seccion_enfermedad_problema',$request->IdEnfermedadregistrada)
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
                return ["mensaje"=>"Informacion actualizada exitosamente ", "error"=>false, "idconstante"=>$buscaConstante->id];
            }else{
                $nuevaConstante=new ConstantesVitales();
                $nuevaConstante->id_seccion_enfermedad_problema=$request->IdEnfermedadregistrada;
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
                return ["mensaje"=>"Informacion registrada exitosamente ", "error"=>false, "idconstante"=>$nuevaConstante->id];
            }
        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }

     public function guardaExamenFisico(Request $request){
        try{
            $verificaConstante= ConstantesVitales::where('id',$request->IdConstantesVitale)
            ->where('estado','Borrador')
            ->orderBy('id','desc')
            ->first();
           
            if(is_null($verificaConstante)){
                return ["mensaje"=>"Debe completar primero la seccion E. CONSTANTES VITALES Y ANTROPOMETRIA", "error"=>true];
            }

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
     

            $buscaExamenFisico=ExamenFisicoRegional::where('id_constante',$request->IdConstantesVitale)
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
                $nuevoExamen->id_constante=$request->IdConstantesVitale;
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

    public function evolucionEmpleadoPaciente($id){
        try{
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
          
            return ["resultado"=>$info, 
                "motivo"=>$motivo,
                "fecha_atencion"=>$fecha_atencion,
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