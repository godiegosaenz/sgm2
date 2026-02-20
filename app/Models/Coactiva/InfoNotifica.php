<?php

namespace App\Models\Coactiva;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class InfoNotifica extends Model
{
    use HasFactory;

    protected $connection = 'pgsql';
    protected $table = 'sgm_coactiva.info_notifica';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function data(){
        return $this->hasMany('App\Models\Coactiva\DataNotifica', 'id_info_notifica', 'id')->with('liquidacion');
    }

    public function ente(){
        return $this->belongsTo('App\Models\PsqlEnte', 'id_persona', 'id');
    }

   
   
}
