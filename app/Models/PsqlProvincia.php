<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlProvincia extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_app.cat_provincia';

    public function contribuyentes()
    {
        return $this->hasMany(PsqlCatastroContribuyente::class);
    }

}
