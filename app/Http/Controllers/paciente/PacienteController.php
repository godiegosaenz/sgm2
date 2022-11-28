<?php

namespace App\Http\Controllers\paciente;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\Models\Provincia;
use App\Models\Persona;
use App\Models\Canton;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Cookie;

class PacienteController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function index(Request $r){
        $provincias = Provincia::all();
        $cantones = new Canton();
        if(Cookie::get('provincia_id') !== null){
            $cantones = Canton::where('id_provincia',Cookie::get('provincia_id'))->get();
        }
        Cookie::queue('provincia_id', '');
        return view('paciente.ingresarpaciente',compact('provincias','cantones'))->with('estado',$r->estado);;
    }

    public function store(Request $r){
            $messages = [
                'required' => 'El campo :attribute es requerido.',
                'unique' => 'El numero de cedula ingresado ya existe',
                'size' => 'El campo :attribute debe tener exactamente :size caracteres',
                'max' => 'El campo :attribute no debe exceder los :max caracteres',
            ];
            if($r->discapacidad == 'NO'){

                $reglas = [
                        'cedula' => 'bail|required|size:10|unique:personas,cedula',
                        'nombres' => 'bail|required|max:250',
                        'apellidos' => 'bail|required|max:250',
                        'fechaNacimiento' => 'required',
                        'estadoCivil' => 'required',
                        'ocupacion' => 'required',
                        'provincia_id' => 'required',
                        'canton_id' => 'required',
                        'ciudad' => 'max:60',
                        'direccion' => '',
                        'telefono' => 'max:12',
                        'discapacidad' => 'required',
                        'porcentaje' => 'max:3',
                        'nota' => '',
                        //'txtFoto' => 'required',
                        'historiaClinica' => 'required|max:6'
                    ];
            }else{

                $reglas = [
                    'cedula' => 'bail|required|size:10|unique:personas,cedula',
                    'nombres' => 'bail|required|max:250',
                    'apellidos' => 'bail|required|max:250',
                    'fechaNacimiento' => 'required',
                    'estadoCivil' => 'required',
                    'ocupacion' => 'required',
                    'provincia_id' => 'required',
                    'canton_id' => 'required',
                    'ciudad' => 'max:60',
                    'direccion' => '',
                    'telefono' => 'max:12',
                    'discapacidad' => 'required',
                    'porcentaje' => 'required|max:3',
                    'nota' => '',
                    //'txtFoto' => 'required',
                    'historiaClinica' => 'required|max:6'
                ];
            }


            Cookie::queue('provincia_id', '');

            $validator = Validator::make($r->all(),$reglas,$messages);

            $validator->after(function ($validator) {

                /*$contador = count($validator->errors());
                if(intval($contador) > 1){
                    $validator->errors()->add('selCanton', 'El campo Canton es requerido');
                }*/
            });
            if ($validator->fails()) {
                if($r->provincia_id > 0 ){
                    Cookie::queue('provincia_id', $r->provincia_id);
                }
                /*return redirect()->back()->withErrors($validator)
                                        ->withInput()
                                        ->with('success', 'your message,here');*/
                return redirect('/paciente/ingresar')
                            ->withErrors($validator)
                            ->withInput();
            }

            if($r->file('txtFoto') != ''){
                $ruta = $r->file('txtFoto')->store('public');
                $arregloRutas = explode('/',$ruta);
                $rutaparaimgen = 'storage/'.$arregloRutas[1];
            }

            $cedula = $r->cedula;
            $personas = new Persona;
            $personas->cedula = $r->cedula;
            $personas->nombres = $r->nombres;
            $personas->apellidos = $r->apellidos;
            $personas->fechaNacimiento = $r->fechaNacimiento;
            $personas->estadoCivil = $r->estadoCivil;
            $personas->ocupacion = $r->ocupacion;
            $provincia = Provincia::find($r->provincia_id);
            $personas->provincia = $provincia->nombre;
            $personas->provincia_id = $r->provincia_id;
            $canton = Canton::find($r->canton_id);
            $personas->canton = $canton->nombre;
            $personas->canton_id = $r->canton_id;
            $personas->ciudad = $r->ciudad;
            $personas->direccion = $r->direccion;
            $personas->telefono = $r->telefono;
            $personas->discapacidad = $r->discapacidad;
            $personas->porcentaje = $r->porcentaje;
            $personas->nota = $r->nota;
            $personas->historiaClinica = $r->historiaClinica;
            if($r->file('txtFoto') != ''){
                $personas->rutaimagen = $rutaparaimgen;
            }

            $personas->save();
            return redirect('paciente/editar/'.$personas->id);


    }

    public function edit(Request $r,$id){
        $provincias = Provincia::all();
        $persona = Persona::find($id);
        $cantones = Canton::where('id_provincia',$persona->provincia_id)->get();
        //$archivo = Archivo::where('idpersona',$id)->get();
        return view('paciente.editarpaciente',compact('persona','provincias','cantones'))->with('estado',$r->estado);
    }

    public function update(Request $r,$id){
        $messages = [
            'required' => 'El campo :attribute es requerido.',
            'unique' => 'El numero de cedula ingresado ya existe',
            'size' => 'El campo :attribute debe tener exactamente :size caracteres',
            'max' => 'El campo :attribute no debe exceder los :max caracteres',
        ];
        if($r->discapacidad == 'NO'){

            $reglas = [
                    'cedula' => ['bail','required','size:10', Rule::unique('App\Models\Persona')->ignore($id)],
                    'nombres' => 'bail|required|max:250',
                    'apellidos' => 'bail|required|max:250',
                    'fechaNacimiento' => 'required',
                    'estadoCivil' => 'required',
                    'ocupacion' => 'required',
                    'provincia_id' => 'required',
                    'canton_id' => 'required',
                    'ciudad' => 'max:60',
                    'direccion' => '',
                    'telefono' => 'max:12',
                    'discapacidad' => 'required',
                    'porcentaje' => '',
                    'nota' => '',
                    //'txtFoto' => 'required',
                    'historiaClinica' => 'required|max:6'
                ];
        }else{

            $reglas = [
                'cedula' => ['bail','required','size:10', Rule::unique('App\Models\Persona')->ignore($id)],
                'nombres' => 'bail|required|max:250',
                'apellidos' => 'bail|required|max:250',
                'fechaNacimiento' => 'required',
                'estadoCivil' => 'required',
                'ocupacion' => 'required',
                'provincia_id' => 'required',
                'canton_id' => 'required',
                'ciudad' => 'max:60',
                'direccion' => '',
                'telefono' => 'max:12',
                'discapacidad' => 'required',
                'porcentaje' => 'required|max:3',
                'nota' => '',
                //'txtFoto' => 'required',
                'historiaClinica' => 'required|max:6'
            ];
        }


        Cookie::queue('provincia_id', '');

        $validator = Validator::make($r->all(),$reglas,$messages);

        $validator->after(function ($validator) {

        });
        if ($validator->fails()) {
            if($r->provincia_id > 0 ){
                Cookie::queue('provincia_id', $r->provincia_id);
            }
            return redirect('/paciente/editar/'.$id)
                        ->withErrors($validator)
                        ->withInput();
        }

        if($r->file('txtFoto') != ''){
            $ruta = $r->file('txtFoto')->store('public');
            $arregloRutas = explode('/',$ruta);
            $rutaparaimgen = 'storage/'.$arregloRutas[1];
        }

        $cedula = $r->txtCedula;
        $personas = Persona::find($id);
        $personas->cedula = $r->cedula;
        $personas->nombres = $r->nombres;
        $personas->apellidos = $r->apellidos;
        $personas->fechaNacimiento = $r->fechaNacimiento;
        $personas->estadoCivil = $r->estadoCivil;
        $personas->ocupacion = $r->ocupacion;
        $provincia = Provincia::find($r->provincia_id);
        $personas->provincia = $provincia->nombre;
        $personas->provincia_id = $r->provincia_id;
        $canton = Canton::find($r->canton_id);
        $personas->canton = $canton->nombre;
        $personas->canton_id = $r->canton_id;
        $personas->ciudad = $r->ciudad;
        $personas->direccion = $r->direccion;
        $personas->telefono = $r->telefono;
        $personas->discapacidad = $r->discapacidad;
        $personas->porcentaje = $r->porcentaje;
        $personas->nota = $r->nota;
        $personas->historiaClinica = $r->historiaClinica;
        if($r->file('txtFoto') != ''){
            $personas->rutaimagen = $rutaparaimgen;
        }

        $personas->save();
        return redirect('paciente/editar/'.$personas->id)->with('estado','actualizado');
    }

    public function verificarCedula(Request $request){
            $cedula = $request->cedula;
            $contadorpersona = DB::table('personas')->where('cedula', $cedula)->count();
            $arrayRespuesta = array('respuesta' => false);
            if($contadorpersona == 1){
                $arrayRespuesta['respuesta'] = true;
            }
            echo json_encode($arrayRespuesta);
    }
}
