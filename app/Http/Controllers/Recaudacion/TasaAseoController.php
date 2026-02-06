<?php

namespace App\Http\Controllers\Recaudacion;

use App\Http\Controllers\Controller;
use App\Models\ActualizaData;
use App\Models\RuralEnteCorreo;
use App\Models\RuralEnteTelefono;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\TituloRural;
use App\Models\NotificacionCoactiva;
use DB;
use Storage;
use Mail;
use Illuminate\Support\Str;
use App\Models\PsqlEnte;
use App\Models\PsqlEnteTelefono;
use App\Models\PsqlEnteCorreo;
use PDF;
class TasaAseoController extends Controller
{
    public function index()
    {
        return view('tasa_aseo.index');
    }

    public function buscaPrediosUrbanos($cedula, $notifica=1){
        try {
           
            $predios_contribuyente= DB::connection('pgsql')->table('sgm_app.cat_ente as e')
            ->join('sgm_app.cat_predio_propietario as pp', 'pp.ente', '=', 'e.id')
            ->join('sgm_app.cat_predio as p', 'p.id', '=', 'pp.predio')
            ->leftjoin('sgm_app.cat_ciudadela as ciu', 'ciu.id', '=', 'p.ciudadela')
            ->leftjoin('sgm_app.cat_tipo_conjunto as tc', 'tc.id', '=', 'p.tipo_conjunto')
            ->leftjoin('sgm_app.ctlg_item as cat', 'cat.id', '=', 'p.uso_solar')
            ->where('pp.estado','A')
            ->where('p.estado','A')
            ->where('e.ci_ruc',$cedula)
            ->select(
                'p.num_predio',
                'p.clave_cat',
                'ciu.nombre as ciudadela',
                'tc.nombre as tipoconj',
                'cat.valor as uso_suelo',
                'p.id as idpredio',

            )
            ->get();
          
           
            return ["resultado"=>$predios_contribuyente, 
                    "error"=>false
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function buscaDeudaAseoUrb(Request $request){
        try {
            $predios=$request->checkNumPredio;
           
            $predios_contribuyente = DB::connection('pgsql')->table('sgm_aseo_recoleccion_desecho.liquidacion as l')
            ->join('sgm_app.cat_predio as p', 'p.id', '=', 'l.predio')
            ->leftJoin('sgm_app.cat_ciudadela as ciu', 'ciu.id', '=', 'p.ciudadela')
            ->leftJoin('sgm_app.cat_tipo_conjunto as tc', 'tc.id', '=', 'p.tipo_conjunto')
            ->leftJoin('sgm_app.ctlg_item as cat', 'cat.id', '=', 'p.uso_solar')
            ->where('p.estado', 'A')
            ->whereIn('l.predio', $predios)
            ->select(
                'p.num_predio',
                'p.clave_cat',
                'ciu.nombre as ciudadela',
                'tc.nombre as tipoconj',
                'cat.valor as uso_suelo',
                'p.id as idpredio',
                'l.anio',
                DB::raw('
                    SUM(l.valor) as lvalor
                '),
                DB::raw('
                    SUM(l.interes) as linteres
                '),
                DB::raw('
                    SUM(l.recargo) as lrecargo
                '),
                DB::raw('
                    SUM(l.descuento) as ldescuento
                '),
               
                DB::raw('
                    SUM(l.valor) + 
                    SUM(COALESCE(l.interes, 0)) + 
                    SUM(COALESCE(l.recargo, 0)) - 
                    SUM(COALESCE(l.descuento, 0)) AS total_cob
                ')
            )
            ->groupBy(
                'p.num_predio',
                'p.clave_cat',
                'ciu.nombre',
                'tc.nombre',
                'cat.valor',
                'p.id',
                'l.anio',
                'l.valor',
                'l.interes',
                'l.recargo',
                'l.descuento'
            )
            ->orderBy('p.num_predio')
            ->orderBy('l.anio')
            ->get();

            return ["resultado"=>$predios_contribuyente, 
                    "error"=>false
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function buscaDeudaAseoUrbParcial(Request $request){
        try {
            $predios=$request->checkLiquidacion;
           
            $predios_contribuyente = DB::connection('pgsql')->table('sgm_aseo_recoleccion_desecho.liquidacion as l')
            ->join('sgm_app.cat_predio as p', 'p.id', '=', 'l.predio')
            ->leftJoin('sgm_app.cat_ciudadela as ciu', 'ciu.id', '=', 'p.ciudadela')
            ->leftJoin('sgm_app.cat_tipo_conjunto as tc', 'tc.id', '=', 'p.tipo_conjunto')
            ->leftJoin('sgm_app.ctlg_item as cat', 'cat.id', '=', 'p.uso_solar')
            ->where('p.estado', 'A')
            ->whereIn('l.predio', $predios)
            ->select(
                'p.num_predio',
                'p.clave_cat',
                'ciu.nombre as ciudadela',
                'tc.nombre as tipoconj',
                'cat.valor as uso_suelo',
                'p.id as idpredio',
                'l.anio',
                'l.mes',
                DB::raw("
                    CASE
                        WHEN l.mes = 1 THEN 'Enero'
                        WHEN l.mes = 2 THEN 'Febrero'
                        WHEN l.mes = 3 THEN 'Marzo'
                        WHEN l.mes = 4 THEN 'Abril'
                        WHEN l.mes = 5 THEN 'Mayo'
                        WHEN l.mes = 6 THEN 'Junio'
                        WHEN l.mes = 7 THEN 'Julio'
                        WHEN l.mes = 8 THEN 'Agosto'
                        WHEN l.mes = 9 THEN 'Septiembre'
                        WHEN l.mes = 10 THEN 'Octubre'
                        WHEN l.mes = 11 THEN 'Noviembre'
                        WHEN l.mes = 12 THEN 'Diciembre'
                        ELSE 'Desconocido'
                    END AS mes_nombre"
                ),
                'l.num_titulo',
                'l.valor as lvalor',
                'l.interes as linteres',
                'l.recargo as lrecargo',
                'l.descuento as ldescuento',
                'l.valor as total_cob'
            )
            ->orderBy('p.num_predio')
            ->orderBy('l.anio')
            ->orderBy('l.mes')
            ->get();

            return ["resultado"=>$predios_contribuyente, 
                    "error"=>false
            ];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

}