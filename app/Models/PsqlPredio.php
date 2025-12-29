<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlPredio extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_app.cat_predio';
    protected $primaryKey  = 'id';
    public $timestamps = false;

    public function propietario(){
        return $this->hasMany('App\Models\PsqlPropietarioPredio', 'predio', 'id')->with('contribuyente')->where('estado','A');
    }
}
