<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class CabeceraAtencion extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.cabecera_atencion';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}