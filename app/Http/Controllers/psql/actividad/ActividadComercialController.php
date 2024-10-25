<?php

namespace App\Http\Controllers\psql\actividad;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlPaActividadesComerciales;

class ActividadComercialController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        //
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }

    public function datatables(Request $r){
        if($r->ajax()){

            $data = PsqlPaActividadesComerciales::select('id','ciiu','descripcion','nivel')->get();
               return Datatables($data)
               ->addColumn('action', function ($data) {
                   $buttonPersona = '';
                   return '<a class="btn btn-primary btn-sm" onclick="seleccionarActividad(\''.$data->id.'\',\''.$data->descripcion.'\',\''.$data->ciiu.'\')">Seleccionar</a>';
               })
               ->rawColumns(['action'])
               ->make(true);
       };
    }
}
