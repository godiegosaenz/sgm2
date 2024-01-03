<?php

namespace App\Http\Controllers\especialista;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Especialista;


class EspecialistaController extends Controller
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
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $r)
    {

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

    public function list(Request $r){
        $especialista = Especialista::all();
        return Datatables($especialista)
                ->addColumn('nombres', function($especialista){
                    return $especialista->persona->nombres;
                })
                ->addColumn('apellidos', function($especialista){
                    return $especialista->persona->apellidos;
                })
                ->addColumn('cedula', function($especialista){
                    return $especialista->persona->cedula;
                })
                ->addColumn('especialidad', function($especialista){
                    return $especialista->especialidad->nombre;
                })
                ->addColumn('action', function ($especialista) {
                    return '<a class="btn btn-primary btn-sm" onclick="seleccionarespecialista(\''.$especialista->persona->id.'\',\''.$especialista->persona->cedula.'\',\''.$especialista->persona->nombres.'\',\''.$especialista->persona->apellidos.'\')">Seleccionar</a>';
                })
        ->make(true);
    }
}
