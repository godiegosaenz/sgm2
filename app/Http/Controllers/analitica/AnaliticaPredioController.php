<?php

namespace App\Http\Controllers\analitica;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlEnte;
use Illuminate\Support\Facades\DB;
use Endroid\QrCode\QrCode;
use Endroid\QrCode\Writer\PngWriter;

class AnaliticaPredioController extends Controller
{
    
    public function predios(){
        return view("analitica.AnaliticaPredioExonerado");
    }

     public function cargaData(Request $request){
       
        try{
           
            $resultados_urbano = [];
            $resultados_rural = [];
            $resultados_final= [];
            $tipo_busqueda="";
            $total_urbano=0;
            $total_rural=0;

            if(isset($request->Interno)){
                $tipo_busqueda="U";
                $resultados_urbano = DB::connection('pgsql')->table('sgm_app.cat_predio as p')
                // ->leftJoin('sgm_app.cat_predio_propietario as pp', 'pp.predio', '=', 'p.id')
                //  ->leftJoin('sgm_app.cat_ente as ce', 'ce.id', '=', 'pp.ente')
                ->leftJoin('sgm_financiero.fn_solicitud_exoneracion as se', 'se.predio', '=', 'p.id')
                ->leftJoin('sgm_financiero.fn_exoneracion_tipo as t', 't.id', '=', 'se.exoneracion_tipo')
                ->where('se.estado', 1)
                ->where('p.estado', 'A')
                ->where('p.esta_exonerado', true)
                ->where('anio_fin', '>=', date('Y'))
                ->whereNotIn('t.descripcion',['BAJAS DE TITULOS'])
                // ->where('pp.estado','A')
                ->select('t.descripcion', 'p.id','p.clave_cat')
                ->get();

                
            }else{
                $tipo_busqueda="U";

            //     $predio= DB::connection('pgsql')->table('sgm_app.cat_predio as p')
            //     // ->distinct('p.id')
            //     ->where('estado','A')
            //     ->distinct('clave_cat')
            //    ->get();
            //     dd($predio);

                $liquidacionActual= DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as r')
                ->where('r.anio',date('Y'))
                ->where('estado_liquidacion',1)
                ->distinct('r.predio')
                ->pluck('r.predio') // Asegúrate de que este es el campo correcto
                ->toArray();
                // dd($liquidacionActual);

                $resultados_urbano = DB::connection('pgsql')->table('sgm_app.cat_predio as p')
                ->leftJoin('sgm_financiero.fn_solicitud_exoneracion as se', 'se.predio', '=', 'p.id')
                ->leftJoin('sgm_financiero.fn_exoneracion_tipo as t', 't.id', '=', 'se.exoneracion_tipo')
                ->where('se.estado', 1)
                ->where('p.estado', 'A')
                ->where('p.esta_exonerado', true)
                ->where('anio_fin', '>=', date('Y'))
                ->whereNotIn('t.descripcion',['BAJAS DE TITULOS'])
                ->whereIn('p.id',$liquidacionActual)
                ->select('t.descripcion as rango', DB::raw('count(*) as cantidad'))
                ->groupBy('t.descripcion')
                ->get();

                // dd($resultados_urbano);


            }

            // foreach($resultados_urbano as $key=> $data){
            //     dd($data);
            //     $liquidacionActual= DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as r')
            //     ->where('r.predio',$data->id)
            //     ->where('r.anio',date('Y'))
            //     ->first();

            //     if(!is_null($liquidacionActual)){
            //         $resultados_urbano->permitir="Si";
            //     }
            // }

            // dd($resultados_urbano);
            
         

            return [
                "resultados_urbano"=>$resultados_urbano, 
                "resultados_rural"=>$resultados_rural, 
                "resultados_final"=>$resultados_final, 
                "tipo_busqueda"=>$tipo_busqueda,
                // "total_urbano"=>sizeof($resultados_urbano),
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

            // dd($consultaInfo);
            foreach($consultaInfo['resultados_urbano'] as $key=> $data){
                $propietario=DB::connection('pgsql')->table('sgm_app.cat_predio_propietario as pp')
                ->leftJoin('sgm_app.cat_ente as ce', 'ce.id', '=', 'pp.ente')
                ->where('pp.predio',$data->id)
                ->where('pp.estado','A')
                ->select('ce.ci_ruc','ce.nombres','ce.apellidos')
                ->first();
                
                $consultaInfo['resultados_urbano'][$key]->ci_propietario=$propietario->ci_ruc;
                $consultaInfo['resultados_urbano'][$key]->propietario=$propietario->nombres." ".$propietario->apellidos;
 
            }

            $listado_final=[];
            foreach ($consultaInfo['resultados_urbano'] as $key => $item){                
                if(!isset($listado_final[$item->descripcion])) {
                    $listado_final[$item->descripcion]=array($item);
            
                }else{
                    array_push($listado_final[$item->descripcion], $item);
                }
            }

            // dd($listado_final);

            $nombrePDF="reporte_predio_exonerado.pdf";

            $pdf=\PDF::LoadView('reportes.reporte_predio_exonerado',['datos_urbano'=>$consultaInfo['resultados_urbano'],'listado_final_urbano'=>$listado_final,'datos_rural'=>$consultaInfo['resultados_rural'],'resultados_final'=>$consultaInfo['resultados_final'] ]);
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