<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use App\Models\PsqlCatastroContribuyente;

class PsqlPaPatente extends Model
{
    use HasFactory;

    protected $connection = 'pgsql'; // Nombre de la conexión configurada
    protected $table = 'sgm_patente.pa_patente';

    protected $fillable = [
        'fecha_declaracion',
        'registro_numero',
        'calificacion_artesanal',
        'Contribuyente_id',
        'year_declaracion',
        'lleva_contabilidad',
        'act_caja_banco',
        'act_ctas_cobrar',
        'act_inv_mercaderia',
        'act_vehiculo_maquinaria',
        'act_equipos_oficinas',
        'act_edificios_locales',
        'act_terrenos',
        'act_total_activos',
        'pas_ctas_dctos_pagar',
        'pas_obligaciones_financieras',
        'pas_otras_ctas_pagar',
        'pas_otros_pasivos',
        'pas_total_pasivos',
        'patrimonio',
        'formulario_sri_num',
        'fecha_declaracion_sri',
        'original_sustitutiva',
        'porc_ing_perc_sv',
        'estado',
    ];

    public function contribuyente(): BelongsTo
    {
        return $this->belongsTo(PsqlCatastroContribuyente::class,'Contribuyente_id','id');
    }

    public function year(): BelongsTo
    {
        return $this->belongsTo(PsqlYearDeclaracion::class,'year_declaracion','id');
    }

}
