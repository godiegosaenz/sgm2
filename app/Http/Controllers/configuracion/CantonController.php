<?php

namespace App\Http\Controllers\configuracion;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use App\models\Provincia;
use App\models\Canton;

class CantonController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function obtener(Request $r){
        $cantones = Canton::where('id_provincia',$r->idprovincia)->get();
        $html = '<option value="" id="optionSelectCanton">Seleccione canton</option>';
        foreach ($cantones as $key => $value) {
            $html .= '<option value="'.$value->id.'">'.$value->nombre.'</option>';
        }
        //echo json_encode($cantones);
        return $html;
    }
}
