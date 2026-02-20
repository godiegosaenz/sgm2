<?php

namespace App\Models\Coactiva;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class DataNotifica extends Model
{
    use HasFactory;

    protected $connection = 'pgsql';
    protected $table = 'sgm_coactiva.data_notifica';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function liquidacion(){
        return $this->belongsTo('App\Models\PsqlLiquidacion', 'id_liquidacion', 'id');
    }

}
