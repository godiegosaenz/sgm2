<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Permiso extends Model
{
    protected $connection = 'mysql';
    protected $table = 'permissions';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}