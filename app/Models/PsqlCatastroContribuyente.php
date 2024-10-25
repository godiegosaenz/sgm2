<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlCatastroContribuyente extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_patente.pa_catastro_contribuyente';

    protected $fillable = ['id',
                            'ruc',
                            'razon_social',
                            'estado_contribuyente_id',
                            'fecha_inicio_actividades',
                            'fecha_actualizacion_actividades',
                            'fecha_reinicio_actividades',
                            'fecha_suspension_definitiva',
                            'obligado_contabilidad',
                            'tipo_contribuyente',
                            'num_establecimiento',
                            'nombre_fantasia_comercial',
                            'estado_establecimiento',
                            'provincia_id',
                            'canton_id',
                            'parroquia_id',
                            'calle_principal',
                            'calle_secundaria',
                            'referencia_ubicacion',
                            'correo_1',
                            'local_propio',
                            'telefono',
                            'es_matriz',
                            'es_turismo',
                            'usuario_ingreso',
                            'created_at',
                            'updated_at',
                            'propietario',
                            'representante_legal',
                        ];

     // Relación muchos a muchos con actividades comerciales
     public function actividades()
     {
         return $this->belongsToMany(PsqlPaActividadesComerciales::class, 'sgm_patente.pa_actividad_contribuyente', 'Catastro_contribuyente_id', 'Actividad_comercial_id');
     }
}
