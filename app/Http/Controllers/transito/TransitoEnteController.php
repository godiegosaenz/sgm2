<?php

namespace App\Http\Controllers\transito;

use App\Http\Controllers\Controller;
use App\Models\PsqlEnte;
use Illuminate\Http\Request;
use App\Models\TransitoEnte;
use App\Models\PsqlEnteTelefono;
use App\Models\PsqlEnteCorreo;

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
        $request->validate([
            'ci_ruc' => 'required|string|max:20|unique:personas,ci_ruc',
            'nombres' => 'required|string|max:100',
            'apellidos' => 'required|string|max:100',
            'es_persona' => 'required|boolean',
            'direccion' => 'nullable|string|max:255',
            'fecha_nacimiento' => 'nullable|date',
            'correo' => 'nullable|email',
            'telefono' => 'nullable|string|max:20',
        ]);

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
        // $data = PsqlEnte::select('id','ci_ruc','nombres','apellidos','direccion','fecha_nacimiento')
        // ->where('ci_ruc',$query)->first();
        // // dd($data);
        // $telefono=PsqlEnteTelefono::where('ente',$data->id)->select('telefono')->orderBy('id', 'desc')->first();
        // $correo=PsqlEnteCorreo::where('ente',$data->id)->select('email')->orderBy('id', 'desc')->first();
        // if ($data) {
        //     // Devolver la información en formato JSON
        //     return response()->json($data, 200);
        // } else {
        //     // Devolver un error 404 si no se encuentra nada
        //     return response()->json(['message' => 'No encontrado'], 404);
        // }


        $data = PsqlEnte::select('id', 'ci_ruc', 'nombres', 'apellidos', 'direccion', 'fecha_nacimiento')
        ->where('ci_ruc', $query)
        ->first();

        if ($data) {
            $telefono = PsqlEnteTelefono::where('ente', $data->id)
                ->select('telefono')
                ->orderBy('id', 'desc')
                ->first();

            $correo = PsqlEnteCorreo::where('ente', $data->id)
                ->select('email')
                ->orderBy('id', 'desc')
                ->first();

            // Agregar teléfono y correo al objeto $data
            $data->telefono = $telefono ? $telefono->telefono : null;
            $data->correo = $correo ? $correo->email : null;

            return response()->json($data, 200);
        } else {
            return response()->json(['error' => 'No se encontró el ente'], 404);

        }
    }
}