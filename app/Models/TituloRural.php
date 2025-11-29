<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;

class TituloRural extends Model
{
    use HasFactory;

    protected $connection = 'sqlsrv'; // Nombre de la conexión configurada
    protected $table = 'TITULOS_PREDIO';
    //protected $primaryKey  = 'id';
    public $timestamps = false;

    
}
