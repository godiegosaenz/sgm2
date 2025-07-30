<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;

class JefeArea extends Model
{
    use HasFactory;

    protected $connection = 'mysql'; // Nombre de la conexión configurada
    protected $table = 'jefe_area';

    protected $primaryKey  = 'id_jefe_area';
    
    public function area()
    {
        return $this->belongsTo(Area::class,'id_area','id_area');
    }

    public function usuario()
    {
        return $this->belongsTo(User::class,'id_usuario','id')->with('persona');
    } 
}
