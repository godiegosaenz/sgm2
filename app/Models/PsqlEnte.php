<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlEnte extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_app.cat_ente';

    /**
     * Validar si la información de la persona está completa según los campos requeridos.
     *
     * @param array $requiredFields
     * @return bool
     */
    public function informacionCompleta(array $requiredFields)
    {
        foreach ($requiredFields as $field) {
            if (empty($this->$field)) {
                return false;
            }
        }
        return true;
    }

}
