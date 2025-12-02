<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\TituloRural;

class ContribuyenteUrbanoController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        if(!Auth()->user()->hasPermissionTo('Info Contribuyente'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        $num_predio=0;
        return view('contribuyente_urbano.index',[
            "num_predio"=>$num_predio
        ]);
    }
    
    public function informacion(Request $request){
        try{
            $tipo=$request->tipo;
            $dataContribuyente=[];
            if($tipo==1){
                $matricula=$request->num_predio;
                $dataContribuyente=\DB::connection('pgsql')->table('sgm_app.cat_predio as p')
                ->leftJoin('sgm_app.cat_predio_propietario as pp','pp.predio','p.id')
                ->leftJoin('sgm_app.cat_ente as e','e.id','pp.ente')
                ->select('e.id','ci_ruc','nombres','apellidos','direccion')
                ->where('p.num_predio',$matricula)
                ->where('pp.estado','A')
                ->get();

            }else if($tipo==2){
                $clave=$request->clave;
                $dataContribuyente=\DB::connection('pgsql')->table('sgm_app.cat_predio as p')
                ->leftJoin('sgm_app.cat_predio_propietario as pp','pp.predio','p.id')
                ->leftJoin('sgm_app.cat_ente as e','e.id','pp.ente')
                ->select('e.id','ci_ruc','nombres','apellidos','direccion')
                ->where('p.clave_cat',$clave)
                ->where('pp.estado','A')
                ->get();
            }else {
                $id_contr=$request->cmb_nombres;
                $dataContribuyente=\DB::connection('pgsql')->table('sgm_app.cat_ente as e')
                ->select('id','ci_ruc','nombres','apellidos','direccion')
                ->where('e.id',$id_contr)
                ->get();
            }
            if(sizeof($dataContribuyente)>0){
                foreach($dataContribuyente as $key=> $item){
                    $telefonos = \DB::connection('pgsql')->table('sgm_app.ente_telefono')
                    ->select('telefono')
                    ->where('ente', $item->id)
                    ->pluck('telefono');
                    
                    $dataContribuyente[$key]->telf=$telefonos;

                    $correos = \DB::connection('pgsql')->table('sgm_app.ente_correo')
                    ->select('email')
                    ->where('ente', $item->id)
                    ->pluck('email');
                    
                    $dataContribuyente[$key]->email=$correos;
                }
            }
            
            return (['resultado'=>$dataContribuyente,'false'=>true]); 

        } catch (\Throwable $th) {
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th->getLine(),'error'=>true]); 
        } 

    }
    public function cargaPredios($id){
        try{
            $dataContribuyente=\DB::connection('pgsql')->table('sgm_app.cat_predio as p')
            ->leftJoin('sgm_app.cat_predio_propietario as pp','pp.predio','p.id')
            ->select('p.clave_cat','p.calle','p.nombre_edificio','calle_s','p.num_predio')
            ->where('pp.ente',$id)
            ->where('pp.estado','A')
            ->get();
            return (['resultado'=>$dataContribuyente,'false'=>true]); 
        } catch (\Throwable $th) {
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th->getLine(),'error'=>true]); 
        } 
    }

    public function buscaPrediosContribuyente(){
        try{
            $prediosContribuyente=\DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->select('Pre_CodigoCatastral','Titpr_RUC_CI','TitPr_NumTitulo')
            ->where('pago.TitPr_Estado','E')
            ->distinct()
            ->get();


            foreach($prediosContribuyente as $key=>$data){
                $verificaRebaja=\DB::connection('sqlsrv')->table('REBAJA_VALOR as r')
                ->where('r.TitPrCarVe_NumTitulo', $data->TitPr_NumTitulo)
                // ->where('RebVal_Valor','>',0)
                ->first();

                if(!is_null($verificaRebaja)){
                    $tieneMasDeUnPredio=\DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
                    ->where('pago.TitPr_Estado','E') 
                    ->where('Titpr_RUC_CI',$data->Titpr_RUC_CI)       
                    ->get();
                    
                    if(sizeof($tieneMasDeUnPredio)==0){
                        $prediosContribuyente[$key]->aplica_exoneracion='S';
                        $prediosContribuyente[$key]->tipo_exoneracion=$verificaRebaja->Reb_Codigo;
                    }else{
                        $prediosContribuyente[$key]->aplica_exoneracion='N';
                        $prediosContribuyente[$key]->tipo_exoneracion='';
                    }                   
                }else{
                    $prediosContribuyente[$key]->aplica_exoneracion='N';
                    $prediosContribuyente[$key]->tipo_exoneracion='';
                }
            }

            //dd($prediosContribuyente);
            $valor_seguridad=\DB::connection('sqlsrv')->table('ordenanzas')
            ->where('codigo','SEGURIDAD')
            ->where('estado','A')
            ->first(); 
            $valor_seg=$valor_seguridad->valor;

            foreach($prediosContribuyente as $data){
                if($data->TitPr_NumTitulo=='2025-000001-PR'){
                    $actualizaRural=TituloRural::where('TitPr_NumTitulo',$data->TitPr_NumTitulo)
                    ->where('TitPr_Estado','E')->first();
                   
                    $sumaValorEmitido=$actualizaRural->TitPr_ValorEmitido + $valor_seg;
                                        
                    $update=TituloRural::where('TitPr_NumTitulo',$data->TitPr_NumTitulo)
                    ->where('TitPr_Estado','E')->update(["TitPr_Valor1"=>number_format($valor_seg,2), "TitPr_ValorEmitido"=>number_format($sumaValorEmitido,2)]);
                }
            }
           

            return (['resultado'=>$prediosContribuyente,'false'=>true]); 

        } catch (\Throwable $th) {
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th->getMessage(),'error'=>true]); 
        } 
    }


}