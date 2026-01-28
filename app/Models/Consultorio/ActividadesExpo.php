<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class ActividadesExpo extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.actividad_expo';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}