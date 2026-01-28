<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class AntecedentesEmpleo extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.antecedentes_empleos';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}