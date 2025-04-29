<?php

namespace App\Http\Controllers\transito;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\TransitoEnte;

class TransitoEnteController extends Controller
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

    public function getEnteCedula(Request $r){
        $query = $r->input('query');
        $data = TransitoEnte::select('id','cc_ruc','nombres','apellidos','correo','telefono','direccion','fecha_nacimiento')->where('cc_ruc',$query)->first();
        if ($data) {
            // Devolver la información en formato JSON
            return response()->json($data, 200);
        } else {
            // Devolver un error 404 si no se encuentra nada
            return response()->json(['message' => 'No encontrado'], 404);
        }
    }
}
