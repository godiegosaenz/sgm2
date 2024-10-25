<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PsqlPaActividadesComerciales extends Model
{
    use HasFactory;
    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_patente.pa_ctlg_actividades_comerciales';

    protected $fillable = ['id',
                            'ciiu',
                            'descripcion',
                            'estado',
                            'nivel',
                            'fecha_ingreso',
                            'usuario_ingreso',
                            'valor',
                        ];

    // Relación muchos a muchos con contribuyentes
    public function contribuyentes()
    {
        return $this->belongsToMany(PsqlCatastroContribuyente::class, 'sgm_patente.pa_actividad_contribuyente', 'Actividad_comercial_id', 'Catastro_contribuyente_id');
    }
}
