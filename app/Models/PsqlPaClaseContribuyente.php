<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlPaClaseContribuyente extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_patente.pa_clase_contribuyente';

    public function contribuyentes()
    {
        return $this->hasMany(PsqlCatastroContribuyente::class);
    }
}
