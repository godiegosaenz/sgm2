<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class ActividadesExtras extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.activida_extras';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}