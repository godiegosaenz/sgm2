<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlParroquia extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_app.cat_parroquia';
    public $timestamps = false;

    public function contribuyentes()
    {
        return $this->hasMany(PsqlCatastroContribuyente::class);
    }
}
