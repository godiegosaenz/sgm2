<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\PsqlEnte;
use Illuminate\Support\Facades\DB;

class HomeController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $totalContribuyentes = PsqlEnte::count();
        $contribuyentesConCedula = PsqlEnte::where('ci_ruc', 'like', '30000000000%')->count();
        $contribuyentesConCorreo = PsqlEnte::has('correo')->count();
        $contribuyentesConTelefono = PsqlEnte::has('telefono')->count();
        return view('home',compact('totalContribuyentes','contribuyentesConCedula','contribuyentesConCorreo','contribuyentesConTelefono'));
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
        //
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

    public function getData()
    {
        $contribuyentesConCedula = PsqlEnte::where('ci_ruc', 'not like', '30000000000%')->count();
        $contribuyentesSinCedula = PsqlEnte::where('ci_ruc', 'like', '30000000000%')->count();
        $contribuyentesConCorreo = PsqlEnte::has('correo')->count();
        $contribuyentesSinCorreo = PsqlEnte::doesntHave('correo')->count();
        $contribuyentesConTelefono = PsqlEnte::has('telefono')->count();
        $contribuyentesSinTelefono = PsqlEnte::doesntHave('telefono')->count();

        return response()->json([
            'conCedula' => $contribuyentesConCedula,
            'sinCedula' => $contribuyentesSinCedula,
            'conCorreo' => $contribuyentesConCorreo,
            'sinCorreo' => $contribuyentesSinCorreo,
            'conTelefono' => $contribuyentesConTelefono,
            'sinTelefono' => $contribuyentesSinTelefono,
        ]);
    }

    public function getContribuyentesData()
    {
        $currentYear = date('Y'); // Obtén el año actual

        $newContribuyentes = PsqlEnte::select(DB::raw("to_char(fecha_cre, 'YYYY-MM') as month"), DB::raw('count(*) as total'))
            ->whereYear('fecha_cre', $currentYear) // Filtra por el año actual
            ->groupBy('month')
            ->orderBy('month')
            ->get();

        // Obtener contribuyentes actualizados
        $updatedContribuyentes = PsqlEnte::select(DB::raw("to_char(fecha_mod, 'YYYY-MM') as month"), DB::raw('count(*) as total'))
            ->whereNotNull('fecha_mod')
            ->whereYear('fecha_mod', $currentYear) // Filtra por el año actual
            ->groupBy('month')
            ->orderBy('month')
            ->get();

        return response()->json([
            'new_contribuyentes' => $newContribuyentes,
            'updated_contribuyentes' => $updatedContribuyentes,
        ]);
    }

    public function obtenerDatosDistribucion()
    {
        $tipoContribuyentes = PsqlEnte::select('es_persona', DB::raw('count(*) as total'))
            ->groupBy('es_persona')
            ->get();

        // Preparar los datos para el gráfico
        return response()->json([
            'natural' => $tipoContribuyentes->where('es_persona', true)->first()->total ?? 0,
            'juridica' => $tipoContribuyentes->where('es_persona', false)->first()->total ?? 0,
        ]);
    }

    public function obtenerDatosDiscapacidad()
    {
        $clientesTerceraEdad = PsqlEnte::where('fecha_nacimiento', '<=', now()->subYears(65))->count();

        $clientesConDiscapacidad = PsqlEnte::whereIn('discapacidad', [201, 202])->count();

        $clientesLlevanContabilidad = PsqlEnte::where('lleva_contabilidad', true)->count();
        $clientesNoLlevanContabilidad = 0;

        return response()->json([
            'terceraEdad' => $clientesTerceraEdad,
            'conDiscapacidad' => $clientesConDiscapacidad,
            'llevanContabilidad' => $clientesLlevanContabilidad,
            'noLlevanContabilidad' => $clientesNoLlevanContabilidad,
        ]);
    }
}
