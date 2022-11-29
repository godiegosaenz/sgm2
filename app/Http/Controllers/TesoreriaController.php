<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\models\ExoneracionAnterior;
use Illuminate\Support\Facades\Validator;

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
    public function store(Request $r)
    {
        DB::beginTransaction();

            $messages = [
                'required' => 'El campo :attribute es requerido.',
                'unique' => 'El numero de cedula ingresado ya existe',
                'size' => 'El campo :attribute debe tener exactamente :size caracteres',
                'max' => 'El campo :attribute no debe exceder los :max caracteres',
            ];

            $reglas = [
                'num_resolucion' => 'bail|required|max:30',
                'ruta_resolucion' => 'bail|required|file',
            ];

            $validator = Validator::make($r->all(),$reglas,$messages);

            if ($validator->fails()) {
                return response()->json(['estado' => 'error','errors'=>$validator->errors()->all()]);
            }
            return response()->json(['estado' => 'ok','success'=>'Record is successfully added']);

            $r->file('ruta_resolucion')->store('exoneracion');
            return true;
        DB::rollBack();
        $ExoneracionAnterior = new ExoneracionAnterior();
        $ExoneracionAnterior->num_predio = $r->num_predio;
        $ExoneracionAnterior->num_resolucion = $r->num_predio;
        $ExoneracionAnterior->observacion = $r->num_predio;
        $ExoneracionAnterior->ruta_resolucion = $r->num_predio;
        $ExoneracionAnterior->usuario = $r->num_predio;
        $ExoneracionAnterior->save();
        foreach($r->checkLiquidacion as $clave => $valor){
            //obtener los datos de la liquidacion
            $liquidacion = DB::connection('pgsql')
                                            ->table('sgm_financiero.ren_liquidacion')
                                            ->join('sgm_app.cat_predio', 'sgm_financiero.ren_liquidacion.predio', '=', 'sgm_app.cat_predio.id')
                                            ->select('sgm_financiero.ren_liquidacion.id','sgm_financiero.ren_liquidacion.id_liquidacion','sgm_financiero.ren_liquidacion.total_pago','sgm_financiero.ren_liquidacion.estado_liquidacion','sgm_financiero.ren_liquidacion.predio','sgm_financiero.ren_liquidacion.anio','sgm_financiero.ren_liquidacion.nombre_comprador','sgm_app.cat_predio.clave_cat')
                                            ->where('sgm_financiero.ren_liquidacion.id','=',$valor)
                                            ->get();
            foreach($liquidacion as $l){
                //obtener el detalle de la liquidacion
                $detalleliquidacion = DB::connection('pgsql')->table('sgm_financiero.ren_det_liquidacion')
                                    ->select('id','liquidacion','rubro','valor','estado')
                                    ->where('liquidacion','=',$l->id)
                                    ->get();
                $total = 0;
                $valor = 0;
                foreach($detalleliquidacion as $dl){
                    $valor = $dl->valor;
                    $total = $total + $valor;
                    //se verifica si el rubro es 2 de impuesto predial
                    if($dl->rubro == 2){
                        $affected = DB::connection('pgsql')
                                            ->table('sgm_financiero.ren_det_liquidacion')
                                            ->where('id', $dl->id)
                                            ->update(['valor' => 0]);
                    }
                }
                //actualizar el saldo en la liquidacion
                $affected = DB::connection('pgsql')
                                            ->table('sgm_financiero.ren_liquidacion')
                                            ->where('id', $l->id)
                                            ->update(['saldo' => $total,'total_pago' => $total]);
                //insertar datos en la tabla exoneracion_anterior
                DB::table('detalle_exoneracion')->insert([
                    'email' => 'kayla@example.com',
                    'votes' => 0
                ]);

            }

        }
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
