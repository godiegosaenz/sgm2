<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Diagnostico extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.cie10';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}