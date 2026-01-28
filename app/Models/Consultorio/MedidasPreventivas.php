<?php

namespace App\Models\Consultorio;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class MedidasPreventivas extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_consultorio.medidas_preventivas';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}