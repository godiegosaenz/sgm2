<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Puesto extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.puesto_trabajo';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}