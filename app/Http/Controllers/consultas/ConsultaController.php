<?php

namespace App\Http\Controllers\consultas;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\models\Cita;
use App\models\Consulta;
use Illuminate\Support\Facades\Validator;
use Carbon\Carbon;

class ConsultaController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        //
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create(Request $r,$id)
    {
        $Cita = Cita::find($id);
        return view('consultas.consultaCreate',compact('Cita'));
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $r)
    {
        $messages = [
            'required' => 'El campo :attribute es requerido.',
            'unique' => 'El numero de cedula ingresado ya existe',
            'size' => 'El campo :attribute debe tener exactamente :size caracteres',
            'max' => 'El campo :attribute no debe exceder los :max caracteres',
        ];

        $reglas = [
            'cita_id' => 'bail|required',
            'diagnostico' => 'bail|required',
            'tratamiento' => 'bail|required'
        ];

        $validator = Validator::make($r->all(),$reglas,$messages);

        if ($validator->fails()) {
            return redirect('/consulta/ingresar/'.$r->cita_id)
                        ->withErrors($validator)
                        ->withInput();
        }

        $Consulta = new Consulta();
        $Consulta->diagnostico = $r->diagnostico;
        $Consulta->tratamiento = $r->tratamiento;
        $Consulta->cita_id = $r->cita_id;
        $Consulta->fecha = Carbon::now();
        $Consulta->hora = date("H:i:s");
        $Consulta->save();

        $Cita = Cita::find($r->cita_id);
        $Cita->estado = 'atendido';
        $Cita->save();

        return back()->withInput()->with('guardado','Consulta guardada con exito');

    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        //
    }
}
