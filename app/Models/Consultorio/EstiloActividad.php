<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class EstiloActividad extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.estilo_vida_actividad_fisica';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}