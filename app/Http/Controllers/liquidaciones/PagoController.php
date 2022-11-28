<?php

namespace App\Http\Controllers\liquidaciones;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\models\Categoria;
use App\models\Cita;
use App\models\Liquidation;
use App\models\LiquidationSequence;
use App\models\RubroLiquidation;
use Illuminate\Support\Facades\Validator;
use PDF;

class PagoController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index($id)
    {
        $Cita = Cita::find($id);
        $Categoria = Categoria::all();
        $Liquidation = new Liquidation();
        $contadorLiquidacion = Liquidation::where('cita_id',$id)->count();
        if(Liquidation::where('cita_id',$id)->count() == 1){
            $Liquidation = Liquidation::where('cita_id',$id)->first();
        }
        return view('liquidaciones.pago',compact('Categoria','Cita','Liquidation','contadorLiquidacion'));
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $r)
    {
        $messages = [
            'required' => 'El campo :attribute es requerido.',
            'unique' => 'El numero de cedula ingresado ya existe',
            'size' => 'El campo :attribute debe tener exactamente :size caracteres',
            'max' => 'El campo :attribute no debe exceder los :max caracteres',
        ];

        $reglas = [
            'inputValorCobrar' => 'bail|required',
            'cita_id' => 'bail|required',
            'categoria_id' => 'bail|required',
        ];

        $validator = Validator::make($r->all(),$reglas,$messages);

        if ($validator->fails()) {
            return redirect('/pago/'.$r->cita_id)
                        ->withErrors($validator)
                        ->withInput();
        }

        //obtener maxima secuencia;
        $secuenciamaxima = LiquidationSequence::max('sequence');
        $secuenciaLiquidacion = $secuenciamaxima + 1;
        //registramos la secuencia nueva
        $LiquidationSequence = new LiquidationSequence();
        $LiquidationSequence->sequence = $secuenciaLiquidacion;
        $LiquidationSequence->year = date('Y');
        $LiquidationSequence->type_liquidation_id = 1;
        $LiquidationSequence->save();

        $Liquidation = new Liquidation();
        $Liquidation->total_payment = $r->inputValorCobrar;
        $Liquidation->cita_id = $r->cita_id;
        $Liquidation->categoria_id = $r->categoria_id;
        $Liquidation->type_liquidation_id = 1;
        $Liquidation->year = '2022';
        $Liquidation->status = 1;
        $Liquidation->username = 'tecnologia.informacion@sanvicente.gob.ec';
        $Liquidation->voucher_number = $secuenciaLiquidacion;
        $Liquidation->save();

        $RubroLiquidation = new RubroLiquidation();
        $RubroLiquidation->rubro_id = 1;
        $RubroLiquidation->liquidation_id = $Liquidation->id;
        $RubroLiquidation->value = $Liquidation->total_payment;
        $RubroLiquidation->status = true;
        $RubroLiquidation->save();

        return redirect('pago/'.$r->cita_id)->with('guardado','Pago realizado con exito, descargue el comprobante');
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        //
    }

    public function recibo(Request $r, $id){
        $Cita = Cita::find($id);
        $Liquidation = Liquidation::where('cita_id',$id)->first();
        $data = [
            'title' => 'Rebibo',
            'date' => date('m/d/Y'),
            'Cita' => $Cita,
            'Liquidation' => $Liquidation
        ];


        $pdf = PDF::loadView('reportes.recibo', $data);

        return $pdf->download('recibo-'.$id.'.pdf');
    }
}
