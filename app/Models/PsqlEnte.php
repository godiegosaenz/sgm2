<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Carbon\Carbon;

class PsqlEnte extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_app.cat_ente';

    protected $fillable = ['id',
                            'ci_ruc',
                            'nombres',
                            'apellidos',
                            'es_persona',
                            'direccion',
                            'fecha_nacimiento',
                            'estado',
                            'user_mod',
                            'fecha_mod',
                            'nombre_comercial',
                            'razon_social',
                            'discapacidad',
                            'porcentaje',
                            'tipo_documento',
                            'lleva_contabilidad',
                        ];

    public $timestamps = false;

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

    public function contribuyentes()
    {
        return $this->hasMany(PsqlCatastroContribuyente::class);
    }

    public function correo()
    {
        return $this->hasMany(PsqlEnteCorreo::class,'ente','id');
    }

    public function telefono()
    {
        return $this->hasMany(PsqlEnteTelefono::class,'ente','id');
    }

    public function getFechaNacimientoFormateadaAttribute()
    {
        return Carbon::parse($this->fecha_nacimiento)->format('Y-m-d');
    }

}
