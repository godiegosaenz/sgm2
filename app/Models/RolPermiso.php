<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class RolPermiso extends Model
{
    protected $connection = 'mysql';
    protected $table = 'role_has_permissions';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function permiso()
    {
        return $this->belongsTo('App\Models\Permiso', 'permission_id', 'id');
    }

}