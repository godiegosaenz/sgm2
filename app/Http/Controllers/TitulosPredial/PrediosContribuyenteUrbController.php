<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\PsqlPredio;
use DB;
class PrediosContribuyenteUrbController extends Controller
{
    public function index()
    {
         if(!Auth()->user()->hasPermissionTo('Predios Urbanos'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('predios.urbanos');
    }

    public function datatable(Request $r)
    {
        if($r->ajax()){
            $obtener = PsqlPredio::select('id', 'clave_cat') // id obligatorio
            ->where('estado', 'A')
            ->with([
                'propietario' => function ($q) {
                    $q->select('id', 'predio', 'ente') // FKs obligatorias
                    ->with([
                        'contribuyente' => function ($q2) {
                            $q2->select('id',DB::raw("CONCAT(ci_ruc,' - ',apellidos, ' ', nombres) AS nombre")); // id obligatorio
                        }
                    ]);
                }
            ])
            // ->limit(100)
            ->get();

            foreach($obtener as $key=>$data){
                $es_tercera_edad=DB::connection('pgsql')->table('sgm_financiero.fn_solicitud_exoneracion as se')
                ->where('se.estado',1)
                ->where('se.predio',$data->id)
                ->whereIn('exoneracion_tipo',[44, 37,17,18])
                ->where('anio_inicio',date('Y'))
                ->orderBy('fecha_ingreso','desc')
                ->select('solicitante','exoneracion_tipo','valor')
                ->first();
                // ->value('solicitante');
                
                foreach($data->propietario as $key2=>$prop){
                    // $prop->tercera_edad = 'NO';
                    $prop->porcentaje = '';

                    if (!is_null($es_tercera_edad) && $prop->ente == $es_tercera_edad->solicitante) {
                        if($es_tercera_edad->exoneracion_tipo==17){
                            $prop->tercera_edad = '';
                            $prop->tercera_edad = 'LEY DEL ANCIANO';
                           
                        }else if($es_tercera_edad->exoneracion_tipo==18){
                            $prop->tercera_edad = '';
                            $prop->tercera_edad = 'LEY DEL ANCIANO PORCENTAJE';
                            $prop->porcentaje = $es_tercera_edad->valor;
                        }else if($es_tercera_edad->exoneracion_tipo==37){
                            $prop->tercera_edad = '';
                            $prop->tercera_edad = 'LEY DEL DISCAPACITADO';
                            $prop->porcentaje = $es_tercera_edad->valor;
                        }else{
                            $prop->tercera_edad = '';
                            $prop->tercera_edad = 'LEY ORGANICA DE DISCAPACIDADES';
                           
                        }
                    }
                }
               
            }
            // dd($obtener);
            return Datatables($obtener)
            ->addColumn('clave', function ($obtener) {
                return $obtener->clave_cat;
            })
           ->addColumn('contribuyente', function ($predio) {

                if ($predio->propietario->isEmpty()) {
                    return '<span class="text-muted">Sin propietario</span>';
                }

                return $predio->propietario
                    ->map(function ($prop) {
                        return $prop->contribuyente->nombre ?? 'N/D';
                    })
                    ->implode('<br>'); // uno debajo del otro
            })
            ->addColumn('tercera_edad', function ($predio) {

                if ($predio->propietario->isEmpty()) {
                    return '<span class="text-muted"></span>';
                }

                return $predio->propietario
                    ->map(function ($prop) {
                        return $prop->tercera_edad;
                    })
                    ->implode('<br>'); // uno debajo del otro
            })
            ->addColumn('porcentaje', function ($predio) {

                if ($predio->propietario->isEmpty()) {
                    return '<span class="text-muted"></span>';
                }

                return $predio->propietario
                    ->map(function ($prop) {
                        return $prop->porcentaje;
                    })
                    ->implode('<br>'); // uno debajo del otro
            })
            ->rawColumns(['contribuyente','tercera_edad','porcentaje']) // importante para <br>
            ->make(true);
        }
    }

    public function rural()
    {
        if(!Auth()->user()->hasPermissionTo('Predios Rurales'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('predios.rural');
    }

    public function datatableRural(Request $r)
    {
        if($r->ajax()){
            $obtener = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->select('Pre_CodigoCatastral',DB::raw("CONCAT(Titpr_RUC_CI,' - ',TitPr_Nombres) AS nombre"))
            ->distinct()
            ->get();

            foreach($obtener as $key=>$data){
                $exoneracion=DB::connection('sqlsrv')->table('PARAMETROS_DETERM_PREDIO')
                ->where('Pre_CodigoCatastral',$data->Pre_CodigoCatastral)
                ->where('ParDeP_valor',1)
                ->whereIn('ParDe_Codigo',['01','04'])
                ->first();
                $obtener[$key]->exoneracion="NO";
                if(!is_null($exoneracion)){
                    if($exoneracion->ParDe_Codigo=='01'){
                        $obtener[$key]->exoneracion="LEY DEL ANCIANO";
                    }else{
                        $obtener[$key]->exoneracion="MINUSVALIDOS (DISCAPACITADOS)";
                    }
                }
            }

            return Datatables($obtener)
            ->addColumn('clave', function ($obtener) {
                return $obtener->Pre_CodigoCatastral;
            })
           ->addColumn('contribuyente', function ($obtener) {

                return $obtener->nombre;
            })
            ->addColumn('exoneracion', function ($obtener) {

                return $obtener->exoneracion;
            })
            // ->rawColumns(['contribuyente']) // importante para <br>
            ->make(true);
        }
    }

    public function ruralPoligono()
    {
        return view('predios.rural_poligono');
    }

    public function llenaTablaPoligono($poligono){
        try{
            // $claveCatastral='1322506501002082000';
            // $poligono = substr($claveCatastral, 10, 3);
            // dd($poligono);

            // $poligono='002';
            $obtener = DB::connection('sqlsrv')->table('PREDIO')
            ->select('Pre_CodigoCatastral','Pre_NombrePredio as nombre')
            ->whereRaw('SUBSTRING(Pre_CodigoCatastral, 11, 3) = ?', [$poligono])
            ->where('Pre_Tipo','Rural')
            ->get();


            return ["resultado"=>$obtener, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];
        }
    }
}