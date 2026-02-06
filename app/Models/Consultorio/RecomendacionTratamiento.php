<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class RecomendacionTratamiento extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.recomendacion_tratamiento';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}