<?php

namespace App\Http\Controllers\enlinea;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;
use Illuminate\Support\Collection;

class ConsultaLineaController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        return view('enlinea.consultasLinea');
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
    public function store(Request $request)
    {
        $liquidacion = null;
        DB::beginTransaction();
        $date = Carbon::now();
        $year = $date->format('Y');
        $total = 0;
        $data = array();
        if($request->selectTipo == 1){
            $countEnteUrbano = DB::connection('pgsql')->table('sgm_app.cat_ente')
                                                ->where('sgm_app.cat_ente.ci_ruc', '=', $request->catCedula)
                                                ->count();
            $countEnteRural = DB::connection('odbc-connection-ame')->select('select * from CIUDADANO where Ciu_Cedula = ?',[$request->catCedula]);
            if($countEnteUrbano == 0 and count($countEnteRural) == 0){
                $data['estado'] = 'no';
                $data['mensaje'] = 'No existen registros';
                return json_encode($data);
            }

            //consulta predios urbanos
            $predios = DB::connection('pgsql')->table('sgm_app.cat_predio_propietario')
                                            ->join('sgm_app.cat_ente', 'sgm_app.cat_predio_propietario.ente', '=', 'sgm_app.cat_ente.id')
                                            ->where('sgm_app.cat_predio_propietario.estado', '=', 'A')
                                            ->where('sgm_app.cat_ente.ci_ruc', '=', $request->catCedula)
                                            ->get();
            //consultar predios rurales
            $prediosRurales = DB::connection('odbc-connection-ame')->select('select * from PREDIO where Ciu_cedula = ?',[$request->catCedula]);
            //se verifica si no tienen ningun predio
            if($predios->count() == 0 and Count($prediosRurales)== 0){
                $data['estado'] = 'no';
                $data['mensaje'] = 'No existen registros';
                return json_encode($data);
            }
            //se obtiene las liquidaciones urbanas
            $contadorliquidacionurbano = 0;
            $liquidacionUrbana = array();
            foreach($predios as $p){
                $liquidacion = DB::connection('pgsql')->select('select * from consultas.f_obtener_liquidacion(?,?)', [$p->predio,$year]);
                $contadorliquidacionurbano = $contadorliquidacionurbano + 1;
                $liquidacionUrbana[$contadorliquidacionurbano] = $liquidacion;
            }

            //se obtiene las liquidaciones rurales

            $liquidacionRural = array();
            $contadorliquidacion = 0;
            foreach($prediosRurales as $preru){
                $datosliquidacion = DB::connection('odbc-connection-ame')->select('select * from TITULOS_PREDIO where Pre_CodigoCatastral = ?',[$preru['Pre_CodigoCatastral']]);
                //$datoscarteravencida = DB::connection('odbc-connection-ame')->select('select * from CARTERA_VENCIDA where Pre_CodigoCatastral = ?',[$preru['Pre_CodigoCatastral']]);
                $contadorliquidacion = $contadorliquidacion + 1;
                $liquidacionRural[$contadorliquidacion] = $datosliquidacion;
                //$liquidacionRural[$contadorliquidacion] = $datoscarteravencida;
            }

            if($contadorliquidacionurbano == 0 and $contadorliquidacion == 0){
                $data['estado'] = 'no';
                $data['mensaje'] = 'No existen registros';
                return json_encode($data);
            }

            //recorremos liquidaciones
            /*$htmlLiquidacionrural = '<table class="table table-bordered">';
            $htmlLiquidacionrural .= '<thead>';
            $htmlLiquidacionrural .= '<tr>';
            $htmlLiquidacionrural .= '<th>Año</th>';
            $htmlLiquidacionrural .= '<th>Clave catastral</th>';
            $htmlLiquidacionrural .= '<th>Contribuyente</th>';
            $htmlLiquidacionrural .= '<th>Total</th>';
            $htmlLiquidacionrural .= '</tr>';
            $htmlLiquidacionrural .= '</thead>';
            $htmlLiquidacionrural .= '<tbody>';
            foreach ($liquidacionRural as $lr){
                $htmlLiquidacionrural .= '<tr>';
                $htmlLiquidacionrural .= '<td>'.$lr[0]['Pre_CodigoCatastral'].'</td>';
                $htmlLiquidacionrural .= '<td>'.$lr[0]['TitPr_FechaEmision'].'</td>';
                $htmlLiquidacionrural .= '<td>'.$lr[0]['TitPr_Nombres'].'</td>';
                $htmlLiquidacionrural .= '<td>'.$lr[0]['TitPr_Valor1'].'</td>';
                $htmlLiquidacionrural .= '</tr>';
            }
            $htmlLiquidacionrural .= '</tbody>';
            $htmlLiquidacionrural .= '</table>';*/


            /*foreach($liquidacion as $l){
                $total = $total + $l->saldo;
            }*/
            //$total = 50;
            //consulta predios rurales
            $data['estado'] = 'ok';
            $data['mensaje'] = 'Existen Valores pendientes de pago';
            $data['liquidacionRural'] = $liquidacionRural;
            $data['liquidacionUrbana'] = $liquidacionUrbana;

            return json_encode($data);

            //return mb_convert_encoding(count($liquidacionRural), 'UTF-8', 'UTF-8');
            //return redirect()->route('catpredio.index')->with(['estado' => 'ok','mensaje' => 'Existen Valores pendientes de pago','liquidacionUrbana' => $liquidacionUrbana,'liquidacionRural' => $liquidacionRural,'total' => $total]);
        }

        DB::rollBack();

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
}
