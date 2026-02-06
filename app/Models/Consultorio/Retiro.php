<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Retiro extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.retiro';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}