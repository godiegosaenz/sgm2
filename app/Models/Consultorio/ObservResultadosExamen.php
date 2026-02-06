<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class ObservResultadosExamen extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.observacion_resultado';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}