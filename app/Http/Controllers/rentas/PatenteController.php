<?php

namespace App\Http\Controllers\rentas;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlYearDeclaracion;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\Validator;
use App\Models\PsqlPaPatente;
use Datatables;

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
    public function store(Request $r)
    {
        $attributes = [
            'catastro_id' => 'contribuyente',
            'actividades.*.id' => 'actividad comercial',
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

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        //
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
                return $listaPatente->contribuyente->ruc;
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
            ->addColumn('action', function ($listaPatente) {
                return '<a class="btn btn-primary btn-sm" href="'.route('index.patente',$listaPatente->id).'">Ver</a> ';
            })
            ->rawColumns(['action','estado'])
            ->make(true);
        }
    }
}
