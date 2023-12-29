<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;
use Exception;

class ConsultaPredioController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('welcome');
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
        try {
            $liquidacion = null;
            $date = Carbon::now();
            $year = $date->format('Y');
            $total = 0;
            $data = array();
            if($request->selectTipo == 1){
                $countEnteUrbano = DB::connection('pgsql')->table('sgm_app.cat_ente')
                                                    ->where('sgm_app.cat_ente.ci_ruc', '=', $request->catCedula)
                                                    ->count();
                //verificar si existe persona
                if($countEnteUrbano == 0 ){
                    //return back()->with('status');
                    return redirect('/consulta')->with('status', 'No existe el usuario');
                }
                //consulta predios urbanos
                $predios = DB::connection('pgsql')->table('sgm_app.cat_predio_propietario')
                                                    ->join('sgm_app.cat_ente', 'sgm_app.cat_predio_propietario.ente', '=', 'sgm_app.cat_ente.id')
                                                    ->where('sgm_app.cat_predio_propietario.estado', '=', 'A')
                                                    ->where('sgm_app.cat_ente.ci_ruc', '=', $request->catCedula)
                                                    ->get();
                //se verifica si no tienen ningun predio
                if($predios->count() == 0){
                    return redirect('/consulta')->with('status', 'No Tiene predios');
                }

                //se obtiene las liquidaciones urbanas
                $contadorliquidacionurbano = 0;
                $liquidacionUrbana = array();
                foreach($predios as $p){
                    $contadorliquidacionurbano++;
                    $liquidacionUrbana[$contadorliquidacionurbano] = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
                                                                    ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                                                    ->leftJoin('sgm_app.cat_ente', 'sgm_financiero.ren_liquidacion.comprador', '=', 'sgm_app.cat_ente.id')
                                                                    ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat','sgm_app.cat_predio.num_predio','sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos')
                                                                    ->where('predio','=',$p->predio)
                                                                    ->whereNot(function($query){
                                                                        $query->where('estado_liquidacion', 4)
                                                                        ->orWhere('estado_liquidacion', '=', 5);
                                                                    })
                                                                    ->orderBy('anio', 'desc')
                                                                    ->get();
                }

                //return redirect('/consulta')->with('status', 'No existe el usuario');

                $valort = 0;
                foreach($liquidacionUrbana as $lu)
                {
                    foreach($lu as $i)
                    {
                        if($i->estado_liquidacion == 2)
                        {
                            $valort = $valort + $i->total_pago;
                        }
                    }
                }
                return view('welcome',compact('liquidacionUrbana','valort'));
            }
        } catch (Exception $e) {
            // if an exception happened in the try block above
            return redirect('/consulta')->with('status', 'Problema de conexion, comuniquese con el administrador de sistemas. '.$e->getMessage());

        }
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
