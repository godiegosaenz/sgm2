<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\PsqlPredio;
use DB;
class PrediosContribuyenteUrb extends Controller
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
            ->get();

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
            ->rawColumns(['contribuyente']) // importante para <br>
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

            return Datatables($obtener)
            ->addColumn('clave', function ($obtener) {
                return $obtener->Pre_CodigoCatastral;
            })
           ->addColumn('contribuyente', function ($obtener) {

                return $obtener->nombre;
            })
            // ->rawColumns(['contribuyente']) // importante para <br>
            ->make(true);
        }
    }
}