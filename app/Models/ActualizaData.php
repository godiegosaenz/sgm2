<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ActualizaData extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_app.actualizacion_datos';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    
}
?>