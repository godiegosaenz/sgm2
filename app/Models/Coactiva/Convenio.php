<?php

namespace App\Models\Coactiva;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Convenio extends Model
{
    use HasFactory;

    protected $connection = 'pgsql';
    protected $table = 'sgm_coactiva.convenio';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function notificacion(){
        return $this->belongsTo('App\Models\Coactiva\InfoNotifica', 'id_info_notifica', 'id')->with('ente');
    }

    public function coactiva(){
        return $this->belongsTo('App\Models\Coactiva\InfoCoa', 'id_info_coact', 'id')->with('notificacion');
    }

}
