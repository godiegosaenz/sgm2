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
}