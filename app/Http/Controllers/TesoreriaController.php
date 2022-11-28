<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class TesoreriaController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        return view('tesoreria.consulta');
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {

    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        //1. obtener los id de las liquidaciones obtenidos en los checkboxs
        //2. recorrer cada liquidacion
        //3. en cada bucle obtener los datos de cada liquidacion
        //4. con los datos obtenidos, se eliminará el valor del rubro de impuesto predial
        //5. se sumara los valores de los demas rubros.
        //6. se actualizara el total en las liquidaciones.
        //7. Una vez completado ese procesos se guardara la informacion de la tabla exoneracion_anterior
        //8. Luego se almacenará la información en la tabla exoneracion_det_liquidacion
    }

    /**
     * Display the specified resource.
     *
     * @param  \App\Models\LiquidationSequence  $liquidationSequence
     * @return \Illuminate\Http\Response
     */
    public function show()
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  \App\Models\LiquidationSequence  $liquidationSequence
     * @return \Illuminate\Http\Response
     */
    public function edit()
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \App\Models\LiquidationSequence  $liquidationSequence
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  \App\Models\LiquidationSequence  $liquidationSequence
     * @return \Illuminate\Http\Response
     */
    public function destroy()
    {
        //
    }

    public function consulta(Request $r)
    {
        if($r->num_predio != null){
            $data = array();
            $predio_id = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id')->where('num_predio', '=', $r->num_predio)->first();
            //se obtiene las liquidaciones urbanas
            $liquidacionUrbana = array();
            $liquidacionUrbana[1] = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')
                                            ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                            ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat')
                                            ->where('predio','=',$predio_id->id)
                                            ->orderBy('anio', 'desc')
                                            ->get();

            $data['estado'] = 'ok';
            $data['mensaje'] = 'Existen Valores pendientes de pago';
            $data['liquidacionUrbana'] = $liquidacionUrbana;
            return json_encode($data);
        }
    }
}
