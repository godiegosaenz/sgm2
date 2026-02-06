<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class ResultadosExamen extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.resultados_examenes';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}