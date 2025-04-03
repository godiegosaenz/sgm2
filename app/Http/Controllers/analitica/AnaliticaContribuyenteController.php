<?php

namespace App\Http\Controllers\analitica;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlEnte;
use Illuminate\Support\Facades\DB;

class AnaliticaContribuyenteController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {

        $duplicados_nombres_apellidos = DB::connection('pgsql')->table('sgm_app.cat_ente')
                        ->select('nombres', 'apellidos', DB::raw('COUNT(*) as cantidad'))
                        ->groupBy('nombres', 'apellidos')
                        ->having(DB::raw('COUNT(*)'), '>', 1)
                        ->get();
        return view("analitica.analiticaContribuyente", compact("duplicados_nombres_apellidos"));
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
}
