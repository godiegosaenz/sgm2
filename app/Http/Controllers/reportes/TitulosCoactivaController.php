<?php

namespace App\Http\Controllers\reportes;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Support\Facades\DB;

class TitulosCoactivaController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $r)
    {
        foreach($r->checkLiquidacion as $titulos){

            $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
            ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
            ->leftJoin('sgm_app.cat_ente', 'sgm_financiero.ren_liquidacion.comprador', '=', 'sgm_app.cat_ente.id')
            ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat','sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos')
            ->where('estado_liquidacion','=',2)
            ->where('sgm_financiero.ren_liquidacion.id','=',$titulos)
            /*->whereNot(function($query){
                $query->where('estado_liquidacion', 4)
                ->orWhere('estado_liquidacion', '=', 5);
            })*/
            ->orderBy('anio', 'desc')
            ->get();

            return $liquidacionUrbana;
        }


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
