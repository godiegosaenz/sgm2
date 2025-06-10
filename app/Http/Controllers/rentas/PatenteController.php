<?php

namespace App\Http\Controllers\rentas;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlYearDeclaracion;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\Validator;
use App\Models\PsqlPaPatente;
use App\Models\PsqlPatenteActividadesCont;
use Datatables;
use Storage;
use DB;
use Carbon\Carbon;

class PatenteController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('rentas.patente');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $PsqlYearDeclaracion = PsqlYearDeclaracion::select('id','year_declaracion','year_ejercicio_fiscal')->get();
        return view('rentas.patenteCrear',compact('PsqlYearDeclaracion'));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store1(Request $r)
    {
        $attributes = [
            'catastro_id' => 'contribuyente',
            'actividades.*.id' => 'actividad comercial',
            'locales.*.id' => 'local comercial',
            'year_declaracion' => 'Año de declaracion',
            'fecha_declaracion' => 'Fecha declaracion',
            'lleva_contabilidad' => 'Lleva contabilidad',
            'act_caja_banco' => 'Caja de banco',
            'act_ctas_cobrar' => 'Cuentas por cobrar ',
            'act_inv_mercaderia' => 'Inventario de mercaderia',
            'act_vehiculo_maquinaria' => 'Vehiculo y maquinaria',
            'act_equipos_oficinas' => 'Equipos de oficina',
            'act_edificios_locales' => 'Edificio locales',
            'act_terrenos' => 'Terrenos',
            'act_total_activos' => 'Total activos',
            'pas_ctas_dctos_pagar' => 'Cuentas y documentos por pagar',
            'pas_obligaciones_financieras' => 'Obligaciones financieras',
            'pas_otras_ctas_pagar' => 'Otras cuentas por pagar',
            'pas_otros_pasivos' => 'Otros pasivos',
            'pas_total_pasivos' => 'Total de pasivos',
            'pas_ctas_dctos_pagar' => 'Cuentas documentos por pagar',
        ];
        $messages = [
            'required' => 'El campo :attribute es obligatorio.',
            'max' => 'El campo :attribute no puede exceder los :max caracteres.',
            'size' => 'El campo :attribute debe tener exactamente :size caracteres.',
            'email' => 'El campo :attribute debe ser un email válido.',
            'numeric' => 'El campo :attribute debe ser numérico.',
            'digits' => 'El campo :attribute debe tener :digits dígitos.',
            'regex' => 'El formato del campo :attribute no es válido.',
            'boolean' => 'El campo :attribute debe ser verdadero o falso.',
            'date' => 'El campo :attribute debe ser una fecha válida.',
            'array' => 'El campo :attribute debe ser una lista.',
            'min' => 'El campo :attribute debe tener al menos :min elementos.',
            'nullable' => 'El campo :attribute es opcional.',
            'unique' => 'El valor del campo :attribute ya ha sido registrado y debe ser único.',
        ];
        if($r->lleva_contabilidad == '1'){
            $reglas = [
                'buscar_contribuyente' => '',
                'catastro_id' => 'required',
                'year_declaracion' => 'required',
                'fecha_declaracion' => 'required|date',
                'lleva_contabilidad' => 'required',
                'cont_total_activos' => 'required|decimal:2,4',
                'cont_total_pasivos' => 'required|decimal:2,4',
                'cont_total_patrimonio' => 'required|decimal:2,4',
                'cont_form_sri' => 'required',
                'cont_original' => 'required',
                'cont_total_percibidos_sv' => 'required|decimal:2,4',
                'actividades' => 'required|array|min:1',
                'actividades.*.id' => 'required',
            ];
        }else{
            $reglas = [
                'buscar_contribuyente' => '',
                'catastro_id' => 'required',
                'year_declaracion' => 'required',
                'fecha_declaracion' => 'required|date',
                'lleva_contabilidad' => 'boolean',
                'act_caja_banco' => 'required|decimal:2,4',
                'act_ctas_cobrar' => 'required|decimal:2,4',
                'act_inv_mercaderia' => 'required|decimal:2,4',
                'act_vehiculo_maquinaria' => 'required|decimal:2,4',
                'act_equipos_oficinas' => 'required|decimal:2,4',
                'act_edificios_locales' => 'required|decimal:2,4',
                'act_terrenos' => 'required|decimal:2,4',
                'act_total_activos' => 'required|decimal:2,4',
                'pas_ctas_dctos_pagar' => 'required|decimal:2,4',
                'pas_obligaciones_financieras' => 'required|decimal:2,4',
                'pas_otras_ctas_pagar' => 'required|decimal:2,4',
                'pas_otros_pasivos' => 'required|decimal:2,4',
                'pas_total_pasivos' => 'required|decimal:2,4',
                'patrimonio_total' => ['required',
                                        'decimal:2,4',
                                        function ($attribute, $value, $fail) use ($r) {
                                            if ($value != $r->input('act_total_activos') - $r->input('pas_total_pasivos')) {
                                                $fail('El valor de ' . $attribute . ' debe ser igual a la resta de total activos y total pasivos.');
                                            }
                                        },
                                    ],
                'actividades' => 'required|array|min:1',
                'actividades.*.id' => 'required',
            ];

        }
        $validator = Validator::make($r->all(),$reglas,$messages,$attributes);

        if ($validator->fails()) {
            return redirect('patente')
            ->withErrors($validator)
            ->withInput();
        }

         // Obtener los datos validados
         $validatedData = $validator->validated();

         // Crear el nuevo contribuyente 
         $PsqlPaPatente = PsqlPaPatente::create([
             'Contribuyente_id' => $validatedData['catastro_id'],
             'fecha_declaracion' => $validatedData['fecha_declaracion'],
             'calificacion_artesanal' => $validatedData['calificacion_artesanal'] ?? false,
             'year_declaracion' => $validatedData['year_declaracion'],
             'lleva_contabilidad' => $validatedData['lleva_contabilidad'] ?? false, // Opcional
             'act_caja_banco' => $validatedData['act_caja_banco'] ?? null, // Opcional
             'act_ctas_cobrar' => $validatedData['act_ctas_cobrar'] ?? null, // Opcional
             'act_inv_mercaderia' => $validatedData['act_inv_mercaderia'] ?? null, // Opcional
             'act_vehiculo_maquinaria' => $validatedData['act_vehiculo_maquinaria'] ?? null, // Opcional
             'act_equipos_oficinas' => $validatedData['act_equipos_oficinas'] ?? null, // Opcional
             'act_edificios_locales' => $validatedData['act_edificios_locales'] ?? null, // Opcional
             'act_terrenos' => $validatedData['act_terrenos'] ?? null, // Opcional
             'act_total_activos' => $validatedData['act_total_activos'] ??
                      $validatedData['cont_total_activos'] ?? null,
             'pas_ctas_dctos_pagar' => $validatedData['pas_ctas_dctos_pagar'] ?? null, // Opcional
             'pas_obligaciones_financieras' => $validatedData['pas_obligaciones_financieras'] ?? null, // Opcional
             'pas_otras_ctas_pagar' => $validatedData['pas_otras_ctas_pagar'] ?? null, // Opcional
             'pas_otros_pasivos' => $validatedData['pas_otros_pasivos'] ?? null, // Opcional
             'pas_total_pasivos' => $validatedData['pas_total_pasivos'] ??
                                $validatedData['cont_total_pasivos'] ?? null, // Obligatorio
             'patrimonio' => $validatedData['patrimonio_total'] ??
                            $validatedData['cont_total_patrimonio'] ?? null, // Obligatorio
             'formulario_sri_num' => $validatedData['cont_form_sri'] ?? null, // Opcional
             //'fecha_declaracion_sri' => $validatedData['cont_original'] ?? null, // Opcional
             'original_sustitutiva' => $validatedData['cont_original'] ?? null, // Opcional
             'porc_ing_perc_sv' => $validatedData['cont_total_percibidos_sv'] ?? null, // Opcional
             'estado' => 1, // Opcional
         ]);
         return redirect()->route('create.patente')->with('success', 'Su patente fue generada exitosamente.');
    }

    public function store(Request $r)
    {
        // dd($r->all());
        DB::beginTransaction(); // Iniciar la transacción
    
        try {
            $attributes = [
                'cmb_propietario' => 'contribuyente',
                'actividades.*.id' => 'actividad comercial',
                'locales.*.id' => 'local comercial',
                'year_declaracion' => 'Año de declaracion',
                'fecha_declaracion' => 'Fecha declaracion',
                'lleva_contabilidad' => 'Lleva contabilidad',
                'act_caja_banco' => 'Caja de banco',
                'act_ctas_cobrar' => 'Cuentas por cobrar ',
                'act_inv_mercaderia' => 'Inventario de mercaderia',
                'act_vehiculo_maquinaria' => 'Vehiculo y maquinaria',
                'act_equipos_oficinas' => 'Equipos de oficina',
                'act_edificios_locales' => 'Edificio locales',
                'act_terrenos' => 'Terrenos',
                'act_total_activos' => 'Total activos',
                'pas_ctas_dctos_pagar' => 'Cuentas y documentos por pagar',
                'pas_obligaciones_financieras' => 'Obligaciones financieras',
                'pas_otras_ctas_pagar' => 'Otras cuentas por pagar',
                'pas_otros_pasivos' => 'Otros pasivos',
                'pas_total_pasivos' => 'Total de pasivos',
            ];
    
            $messages = [
                'required' => 'El campo :attribute es obligatorio.',
                'numeric' => 'El campo :attribute debe ser numérico.',
                'decimal' => 'El campo :attribute debe tener decimales válidos.',
                'date' => 'El campo :attribute debe ser una fecha válida.',
                'array' => 'El campo :attribute debe ser una lista.',
                'min' => 'El campo :attribute debe tener al menos :min elementos.',
                'boolean' => 'El campo :attribute debe ser verdadero o falso.',
            ];
            $reglas=[];
            if (!isset($r->profesionales)) {
                // dd(sset($r->profesional));
                // Reglas de validación según lleva contabilidad
                $reglas = ($r->lleva_contabilidad == '1') ? [
                    'cmb_propietario' => 'required',
                    'year_declaracion' => 'required',
                    'fecha_declaracion' => 'required|date',
                    'lleva_contabilidad' => 'required',
                    'cont_total_activos' => 'required|decimal:2,4',
                    'cont_total_pasivos' => 'required|decimal:2,4',
                    'cont_total_patrimonio' => 'required|decimal:2,4',
                    'cont_form_sri' => 'required',
                    'cont_original' => 'required',
                    'cont_total_percibidos_sv' => 'required',
                    // 'cantidad_ingreso_percibido' => 'required|decimal:2,4',
                    'actividades' => 'required|array|min:1',
                    'actividades.*.id' => 'required',
                ] : [
                    'cmb_propietario' => 'required',
                    'year_declaracion' => 'required',
                    'fecha_declaracion' => 'required|date',
                    'lleva_contabilidad' => 'boolean',
                    'act_caja_banco' => 'required|decimal:2,4',
                    'act_ctas_cobrar' => 'required|decimal:2,4',
                    'act_inv_mercaderia' => 'required|decimal:2,4',
                    'act_vehiculo_maquinaria' => 'required|decimal:2,4',
                    'act_equipos_oficinas' => 'required|decimal:2,4',
                    'act_edificios_locales' => 'required|decimal:2,4',
                    'act_terrenos' => 'required|decimal:2,4',
                    'act_total_activos' => 'required|decimal:2,4',
                    'pas_ctas_dctos_pagar' => 'required|decimal:2,4',
                    'pas_obligaciones_financieras' => 'required|decimal:2,4',
                    'pas_otras_ctas_pagar' => 'required|decimal:2,4',
                    'pas_otros_pasivos' => 'required|decimal:2,4',
                    'pas_total_pasivos' => 'required|decimal:2,4',
                    'patrimonio_total' => ['required', 'decimal:2,4', function ($attribute, $value, $fail) use ($r) {
                        if ($value != $r->input('act_total_activos') - $r->input('pas_total_pasivos')) {
                            $fail('El valor de ' . $attribute . ' debe ser igual a la resta de total activos y total pasivos.');
                        }
                    }],
                    'actividades' => 'required|array|min:1',
                    'actividades.*.id' => 'required',
                ];
            }else{
                $reglas =[
                    'cmb_propietario' => 'required',
                    'year_declaracion' => 'required',
                    'fecha_declaracion' => 'required|date',
                    'lleva_contabilidad' => 'boolean',
                    // 'lleva_contabilidad' => 'boolean',
                ];
            }
            // dd($r->profesionales);
            // Validar datos
            $validator = Validator::make($r->all(), $reglas, $messages, $attributes);
           
            if ($validator->fails()) {
                return [
                    'error' => true,
                    'mensaje' => 'Complete todos los campos.',
                    'errores' => $validator->errors()
                ];
            }
            // dd($r->all());
            // Datos validados
            $validatedData = $validator->validated();
            
            $estado=$r->emision;
            $cantidad_percibida=null;
            if($r->lleva_contabilidad == '1'){
                $archivo=$r->archivo_patente;
                $extension = pathinfo($archivo->getClientOriginalName(), PATHINFO_EXTENSION);
                $nombre_original=pathinfo($archivo->getClientOriginalName(), PATHINFO_FILENAME);;
                $nombre="patente";
                $nombreDocumento =  "$nombre".date('Ymd').'-'.time();

                $cantidad_percibida=$r->cont_total_patrimonio * ($r->cont_total_percibidos_sv / 100);
            }

            // $PsqlPaPatente = PsqlPaPatente::create([
            //     'Contribuyente_id' => $validatedData['catastro_id'],
            //     'fecha_declaracion' => $validatedData['fecha_declaracion'],
            //     'calificacion_artesanal' => $validatedData['calificacion_artesanal'] ?? false,
            //     'year_declaracion' => $validatedData['year_declaracion'],
            //     'lleva_contabilidad' => $validatedData['lleva_contabilidad'] ?? false,
            //     'act_caja_banco' => $validatedData['act_caja_banco'] ?? null,
            //     'act_ctas_cobrar' => $validatedData['act_ctas_cobrar'] ?? null,
            //     'act_inv_mercaderia' => $validatedData['act_inv_mercaderia'] ?? null,
            //     'act_vehiculo_maquinaria' => $validatedData['act_vehiculo_maquinaria'] ?? null,
            //     'act_equipos_oficinas' => $validatedData['act_equipos_oficinas'] ?? null,
            //     'act_edificios_locales' => $validatedData['act_edificios_locales'] ?? null,
            //     'act_terrenos' => $validatedData['act_terrenos'] ?? null,
            //     'act_total_activos' => $validatedData['act_total_activos'] ?? $validatedData['cont_total_activos'] ?? null,
            //     'pas_ctas_dctos_pagar' => $validatedData['pas_ctas_dctos_pagar'] ?? null,
            //     'pas_obligaciones_financieras' => $validatedData['pas_obligaciones_financieras'] ?? null,
            //     'pas_otras_ctas_pagar' => $validatedData['pas_otras_ctas_pagar'] ?? null,
            //     'pas_otros_pasivos' => $validatedData['pas_otros_pasivos'] ?? null,
            //     'pas_total_pasivos' => $validatedData['pas_total_pasivos'] ?? $validatedData['cont_total_pasivos'] ?? null,
            //     'patrimonio' => $validatedData['patrimonio_total'] ?? $validatedData['cont_total_patrimonio'] ?? null,
            //     'formulario_sri_num' => $validatedData['cont_form_sri'] ?? null,
            //     'original_sustitutiva' => $validatedData['cont_original'] ?? null,
            //     'porc_ing_perc_sv' => $validatedData['cont_total_percibidos_sv'] ?? null,
            //     // 'cantidad_ingreso_percibido' => $cantidad_percibida,
            //     // 'archivo_patente' => $nombreDocumento.".".$extension;
            //     'estado' => $estado,

               
            // ]);

            $PsqlPaPatente = PsqlPaPatente::create([
                'Contribuyente_id' => $validatedData['cmb_propietario'],
                'fecha_declaracion' => $validatedData['fecha_declaracion'],
                'calificacion_artesanal' => $validatedData['calificacion_artesanal'] ?? false,
                'year_declaracion' => $validatedData['year_declaracion'],
                'lleva_contabilidad' => $validatedData['lleva_contabilidad'] ?? false,
                'act_caja_banco' => $r->act_caja_banco ?? null,
                'act_ctas_cobrar' => $r->act_ctas_cobrar ?? null,
                'act_inv_mercaderia' => $r->act_inv_mercaderia ?? null,
                'act_vehiculo_maquinaria' => $r->act_vehiculo_maquinaria ?? null,
                'act_equipos_oficinas' => $r->act_equipos_oficinas ?? null,
                'act_edificios_locales' => $r->act_edificios_locales ?? null,
                'act_terrenos' => $r->act_terrenos ?? null,
                'act_total_activos' => $r->act_total_activos ?? $r->cont_total_activos ?? null,
                'pas_ctas_dctos_pagar' => $r->pas_ctas_dctos_pagar ?? null,
                'pas_obligaciones_financieras' => $r->pas_obligaciones_financieras ?? null,
                'pas_otras_ctas_pagar' => $r->pas_otras_ctas_pagar ?? null,
                'pas_otros_pasivos' => $r->pas_otros_pasivos ?? null,
                'pas_total_pasivos' => $r->pas_total_pasivos ?? $r->cont_total_pasivos ?? null,
                'patrimonio' => $r->patrimonio_total ?? $r->cont_total_patrimonio ?? null,
                'formulario_sri_num' => $r->cont_form_sri ?? null,
                'original_sustitutiva' => $r->cont_original ?? null,
                'porc_ing_perc_sv' => $r->cont_total_percibidos_sv ?? null,
                // 'cantidad_ingreso_percibido' => $cantidad_percibida,
                // 'archivo_patente' => $nombreDocumento.".".$extension;
                'estado' => $estado,

               
            ]);
            if(isset($nombreDocumento)){
                \Storage::disk('disksDocumentoRenta')->put($nombreDocumento.".".$extension,  \File::get($archivo));
            }

            foreach ($r->actividades as $key => $value) {
                if(isset($value['id'])){
                    $guardaActividades=new PsqlPatenteActividadesCont();
                    $guardaActividades->id_patente=$PsqlPaPatente->id;
                    $guardaActividades->id_actividad_cont=$value['id'];
                    $guardaActividades->save();
                }                    
            }
            $es_activo=false;
            foreach ($r->locales as $key => $value) {
                if(isset($value['id'])){
                    
                    if(isset($r->impuesto_1punto5)){
                        $es_activo=true;
                    }
                 
                    $guardaLocal=PsqlPaPatente::find($PsqlPaPatente->id);
                    $guardaLocal->id_pa_local=$value['id'];
                    if(isset($nombreDocumento)){
                        $guardaLocal->archivo_patente=$nombreDocumento.".".$extension;
                    }
                    $guardaLocal->cantidad_ingreso_percibido=$cantidad_percibida;

                    $guardaLocal->valor_impuesto= $r->cont_impuesto;
                    $guardaLocal->valor_exoneracion=$r->cont_exoneracion;
                    $guardaLocal->valor_sta=$r->cont_sta;
                    $guardaLocal->valor_patente=$r->cont_pago_patente;
    
                    $guardaLocal->valor_impuesto_act=$r->cont_impuesto_act;
                    $guardaLocal->valor_exoneracion_act=$r->cont_exoneracion_act;
                    $guardaLocal->valor_sta_act=$r->cont_sta_act;
                    $guardaLocal->valor_activo_total=$r->cont_pago_activo_total;

                    $guardaLocal->es_patente=true;
                    $guardaLocal->es_activo=$es_activo;

                    $guardaLocal->save();
                }                   
            }

            if($estado==1){
                $guardaLiquidacionPatente=$this->guardaLiquidacion($PsqlPaPatente->id,259);
                if($es_activo==true){
                    $guardaLiquidacionPatente=$this->guardaLiquidacion($PsqlPaPatente->id,17);
                   
                }
            }

            DB::commit(); // Confirmar los cambios en la BD

            return [
                'error' => false,
                'mensaje' => 'Su patente fue generada exitosamente.',
                'id'=>$PsqlPaPatente->id

            ];   
           
        } catch (\Exception $e) {
            DB::rollback(); // Revertir cambios en caso de error
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al generar la patente: ' . $e->getMessage()
            ];
        }
    }

    public function guardaLiquidacion($id=44, $codigo=17){
        DB::beginTransaction(); // Iniciar la transacción
        try{
            $PsqlPaPatente=PsqlPaPatente::find($id);
            $anio_fis=DB::connection('pgsql')
            ->table('sgm_patente.year_declaracion')
            ->where('id', $PsqlPaPatente->year_declaracion)
            ->first();
            if($codigo==259){
                $impuesto=$PsqlPaPatente->valor_impuesto;
                $valor_exoneracion=$PsqlPaPatente->valor_exoneracion;
                $valor_patente=$PsqlPaPatente->valor_patente;
                $valor_sta=$PsqlPaPatente->valor_sta;
                $rubro_codigo=656;
            }else{
                $impuesto=$PsqlPaPatente->valor_impuesto_act;
                $valor_exoneracion=$PsqlPaPatente->valor_exoneracion_act;
                $valor_patente=$PsqlPaPatente->valor_activo_total;
                $valor_sta=$PsqlPaPatente->valor_sta_act;
                $rubro_codigo=27;
            }

            $rubro_patente= $impuesto - $valor_exoneracion;
            $patente_id = $id;
            $total_pago = $valor_patente;
            $usuario_registro = 'cintriago';
            $fecha_ingreso = date('Y-m-d H:i:s');
            $valor_rubro_patente = $rubro_patente;
            $valor_rubro_tasa_admin = $valor_sta;
            $contribuyente_id=$PsqlPaPatente->Contribuyente_id;

            // $anio_fiscal = $anio_fis->year_ejercicio_fiscal;
            $anio_fiscal = $anio_fis->year_declaracion;
            $existe = DB::connection('pgsql')
                        ->table('sgm_financiero.ren_liquidacion')
                        ->where('pa_patente_id', '=', $patente_id)
                        ->exists();
            if ($existe == true)
            {
                // return false;
                //enviar mensaje de error
            }
    
            // Contar cuántas secuencias existen para ese año y tipo de liquidación 13
            $existeSecuencia = DB::connection('pgsql')
                                    ->table('sgm_financiero.ren_secuencia_num_liquidacion')
                                    ->where('anio', $anio_fiscal)
                                    ->where('id_tipo_liquidacion', $codigo)
                                    ->count();
    
            if ($existeSecuencia == 0) {
                // Si no hay secuencia, insertar la primera con secuencia 1
                $max_num_liquidacion = 1;
            } else {
                // Si ya existen secuencias, obtener el número máximo actual y sumarle 1
                $max_num_liquidacion = DB::connection('pgsql')
                    ->table('sgm_financiero.ren_secuencia_num_liquidacion')
                    ->where('anio', $anio_fiscal)
                    ->where('id_tipo_liquidacion', $codigo)
                    ->max('secuencia');
    
                $max_num_liquidacion = $max_num_liquidacion + 1;
            }
    
             // Insertar el nuevo número de secuencia
            DB::connection('pgsql')
                        ->table('sgm_financiero.ren_secuencia_num_liquidacion')
                        ->insert([
                            'secuencia'          => $max_num_liquidacion,
                            'anio'               => $anio_fiscal,
                            'id_tipo_liquidacion'=> $codigo
                        ]);
    
            $tipo = DB::connection('pgsql')
                        ->table('sgm_financiero.ren_tipo_liquidacion')
                        ->select('prefijo', 'nombre_titulo')
                        ->where('id', $codigo)
                        ->first();
    
            $contribuyente = DB::connection('pgsql')
                        ->table('sgm_patente.pa_catastro_contribuyente')
                        ->select('propietario_id')
                        ->where('id', $contribuyente_id)
                        ->first();
    
            $liquidacionId = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')->insertGetId([
                'num_liquidacion'    => $max_num_liquidacion,
                'id_liquidacion'     => $anio_fiscal.'-'.str_pad($max_num_liquidacion, 6, '0', STR_PAD_LEFT).'-'.$tipo->prefijo,
                'tipo_liquidacion'   => $codigo,
                'total_pago'         => $total_pago,
                'usuario_ingreso'    => $usuario_registro,
                'fecha_ingreso'      => $fecha_ingreso,
                'saldo'              => $total_pago,
                'estado_liquidacion' => '2',
                'observacion'        => $tipo->nombre_titulo,
                'anio'               => $anio_fiscal,
                'bombero'            => 'f',
                'estado_coactiva'    => 1,
                'nombre_comprador'   => 1,
                'pa_patente_id'      => $patente_id,
                'comprador'          => $contribuyente->propietario_id,
            ]);
            
            
            $rubros = [
                ['rubro' => $rubro_codigo, 'valor' => $valor_rubro_patente,'estado' => true, 'valor_recaudado' => 0],
                ['rubro' => 3, 'valor' => $valor_rubro_tasa_admin, 'estado' => true, 'valor_recaudado' => 0]
            ];
            $datos = [];
            foreach ($rubros as $rubro) {
                $datos[] = [
                    'liquidacion' => $liquidacionId,
                    'rubro' => $rubro['rubro'],
                    'valor' => $rubro['valor'],
                    'estado' => $rubro['estado'] ?? true, // Por defecto activo
                    'cantidad' => $rubro['cantidad'] ?? 1,
                    'valor_recaudado' => $rubro['valor_recaudado'] ?? 0,
                ];
            }
            DB::connection('pgsql')->table('sgm_financiero.ren_det_liquidacion')->insert($datos);
            return [
                'error' => false,
                'mensaje' => 'Informacion guardad exitosamente'
            ];
        
        } catch (\Exception $e) {
            dd($e);
            DB::rollback();
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al generar la patente: ' . $e->getMessage()
            ];
        }
    }

    public function guardaLiquidacion1($id, $impuesto, $exoneracion, $sta,$total, $codigo){
        try{
            $rubro_patente= $impuesto - $exoneracion;
            $patente_id = $id;
            $total_pago = $total;
            $usuario_registro = 'cintriago';
            $fecha_ingreso = date('Y-m-d H:i:s');
            $valor_rubro_patente = $rubro_patente;
            $valor_rubro_tasa_admin = $sta;
            $existe = DB::connection('pgsql')
                        ->table('sgm_financiero.ren_liquidacion')
                        ->where('pa_patente_id', '=', $patente_id)
                        ->exists();
            if ($existe == true)
            {
                return false;
            }
            //año actual
            $anio_actual = Carbon::now()->year;
            // Contar cuántas secuencias existen para ese año y tipo de liquidación 13
            $existeSecuencia = DB::connection('pgsql')
                                ->table('sgm_financiero.ren_secuencia_num_liquidacion')
                                ->where('anio', $anio_actual)
                                ->where('id_tipo_liquidacion', $codigo)
                                ->count();

            if ($existeSecuencia == 0) {
                // Si no hay secuencia, insertar la primera con secuencia 1
                $max_num_liquidacion = 1;
            } else {
                // Si ya existen secuencias, obtener el número máximo actual y sumarle 1
                $max_num_liquidacion = DB::connection('pgsql')
                    ->table('sgm_financiero.ren_secuencia_num_liquidacion')
                    ->where('anio', $anio_actual)
                    ->where('id_tipo_liquidacion', $codigo)
                    ->max('secuencia');

                $max_num_liquidacion = $max_num_liquidacion + 1;
            }

            // Insertar el nuevo número de secuencia
            DB::connection('pgsql')
                        ->table('sgm_financiero.ren_secuencia_num_liquidacion')
                        ->insert([
                            'secuencia'          => $max_num_liquidacion,
                            'anio'               => $anio_actual,
                            'id_tipo_liquidacion'=> $codigo
                        ]);

            $tipo = DB::connection('pgsql')
                        ->table('sgm_financiero.ren_tipo_liquidacion')
                        ->select('prefijo', 'nombre_titulo')
                        ->where('id', $codigo)
                        ->first();

                        // if ($tipo) {
                        //     echo "Prefijo: {$tipo->prefijo}, Nombre: {$tipo->nombre_titulo}";
                        // } else {
                        //     echo "No se encontró el registro.";
                        // }
            $liquidacionId = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion')->insertGetId([
                'num_liquidacion'       => $max_num_liquidacion,
                'id_liquidacion'           => $tipo->prefijo.'-'.$max_num_liquidacion,
                'tipo_liquidacion'   => $codigo,
                'total_pago'         => $total_pago,
                'usuario_ingreso'    => $usuario_registro,
                'fecha_ingreso'      => $fecha_ingreso,
                'saldo'              => $total_pago,
                'estado_liquidacion' => '2',
                'observacion'        => $tipo->nombre_titulo,
                'anio'               => $anio_actual,
                'bombero'            => 'f',
                'estado_coactiva'    => 1,
                'nombre_comprador'   => 1,
                'pa_patente_id'         => $patente_id,
            ]);

            $rubros = [
                ['rubro' => 656, 'valor' => $valor_rubro_patente,'estado' => true, 'valor_recaudado' => 0],
                ['rubro' => 3, 'valor' => $valor_rubro_tasa_admin, 'estado' => true, 'valor_recaudado' => 0]
            ];
            $datos = [];
            foreach ($rubros as $rubro) {
                $datos[] = [
                    'liquidacion' => $liquidacionId,
                    'rubro' => $rubro['rubro'],
                    'valor' => $rubro['valor'],
                    'estado' => $rubro['estado'] ?? true, // Por defecto activo
                    'cantidad' => $rubro['cantidad'] ?? 1,
                    'valor_recaudado' => $rubro['valor_recaudado'] ?? 0,
                ];
            }

            DB::connection('pgsql')->table('sgm_financiero.ren_det_liquidacion')->insert($datos);
            return [
                'error' => false,
                'mensaje' => 'Informacion guardad exitosamente'
            ];
        
        } catch (\Exception $e) {
           
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al generar la patente: ' . $e->getMessage()
            ];
        }
    }

    public function buscaInfoContribuyente($id){

        try {

            $info=DB::connection('pgsql')->table('sgm_patente.pa_catastro_contribuyente as co')
            ->leftJoin('sgm_app.cat_ente as e','e.id','co.propietario_id')
            ->leftJoin('sgm_app.cat_ente as rl','rl.id','co.representante_legal_id')
            ->leftJoin('sgm_patente.pa_clase_contribuyente as cc','cc.id','co.clase_contribuyente_id')
            ->where('co.id',$id)
            ->select(DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS contribuyente"),'co.ruc',
                DB::raw("CONCAT(rl.apellidos, ' ', rl.nombres) AS representante_legal"),'co.tipo_contribuyente'
                ,'rl.ci_ruc as cedula_rl',
                DB::raw("(SELECT COUNT(*) FROM sgm_patente.pa_locales WHERE idcatastro_contr = co.id and estado='A') as cantidad_locales"),'co.fecha_inicio_actividades','co.estado_contribuyente_id','cc.nombre as clase_contri')
            ->first();

            $actividad=DB::connection('pgsql')->table('sgm_patente.pa_actividad_contribuyente as act_cont')
            ->leftJoin('sgm_patente.pa_ctlg_actividades_comerciales as ac','ac.id','act_cont.Actividad_comercial_id')
            ->where('act_cont.Catastro_contribuyente_id',$id)
            ->select('ac.ciiu','ac.descripcion','act_cont.Actividad_comercial_id as idActividad','act_cont.id')
            ->get();

            $locales=DB::connection('pgsql')->table('sgm_patente.pa_locales as local')
            ->leftJoin('sgm_app.cat_provincia as pr','pr.id','local.provincia_id')
            ->leftJoin('sgm_app.cat_canton as canton','canton.id','local.canton_id')
            ->leftJoin('sgm_app.cat_parroquia as parroquia','parroquia.id','local.parroquia_id')
            ->where('local.idcatastro_contr',$id)
            ->where('local.estado','A')
            ->select('pr.descripcion as provin','canton.nombre as canton_','parroquia.descripcion as parroquia_'
            ,'local.calle_principal','local.calle_secundaria','local.actividad_descripcion','local.local_propio',
            DB::raw("CASE WHEN local.estado_establecimiento = 1 THEN 'Abierto' ELSE 'Cerrado' END as estado_establecimiento")
            ,'local.id')
            ->get();
            
            return [
                'data' => $info,
                'actividad' => $actividad,
                'locales' => $locales,
                'error' =>false
            ];
        } catch (\Exception $e) {

            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al generar la patente: ' . $e->getMessage()
            ];

        }
    }

    public function verLocal($id){
        try {
            $local=DB::connection('pgsql')->table('sgm_patente.pa_locales')
            ->where('id',$id)
            ->first();

            return [
                'error' => false,
                'data' => $local
            ];

        }catch (\Exception $e) {
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al obtener los datos: ' . $e->getMessage()
            ];
        }

    }
    
    public function pdfDeclaracionCobro($id=44){
        try{
            $patente=DB::connection('pgsql')->table('sgm_patente.pa_patente as pa')
            ->leftJoin('sgm_patente.pa_catastro_contribuyente as co','pa.Contribuyente_id','co.id')
            ->leftJoin('sgm_app.cat_ente as e','e.id','co.propietario_id')
            ->leftJoin('sgm_patente.pa_locales as lo','pa.id_pa_local','lo.id')
            ->leftJoin('sgm_app.cat_canton as ca','lo.canton_id','ca.id')
            ->where('pa.id',$id)
            ->select('actividad_descripcion','co.ruc','co.razon_social',
                DB::raw("CONCAT(lo.calle_principal, ' y ', lo.calle_secundaria) AS calle"),'pa.fecha_declaracion',
                'ca.nombre as canton','pa.valor_patente',DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS contribuyente"),'pa.estado',
                'pa.valor_activo_total','es_activo')
            ->get();

            foreach($patente as $key=>&$value){
                $actividades=DB::connection('pgsql')->table('sgm_patente.pa_patente_actividad_contr as pa_act')
                ->leftJoin('sgm_patente.pa_actividad_contribuyente as act','pa_act.id_actividad_cont','act.Actividad_comercial_id')
                ->leftJoin('sgm_patente.pa_ctlg_actividades_comerciales as nom_act','nom_act.id','act.Actividad_comercial_id')
                ->where('id_patente',$id)
                ->select(DB::raw("CONCAT(nom_act.descripcion) AS actividad"))
                ->get(); 
                $value->act = $actividades->pluck('actividad')->toArray(); 
            }
            
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');

            $fecha_timestamp = strtotime($patente[0]->fecha_declaracion);
    
            $fecha_formateada = strftime("%d de %B de %Y", $fecha_timestamp);
            
            if($patente[0]->estado==2){
                if(!is_null($patente[0]->valor_activo_total)){
                    $nombrePDF="simulacion_patente_activo_total.pdf";
                }else{
                    $nombrePDF="simulacion_patente.pdf";
                }
                    
            }else{
                if(!is_null($patente[0]->valor_activo_total)){
                    $nombrePDF="emision_patente_activo_total.pdf";
                }else{
                    $nombrePDF="emision_patente.pdf";
                }
               
            }
            // dd($patente);

            $pdf=\PDF::LoadView('reportes.reporte_patente_declaracion',['patente'=>$patente[0], "fecha_formateada"=>$fecha_formateada] );
            $pdf->setPaper("A4", "portrait");

            return $pdf->stream("aa.pdf");
        }catch (\Throwable $e) {
            dd($e);
            // Log::error('Sgm2Controller => pdfPagosImpPrediales => mensaje => '.$e->getMessage());
            return [
                'error'=>true,
                'mensaje'=>'Ocurrió un error'
            ];
            
        }
    }
    public function crearTitulo($id){
        try{
            $patente=DB::connection('pgsql')->table('sgm_patente.pa_patente as pa')
            ->leftJoin('sgm_patente.pa_catastro_contribuyente as co','pa.Contribuyente_id','co.id')
            ->leftJoin('sgm_app.cat_ente as e','e.id','co.propietario_id')
            ->leftJoin('sgm_patente.pa_locales as lo','pa.id_pa_local','lo.id')
            ->leftJoin('sgm_app.cat_canton as ca','lo.canton_id','ca.id')
            ->where('pa.id',$id)
            ->select('actividad_descripcion','co.ruc','co.razon_social',
                DB::raw("CONCAT(lo.calle_principal, ' y ', lo.calle_secundaria) AS calle"),'pa.fecha_declaracion',
                'ca.nombre as canton','pa.valor_patente',DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS contribuyente"),'pa.estado',
                'pa.valor_activo_total','es_activo')
            ->get();

            foreach($patente as $key=>&$value){
                $actividades=DB::connection('pgsql')->table('sgm_patente.pa_patente_actividad_contr as pa_act')
                // ->leftJoin('sgm_patente.pa_actividad_contribuyente as act','pa_act.id_actividad_cont','act.Actividad_comercial_id')
                ->leftJoin('sgm_patente.pa_ctlg_actividades_comerciales as nom_act','nom_act.id','pa_act.id_actividad_cont')
                ->where('id_patente',$id)
                ->select(DB::raw("CONCAT(nom_act.descripcion) AS actividad"))
                ->get(); 
                $value->act = $actividades->pluck('actividad')->toArray(); 
            }

            // dd($patente);
            
            setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');

            $fecha_timestamp = strtotime($patente[0]->fecha_declaracion);
    
            $fecha_formateada = strftime("%d de %B de %Y", $fecha_timestamp);
            
            if($patente[0]->estado==2){
                if(!is_null($patente[0]->valor_activo_total)){
                    $nombrePDF="simulacion_patente_activo_total.pdf";
                }else{
                    $nombrePDF="simulacion_patente.pdf";
                }
                    
            }else{
                if(!is_null($patente[0]->valor_activo_total)){
                    $nombrePDF="emision_patente_activo_total.pdf";
                }else{
                    $nombrePDF="emision_patente.pdf";
                }
               
            }
            

            $pdf=\PDF::LoadView('reportes.reporte_patente',['patente'=>$patente[0], "fecha_formateada"=>$fecha_formateada] );
            $pdf->setPaper("A4", "portrait");

            // return $pdf->stream("aa.pdf");
            
            $estadoarch = $pdf->stream();

            //lo guardamos en el disco temporal
            Storage::disk('disksDocumentoRenta')->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = Storage::disk('disksDocumentoRenta')->exists($nombrePDF); 
            if($exists_destino){ 
                return[
                    'error'=>false,
                    'pdf'=>$nombrePDF
                ];
            }else{
                return [
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ];
            }
           
        }catch (\Throwable $e) {
            dd($e);
            // Log::error('Sgm2Controller => pdfPagosImpPrediales => mensaje => '.$e->getMessage());
            return [
                'error'=>true,
                'mensaje'=>'Ocurrió un error'
            ];
            
        }
    }

    public function verDocumento($documentName){
        try {
            $info = new \SplFileInfo($documentName);
            $extension = $info->getExtension();
            if($extension!= "pdf" && $extension!="PDF"){
                return \Storage::disk('disksDocumentoRenta')->download($documentName);
            }else{
                // obtenemos el documento del disco en base 64
                $documentEncode= base64_encode(\Storage::disk('disksDocumentoRenta')->get($documentName));
                // dd($documentEncode);
                return view("vistaPreviaDocumento")->with([
                    "documentName"=>$documentName,
                    "documentEncode"=>$documentEncode
                ]);        
            }            
        } catch (\Throwable $th) {
            // Log::error('AbonoController => visualizardoc => mensaje => '.$th->getMessage());
            abort("404");
        }

    }

    public function descargarArchivo($archivo){
        try{   
            
            $exists_destino = \Storage::disk('disksDocumentoRenta')->exists($archivo); 
            // dd($exists_destino);
            if($exists_destino){
                // return response()->download( public_path('storage/documentosRentas/'.$archivo));
                $filePath = \Storage::disk('disksDocumentoRenta')->path($archivo);

                // Devuelve la respuesta para descargar el archivo
                return response()->download($filePath);
            }else{
                // Log::error("DocumentosController =>descargarArchivo =>sms => Documento no encontrado");
                return back()->with(['error'=>'Ocurrió un error','error'=>'danger']);
            } 

        } catch (\Throwable $th) {
            dd($th);
            // Log::error("DocumentosController =>descargarArchivo =>sms => ".$th->getMessage());
            return back()->with(['error'=>'Ocurrió un error','error'=>'danger']);
        } 
         
    }

    public function calcular($valor, $tipo,$anio, $terceraEdad){
        try{
            $obtener_anio=DB::connection('pgsql')->table('sgm_patente.year_declaracion')
            ->where('id',$anio)
            ->select('year_declaracion')
            ->first();
            
            $aplica=0;
            if($terceraEdad==1){
                $salario_basico=DB::connection('pgsql')->table('sgm_patente.salario_basico')
                ->where('anio', $obtener_anio->year_declaracion)
                ->select('valor')
                ->first();
                if(!is_null($salario_basico)){
                    $comprobar=$salario_basico->valor * 500;            
                    if($valor > $comprobar){
                        $valor=$valor - $comprobar;
                        $aplica=1;
                    }
                }
            }
           
            $rangos = DB::connection('pgsql')
            ->table('sgm_patente.pa_base_imponible')
            ->selectRaw('MIN(desde) as minimo, MAX(desde) as maximo')
            ->first();
            // dd($rangos);
            $minimo = $rangos->minimo;
            $maximo = $rangos->maximo;

            $valor_pagar=0;
            $valor_porc_exced=0;
            $total_porcent_pagar=0;
            $desde=0;
            $obtener_dif=0;

            if($valor <= $minimo){
                $valor_pagar=10;
                $valor_porc_exced=0;
                $total_porcent_pagar=0;
                $calcular=null;
            }else if($valor >= $maximo){
                
                $calcular= DB::connection('pgsql')->table('sgm_patente.pa_base_imponible as bi')
                ->where('desde','=',$maximo)
                ->select('dolares','imp_sobre_fraccion_exec','desde','hasta','codigo')
                ->where('anio',$obtener_anio->year_declaracion)
                ->where('estado','A')
                ->first();
            }else{
                $calcular= DB::connection('pgsql')->table('sgm_patente.pa_base_imponible as bi')
                ->where('desde','<=',$valor)
                ->where('hasta','>=',$valor)
                ->select('dolares','imp_sobre_fraccion_exec','desde','hasta','codigo')
                ->where('anio',$obtener_anio->year_declaracion)
                ->where('estado','A')
                ->first();
            }
           
            if(!is_null($calcular)){
                $valor_pagar=$calcular->dolares;
                $valor_porc_exced=$calcular->imp_sobre_fraccion_exec;
                $obtener_dif=$valor -$calcular->desde;
                $total_porcent_pagar= $obtener_dif * ($valor_porc_exced /100);
                $desde=$calcular->desde;
                 
            }
           
            $sumar_total=$valor_pagar + $total_porcent_pagar;
            $sumar_total= number_format(($sumar_total),2,'.', '');
            $valores=["valor_pagar"=>$valor_pagar, "valor_porc_exced"=>$valor_porc_exced, "total_porcent_pagar"=>$total_porcent_pagar, "obtener_dif"=>$obtener_dif,"valor"=>$valor,"desde"=>$desde,"sumar_total"=>$sumar_total,"aplica"=>$aplica];
            return response()->json([
                'error'=>false,
                'calcular'=>$valores,
                // 'salario_basico'=>$salario_basico
            ]);
           

        }catch (\Throwable $e) {
            dd($e);
            // Log::error('Sgm2Controller => pdfPagosImpPrediales => mensaje => '.$e->getMessage());
            return response()->json([
                'error'=>true,
                'mensaje'=>'Ocurrió un error'
            ]);
            
        }
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        $patente = PsqlPaPatente::find($id);
        $PsqlYearDeclaracion = PsqlYearDeclaracion::select('id','year_declaracion','year_ejercicio_fiscal')->get();
        return view('rentas.patenteEditar',compact('PsqlYearDeclaracion','patente'));
    }


    public function previsualizar(string $id){
        return view('rentas.patentePrevisualizarRubros');
    }
    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }

    public function datatable(Request $r)
    {
        if($r->ajax()){
            $listaPatente = PsqlPaPatente::all();
            return Datatables($listaPatente)
            ->editColumn('lleva_contabilidad', function($listaPatente){
                    if($listaPatente->lleva_contabilidad == true){
                        return 'SI';
                    }else{
                        return 'NO';
                    }
                })
            ->editColumn('estado', function($listaPatente){
                    if($listaPatente->estado == 1){
                        return '<span class="badge text-bg-primary">Generada</span>';
                    }else{
                        return '<span class="badge text-bg-success">Completada</span>';
                    }
                })
            ->addColumn('ruc', function ($listaPatente) {
                // return $listaPatente->contribuyente->ruc ;
                return '<b>RUC:</b>'.$listaPatente->contribuyente->ruc . '<br> <b>NOMBRES:</b>' . $listaPatente->contribuyente->razon_social. '<br> <b>CONTABILIDAD:</b>' . $listaPatente->lleva_contabilidad;
            })

            ->addColumn('contribuyente_name', function ($listaPatente) {
                return $listaPatente->contribuyente->razon_social;
            })
            ->addColumn('year_declaracion', function ($listaPatente) {
                return $listaPatente->year->year_declaracion;
            })
            ->addColumn('year_balance', function ($listaPatente) {
                return $listaPatente->year->year_ejercicio_fiscal;
            })
            // ->addColumn('action', function ($listaPatente) {
            //     return '<a class="btn btn-primary btn-sm" href="'.route('index.patente',$listaPatente->id).'">Ver</a> 
            //      ';
            // })
            ->addColumn('action', function ($listaPatente) {
                // return '<button type="button" class="btn btn-primary btn-sm" onclick="verPatente('$listaPatente->id')">Ver</a> 
                //  ';
                return '<a class="btn btn-primary btn-sm" onclick="verPatente(\''.$listaPatente->id.'\')">Ver</a>';
            })
            ->rawColumns(['action','estado','ruc'])
            ->make(true);
        }
    }
}
