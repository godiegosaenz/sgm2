<?php

namespace App\Http\Controllers\tesoreria;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Models\PsqlEnte;
use App\Models\PsqlLiquidacion;
use Exception;
use Barryvdh\DomPDF\Facade\Pdf;

class TituloRuralController extends Controller
{
    public function index(){
        $num_predio = 0;
        return view('tesoreria.TitulosRural',compact('num_predio'));
    }

    public function consulta(Request $r)
    {
        try {
        $data = array();
        $cedula=1;
        $clave=2;
        $valor=29.21;
        // $intereses=DB::connection('sqlsrv')->table('INTERES_MORA as cv')
        // ->whereBetween('IntMo_Año',[2017,2025])
        // ->select('IntMo_Valor')
        // ->get();

        // foreach ($intereses as $anio => $tasa) {
        //     // $valor *= $tasa->IntMo_Valor;
        //     $valor *= (1 + $tasa->IntMo_Valor);
        // }

        // dd($valor);


        // $interesInicio = DB::connection('sqlsrv')->table('INTERES_MORA')->where('IntMo_Año', 2017)->value('IntMo_Valor');
        // $interesFin = DB::connection('sqlsrv')->table('INTERES_MORA')->where('IntMo_Año', 2024)->value('IntMo_Valor');

        // $interesCalculado = $valor * (($interesInicio / $interesFin) - 1);
        // $montoFinal = $valor + $interesCalculado;
        // dd($montoFinal);


        if($r->tipo=="2"){
            $predio_id = DB::connection('sqlsrv')->table('CARTERA_VENCIDA')->select('Pre_CodigoCatastral')
            ->where('Pre_CodigoCatastral', '=', $r->clave)->first();
            //se obtiene las liquidaciones rurales
            $num_predio = $r->clave;
            $clave = $r->clave;

            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral','cv.CarVe_FechaEmision','cv.CarVe_NumTitulo','cv.CarVe_CI'
            ,'cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','cv.CarVe_Nombres')
            ->where('cv.Pre_CodigoCatastral', '=', $r->clave)
            ->whereIn('cv.CarVe_Estado',['E'])
            // ->where('Pre_Tipo','Rural')
            ->orderby('CarVe_NumTitulo','desc')
            ->get();

           


            // dd($liquidacionRural);

        }else{
            $predio_id = DB::connection('sqlsrv')->table('CARTERA_VENCIDA')->select('CarVe_CI')
            ->where('CarVe_CI', '=', $r->cedula)->first();
            //se obtiene las liquidaciones rurales
            $num_predio = $r->cedula;
            $cedula = $r->cedula;
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->Join('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral','cv.CarVe_FechaEmision','cv.CarVe_NumTitulo','cv.CarVe_CI'
            ,'cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres')
            ->where('CarVe_CI', '=', $r->cedula)
            ->whereIn('cv.CarVe_Estado',['E'])
            ->where('Pre_Tipo','Rural')
            ->get();
            
        }
        

        if(count($liquidacionRural) >= 1) {
            return view('tesoreria.TitulosRural',compact('liquidacionRural','num_predio','clave','cedula'));
        }else{
            return redirect('titulorural/')->with('status', 'No existe la matricula ingresada')->withInput();
        }

        } catch (Exception $e) {
            // Log the message locally OR use a tool like Bugsnag/Flare to log the error
            return redirect('titulorural/')->with('status', 'Problema de conexion '.$e->getMessage());

        }
    }

    public function reportetest(Request $r){

        $dataArray = array();
        foreach($r->checkLiquidacion as $clave => $valor){
            $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->leftJoin('PROPIETARIO as pr', 'pr.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral','cv.CarVe_FechaEmision','cv.CarVe_NumTitulo','cv.CarVe_CI'
            ,'cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','p.Pre_NombrePredio','cv.CarVe_ValTotalTerrPredio'
            ,'cv.CarVe_ValTotalEdifPredio','cv.CarVe_ValOtrasInver','cv.CarVe_ValComerPredio','cv.CarVe_RebajaHipotec'
            ,'cv.CarVe_BaseImponible','cv.CarVe_IPU','cv.CarVe_TasaAdministrativa','cv.CarVe_Bomberos'
            ,'cv.CarVe_ValorEmitido','pr.Pro_DireccionDomicilio')
            ->where('CarVe_NumTitulo', '=', $valor)
            ->get();
            // Pre_NombrePredio
            //dd($liquidacion);
        

            $fecha_hoy=date('Y-m-d');
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
            $fecha_timestamp = strtotime($fecha_hoy);    
            $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);
    

            // $rubros = DB::connection('pgsql')->table('sgm_financiero.ren_det_liquidacion as rdl')
            //                                     ->join('sgm_financiero.ren_rubros_liquidacion as rrl', 'rdl.rubro', '=', 'rrl.id')
            //                                     ->select('rdl.id', 'rdl.liquidacion', 'rdl.rubro', 'rdl.valor', 'rdl.estado', 'rrl.descripcion')
            //                                     ->where('rdl.liquidacion', $valor)
            //                                     ->get();

            $rubros=[];
                                               
            $liquidacionRural['rubros'] = $rubros;

            array_push($dataArray, $liquidacionRural);
        }
        // dd($dataArray);

        $data = [
            'title' => 'Reporte de liquidacion',
            'date' => date('m/d/Y'),
            'DatosLiquidacion' => $dataArray,
            'fecha_formateada'=>$fecha_formateada
        ];

        $pdf = PDF::loadView('reportes.reporteTitulosRural',$data);

        // return $pdf->stream("aa.pdf");
        return $pdf->download('reporteTituloRural.pdf');
    }

}