<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlLiquidacion extends Model
{
    use HasFactory;

    protected $connection = 'pgsql';
    protected $table = 'sgm_financiero.ren_liquidacion';
    protected $primaryKey  = 'id';
    public $timestamps = false;

}
