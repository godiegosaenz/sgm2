<?php

namespace App\Models\Coactiva;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class CuotaConvenio extends Model
{
    use HasFactory;

    protected $connection = 'pgsql';
    protected $table = 'sgm_coactiva.cuota_convenio';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function convenio(){
        return $this->belongsTo('App\Models\Coactiva\Convenio', 'id_convenio', 'id');
    }
    
}
