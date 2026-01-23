<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class ExamenFisicoRegional extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.examen_fisico_regional';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}