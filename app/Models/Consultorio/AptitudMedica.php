<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class AptitudMedica extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.aptitudes_medicas_trabajo';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}