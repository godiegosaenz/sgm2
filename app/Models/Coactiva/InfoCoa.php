<?php

namespace App\Models\Coactiva;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class InfoCoa extends Model
{
    use HasFactory;

    protected $connection = 'pgsql';
    protected $table = 'sgm_coactiva.info_coact';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function data(){
        return $this->hasMany('App\Models\Coactiva\DataCoa', 'id_info_coact', 'id')->with('liquidacion');
    }

    public function notificacion(){
        return $this->belongsTo('App\Models\Coactiva\InfoNotifica', 'id_info_notifica', 'id');
    }

   
   
}
