<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class DiagnosticoEmpleado extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.diagnostico_empleado';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function cie10()
    {
        return $this->belongsTo('App\Models\Consultorio\Diagnostico', 'id_cie10', 'id');
    }

}