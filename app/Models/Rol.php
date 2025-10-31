<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Rol extends Model
{
    protected $connection = 'mysql';
    protected $table = 'roles';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function permiso_rol()
    {
        return $this->hasMany('App\Models\RolPermiso', 'role_id', 'id')
        ->with('permiso');
    }

}