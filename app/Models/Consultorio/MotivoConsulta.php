<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class MotivoConsulta extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.seccion_motivo_consulta';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}