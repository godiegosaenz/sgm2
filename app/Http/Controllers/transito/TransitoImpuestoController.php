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
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use Exception;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Support\Facades\Auth;

class TransitoImpuestoController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('transito.impuestos_index');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
{
    $entes = TransitoEnte::all();
    $vehiculos = TransitoVehiculo::all();
    $conceptos = TransitoConcepto::all();
    $year = TransitoYearImpuesto::all();
    $marcas = TransitoMarca::all();
    $tipo_vehiculo = TransitoTipoVehiculo::all();
    return view('transito.impuestos', compact('entes', 'vehiculos', 'conceptos','year','tipo_vehiculo','marcas'));
}

public function store(Request $request)
{
    $validator = Validator::make($request->all(), [
        'conceptos' => 'required|array|min:1',
        'conceptos.*.id' => 'required',
        'conceptos.*.valor' => 'required|numeric|min:0',
        'vehiculo_id_2' => 'required',
        'cliente_id_2' => 'required',
        'year_declaracion' => 'required'
    ]);

    if ($validator->fails()) {
        return response()->json([
            'errors' => $validator->errors()
        ], 422);
    }

    $TransitoImpuesto = new TransitoImpuesto();

    $TransitoImpuesto->year_impuesto = $request->year_declaracion;
    $TransitoImpuesto->cat_ente_id = $request->cliente_id_2;
    $TransitoImpuesto->vehiculo_id = $request->vehiculo_id_2;
    $TransitoImpuesto->estado = 1; //estado 1 = pagado
    $TransitoImpuesto->usuario = Auth()->user()->name; //estado 1 = pagado
    $TransitoImpuesto->save();

    $total = 0;
    foreach ($request->conceptos as $concepto) {
        TransitoImpuestoConcepto::create([
            'concepto_id' => $concepto['id'],
            'impuesto_matriculacion_id' => $TransitoImpuesto->id,
            'valor' => $concepto['valor'],
        ]);
        $total += $concepto['valor'];
    }

    // Ahora actualizas el total
    $TransitoImpuesto->numero_titulo = 'TR-'.str_pad($TransitoImpuesto->id, 5, '0', STR_PAD_LEFT).'-'.$TransitoImpuesto->year_impuesto;
    $TransitoImpuesto->total_pagar = $total;
    $TransitoImpuesto->save();

    return response()->json(['success' => true, 'id' => $TransitoImpuesto->id]);
}

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        $TransitoImpuesto = TransitoImpuesto::with('vehiculo','cliente')->find($id);
        $vehiculo =  $TransitoImpuesto->vehiculo;
        $cliente = $TransitoImpuesto->cliente;
        $transitoimpuestoconcepto = $TransitoImpuesto->conceptos;
        return view('transito.impuestos_show', compact('TransitoImpuesto','transitoimpuestoconcepto','vehiculo','cliente'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }

    public function calcular(Request $request){
        $conceptos = $request->input('conceptos', []);
        $vehiculo = TransitoVehiculo::where('id',$request->input('vehiculo_id'))->first();
        $DatosTasaAdministrativa = TransitoConcepto::find(5);
        $DatosStickerVehicular = TransitoConcepto::find(4);
        $DatosDuplicadoEspecie = TransitoConcepto::find(2);
        $tasaAdministrativa = $DatosTasaAdministrativa->valor;
        $tasaStickerVehicular = $DatosStickerVehicular->valor;
        $tasaDuplicadoEspecie = $DatosDuplicadoEspecie->valor;

        $tarifa = null;
        $valortipoclase = null;
        if ($vehiculo) {
            $avaluo = $vehiculo->avaluo;

            $tarifa = TransitoTarifaAnual::where('desde', '<=', $avaluo)
                ->where(function ($query) use ($avaluo) {
                    $query->where('hasta', '>=', $avaluo)
                          ->orWhereNull('hasta'); // Para el caso de "En adelante"
                })
                ->first();

            $clasetipo = TransitoClaseTipo::where('id', $vehiculo->tipo_clase_id)->first();
            $valortipoclase = $clasetipo->valor;
        }
        $tarifaAnual = $tarifa->valor;

        // Simulación de cálculo: aquí puedes aplicar lógica propia
        $nuevosConceptos = collect($conceptos)->map(function ($item) use ($tarifaAnual,$valortipoclase,$tasaAdministrativa,$tasaStickerVehicular,$tasaDuplicadoEspecie) {
            // verificas cada concepto y colocas el valor
            $nuevoValor = 0;
            if($item['id'] == 1)
            {
                $nuevoValor = $tarifaAnual;
            }else if($item['id'] == 3)
            {
                $nuevoValor = $valortipoclase;
            }else if($item['id'] == 5)
            {
                $nuevoValor = $tasaAdministrativa;
            }
            else if($item['id'] == 4)
            {
                $nuevoValor = $tasaStickerVehicular;
            }
            else if($item['id'] == 2)
            {
                $nuevoValor = $tasaDuplicadoEspecie;
            }
            else
            {
                $nuevoValor = $item['valor'];
            }
            return [
                'id' => $item['id'],
                'nuevo_valor' => round($nuevoValor, 2)
            ];
        });

        $total = $nuevosConceptos->sum('nuevo_valor');

        return response()->json([
            'conceptos' => $nuevosConceptos,
            'total' => round($total, 2)
        ]);
    }

    public function reportetituloimpuesto(Request $r,string $id)
    {
        $dataArray = array();
        $TransitoImpuesto = TransitoImpuesto::with('cliente','vehiculo')->find($id);
        $vehiculo =  $TransitoImpuesto->vehiculo;
        $cliente = $TransitoImpuesto->cliente;
        $transitoimpuestoconcepto = $TransitoImpuesto->conceptos;

        $fecha_hoy=date('Y-m-d');
        setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
        $fecha_timestamp = strtotime($fecha_hoy);
        $fecha_formateada = strftime("%d de %B del %Y", $fecha_timestamp);

        $liquidacion['TransitoImpuesto'] = $TransitoImpuesto;
        $liquidacion['vehiculo'] = $vehiculo;
        $liquidacion['cliente'] = $cliente;
        $liquidacion['transitoimpuestoconcepto'] = $transitoimpuestoconcepto;
        array_push($dataArray, $liquidacion);

        $data = [
            'title' => 'Reporte de liquidacion',
            'date' => date('m/d/Y'),
            'datosTitulo' => $dataArray,
            'fecha_formateada'=>$fecha_formateada
        ];

        $pdf = PDF::loadView('transito.reporteTitulosTransito', $data);

        // return $pdf->stream("aa.pdf");

        return $pdf->download('reporte_titulo_impuesto'.$r->id.'.pdf');
    }

    public function datatable(Request $r)
    {
        if($r->ajax()){
            $listaimpuesto = TransitoImpuesto::with('cliente')->get();
            return Datatables($listaimpuesto)
            ->addColumn('cc_ruc', function ($listaimpuesto) {
                return $listaimpuesto->cliente->cc_ruc;
            })
            ->addColumn('contribuyente', function ($listaimpuesto) {
                return $listaimpuesto->cliente->nombres.' '.$listaimpuesto->cliente->apellidos;
            })
            ->addColumn('vehiculo', function ($listaimpuesto) {
                return $listaimpuesto->vehiculo->placa;
            })
            ->addColumn('action', function ($listaimpuesto) {
                return '<a href="' . route('show.transito', $listaimpuesto->id) . '" class="btn btn-primary btn-sm">Ver</a>';
            })
            ->rawColumns(['action','contribuyente','vehiculo'])
            ->make(true);
        }
    }
}
