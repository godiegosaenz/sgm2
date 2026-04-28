<?php

namespace App\Http\Controllers\transito;

use App\Http\Controllers\Controller;
use App\Models\TransitoClaseTipo;
use App\Models\TransitoConcepto;
use App\Models\TransitoEnte;
use App\Models\TransitoImpuesto;
use App\Models\TransitoImpuestoConcepto;
use App\Models\TransitoMarca;
use App\Models\TransitoTarifaAnual;
use App\Models\TransitoTipoVehiculo;
use App\Models\TransitoVehiculo;
use App\Models\TransitoYearImpuesto;
use App\Models\ClaseVehiculo;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use Exception;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Support\Facades\Auth;
use GuzzleHttp\Client;
use Illuminate\Support\Facades\Gate;
use App\BSrE_PDF_Signer_Cli;
use Illuminate\Support\Facades\Crypt;
use App\Models\PsqlEnte;
use Endroid\QrCode\Builder\Builder;
use Endroid\QrCode\Writer\PngWriter;


class EdicionTransitoImpuestoController extends Controller
{

    public function index(){
        $vehiculos = TransitoVehiculo::all();
        $conceptos = TransitoConcepto::where('anio',date('Y'))->orderby('orden','asc')->WHERE('estado','A')->get();
        $year = TransitoYearImpuesto::all();
        $marcas = TransitoMarca::where('estado','A')->get();
        $tipo_vehiculo = TransitoTipoVehiculo::where('estado','A')->get();
        $rangos = TransitoTarifaAnual::all();
        return view('transito.edicion',compact('vehiculos', 'conceptos','year','tipo_vehiculo','marcas','rangos'));
    }

    public function datatableEdicion(Request $r)
    {
        if($r->ajax()){
            $listaimpuesto = TransitoImpuesto::with('cliente')->orderBy('id','desc')
            ->whereIN('estado',[1,3])//generado
            ->where('created_at', '>=', now()->subDays(3))
            ->get();
            return Datatables($listaimpuesto)
            ->addColumn('cc_ruc', function ($listaimpuesto) {
                return $listaimpuesto->cliente->ci_ruc;
            })
            ->addColumn('contribuyente', function ($listaimpuesto) {
                return $listaimpuesto->cliente->nombres.' '.$listaimpuesto->cliente->apellidos;
            })
            ->addColumn('vehiculo', function ($listaimpuesto) {
                return $listaimpuesto->vehiculo->placa_cpn_ramv;
            })
            ->editColumn('created_at', function ($item) {
                // Formato: día-mes-año hora:minuto:segundo
                return \Carbon\Carbon::parse($item->created_at)->format('d-m-Y H:i:s');
            })
            
            ->addColumn('action', function ($listaimpuesto) {
                $disabled="";
                if($listaimpuesto->estado==1 || $listaimpuesto->estado==3){
                   
                    $btn2='<a class="btn btn-success btn-sm" onclick="cobrarTitulo(\''.$listaimpuesto->id.'\')">Visualizar</a>';
                    $disabled="disabled";

                  
                }else if($listaimpuesto->estado==3){
                    
                }
                return $btn2;;
            })

            ->rawColumns(['action','contribuyente','vehiculo'])
            ->make(true);
        }
    }

    public function editar($id){
        try{
            $impuesto = TransitoImpuesto::with('cliente','vehiculo','conceptos')
            ->wherein('estado',[1,3])->first();

            return["error"=>false, "resultado"=>$impuesto];

        } catch (Exception $e) {
            DB::rollback();
            return (['error' => true, 'mensaje'=>'Ocurrio un error, intentelo mas tarde']);
        }
    }
    

}