<?php

namespace App\Http\Controllers;
use \Log;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use DB;
use Carbon\Carbon;
use Illuminate\Support\Facades\Gate;

class RecaudacionesController extends Controller
{
    public function index(){
        return view('recaudaciones.index');
    }

    public function pagosRecaudados(Request $request){
        try{
            if($request->area=='Rural'){
                $valoresRecaudado=$this->valoresRurales($request);
                if($valoresRecaudado['error']==true){
                    return [
                        'error'=>true,
                        'mensaje'=>'Ocurrió un error'
                    ];
                }
                // dd($valoresRecaudado);
                return [
                    'error'=>false,
                    'data'=>$valoresRecaudado['resultado']
                ];
            }
        }catch (\Throwable $e) {
            Log::error('RecaudacionesController => pagosRecaudados => mensaje => '.$e->getMessage().' linea => '.$e->getLine());
            return [
                'error'=>true,
                'mensaje'=>'Ocurrió un error'
            ];
            
        }
    }

    public function valoresRurales(Request $request){
        try{
            $fecha_ini=$request->filtroDesde;
            $datosPagoPredialRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'PREDIAL RUSTICO' as tipo,
                '11.02.02.' AS codigo,                    
                CAST(SUM(pago.TItPr_IPU) AS DECIMAL(12,2)) AS total_pago_anio_actual,                
                'IMPUESTO PREDIAL RUSTICO' AS detalle_imp
            ")
            ->get();

           $datosPagoPredialRustAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
                
                CAST(SUM(cv.CarVe_IPU) AS DECIMAL(12,2)) AS total_pago_anteriores    
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoPredialRustAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoPredialRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            $datosPagoDescuentosRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'PREDIAL RUSTICO' as tipo,
                '11.02.02.' AS codigo,                    
               
                CAST(SUM(pago.TitPr_Descuento) AS DECIMAL(12,2)) AS total_pago_anio_actual,    
                
                'DESCUENTOS' AS detalle_imp
            ")
            ->get();

           $datosPagoDescuentoAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
               
                CAST(SUM(cv.CarVe_Descuento) AS DECIMAL(12,2)) AS total_pago_anteriores    
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoDescuentoAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoDescuentosRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };


            $datosPagoRecargosRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'PREDIAL RUSTICO' as tipo,
                '17.04.01.' AS codigo,                    
              
                CAST(SUM(pago.TitPr_Recargo) AS DECIMAL(12,2)) AS total_pago_anio_actual,    
                'RECARGO (MULTA TRIBUTARIA)' AS detalle_imp
            ")
            ->get();

           $datosPagoRecargosAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
                
                CAST(SUM(cv.Carve_Recargo) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoRecargosAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoRecargosRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            $datosPagoInteresesRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'PREDIAL RUSTICO' as tipo,
                '17.03.01.' AS codigo,                    
               
                CAST(SUM(pago.TitPr_Interes) AS DECIMAL(12,2)) AS total_pago_anio_actual,    
                'INTERES POR MORA TRIBUTARIA' AS detalle_imp
            ")
            ->get();

           $datosPagoInteresesAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
              
                CAST(SUM(cv.CarVe_Interes) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoInteresesAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoInteresesRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            $datosPagoSeguridadCiudadanaRust = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'OTROS INGRESOS TRIBUTARIO' as tipo,
                '17.03.01.' AS codigo,                    
               
                CAST(SUM(pago.TitPr_Valor1) AS DECIMAL(12,2)) AS total_pago_anio_actual,  
                'SEGURIDAD CIUDADANA' AS detalle_imp
            ")
            ->get();

           $datosPagoSeguridadCiudadanaAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
               
                CAST(SUM(cv.Carve_Valor1) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoSeguridadCiudadanaAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoSeguridadCiudadanaRust as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

            $datosPagoBomberoRural = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '' AS codigo,                    
              
                CAST(SUM(pago.TitPr_Bomberos) AS DECIMAL(12,2)) AS total_pago_anio_actual,
                'CUERPO DE BOMBEROS RURALES' AS detalle_imp
            ")
            ->get();

           $datosPagoBomberoRuralAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
               
                CAST(SUM(cv.CarVe_Bomberos) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoBomberoRuralAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoBomberoRural as $item) {
                $item->total_pago_anteriores = $total_ant;
            };

             $datosPagoServicioAdmRural = DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->whereRaw("CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?", [$fecha_ini])
            ->whereIn('pago.TitPr_Estado', ['C','Q'])
            ->selectRaw("
                '0' as orden,
                '3' as ordenlista,
                'FONDOS AJENOS' as tipo,
                '3.01.99.011' AS codigo,                    
              
                CAST(SUM(pago.TitPr_TasaAdministrativa) AS DECIMAL(12,2)) AS total_pago_anio_actual,
                'SERVICIOS ADMINISTRATIVOS RURALES' AS detalle_imp
            ")
            ->get();

           $datosPagoServicioAdmAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
               
                CAST(SUM(cv.CarVe_TasaAdministrativa) AS DECIMAL(12,2)) AS total_pago_anteriores
            ")
            ->get();

            // Suponiendo que ambos resultados son colecciones con un solo elemento cada uno:
            $total_ant = $datosPagoServicioAdmAnt[0]->total_pago_anteriores ?? 0;

            // Recorres el array principal y le agregas el campo adicional
            foreach ($datosPagoServicioAdmRural as $item) {
                $item->total_pago_anteriores = $total_ant;
            };
           

            
            $datosPagos = $datosPagoPredialRust
            ->merge($datosPagoDescuentosRust)
            ->merge($datosPagoServicioAdmRural)
            ->merge($datosPagoRecargosRust)
            ->merge($datosPagoInteresesRust)
            ->merge($datosPagoSeguridadCiudadanaRust)
            ->merge($datosPagoBomberoRural)
            ->sortBy('ordenlista') // Ordena por ordenlista
            ->sortBy('orden') 
            ->values(); // Restablece los índices del array

         
            // dd($datosPagos);
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');

            $fecha_timestamp = strtotime($fecha_ini);
    
            $fecha_formateada = strftime("%d de %B de %Y", $fecha_timestamp);
    
            $fecha_mayusculas = strtoupper($fecha_formateada);
            
            return[
                'error'=>false,
                'resultado'=>$datosPagos,
                'fecha_actual'=>$fecha_mayusculas,
                'anio'=>$fecha_ini
            ];
        }catch (\Throwable $e) {
            Log::error('RecaudacionesController => valoresRurales => mensaje => '.$e->getMessage().' linea => '.$e->getLine());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'
            ]);            
        }
    }

    public function generarReporte(Request $request){
        try{
            // dd("ss");
            $fecha_ini=$request->filtroDesde;
            $datosPagoPredialRust = DB::connection('sqlsrv')
            ->table('TITULOS_PREDIO as pago')
            ->whereRaw(
                "CAST(pago.TitPr_FechaRecaudacion AS DATE) = ?",
                [$fecha_ini]
            )
            ->whereIn('pago.TitPr_Estado', ['C', 'Q'])
            ->selectRaw("
                'ANIO_ACTUAL' AS tipo_anio,
                TitPr_NumTitulo as num_titulo,
                CAST(pago.TItPr_IPU AS DECIMAL(12,2)) AS total_pago_anio_actual_ipr,
                CAST(pago.TitPr_Descuento AS DECIMAL(12,2)) AS total_pago_anio_actual_desc,
                CAST(pago.TitPr_Recargo AS DECIMAL(12,2)) AS total_pago_anio_actual_rec, 
                CAST(pago.TitPr_Interes AS DECIMAL(12,2)) AS total_pago_anio_actual_int,
                CAST(pago.TitPr_Valor1 AS DECIMAL(12,2)) AS total_pago_anio_actual_seguridad,
                CAST(pago.TitPr_Bomberos AS DECIMAL(12,2)) AS total_pago_anio_actual_bombero,
                CAST(pago.TitPr_TasaAdministrativa AS DECIMAL(12,2)) AS total_pago_anio_actual_tasa,
                CAST(pago.TitPr_ValorTCobrado AS DECIMAL(12,2)) AS total_cobrado
            ")
            ->get();

          
           $datosPagoPredialRustAnt = DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->whereRaw("CAST(cv.CarVe_FechaRecaudacion AS DATE) = ?", [$fecha_ini." 00:00:00.000" ])
            ->where('cv.CarVe_Estado', 'C')
            ->selectRaw("
                'ANIOS_ANTERIORES' AS tipo_anio,
                CarVe_NumTitulo as num_titulo,
                CAST(cv.CarVe_IPU AS DECIMAL(12,2)) AS total_pago_anio_actual_ipr,
                CAST(cv.CarVe_Descuento AS DECIMAL(12,2)) AS total_pago_anio_actual_desc,
                CAST(cv.Carve_Recargo AS DECIMAL(12,2)) AS total_pago_anio_actual_rec,  
                CAST(cv.CarVe_Interes AS DECIMAL(12,2)) AS total_pago_anio_actual_int,  
                CAST(cv.Carve_Valor1 AS DECIMAL(12,2)) AS total_pago_anio_actual_seguridad,
                CAST(cv.CarVe_Bomberos AS DECIMAL(12,2)) AS total_pago_anio_actual_bombero,
                CAST(cv.CarVe_TasaAdministrativa AS DECIMAL(12,2)) AS total_pago_anio_actual_tasa,
                CAST(cv.CarVe_ValorTCobrado AS DECIMAL(12,2)) AS total_cobrado
                
            ")
            ->get();
            $resultado = $datosPagoPredialRust
            ->merge($datosPagoPredialRustAnt)->values();

            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');

            $fecha_timestamp = strtotime($fecha_ini);
    
            $fecha_formateada = strftime("%d de %B de %Y", $fecha_timestamp);
    
            $fecha_mayusculas = strtoupper($fecha_formateada);

            return [
                'error'=>false,
                'data'=>$resultado,
                'fecha'=>$fecha_mayusculas
            ]; 
           
        }catch (\Throwable $e) {
            Log::error('RecaudacionesController => generarReporte => mensaje => '.$e->getMessage().' linea => '.$e->getLine());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'.$e->getMessage()
            ]);            
        }
    }
}