<?php

namespace App\Http\Controllers\transito;

use App\Http\Controllers\Controller;
use App\Models\TransitoVehiculo;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class TransitoVehiculoController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        //
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'placa_v'   => 'required|string|max:10',
            'chasis_v'  => 'required|string|max:50',
            'avaluo_v'  => 'required|numeric|min:0',
            'year_v'    => 'required|integer|min:1900|max:' . (date('Y') + 1),
            'marca_v'   => 'required',
            'tipo_v'    => 'required' // Validamos que exista en la tabla tipos
        ]);
        // Guardado mapeando los campos del formulario a los de la base de datos
        $vehiculo = new TransitoVehiculo();
        $vehiculo->placa = $request->placa_v;
        $vehiculo->chasis = $request->chasis_v;
        $vehiculo->avaluo = $request->avaluo_v;
        $vehiculo->year = $request->year_v;
        $vehiculo->username = Auth()->user()->name;
        $vehiculo->marca_id = $request->marca_v;
        $vehiculo->tipo_clase_id = $request->tipo_v; // Mapeo desde "tipo"
        $vehiculo->save();

        return response()->json(['success' => true, 'vehiculo' => $vehiculo]);
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        //
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

    public function getVehiculoPlaca(Request $r){
        $query = $r->input('query');
        $data = TransitoVehiculo::with('tipo_vehiculo','marca')->where('placa',$query)->first();
        if ($data) {
            // Devolver la información en formato JSON
            return response()->json($data, 200);
        } else {
            // Devolver un error 404 si no se encuentra nada
            return response()->json(['message' => 'No encontrado'], 404);
        }
    }
}
