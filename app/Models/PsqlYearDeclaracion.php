<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class PsqlYearDeclaracion extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_patente.year_declaracion';

    public function patente(): HasMany
    {
        return $this->hasMany(PsqlPaPatente::class);
    }
}
