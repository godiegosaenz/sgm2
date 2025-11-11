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
use App\Models\PsqlCatastroContribuyente;
use App\Models\PsqlProvincia;
use App\Models\PsqlCanton;
use App\Models\PsqlParroquia;
use Number;
use Storage;
use App\Models\PsqlEnte;
use App\Models\PsqlCtlgItem;
use App\Models\PsqlEnteTelefono;
use App\Models\PsqlEnteCorreo;
use App\Models\PsqlPaClaseContribuyente;
use App\Models\BaseImponiblePatente;
use DB;
use Carbon\Carbon;
use Illuminate\Support\Facades\Gate;

class PatenteController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        //Gate::authorize('index', PsqlPaPatente::class);
        if(!Auth()->user()->hasPermissionTo('Lista de patente'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('rentas.patente');
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
       // Gate::authorize('create', PsqlPaPatente::class);
        if(!Auth()->user()->hasPermissionTo('Declarar patente'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        $PsqlYearDeclaracion = PsqlYearDeclaracion::select('id','year_declaracion','year_ejercicio_fiscal')->get();
        $PsqlProvincia = PsqlProvincia::all();
        $clase = PsqlPaClaseContribuyente::all();
        return view('rentas.patenteCrear',compact('PsqlYearDeclaracion','PsqlProvincia','clase'));
    }

    /* *
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
             'idusuario_registra'=>auth()->user()->id
         ]);
         return redirect()->route('create.patente')->with('success', 'Su patente fue generada exitosamente.');
    }

    public function store(Request $r)
    {
        
        DB::beginTransaction(); // Iniciar la transacción

        try {
            $attributes = [
                'cmb_propietario' => 'contribuyente',
                // 'actividades.*.id' => 'actividad comercial11',
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
                    // 'actividades.*.id' => 'required',
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
                    // 'actividades.*.id' => 'required',
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
           
            // Validar datos
            $validator = Validator::make($r->all(), $reglas, $messages, $attributes);

            if ($validator->fails()) {
                return [
                    'error' => true,
                    'mensaje' => 'Complete todos los campos.',
                    'errores' => $validator->errors()
                ];
            }
         
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
                'idusuario_registra'=>auth()->user()->id


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
            $anio=date('Y');

            $verificaNum=PsqlPaPatente::with('year')
            ->whereHas('year', function($query){
                $query->where('year_declaracion', date('Y'));
            })
            ->whereNotNull('codigo')
            ->select('codigo')
            ->orderBy('id','desc')->first();

            $num=0;
            if(is_null($verificaNum)){
                $num=1;
            }else{
                $solo_numero=explode("-",$verificaNum->codigo);
                $num = (int)$solo_numero[1] + 1;
            }

            $codigo_patente= 'PAT-'.str_pad($num, 5, '0', STR_PAD_LEFT).'-'.$anio;

            $verificaNum=PsqlPaPatente::with('year')
            ->whereHas('year', function($query){
                $query->where('year_declaracion', date('Y'));
            })
            ->whereNotNull('codigo_act')
            ->select('codigo_act')
            ->orderBy('id','desc')->first();

            $num=0;
            if(is_null($verificaNum)){
                $num=1;
            }else{
                $solo_numero=explode("-",$verificaNum->codigo_act);
                $num = (int)$solo_numero[1] + 1;
            }

            $codigo_activo= 'ACT-'.str_pad($num, 5, '0', STR_PAD_LEFT).'-'.$anio;
           
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
                    $guardaLocal->valor_intereses=$r->cont_intereses;
                    $guardaLocal->valor_recargos=$r->cont_recargos;
                    $guardaLocal->valor_patente=$r->cont_pago_patente;

                    // dd($codigo_patente);
                    if($estado==1){
                        $guardaLocal->codigo=$codigo_patente;
                        if($es_activo==true){
                            $guardaLocal->codigo_act=$codigo_activo;
                        }

                    }


                    $guardaLocal->valor_impuesto_act=$r->cont_impuesto_act;
                    $guardaLocal->valor_exoneracion_act=$r->cont_exoneracion_act;
                    $guardaLocal->valor_sta_act=$r->cont_sta_act;
                    $guardaLocal->valor_intereses_act=$r->cont_intereses_act;
                    $guardaLocal->valor_recargos_act=$r->cont_recargos_act;


                    $guardaLocal->valor_activo_total=$r->cont_pago_activo_total;

                    $guardaLocal->es_patente=true;
                    $guardaLocal->es_activo=$es_activo;
                    
                    $guardaLocal->save();
                }
            }

            /*if($estado==1){
                $guardaLiquidacionPatente=$this->guardaLiquidacion($PsqlPaPatente->id,259);
                if($es_activo==true){
                    $guardaLiquidacionPatente=$this->guardaLiquidacion($PsqlPaPatente->id,17);

                }
            }*/

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

    public function guardaLiquidacion($id, $codigo){
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
            $usuario_registro = auth()->user()->name;
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
                // DB::raw("CONCAT(rl.apellidos, ' ', rl.nombres) AS representante_legal"),'co.tipo_contribuyente'
                // ,'rl.ci_ruc as cedula_rl',
                'nombre_representante_legal as representante_legal', 'ruc_representante_legal as cedula_rl','co.tipo_contribuyente',
                'co.obligado_contabilidad','es_artesano','co.archivo_ruc','co.archivo_artesano',
                DB::raw("(SELECT COUNT(*) FROM sgm_patente.pa_locales WHERE idcatastro_contr = co.id and estado='A') as cantidad_locales"),'co.fecha_inicio_actividades','co.estado_contribuyente_id','cc.nombre as clase_contri',DB::raw("EXTRACT(YEAR FROM age(e.fecha_nacimiento)) AS edad_contribuyente"))
            ->first();

            $actividad=DB::connection('pgsql')->table('sgm_patente.pa_actividad_contribuyente as act_cont')
            ->leftJoin('sgm_patente.pa_ctlg_actividades_comerciales as ac','ac.id','act_cont.Actividad_comercial_id')
            ->where('act_cont.Catastro_contribuyente_id',$id)
            ->select('ac.ciiu','ac.descripcion','act_cont.Actividad_comercial_id as idActividad','act_cont.id')
            ->where('act_cont.estado','A')
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
    public function crearTitulo1($id){
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
            if($exists_destino){
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
            // ->where('anio', $obtener_anio->year_declaracion)
            ->where('anio', date('Y'))
            ->where('estado','A')
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
                // ->where('anio',$obtener_anio->year_declaracion)
                ->where('anio', date('Y'))
                ->where('estado','A')
                ->first();
            }else{
                $calcular= DB::connection('pgsql')->table('sgm_patente.pa_base_imponible as bi')
                ->where('desde','<=',$valor)
                ->where('hasta','>=',$valor)
                ->select('dolares','imp_sobre_fraccion_exec','desde','hasta','codigo')
                // ->where('anio',$obtener_anio->year_declaracion)
                ->where('anio', date('Y'))
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

            $valor_intereses=0;
            $porcentaje_intereses=0;
            //dd($obtener_anio->year_declaracion);
            if($obtener_anio->year_declaracion < date('Y')){
                
                $intereses= DB::connection('pgsql')->table('sgm_financiero.ren_intereses as i')
                // ->where('anio', $obtener_anio->year_declaracion)
                ->where('anio', date('Y'))
                ->select('porcentaje')
                ->first();
                $porcentaje_intereses= $intereses->porcentaje;
                $valor_intereses=(($intereses->porcentaje/100) * ($sumar_total));
                $valor_intereses=number_format($valor_intereses,2);
            }

            $valor_recargo=0;
            if($obtener_anio->year_declaracion == date('Y') && date('m') >=7){
                $valor_recargo=$sumar_total*0.10;
                $valor_recargo=number_format($valor_recargo,2);
            }


            $valores=["valor_pagar"=>$valor_pagar, "valor_porc_exced"=>$valor_porc_exced, "total_porcent_pagar"=>$total_porcent_pagar, "obtener_dif"=>$obtener_dif,"valor"=>$valor,"desde"=>$desde,"sumar_total"=>$sumar_total,"aplica"=>$aplica
            ,"valor_intereses"=>number_format($valor_intereses,2),"valor_recargo"=>number_format($valor_recargo,2),"porcentaje_intereses"=>number_format($porcentaje_intereses,2)];
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

    public function datatable(Request $r)
    {
       
        if($r->ajax()){
            $listaPatente = PsqlPaPatente::whereIn('estado',[1,4])->orderby('id','desc')->get();
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
                    }else  if($listaPatente->estado == 4){
                        return '<span class="badge text-bg-success">Completada</span>';
                    }
                })
            ->addColumn('ruc', function ($listaPatente) {
                return $listaPatente->contribuyente->ruc ;
                /*return '<b>RUC:</b>'.$listaPatente->contribuyente->ruc . '<br> <b>NOMBRES:</b>' . $listaPatente->contribuyente->razon_social;*/
            })

            ->addColumn('contribuyente_name', function ($listaPatente) {
                return $listaPatente->contribuyente->razon_social;
            })
            ->addColumn('year_declaracion', function ($listaPatente) {
                // return $listaPatente->year->year_declaracion;
                return '<b>DECLARACION:</b>'.$listaPatente->year->year_declaracion . '<br> <b>BALANCE:</b>' . $listaPatente->year->year_ejercicio_fiscal;
            })
            ->addColumn('codigo', function ($listaPatente) {
                // return $listaPatente->year->year_ejercicio_fiscal;
                return ''.$listaPatente->codigo . '<br> ' . $listaPatente->codigo_act;
            })

            ->addColumn('total_pagar', function ($listaPatente) {
                // return $listaPatente->year->year_ejercicio_fiscal;
                return number_format($listaPatente->valor_patente + $listaPatente->valor_activo_total,2);
            })
            // ->addColumn('action', function ($listaPatente) {
            //     return '<a class="btn btn-primary btn-sm" href="'.route('index.patente',$listaPatente->id).'">Ver</a>
            //      ';
            // })

            // ->addColumn('action', function ($listaPatente) {
            //     return '<a class="btn btn-primary btn-sm" onclick="verPatentePdf(\''.$listaPatente->id.'\')">Ver</a>
            //     <a class="btn btn-danger btn-sm" onclick="eliminarTitulo(\''.$listaPatente->id.'\')">Dar Baja</a>';
            // })
             ->editColumn('created_at', function ($item) {
                // Formato: día-mes-año hora:minuto:segundo
                return \Carbon\Carbon::parse($item->created_at)->format('d-m-Y H:i:s');
            })
            ->addColumn('action', function ($listaPatente) {
                $disabled="";
                $btn_pdf="";
                $btn="";
                if($listaPatente->estado==1){
                    $btn='<a class="btn btn-success btn-sm" onclick="cobrarTitulo(\''.$listaPatente->id.'\')">Cobrar</a>';
                    $disabled="disabled";

                    $btn_pdf='<button class="btn btn-primary btn-sm" onclick="verpdf(\''.$listaPatente->id.'\')" disabled>Titulo</button>';

                }else if($listaPatente->estado==4){
                    $btn='<a class="btn btn-danger btn-sm" onclick="eliminarTitulo(\''.$listaPatente->id.'\')">Dar Baja</a>';
                    // $btn_pdf=' <a class="btn btn-primary btn-sm" onclick="verpdf(\''.$listaimpuesto->documento_firmado.'\')" >Titulo</a>';

                    $btn_pdf=' <a class="btn btn-primary btn-sm" onclick="verPatentePdf(\''.$listaPatente->id.'\')" >Titulo</a>';
                }
                return $btn_pdf.' '.$btn;
            })
            ->rawColumns(['action','estado','ruc','year_declaracion','codigo'])
            ->make(true);
        }
    }

    public function bajaTituloPatente(Request $request){
        try {

            $baja=PsqlPaPatente::find($request->id_impuesto);
            if (!$baja) {
                return response()->json([
                    "error" => true,
                    "mensaje" => "No se encontró el registro especificado.",
                ]);
            }
           
            // Convierte ambas fechas a formato Y-m-d (sin hora)
            $fechaRegistro = date('Y-m-d', strtotime($baja->created_at));
            $fechaActual   = date('Y-m-d');

            if ($fechaRegistro < $fechaActual) {
                return response()->json([
                    "error"   => true,
                    "mensaje" => "Solo se permite dar de baja el titulo el mismo dia de la emision.",
                ]);
            }

            $baja->observacion_baja=$request->motivo_baja;
            $baja->idusuariobaja=auth()->user()->id;
            $baja->fecha_baja=date('Y-m-d H:i:s');
            $baja->estado=5;
            $baja->save();

            return ["mensaje"=>"Informacion eliminada exitosamente", "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function anularCobro($id){
        try {
            $anularCobro= PsqlPaPatente::find($id);
            if($anularCobro->estado==5){
                return ["mensaje"=>"La informacion ya ha sido dada de baja y no se puede eliminar", "error"=>true];   
            }else if($anularCobro->estado==4){
                return ["mensaje"=>"La informacion ya sido cobrada y no se puede anular", "error"=>true];
            }else if($anularCobro->estado==3){
                return ["mensaje"=>"La informacion ya sido eliminada", "error"=>true];
            }
            $anularCobro->estado=3;
            $anularCobro->idusuario_anula=auth()->user()->id;
            $anularCobro->fecha_anula=date('Y-m-d H:i:s');
            $anularCobro->save();

            return ["mensaje"=>"Registro eliminado exitosamente", "error"=>false];

        } catch (Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }
    public function realizarCobro($id){
        try {

            $realizarCobro= PsqlPaPatente::find($id);
            
            if (!$realizarCobro) {
                return response()->json([
                    "error" => true,
                    "mensaje" => "No se encontró el registro especificado.",
                ]);
            }
           
            // Convierte ambas fechas a formato Y-m-d (sin hora)
            $fechaRegistro = date('Y-m-d', strtotime($realizarCobro->created_at));
            $fechaActual   = date('Y-m-d');

            if ($fechaRegistro < $fechaActual) {
                return response()->json([
                    "error"   => true,
                    "mensaje" => "Solo se permite el cobro el mismo dia de la emision.",
                ]);
            }
                        
            if($realizarCobro->estado==3){
                return ["mensaje"=>"La informacion ha sido eliminada y no se puede cobrar", "error"=>true];   
            }else if($realizarCobro->estado==4){
                return ["mensaje"=>"La informacion ya sido cobrada y no se puede volver a cobrar", "error"=>true];
            }
            $realizarCobro->estado=4;
            $realizarCobro->id_usuario_cobra=auth()->user()->id;
            $realizarCobro->fecha_cobro=date('Y-m-d H:i:s');
            $realizarCobro->save();

            // $generarDocumento=$this->pdfTransito($id,'');
            //queme la G para que no firme electronicamente
            $generarDocumento=$this->crearTitulo($id);
          

            if($generarDocumento['error']==true){
                return ["mensaje"=>$generarDocumento['mensaje'], "error"=>true];
            }
            
            //change LOCALES
            return ["mensaje"=>"Cobro registrado exitosamente", "error"=>false, 'pdf'=>$generarDocumento['pdf']];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

   public function guardaContribuyente(Request $request){

        DB::beginTransaction();
        try {

            $separaNombre = $this->separarNombreApellido($request->contribuyente);

            $ci_ruc = $request->cmb_ruc;

            $verificaExiste=PsqlCatastroContribuyente::where('ruc', $ci_ruc)
                ->orWhere('ruc', substr($ci_ruc, 0, 10))
                ->first();
            if(!is_null($verificaExiste)){
                if($verificaExiste->estado_contribuyente_id==1){
                    return [
                        'error' => true,
                        'mensaje' => 'El contribuyente ingresado ya existe '
                    ];
                }

                $validaContribuyente = PsqlEnte::where('ci_ruc', $ci_ruc)
                ->orWhere('ci_ruc', substr($ci_ruc, 0, 10))
                ->first();

                $archivo_ruc = $request->doc_ruc;
                $extension = pathinfo($archivo_ruc->getClientOriginalName(), PATHINFO_EXTENSION);
                $nombreDocumento = "ruc_" . $request->cmb_ruc . "-" . date('Ymd') . '-' . time();

                $es_artesano=0;
                if ($request->has('es_artesano')) {
                // if(isset($request->es_artesano)){
                    $archivo_artesano = $request->doc_artesano;
                    $extension_artesano = pathinfo($archivo_artesano->getClientOriginalName(), PATHINFO_EXTENSION);
                    $nombreDocumentoArtesano = "docArtesano_" . $request->cmb_ruc . "-" . date('Ymd') . '-' . time();
                    $es_artesano=1;
                }

                $guardaContribuyente = new PsqlCatastroContribuyente();
                $guardaContribuyente->ruc = $request->cmb_ruc;
                $guardaContribuyente->razon_social = $separaNombre['primer_nombre'] . " " . $separaNombre['segundo_nombre'] . " " . $separaNombre['apellido_paterno'] . " " . $separaNombre['apellido_materno'];
                $guardaContribuyente->estado_contribuyente_id = 1;
                $guardaContribuyente->fecha_inicio_actividades = $request->fecha_inicio_act;
                $guardaContribuyente->fecha_actualizacion_actividades = $request->fecha_actualizacion_act;
                $guardaContribuyente->fecha_reinicio_actividades = $request->fecha_reinicio_act;
                $guardaContribuyente->fecha_suspension_definitiva = $request->fecha_suspension_act;
                $guardaContribuyente->tipo_contribuyente = $request->tipo_persona_new;
                $guardaContribuyente->provincia_id = $request->provincia;
                $guardaContribuyente->canton_id = $request->canton_id;
                $guardaContribuyente->parroquia_id = $request->parroquia_id_;
                $guardaContribuyente->direccion = $request->direccion;
                $guardaContribuyente->correo_1 = $request->correo;
                $guardaContribuyente->telefono = $request->telefono;
                $guardaContribuyente->usuario_ingreso = auth()->user()->id;
                $guardaContribuyente->propietario_id = $validaContribuyente->id;
                if($guardaContribuyente->tipo_contribuyente==1){
                    $guardaContribuyente->ruc_representante_legal= $request->cmb_ruc;
                    $guardaContribuyente->nombre_representante_legal= $guardaContribuyente->razon_social;
                }else{
                    $guardaContribuyente->ruc_representante_legal= $request->cmb_ruc_rep;
                    $guardaContribuyente->nombre_representante_legal= $request->representante;
                }

                $guardaContribuyente->clase_contribuyente_id = $request->clase_contribuyente_id;

                $guardaContribuyente->archivo_ruc = $nombreDocumento . "." . $extension;
                if($es_artesano==1){
                    $guardaContribuyente->archivo_artesano = $nombreDocumentoArtesano . "." . $extension_artesano;
                    $guardaContribuyente->es_artesano = true;
                }

                if ($request->has('obligado_contabilidad')) {
                    $guardaContribuyente->obligado_contabilidad =true;
                }

                if ($guardaContribuyente->save()) {
                    Storage::disk('disksDocumentoRenta')->put($nombreDocumento . "." . $extension, \File::get($archivo_ruc));
                    if($es_artesano==1){
                        Storage::disk('disksDocumentoRenta')->put($nombreDocumentoArtesano . "." . $extension_artesano, \File::get($archivo_artesano));
                    }
                }

                DB::commit();
                return [
                    'error' => false,
                    'mensaje' => 'Contribuyente guardado correctamente.'
                ];
            }


            $validaContribuyente = PsqlEnte::where('ci_ruc', $ci_ruc)
                ->orWhere('ci_ruc', substr($ci_ruc, 0, 10))
                ->first();

            if (!is_null($validaContribuyente)) {
                if ($validaContribuyente->estado === "A") {
                    goto guardar_contribuyente;
                }

                // Actualizar datos si no está activo
                $validaContribuyente->ci_ruc = $ci_ruc;
                $validaContribuyente->nombres = $separaNombre['primer_nombre'] . " " . $separaNombre['segundo_nombre'];
                $validaContribuyente->apellidos = $separaNombre['apellido_paterno'] . " " . $separaNombre['apellido_materno'];
                $validaContribuyente->razon_social = $separaNombre['primer_nombre'] . " " . $separaNombre['segundo_nombre'];
                $validaContribuyente->nombre_comercial = $separaNombre['apellido_paterno'] . " " . $separaNombre['apellido_materno'];
                $validaContribuyente->es_persona = true;
                $validaContribuyente->direccion = strtoupper(str_replace(' ', '', $request->direccion));
                $validaContribuyente->fecha_nacimiento = $request->fecha_nacimiento;
                $validaContribuyente->tipo_documento = 606;
                $validaContribuyente->save();

                $this->storeTlfEmail($request->telefono, $request->correo, $validaContribuyente->id);
                goto guardar_contribuyente;
            }

            // Crear nuevo ente
            $ente = new PsqlEnte();
            $ente->ci_ruc = $ci_ruc;
            $ente->nombres = $separaNombre['primer_nombre'] . " " . $separaNombre['segundo_nombre'];
            $ente->apellidos = $separaNombre['apellido_paterno'] . " " . $separaNombre['apellido_materno'];
            $ente->razon_social = $ente->nombres;
            $ente->nombre_comercial = $ente->apellidos;
            $ente->es_persona = true;
            $ente->direccion = strtoupper(str_replace(' ', '', $request->direccion));
            $ente->fecha_nacimiento = $request->fecha_nacimiento;
            $ente->tipo_documento = 606;
            if ($request->has('obligado_contabilidad')) {
                $ente->lleva_contabilidad =true;
            }

            $ente->save();

            $this->storeTlfEmail($request->telefono, $request->correo, $ente->id);
            $validaContribuyente = $ente;

            guardar_contribuyente:

            $archivo_ruc = $request->doc_ruc;
            $extension = pathinfo($archivo_ruc->getClientOriginalName(), PATHINFO_EXTENSION);
            $nombreDocumento = "ruc_" . $request->cmb_ruc . "-" . date('Ymd') . '-' . time();

            $es_artesano=0;
            if ($request->has('es_artesano')) {
            // if(isset($request->es_artesano)){
                $archivo_artesano = $request->doc_artesano;
                $extension_artesano = pathinfo($archivo_artesano->getClientOriginalName(), PATHINFO_EXTENSION);
                $nombreDocumentoArtesano = "docArtesano_" . $request->cmb_ruc . "-" . date('Ymd') . '-' . time();
                $es_artesano=1;
            }

            $guardaContribuyente = new PsqlCatastroContribuyente();
            $guardaContribuyente->ruc = $request->cmb_ruc;
            $guardaContribuyente->razon_social = $separaNombre['primer_nombre'] . " " . $separaNombre['segundo_nombre'] . " " . $separaNombre['apellido_paterno'] . " " . $separaNombre['apellido_materno'];
            $guardaContribuyente->estado_contribuyente_id = 1;
            $guardaContribuyente->fecha_inicio_actividades = $request->fecha_inicio_act;
            $guardaContribuyente->fecha_actualizacion_actividades = $request->fecha_actualizacion_act;
            $guardaContribuyente->fecha_reinicio_actividades = $request->fecha_reinicio_act;
            $guardaContribuyente->fecha_suspension_definitiva = $request->fecha_suspension_act;
            $guardaContribuyente->tipo_contribuyente = $request->tipo_persona_new;
            $guardaContribuyente->provincia_id = $request->provincia;
            $guardaContribuyente->canton_id = $request->canton_id;
            $guardaContribuyente->parroquia_id = $request->parroquia_id_;
            $guardaContribuyente->direccion = $request->direccion;
            $guardaContribuyente->correo_1 = $request->correo;
            $guardaContribuyente->telefono = $request->telefono;
            $guardaContribuyente->usuario_ingreso = auth()->user()->id;
            $guardaContribuyente->propietario_id = $validaContribuyente->id;
            if($guardaContribuyente->tipo_contribuyente==1){
                $guardaContribuyente->ruc_representante_legal= $request->cmb_ruc;
                $guardaContribuyente->nombre_representante_legal= $guardaContribuyente->razon_social;
            }else{
                $guardaContribuyente->ruc_representante_legal= $request->cmb_ruc_rep;
                $guardaContribuyente->nombre_representante_legal= $request->representante;
            }

            $guardaContribuyente->clase_contribuyente_id = $request->clase_contribuyente_id;
            $guardaContribuyente->archivo_ruc = $nombreDocumento . "." . $extension;
            if($es_artesano==1){
                $guardaContribuyente->archivo_artesano = $nombreDocumentoArtesano . "." . $extension_artesano;
                $guardaContribuyente->es_artesano = true;
            }
            if ($request->has('obligado_contabilidad')) {
                $guardaContribuyente->obligado_contabilidad =true;
            }

            if ($guardaContribuyente->save()) {
                Storage::disk('disksDocumentoRenta')->put($nombreDocumento . "." . $extension, \File::get($archivo_ruc));
                if($es_artesano==1){
                    Storage::disk('disksDocumentoRenta')->put($nombreDocumentoArtesano . "." . $extension_artesano, \File::get($archivo_artesano));
                }
            }

            DB::commit();
            return [
                'error' => false,
                'mensaje' => 'Contribuyente guardado correctamente.'
            ];

        } catch (\Exception $e) {
            DB::rollback();
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al guardar el contribuyente: ' . $e->getMessage()
            ];
        }
    }


    public function actualizaContribuyente(Request $request){
        DB::beginTransaction();
        try {

            $verificaExiste=PsqlCatastroContribuyente::Where('id', $request->contribuyente_id)
            ->first();

            if(!is_null($verificaExiste)){

                $archivo_ruc = $request->doc_ruc;
                $archivo_artesano = $request->doc_artesano;

                $es_artesano=0;
                if ($request->has('es_artesano')) {
                    $es_artesano=1;
                }

                $validaContribuyente = PsqlEnte::where('id', $verificaExiste->propietario_id)
                ->first();

                if (!is_null($validaContribuyente)) {

                    $validaContribuyente->es_persona = true;
                    $validaContribuyente->direccion = strtoupper(str_replace(' ', '', $request->direccion));
                    $validaContribuyente->fecha_nacimiento = $request->fecha_nacimiento;
                    $validaContribuyente->tipo_documento = 606;
                    $validaContribuyente->save();
                    $this->storeTlfEmail($request->telefono, $request->correo, $validaContribuyente->id);

                }

                // $verificaExiste->ruc = $request->cmb_ruc;
                $verificaExiste->razon_social = $request->contribuyente;
                $verificaExiste->estado_contribuyente_id = 1;
                $verificaExiste->fecha_inicio_actividades = $request->fecha_inicio_act;
                $verificaExiste->fecha_actualizacion_actividades = $request->fecha_actualizacion_act;
                $verificaExiste->fecha_reinicio_actividades = $request->fecha_reinicio_act;
                $verificaExiste->fecha_suspension_definitiva = $request->fecha_suspension_act;
                $verificaExiste->tipo_contribuyente = $request->tipo_persona_new;
                $verificaExiste->provincia_id = $request->provincia;
                $verificaExiste->canton_id = $request->canton_id;
                $verificaExiste->parroquia_id = $request->parroquia_id_;
                $verificaExiste->direccion = $request->direccion;
                $verificaExiste->correo_1 = $request->correo;
                $verificaExiste->telefono = $request->telefono;
                $verificaExiste->usuario_ingreso = auth()->user()->id;
                // $guardaContribuyente->propietario_id = $validaContribuyente->id;
                if($verificaExiste->tipo_contribuyente==1){
                    $verificaExiste->ruc_representante_legal= $request->cmb_ruc;
                    $verificaExiste->nombre_representante_legal= $verificaExiste->razon_social;
                }else{
                    $verificaExiste->ruc_representante_legal= $request->cmb_ruc_rep;
                    $verificaExiste->nombre_representante_legal= $request->representante;
                }

                if(!is_null($archivo_ruc)){
                    $extension = pathinfo($archivo_ruc->getClientOriginalName(), PATHINFO_EXTENSION);
                    $nombreDocumento = "ruc_" . $request->cmb_ruc . "-" . date('Ymd') . '-' . time();
                    $verificaExiste->archivo_ruc = $nombreDocumento . "." . $extension;
                }
                $verificaExiste->clase_contribuyente_id = $request->clase_contribuyente_id;


                if($es_artesano==1){
                    if(!is_null($archivo_artesano)){
                        $extension_artesano = pathinfo($archivo_artesano->getClientOriginalName(), PATHINFO_EXTENSION);
                        $nombreDocumentoArtesano = "docArtesano_" . $request->cmb_ruc . "-" . date('Ymd') . '-' . time();
                        $verificaExiste->archivo_artesano = $nombreDocumentoArtesano . "." . $extension_artesano;
                    }
                    $verificaExiste->es_artesano = true;
                }

                if ($request->has('obligado_contabilidad')) {
                    $verificaExiste->obligado_contabilidad =true;
                }

                if ($verificaExiste->save()) {
                    if(!is_null($archivo_ruc)){
                        Storage::disk('disksDocumentoRenta')->put($nombreDocumento . "." . $extension, \File::get($archivo_ruc));
                    }
                    if(!is_null($archivo_artesano)){
                        Storage::disk('disksDocumentoRenta')->put($nombreDocumentoArtesano . "." . $extension_artesano, \File::get($archivo_artesano));
                    }
                }

                DB::commit();
                return [
                    'error' => false,
                    'mensaje' => 'Contribuyente guardado correctamente.'
                ];
            }



            DB::commit();
            return [
                'error' => false,
                'mensaje' => 'Contribuyente guardado correctamente.'
            ];

        } catch (\Exception $e) {
            DB::rollback();
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al guardar el contribuyente: ' . $e->getMessage()
            ];
        }
    }


    public function storeTlfEmail($telefono, $correo, $id){
        try{
            $verificaTlfo=PsqlEnteTelefono::where('ente',$id)->delete();
            $verificaCorreo=PsqlEnteCorreo::where('ente',$id)->delete();

            $guardaTlfo=new PsqlEnteTelefono();
            $guardaTlfo->telefono=$telefono;
            $guardaTlfo->ente=$id;
            $guardaTlfo->save();

            $guardaCorreo=new PsqlEnteCorreo();
            $guardaCorreo->email=$correo;
            $guardaCorreo->ente=$id;
            $guardaCorreo->save();

            return ["error"=>false];
        } catch (\Exception $e) {

            return ["error"=>true];
        }
    }

    public function separarNombreApellido($nombreCompleto) {
        $nombreCompleto = strtoupper(trim($nombreCompleto));
        $partes = preg_split('/\s+/', $nombreCompleto);

        $preposiciones = ['DE', 'DEL', 'LA', 'LOS', 'LAS'];

        $apellido_paterno = '';
        $apellido_materno = '';
        $primer_nombre = '';
        $segundo_nombre = '';

        $i = 0;

        // Capturar apellido paterno (considerando preposiciones)
        $apellido_paterno .= $partes[$i++];
        while (isset($partes[$i]) && in_array($partes[$i], $preposiciones)) {
            $apellido_paterno .= ' ' . $partes[$i++];
            if (isset($partes[$i])) {
                $apellido_paterno .= ' ' . $partes[$i++];
            }
        }

        // Capturar apellido materno (igual)
        if (isset($partes[$i])) {
            $apellido_materno .= $partes[$i++];
            while (isset($partes[$i]) && in_array($partes[$i], $preposiciones)) {
                $apellido_materno .= ' ' . $partes[$i++];
                if (isset($partes[$i])) {
                    $apellido_materno .= ' ' . $partes[$i++];
                }
            }
        }

        // Lo que queda son los nombres
        $resto = array_slice($partes, $i);
        if (count($resto) > 0) {
            $primer_nombre = array_shift($resto);
            $segundo_nombre = implode(' ', $resto); // todo lo demás como segundo nombre
        }

        return [
            'apellido_paterno' => trim($apellido_paterno),
            'apellido_materno' => trim($apellido_materno),
            'primer_nombre'    => trim($primer_nombre),
            'segundo_nombre'   => trim($segundo_nombre),
        ];
    }

    public function crearTitulo($id){

        $patente=DB::connection('pgsql')->table('sgm_patente.pa_patente as pa')
        ->leftJoin('sgm_patente.pa_catastro_contribuyente as co','pa.Contribuyente_id','co.id')
        ->leftJoin('sgm_app.cat_ente as e','e.id','co.propietario_id')
        ->leftJoin('sgm_patente.pa_clase_contribuyente as cc','cc.id','co.clase_contribuyente_id')
        ->leftJoin('sgm_patente.pa_locales as lo','pa.id_pa_local','lo.id')
        ->leftJoin('sgm_app.cat_canton as ca','lo.canton_id','ca.id')
        ->leftJoin('sgm_patente.year_declaracion as y','pa.year_declaracion','y.id')
        ->where('pa.id',$id)
        ->select('actividad_descripcion','co.ruc','co.razon_social','co.obligado_contabilidad','codigo'
            ,'cc.nombre as regimen','valor_sta','valor_exoneracion','valor_impuesto','y.year_ejercicio_fiscal',
            'codigo_act','valor_impuesto_act','valor_exoneracion_act','valor_sta_act','valor_activo_total',
            'pa.valor_intereses','pa.valor_recargos','pa.valor_intereses_act','pa.valor_recargos_act',
            DB::raw("CONCAT(lo.calle_principal, ' y ', lo.calle_secundaria) AS calle"),'pa.fecha_declaracion','lo.local_propio',
            'ca.nombre as canton','pa.valor_patente',DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS contribuyente"),'pa.estado',
            'pa.valor_activo_total','es_activo')
        ->get();

        foreach($patente as $key=>&$value){
            $actividades=DB::connection('pgsql')->table('sgm_patente.pa_patente_actividad_contr as pa_act')
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

         $fecha_hoy=date('Y-m-d');
        setlocale(LC_TIME, 'es_ES.UTF-8', 'es_ES@euro', 'es_ES', 'esp');
        $fecha_timestamp_hoy = strtotime($fecha_hoy);
        $fecha_formateada_hoy = strftime("%d de %B del %Y", $fecha_timestamp_hoy);

        $simulacion=0;
        if($patente[0]->estado==2){
            if(!is_null($patente[0]->valor_activo_total)){
                $nombrePDF="simulacion_patente_activo_total.pdf";
            }else{
                $nombrePDF="simulacion_patente.pdf";
            }
            $simulacion=1;
        }else{
            if(!is_null($patente[0]->valor_activo_total)){
                $nombrePDF="emision_patente_activo_total.pdf";
            }else{
                $nombrePDF="emision_patente.pdf";
            }

        }

        if($simulacion==1){
            $pdf=\PDF::LoadView('reportes.reporte_patente',['patente'=>$patente[0], "fecha_formateada"=>$fecha_formateada] );
            $pdf->setPaper("A4", "portrait");

        }else{
            $pdf=\PDF::LoadView('reportes.reportePatente',['patente'=>$patente[0], "fecha_formateada"=>$fecha_formateada, "fecha_formateada_hoy"=>$fecha_formateada_hoy] );
            $pdf->setPaper("A4", "portrait");
        }
        //return $pdf->stream("aa.pdf");
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


        // $pdf=\PDF::LoadView('reportes.reportePatente',[] );
        // $pdf->setPaper("A4", "portrait");

       // return $pdf->stream("aa.pdf");
    }

    public function tablaRango(){
        try {
        
            $info=DB::connection('pgsql')->table('sgm_patente.pa_base_imponible')
            ->where('estado','A')
            ->where('anio',date('Y'))
            ->orderBy('desde','asc')
            ->get();

            return ["resultado"=>$info, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function guardarRango(Request $request){
        try {

            $valida=BaseImponiblePatente::where('desde',$request->desde_base)
            ->where('hasta',$request->hasta_base)
            ->where('dolares',$request->valor_base)
            ->where('imp_sobre_fraccion_exec',$request->impuesto_fracion)
            ->where('estado','A')
            ->first();
            if(!is_null($valida)){
                return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
            }
            // dd($valida);

            $guardaRango=new BaseImponiblePatente();
            $guardaRango->desde=$request->desde_base;
            $guardaRango->hasta=$request->hasta_base;
            $guardaRango->dolares=$request->valor_base;
            $guardaRango->imp_sobre_fraccion_exec=$request->impuesto_fracion;
            $guardaRango->estado='A';
            $guardaRango->anio=date('Y');
            $guardaRango->save();
            
            return ["mensaje"=>"Informacion Guardada exitosamente", "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function actualizarRango(Request $request, $id){
        try {

            $valida=BaseImponiblePatente::where('desde',$request->desde_base)
            ->where('hasta',$request->hasta_base)
            ->where('dolares',$request->valor_base)
            ->where('imp_sobre_fraccion_exec',$request->impuesto_fracion)
            ->where('estado','A')
            ->where('id','!=',$id)
            ->first();
            if(!is_null($valida)){
                return ["mensaje"=>"La informacion ingresada ya existe ", "error"=>true];
            }

            $guardaRango= BaseImponiblePatente::find($id);
            $guardaRango->desde=$request->desde_base;
            $guardaRango->hasta=$request->hasta_base;
            $guardaRango->dolares=$request->valor_base;
            $guardaRango->imp_sobre_fraccion_exec=$request->impuesto_fracion;
            $guardaRango->estado='A';
            $guardaRango->save();
            
            return ["mensaje"=>"Informacion actualizada exitosamente", "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function vistaReportePatente(){
        // Gate::authorize('reporte_patente', PsqlPaPatente::class);
        if(!Auth()->user()->hasPermissionTo('Reporteria Patente'))
        {
            abort(403, 'No tienes acceso a esta seccion.');
        }
        return view('rentas.consultaPagosPatente');
    }

    public function consultarPagos(Request $request){
        try{
            $desde=$request->filtroDesde;
            $hasta=$request->filtroHasta;
            $tipo=$request->filtroTipo;

            $desde=$desde." 00:00:00.000";
            $hasta=$hasta." 23:59:59.000";

            $consultar= DB::connection('pgsql')->table('sgm_patente.pa_patente as i')
            // ->leftJoin('sgm_transito.vehiculo as v', 'v.id', '=', 'i.vehiculo_id')
            // ->leftJoin('sgm_transito.clase_tipo_vehiculo as cv', 'cv.id', '=', 'v.tipo_clase_id')
            // ->leftJoin('sgm_transito.marca_vehiculo as mv', 'mv.id', '=', 'v.marca_id')
            // ->leftJoin('sgm_transito.cat_ente as en', 'en.id', '=', 'i.cat_ente_id')
            ->leftJoin('sgm_patente.pa_catastro_contribuyente as en', 'en.id', '=', 'i.Contribuyente_id')
            
            ->whereBetween('i.created_at', [$desde, $hasta])
            ->where('i.estado',1)
            ->select('en.razon_social','en.ruc as identificacion_propietario' ,'i.created_at','i.codigo', 'i.codigo_act','i.valor_activo_total','i.valor_patente','i.idusuario_registra as usuario','i.created_at','i.id as identificador')
            ->get();

            foreach($consultar as $key=> $data){
                $usuarioRegistra=DB::connection('mysql')->table('users as u')
                ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                ->where('u.id',$data->usuario)
                ->select('p.nombres','p.apellidos','p.cedula')
                ->first();
                if(is_null($usuarioRegistra)){
                    $consultar[$key]->nombre_usuario=$data->usuario;
                }else{
                    $consultar[$key]->nombre_usuario=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;
                    $consultar[$key]->cedula_usuario=$usuarioRegistra->cedula;
                }

             


            }

            return ['data'=>$consultar,'error'=>false];

        } catch (\Throwable $th) {
            return ['mensaje'=>'Ocurrió un error '.$th,'error'=>true];
        }
    }

    public function ReporteTransito(Request $request){
        // dd("s");
        try{
            set_time_limit(0);
            ini_set("memory_limit",-1);
            ini_set('max_execution_time', 0);

            $consultaInfo=$this->consultarPagos($request);
            // dd($consultaInfo);

            if($consultaInfo['error']==true){
                return (['mensaje'=>'Ocurrió un error al consultar los datos,intentelo más tarde','error'=>true]);
            }

            $nombrePDF="reporte_pago_patente.pdf";
            // dd($nombrePDF);

            $pdf=\PDF::LoadView('reportes.reporte_pago_patente',['datos'=>$consultaInfo['data'],'desde'=>$request->filtroDesde,'hasta'=>$request->filtroHasta,'tipo'=>$request->filtroTipo ]);
            $pdf->setPaper("A4", "portrait");
            $estadoarch = $pdf->stream();

            //lo guardamos en el disco temporal
            \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
            $exists_destino = \Storage::disk('public')->exists($nombrePDF);
            if($exists_destino){
                return response()->json([
                    'error'=>false,
                    'pdf'=>$nombrePDF
                ]);
            }else{
                return response()->json([
                    'error'=>true,
                    'mensaje'=>'No se pudo crear el documento'
                ]);
            }

        } catch (\Throwable $th) {
            dd($th);
            return ['mensaje'=>'Ocurrió un error '.$th,'error'=>true];
        }

    }

    public function detalle($id){
        try {

            $baja=PsqlPaPatente::with('contribuyente','local','actividades')->where('id',$id)->get();

            return ["resultado"=>$baja, "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

    public function guardaParroquia(Request $request){
        try {

            $existe=PsqlParroquia::where('id_canton',$request->canton_id_selecc)
            ->where('descripcion',$request->parroqui_cont)
            ->first();
            if(!is_null($existe)){
                return ["mensaje"=>"Ya existe esa parroquia, para ese canton", "error"=>true];

            }
            $guardaParroquia=new PsqlParroquia();
            $guardaParroquia->codigo_parroquia=$request->parroquia_contr_codigo;
            $guardaParroquia->descripcion=$request->parroqui_cont;
            $guardaParroquia->id_canton=$request->canton_id_selecc;
            $guardaParroquia->tipo=$request->urbano_rural;
            $guardaParroquia->save();

            return ["mensaje"=>'Parroquia agregada exitosamente', "error"=>false];

        } catch (\Exception $e) {
            return ["mensaje"=>"Ocurrio un error intentelo mas tarde ".$e, "error"=>true];

        }
    }

}
