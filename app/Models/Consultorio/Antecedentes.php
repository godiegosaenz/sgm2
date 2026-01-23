<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Antecedentes extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.seccion_antecedentes';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}