<?php

namespace App\Http\Controllers\psql\ente;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlEnte;

class EnteController extends Controller
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
            if($r->tipo == 'propietario'){
                $listapersona = PsqlEnte::select('id','ci_ruc','nombres','apellidos')->get();
                return Datatables($listapersona)
                ->addColumn('action', function ($listapersona) {
                    $buttonPersona = '';
                    return '<a class="btn btn-primary btn-sm" onclick="seleccionarpropietario(\''.$listapersona->id.'\',\''.$listapersona->ci_ruc.'\',\''.$listapersona->nombres.'\',\''.$listapersona->apellidos.'\')">Seleccionar</a>';
                    return $buttonPersona;

                })
                ->rawColumns(['action'])
                ->make(true);
            }else{
                $listapersona = PsqlEnte::select('id','ci_ruc','nombres','apellidos')->get();
                return Datatables($listapersona)
                ->addColumn('action', function ($listapersona) {
                    $buttonPersona = '';
                    return '<a class="btn btn-primary btn-sm" onclick="seleccionarrepresentante(\''.$listapersona->id.'\',\''.$listapersona->ci_ruc.'\',\''.$listapersona->nombres.'\',\''.$listapersona->apellidos.'\')">Seleccionar</a>';
                    return $buttonPersona;

                })
                ->rawColumns(['action'])
                ->make(true);
            }

       };
    }

    public function getEnteCedula(Request $r){
        $query = $r->input('query');
        $data = PsqlEnte::select('id','ci_ruc','nombres','apellidos')->where('ci_ruc',$query)->first();
        if ($data) {
            // Devolver la información en formato JSON
            return response()->json($data, 200);
        } else {
            // Devolver un error 404 si no se encuentra nada
            return response()->json(['message' => 'No encontrado'], 404);
        }
    }
}
