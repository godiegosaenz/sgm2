<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class CoactListadoTitulo extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_financiero.coact_listado_titulos';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}
