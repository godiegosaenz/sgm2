<?php

namespace App\Http\Controllers\TitulosPredial;

use App\Http\Controllers\Controller;
use App\Models\TransitoVehiculo;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class CobroTituloRuralController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        if(!Auth()->user()->hasPermissionTo('Cobro Titulo Rural'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('cobroTituloRural.index');
    }
}