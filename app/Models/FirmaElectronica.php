<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;

class FirmaElectronica extends Model
{
    use HasFactory;

    protected $connection = 'mysql'; // Nombre de la conexión configurada
    protected $table = 'archivo_p12';

    protected $primaryKey  = 'id';
    
   protected $fillable = [
        'archivo',
        'f_emision',
        'f_expiracion',
        'propietario',
        'estado',
        'id_usuario',
    ];

    // public $timestamps = false;
    
    public function usuario()
    {
        return $this->belongsTo(User::class,'id_usuario','id')->with('persona');
    } 
}
