<?php

namespace App\Http\Controllers\analitica;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlEnte;
use App\Models\TransitoImpuesto;
use Illuminate\Support\Facades\DB;
use Endroid\QrCode\QrCode;
use Endroid\QrCode\Writer\PngWriter;
use Illuminate\Support\Facades\Gate;

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

    public function predios(){

        return view("analitica.AnaliticaPredio");
    }

    public function cargaData(Request $request){

        try{

            $desde = (float) $request->input('filtroDesde');
            $hasta = (float) $request->input('filtroHasta');
            $rango = (float) $request->input('filtroRango');
            $tipo=$request->filtroTipo;

            $resultados_urbano = [];
            $resultados_rural = [];
            $resultados_final= [];
            $tipo_busqueda="";
            $total_urbano=0;
             $total_rural=0;
            if($tipo=="Urbano"){
                for ($i = $desde; $i < $hasta; $i += $rango) {
                    $inicio = $i;
                    $fin = $i + $rango;

                    $cantidad = DB::connection('pgsql')->table('sgm_app.cat_predio')
                        ->whereBetween('avaluo_municipal', [$inicio, $fin])
                        ->where('estado','A')
                        ->count();
                    $total_urbano=$total_urbano+$cantidad;

                    $resultados_urbano[] = [
                        'rango' => "De {$inicio} a {$fin}",
                        'cantidad' => $cantidad
                    ];
                    $tipo_busqueda="U";
                }
            }else if($tipo== 'Rural'){
                for ($i = $desde; $i < $hasta; $i += $rango) {
                    $inicio = $i;
                    $fin = $i + $rango;

                    $cantidad = DB::connection('sqlsrv')
                    ->table('dbo.TITULOS_PREDIO')
                    ->whereDate('TitPr_FechaEmision', '2025-01-01')
                    ->whereBetween('TitPr_ValComerPredio', [$inicio, $fin])
                    ->distinct('Pre_CodigoCatastral')
                    ->count();

                    $total_rural=$total_rural+$cantidad;

                    $resultados_rural[] = [
                        'rango' => "De {$inicio} a {$fin}",
                        'cantidad' => $cantidad
                    ];

                    $tipo_busqueda="R";
                }
            }else{
                for ($i = $desde; $i < $hasta; $i += $rango) {
                    $inicio = $i;
                    $fin = $i + $rango;

                    $cantidad_urbano = DB::connection('pgsql')->table('sgm_app.cat_predio')
                        ->whereBetween('avaluo_municipal', [$inicio, $fin])
                        ->where('estado','A')
                        ->count();
                    $total_urbano=$total_urbano+$cantidad_urbano;

                    $resultados_urbano[] = [
                        'rango' => "De {$inicio} a {$fin}",
                        'cantidad' => $cantidad_urbano
                    ];

                    $tipo_busqueda="T";
                }


                $i = $desde;
                foreach ($resultados_urbano as $index => $urbano) {
                    $inicio = $i;
                    $fin = $i + $rango;

                    $cantidad_rural = DB::connection('sqlsrv')
                    ->table('dbo.TITULOS_PREDIO')
                    ->whereDate('TitPr_FechaEmision', '2025-01-01')
                    ->whereBetween('TitPr_ValComerPredio', [$inicio, $fin])
                    ->distinct('Pre_CodigoCatastral')
                    ->count();

                    $total_rural=$total_rural+$cantidad_rural;

                    $resultados_rural[] = [
                        'rango' => "De {$inicio} a {$fin}",
                        'cantidad' => $cantidad_rural
                    ];

                    // Unimos los dos resultados en uno final
                    $resultados_final[] = [
                        'rango' => "De {$inicio} a {$fin}",
                        'cantidad_urbano' => $resultados_urbano[$index]['cantidad'],
                        'cantidad_rural' => $cantidad_rural,
                        'cantidad_total' => $resultados_urbano[$index]['cantidad'] + $cantidad_rural
                    ];

                    $i += $rango;
                }
            }

            return [
                "resultados_urbano"=>$resultados_urbano,
                "resultados_rural"=>$resultados_rural,
                "resultados_final"=>$resultados_final,
                "tipo_busqueda"=>$tipo_busqueda,
                "total_urbano"=>$total_urbano,
                "total_rural"=>$total_rural,
                "error"=>false
            ];


        } catch (\Throwable $e) {
            dd($e);
            // DB::connection('pgsql')->rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde','error'=>true]);
        }
    }

    public function pdfData(Request $request){

        try{
            set_time_limit(0);
            ini_set("memory_limit",-1);
            ini_set('max_execution_time', 0);

            $consultaInfo=$this->cargaData($request);

            if($consultaInfo['error']==true){
                return (['mensaje'=>'Ocurrió un error al consultar los datos,intentelo más tarde','error'=>true]);
            }

            $nombrePDF="reporte_predio_avaluo_rango.pdf";

            $pdf=\PDF::LoadView('reportes.reporte_predio_avaluo_rango',['datos_urbano'=>$consultaInfo['resultados_urbano'],'datos_rural'=>$consultaInfo['resultados_rural'],'resultados_final'=>$consultaInfo['resultados_final'] ]);
            $pdf->setPaper("A4", "portrait");
            $estadoarch = $pdf->stream();

            //lo guardamos en el disco temporal
            \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk('public')->exists($nombrePDF);
            if($exists_destino){
                return response()->json([
                    'error'=>false,
                    'pdf'=>$nombrePDF
                ]);
            }else{
                return response()->json([
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ]);
            }

        } catch (\Throwable $e) {
            dd($e);
            // DB::connection('pgsql')->rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde ','error'=>true]);
        }
    }

    public function descargarPdf($archivo){
        try{

            $exists_destino = \Storage::disk('public')->exists($archivo);

            if($exists_destino){
                return response()->download( storage_path('app/public/'.$archivo))->deleteFileAfterSend(true);
            }else{
                return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
            }

        } catch (\Throwable $th) {
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
        }
    }

    public function vistaReporteTransito(){
        //Gate::authorize('reporte_transito', TransitoImpuesto::class);
        return view('transito.consultaPagos');
    }

    public function consultarPagos(Request $request){
        try{
            $desde=$request->filtroDesde;
            $hasta=$request->filtroHasta;
            $tipo=$request->filtroTipo;

            $desde=$desde." 00:00:00.000";
            $hasta=$hasta." 23:59:59.000";

            $consultar= DB::connection('pgsql')->table('sgm_transito.impuestos as i')
            ->leftJoin('sgm_transito.vehiculo as v', 'v.id', '=', 'i.vehiculo_id')
            ->leftJoin('sgm_transito.clase_tipo_vehiculo as cv', 'cv.id', '=', 'v.tipo_clase_id')
            ->leftJoin('sgm_transito.clase_vehiculo as clv', 'clv.id', '=', 'v.clase_id')
            ->leftJoin('sgm_transito.marca_vehiculo as mv', 'mv.id', '=', 'v.marca_id')
            // ->leftJoin('sgm_transito.cat_ente as en', 'en.id', '=', 'i.cat_ente_id')
            ->leftJoin('sgm_app.cat_ente as en', 'en.id', '=', 'i.cat_ente_id')
            ->where(function($query) use($tipo) {
                if($tipo!="Todos"){
                    $query->where(' usuario',$tipo);
                }
            })
            ->whereBetween('i.created_at', [$desde, $hasta])
            ->where('i.estado',3)
            ->select('v.placa_cpn_ramv','v.chasis','v.avaluo','v.year','mv.descripcion as marca_veh','cv.descripcion as clase','en.nombres as nombre_propietario','en.apellidos as apellido_propietario','en.ci_ruc as identificacion_propietario' ,'i.year_impuesto','i.numero_titulo','i.total_pagar','i.usuario','i.created_at','i.id as identificador','clv.descripcion as clase_desc')
            ->get();

            foreach($consultar as $key=> $data){
                $usuarioRegistra=DB::connection('mysql')->table('users as u')
                ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                ->where('u.id',$data->usuario)
                ->select('p.nombres','p.apellidos','p.cedula')
                ->first();
                if(is_null($usuarioRegistra)){
                    $consultar[$key]->nombre_usuario=$data->usuario;
                }else{
                    $consultar[$key]->nombre_usuario=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;
                    $consultar[$key]->cedula_usuario=$usuarioRegistra->cedula;
                }

                $conceptos=DB::connection('pgsql')->table('sgm_transito.concepto_impuesto as ci')
                ->leftJoin('sgm_transito.conceptos as c', 'c.id', '=', 'ci.concepto_id')
                ->where('ci.impuesto_matriculacion_id',$data->identificador)
                ->select('c.concepto','ci.valor')
                ->get();
                $consultar[$key]->conceptos = $conceptos;


            }

            return ['data'=>$consultar,'error'=>false];

        } catch (\Throwable $th) {
            return ['mensaje'=>'Ocurrió un error '.$th,'error'=>true];
        }
    }

    public function ReporteTransito(Request $request){
        try{
            set_time_limit(0);
            ini_set("memory_limit",-1);
            ini_set('max_execution_time', 0);

            $consultaInfo=$this->consultarPagos($request);
            // dd($request->all());

            if($consultaInfo['error']==true){
                return (['mensaje'=>'Ocurrió un error al consultar los datos,intentelo más tarde','error'=>true]);
            }

            $nombrePDF="reporte_pago_transito.pdf";

            $pdf=\PDF::LoadView('reportes.reporte_pago_transito',['datos'=>$consultaInfo['data'],'desde'=>$request->filtroDesde,'hasta'=>$request->filtroHasta,'tipo'=>$request->filtroTipo ]);
            $pdf->setPaper("A4", "landscape");
            $estadoarch = $pdf->stream();

            //lo guardamos en el disco temporal
            \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk('public')->exists($nombrePDF);
            if($exists_destino){
                return response()->json([
                    'error'=>false,
                    'pdf'=>$nombrePDF
                ]);
            }else{
                return response()->json([
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ]);
            }

        } catch (\Throwable $th) {
            return ['mensaje'=>'Ocurrió un error '.$th,'error'=>true];
        }

    }

    public function testReporteTransito(){
        try{
            set_time_limit(0);
            ini_set("memory_limit",-1);
            ini_set('max_execution_time', 0);

            $desde="01-04-2025";
            $hasta="30-04-2025";;
            $tipo="Todos";

            $consultar= DB::connection('pgsql')->table('sgm_transito.impuestos as i')
            ->leftJoin('sgm_transito.vehiculo as v', 'v.id', '=', 'i.vehiculo_id')
            ->leftJoin('sgm_transito.clase_tipo_vehiculo as cv', 'cv.id', '=', 'v.tipo_clase_id')
            ->leftJoin('sgm_transito.marca_vehiculo as mv', 'mv.id', '=', 'v.marca_id')
            // ->leftJoin('sgm_transito.concepto_impuesto as ci', 'ci.impuesto_matriculacion_id', '=', 'i.id')
            ->leftJoin('sgm_app.cat_ente as en', 'en.id', '=', 'i.cat_ente_id')
            ->where(function($query) use($tipo) {
                if($tipo!="Todos"){
                    $query->where(' usuario',$tipo);
                }
            })
            ->whereBetween('i.created_at', [$desde, $hasta])
            ->where('i.estado',1)
            ->select('v.placa','v.chasis','v.avaluo','v.year','mv.descripcion as marca_veh','cv.descripcion as clase','en.nombres as nombre_propietario','en.apellidos as apellido_propietario','en.ci_ruc as identificacion_propietario' ,'i.year_impuesto','i.numero_titulo','i.total_pagar','i.usuario','i.created_at','i.id as identificador')
            ->get();

            foreach($consultar as $key=> $data){
                $usuarioRegistra=DB::connection('mysql')->table('users as u')
                ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                ->where('u.id',$data->usuario)
                ->select('p.nombres','p.apellidos','p.cedula')
                ->first();
                $consultar[$key]->nombre_usuario=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;
                $consultar[$key]->cedula_usuario=$usuarioRegistra->cedula;

                $conceptos=DB::connection('pgsql')->table('sgm_transito.concepto_impuesto as ci')
                ->leftJoin('sgm_transito.conceptos as c', 'c.id', '=', 'ci.concepto_id')
                ->where('ci.impuesto_matriculacion_id',$data->identificador)
                ->select('c.concepto','ci.valor')
                ->get();
                $consultar[$key]->conceptos = $conceptos;

            }

            // dd($consultar);
            $nombrePDF="reporte_pago_transito.pdf";

            $pdf=\PDF::LoadView('reportes.reporte_pago_transito',['datos'=>$consultar,'desde'=>$desde,'hasta'=>$hasta,'tipo'=>$tipo]);
            $pdf->setPaper("A4", "landscape");
            $estadoarch = $pdf->stream();

            return $pdf->stream("a.pdf");

            // //lo guardamos en el disco temporal
            // \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
            // $exists_destino = \Storage::disk('public')->exists($nombrePDF);
            // if($exists_destino){
            //     return response()->json([
            //         'error'=>false,
            //         'pdf'=>$nombrePDF
            //     ]);
            // }else{
            //     return response()->json([
            //         'error'=>true,
            //         'mensaje'=>'No se pudo crear el documento'
            //     ]);
            // }

        } catch (\Throwable $th) {
            return ['mensaje'=>'Ocurrió un error '.$th,'error'=>true];
        }

    }



}
