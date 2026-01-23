<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class MedicacionHabitual extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.condicion_medicacion_habitual';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}