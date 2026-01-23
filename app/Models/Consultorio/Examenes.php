<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Examenes extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.examenes_femino_masc';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}