<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class EmpleadoPaciente extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.empleado';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function permiso_rol()
    {
        return $this->hasMany('App\Models\RolPermiso', 'role_id', 'id')
        ->with('permiso');
    }

}