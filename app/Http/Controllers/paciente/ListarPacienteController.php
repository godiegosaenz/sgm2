<?php

namespace App\Http\Controllers\paciente;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Datatables;
use App\models\Persona;

class ListarPacienteController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function index(){
        return view('paciente.listarpaciente');
    }

    public function listar(Request $r){
        if($r->ajax()){

             $listapersona = Persona::all();
            if($r->formulario == 'cita'){
                return Datatables($listapersona)
                ->removeColumn('idocupacion')
                ->removeColumn('idhistoriaClinica')
                /*->addColumn('edad', function($listapersona){
                    $edad = 0;
                    $edad = date("Y") - $listapersona->fechaNacimiento;
                    return $edad;
                })*/
                ->addColumn('edad', function ($listapersona) {
                    $yearactual = date('Y');
                    $yearnacimiento = explode('-',$listapersona->fechaNacimiento);
                    $edad = $yearactual - $yearnacimiento[0];
                    return $edad.' años';
                })
                ->addColumn('foto', function($listapersona){

                    if(isset($listapersona->rutaimagen)){
                        return '<img height="80" src="'.asset($listapersona->rutaimagen).'" alt="">';
                    }else{
                        return '<img height="80" src="'.asset('img/perfil.png').'" alt="">';
                    }

                })
                ->addColumn('action', function ($listapersona) {
                    return '<a class="btn btn-primary btn-sm" onclick="seleccionarpersona(\''.$listapersona->id.'\',\''.$listapersona->cedula.'\',\''.$listapersona->nombres.'\',\''.$listapersona->apellidos.'\')">Seleccionar</a>';

                })
                ->make(true);

            }else if($r->formulario == 'persona'){
                return Datatables($listapersona)
                ->removeColumn('idocupacion')
                ->removeColumn('idhistoriaClinica')
                /*->addColumn('edad', function($listapersona){
                    $edad = 0;
                    $edad = date("Y") - $listapersona->fechaNacimiento;
                    return $edad;
                })*/
                ->addColumn('edad', function ($listapersona) {
                    $yearactual = date('Y');
                    $yearnacimiento = explode('-',$listapersona->fechaNacimiento);
                    $edad = $yearactual - $yearnacimiento[0];
                    return $edad.' años';
                })
                ->addColumn('foto', function($listapersona){

                    if(isset($listapersona->rutaimagen)){
                        return '<img height="80" src="'.asset($listapersona->rutaimagen).'" alt="">';
                    }else{
                        return '<img height="80" src="'.asset('img/perfil.png').'" alt="">';
                    }
                })
                ->addColumn('action', function ($listapersona) {
                    $buttonPersona = '';
                    $buttonPersona .= '<a class="btn btn-primary btn-sm" href="'.route('detallar.persona',$listapersona->id).'">Ver</a> ';
                    $buttonPersona .= '<a class="btn btn-warning btn-sm" href="'.route('editar.paciente',$listapersona->id).'">Editar</a>';
                    return $buttonPersona;

                })
                ->rawColumns(['foto','action'])
                ->make(true);
            }


        };
    }
}
