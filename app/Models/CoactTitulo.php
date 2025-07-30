<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class CoactTitulo extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_financiero.coact_titulos';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}
