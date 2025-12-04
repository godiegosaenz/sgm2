<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\TituloRural;

class PredioRuralController extends Controller
{
    public function index()
    {
        return view('rural.seguridad_ciudadana');
    }

     public function datatable(Request $r)
    {
        if($r->ajax()){
            $prediosContribuyente=\DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
            ->select('Pre_CodigoCatastral','Titpr_RUC_CI','TitPr_NumTitulo')
            ->whereIn('pago.TitPr_Estado',['E','C'])
            ->distinct()
            ->get();


            foreach($prediosContribuyente as $key=>$data){
                $verificaRebaja=\DB::connection('sqlsrv')->table('REBAJA_VALOR as r')
                ->where('r.TitPrCarVe_NumTitulo', $data->TitPr_NumTitulo)
                ->where('RebVal_Valor','>',0)
                ->first();

                if(!is_null($verificaRebaja)){
                    $tieneMasDeUnPredio=\DB::connection('sqlsrv')->table('TITULOS_PREDIO as pago')
                    ->where('pago.TitPr_Estado','E') 
                    ->where('Titpr_RUC_CI',$data->Titpr_RUC_CI)       
                    ->get();
                    
                    if(sizeof($tieneMasDeUnPredio)==0){
                        $prediosContribuyente[$key]->aplica_exoneracion='Si';
                        $prediosContribuyente[$key]->tipo_exoneracion=$verificaRebaja->Reb_Codigo;
                    }else{
                        $prediosContribuyente[$key]->aplica_exoneracion='No';
                        $prediosContribuyente[$key]->tipo_exoneracion='';
                    }                   
                }else{
                    $prediosContribuyente[$key]->aplica_exoneracion='No';
                    $prediosContribuyente[$key]->tipo_exoneracion='';
                }
            }
            return Datatables($prediosContribuyente)
            ->addColumn('Pre_CodigoCatastral', function ($prediosContribuyente) {
                return $prediosContribuyente->Pre_CodigoCatastral;
            })
            ->addColumn('Titpr_RUC_CI', function ($prediosContribuyente) {
                return $prediosContribuyente->Titpr_RUC_CI;
            })
            ->addColumn('TitPr_NumTitulo', function ($prediosContribuyente) {
                return $prediosContribuyente->TitPr_NumTitulo;
            })
            ->addColumn('aplica_exoneracion', function ($prediosContribuyente) {
                return $prediosContribuyente->aplica_exoneracion;
            })
            ->addColumn('tipo_exoneracion', function ($prediosContribuyente) {
                return $prediosContribuyente->tipo_exoneracion;
            })
            // ->rawColumns(['action','contribuyente','vehiculo'])
            ->make(true);
        }
    }
}