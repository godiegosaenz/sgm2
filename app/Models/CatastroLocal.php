<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class CatastroLocal extends Model
{
    protected $connection = 'pgsql';
    protected $table = 'sgm_patente.pa_locales';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function provincia(){
        return $this->belongsTo('App\Models\PsqlProvincia', 'provincia_id', 'id');
    }

    public function canton(){
        return $this->belongsTo('App\Models\PsqlCanton', 'canton_id', 'id');
    }

    public function parroquia(){
        return $this->belongsTo('App\Models\PsqlParroquia', 'parroquia_id', 'id');
    }

}
?>