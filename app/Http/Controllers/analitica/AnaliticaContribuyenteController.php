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

    public function predios(){

        $cantidad = DB::connection('pgsql')->table('sgm_app.cat_predio')
        // ->whereBetween('avaluo_municipal', [$inicio, $fin])
        ->where('estado','A')
        ->count();
        // dd($cantidad); 
        
        $cantidad = DB::connection('sqlsrv')
        ->table('dbo.TITULOS_PREDIO')
        ->whereDate('TitPr_FechaEmision', '2025-01-01')
        // ->select('Pre_CodigoCatastral')
        // ->distinct()
        ->count();
        // dd($cantidad);

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
            if($tipo=="Urbano"){
                for ($i = $desde; $i < $hasta; $i += $rango) {
                    $inicio = $i;
                    $fin = $i + $rango;
            
                    $cantidad = DB::connection('pgsql')->table('sgm_app.cat_predio')
                        ->whereBetween('avaluo_municipal', [$inicio, $fin])
                        ->where('estado','A')
                        ->count();
            
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
            
                    // $cantidad = DB::connection('sqlsrv')->table('dbo.TERRENO_RURAL_PREDIO')
                    //     ->whereBetween('TerRup_ValSubTotal', [$inicio, $fin])
                    //     ->count();
            
                    // $resultados_rural[] = [
                    //     'rango' => "De {$inicio} a {$fin}",
                    //     'cantidad' => $cantidad
                    // ];

                    $cantidad = DB::connection('sqlsrv')
                    ->table('dbo.TITULOS_PREDIO')
                    ->whereDate('TitPr_FechaEmision', '2025-01-01') 
                    ->whereBetween('TitPr_ValComerPredio', [$inicio, $fin])
                    ->distinct('Pre_CodigoCatastral')
                    ->count();

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
            
                    $resultados_urbano[] = [
                        'rango' => "De {$inicio} a {$fin}",
                        'cantidad' => $cantidad_urbano
                    ];

                    $tipo_busqueda="T";
                }

                // for ($i = $desde; $i < $hasta; $i += $rango) {
                //     $inicio = $i;
                //     $fin = $i + $rango;
            
                //     $cantidad_rural = DB::connection('sqlsrv')->table('dbo.TERRENO_RURAL_PREDIO')
                //         ->whereBetween('TerRup_ValSubTotal', [$inicio, $fin])
                //         ->count();
            
                //     $resultados_rural[] = [
                //         'rango' => "De {$inicio} a {$fin}",
                //         'cantidad' => $cantidad_rural
                //     ];

                //     $resultados_final[] = [
                //         'rango' => "De {$inicio} a {$fin}",
                //         'cantidad_urbano' => $cantidad_urbano,
                //         'cantidad_rural' => $cantidad_rural,
                //         'cantidad_total' => $cantidad_urbano + $cantidad_rural
                //     ];

                //     $tipo_busqueda="T";
                // }

                $i = $desde;
                foreach ($resultados_urbano as $index => $urbano) {
                    $inicio = $i;
                    $fin = $i + $rango;

                    // $cantidad_rural = DB::connection('sqlsrv')->table('dbo.TERRENO_RURAL_PREDIO')
                    //     ->whereBetween('TerRup_ValSubTotal', [$inicio, $fin])
                    //     ->count();
                    $cantidad_rural = DB::connection('sqlsrv')
                    ->table('dbo.TITULOS_PREDIO')
                    ->whereDate('TitPr_FechaEmision', '2025-01-01') 
                    ->whereBetween('TitPr_ValComerPredio', [$inicio, $fin])
                    ->distinct('Pre_CodigoCatastral')
                    ->count();

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

}
