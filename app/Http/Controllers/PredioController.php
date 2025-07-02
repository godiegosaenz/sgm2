<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;
use Illuminate\Support\Collection;
use App\Models\PredioRural;
use App\Models\CertificadoSolvencia;
use App\Models\PsqlEnte;
// use QrCode;
use Endroid\QrCode\QrCode;
use Endroid\QrCode\Builder\Builder;
use Endroid\QrCode\Writer\PngWriter;
use Endroid\QrCode\Color\Color;
class PredioController extends Controller
{
    public function index(){
        return view('nodeudor.index');
    }

    public function buscarDeudas($cedula){
        $id_predio=DB::connection('pgsql')->table('sgm_app.cat_ente as en')
        ->leftJoin('sgm_app.cat_predio_propietario as pre_pro', 'pre_pro.ente', '=', 'en.id')
        ->where('en.ci_ruc', $cedula)
        ->where('pre_pro.estado','A')
        ->pluck('pre_pro.predio')  // Devuelve una colección con solo los valores del campo 'id'
        ->toArray(); 
       
        $liquidacion = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as liq')
        ->leftJoin('sgm_app.cat_ente as en', 'en.id', '=', 'liq.comprador')
        ->leftJoin('sgm_app.cat_predio as pre', 'pre.id', '=', 'liq.predio')
        ->leftJoin('sgm_app.cat_ciudadela as cdla', 'cdla.id', '=', 'pre.ciudadela')
        ->select(
            'liq.num_liquidacion',
            'liq.anio',
            'liq.avaluo_municipal',
            'liq.avaluo_construccion',
            'liq.avaluo_solar',
            'liq.fecha_ingreso',
            'liq.total_pago',
            'pre.num_predio',
            'saldo',
            'liq.id',
            'liq.anio',
            'en.direccion',
            DB::raw("
                CASE
                    WHEN liq.comprador IS NULL THEN liq.nombre_comprador
                    ELSE CASE en.es_persona
                        WHEN TRUE THEN COALESCE(en.apellidos, '') || ' ' || COALESCE(en.nombres, '')
                        ELSE COALESCE(en.razon_social, '')
                    END
                END AS nombres
            "),
            DB::raw("
                CASE
                    WHEN liq.comprador IS NULL THEN 'S/N'
                    ELSE (SELECT ci_ruc FROM sgm_app.cat_ente WHERE cat_ente.id = liq.comprador)
                END AS cedula
            "),
            DB::raw("cdla.nombre || ' MZ: ' || pre.urb_mz || ' SL: ' || pre.urb_solarnew AS direccion1"),
            'pre.clave_cat as cod_predial',
            DB::raw("(SELECT razon_social FROM sgm_application.empresa) AS empresa"),
            DB::raw("
                (
                    SELECT
                        CASE
                            WHEN (liq.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
                                (ROUND(d.valor * (
                                    SELECT porcentaje
                                    FROM sgm_app.ctlg_descuento_emision
                                    WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                    AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)) / 100, 2) * (-1))
                            WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN
                                (ROUND((d.valor * 0.1), 2) + ROUND((liq.saldo) *
                                (SELECT ROUND((porcentaje / 100), 2) FROM sgm_financiero.ren_intereses i WHERE i.anio = liq.anio), 2))
                            ELSE
                                ROUND((d.valor * 0.1), 2)
                        END AS valor_complemento
                    FROM sgm_financiero.ren_det_liquidacion d
                    WHERE d.liquidacion = liq.id AND d.rubro = 2
                ) AS valor_complemento
            "),
            'liq.id_liquidacion'
        )
        ->whereIN('liq.predio', $id_predio)
        ->where('estado_liquidacion',2)
        ->get();

        $total_urbano=0;
        $array_total=[];
        foreach($liquidacion as $data){
            $total_urbano=$total_urbano +($data->total_pago +$data->valor_complemento);
            
        }
        if(sizeof($liquidacion)>0){
            array_push($array_total,["tipo"=>"Predio Urbano", "total"=>$total_urbano, "nombres"=>$liquidacion[0]->nombres]);
        }
            

        $liquidacionRural=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
        ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
        ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
        ->select('cv.Pre_CodigoCatastral','cv.CarVe_FechaEmision','cv.CarVe_NumTitulo','cv.CarVe_CI'
        ,'cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','cv.CarVe_Nombres','cv.CarVe_ValorEmitido'
        ,'cv.CarVe_TasaAdministrativa')
        ->where('CarVe_CI', '=', $cedula)
        ->whereIn('cv.CarVe_Estado',['E'])
        // ->where('Pre_Tipo','Rural')
        ->orderby('CarVe_NumTitulo','desc')
        ->get();
        // dd($liquidacionRural);

        $titulos = DB::connection('sqlsrv')->table('TITULOS_PREDIO as t')
        ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 't.Titpr_RUC_CI')
        ->leftJoin('PREDIO as p', 'p.Pre_CodigoCatastral', '=', 't.Pre_CodigoCatastral')
        ->select('t.Pre_CodigoCatastral as clave_cat','t.TitPr_FechaEmision as CarVe_FechaEmision',
                't.TitPr_NumTitulo as CarVe_NumTitulo','t.Titpr_RUC_CI as CarVe_CI','t.TitPr_Estado as CarVe_Estado',
                'c.Ciu_Apellidos','c.Ciu_Nombres','t.TitPr_Nombres as CarVe_Nombres',
                't.TitPr_ValorEmitido as CarVe_ValorEmitido','t.TitPr_TasaAdministrativa as CarVe_TasaAdministrativa')
        ->where('t.Titpr_RUC_CI', '=', $cedula)
        ->where('t.TitPr_Estado', '=', 'E') // o el estado correspondiente
        ->get();

        $liquidacionRural = $liquidacionRural->merge($titulos);

        foreach($liquidacionRural as $key=> $data){

            $interesMora=0;
            $anio=explode("-",$data->CarVe_NumTitulo);
            if($anio[0]!=date('Y')){
                $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                ->where('IntMo_Año',$anio)
                ->select('IntMo_Valor')
                ->first();
                $interesMora=$consultaInteresMora->IntMo_Valor;
            }

            $valor=(($interesMora/100) * ($data->CarVe_ValorEmitido - $data->CarVe_TasaAdministrativa));
            
            $valor=number_format($valor,2);

            $liquidacionRural[$key]->porcentaje_intereses=$interesMora;
            $liquidacionRural[$key]->intereses=$valor;

            $total_pago=$valor +$data->CarVe_ValorEmitido;
            $liquidacionRural[$key]->total_pagar=number_format($total_pago,2);
        }
        $total_rural=0;
        foreach($liquidacionRural as $data){
            $total_rural=$total_rural + $data->total_pagar;
            
        }
        if(sizeof($liquidacionRural)>0){
            array_push($array_total,["tipo"=>"Predio Rural", "total"=>number_format($total_rural,2), "nombres"=>$liquidacionRural[0]->Ciu_Nombres." ".$liquidacionRural[0]->Ciu_Apellidos]);
        }
        // dd($liquidacionRural);

        if(sizeof($array_total)==0){
            $dataContribuyente=DB::connection('pgsql')->table('sgm_app.cat_ente as en')
            ->where('en.ci_ruc', $cedula)
            ->select(DB::raw("CONCAT(en.apellidos, ' ', en.nombres) AS nombre"))
            ->first();

            $contribuyente=$dataContribuyente->nombre;
            $cero=0;
            array_push($array_total,["nombres"=>$contribuyente, "tipo"=>"SD","total"=>number_format($cero,2)]);
            
        }
        // return ["resultado"=>$liquidacion, "error"=>false];

        return ["resultado"=>$array_total, "error"=>false];

    }

     public function buscarDetalleDeudas($cedula, $tipo){ 
        $liquidacion=[];
        if($tipo=="Predio Urbano"){
            $id_predio=DB::connection('pgsql')->table('sgm_app.cat_ente as en')
            ->leftJoin('sgm_app.cat_predio_propietario as pre_pro', 'pre_pro.ente', '=', 'en.id')
            ->where('en.ci_ruc', $cedula)
            ->where('pre_pro.estado','A')
            ->pluck('pre_pro.predio')  // Devuelve una colección con solo los valores del campo 'id'
            ->toArray(); 
       
            $liquidacion = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as liq')
                ->leftJoin('sgm_app.cat_ente as en', 'en.id', '=', 'liq.comprador')
                ->leftJoin('sgm_app.cat_predio as pre', 'pre.id', '=', 'liq.predio')
                ->leftJoin('sgm_app.cat_ciudadela as cdla', 'cdla.id', '=', 'pre.ciudadela')
                ->select(
                    'liq.num_liquidacion',
                    'liq.anio',
                    'liq.avaluo_municipal',
                    'liq.avaluo_construccion',
                    'liq.avaluo_solar',
                    'liq.fecha_ingreso',
                    'liq.total_pago',
                    'pre.num_predio',
                    'pre.clave_cat',
                    'saldo',
                    'liq.id',
                    'liq.anio',
                    'en.direccion',
                    'en.ci_ruc',
                    DB::raw("
                        CASE
                            WHEN liq.comprador IS NULL THEN liq.nombre_comprador
                            ELSE CASE en.es_persona
                                WHEN TRUE THEN COALESCE(en.apellidos, '') || ' ' || COALESCE(en.nombres, '')
                                ELSE COALESCE(en.razon_social, '')
                            END
                        END AS nombres
                    "),
                   
                    DB::raw("
                        CASE
                            WHEN liq.comprador IS NULL THEN 'S/N'
                            ELSE (SELECT ci_ruc FROM sgm_app.cat_ente WHERE cat_ente.id = liq.comprador)
                        END AS cedula
                    "),
                    DB::raw("cdla.nombre || ' MZ: ' || pre.urb_mz || ' SL: ' || pre.urb_solarnew AS direccion1"),
                    'pre.clave_cat as cod_predial',
                    DB::raw("(SELECT razon_social FROM sgm_application.empresa) AS empresa"),
                  
                    DB::raw("(SELECT razon_social FROM sgm_application.empresa) AS empresa"),
                    
                    DB::raw("
                        (
                            SELECT
                                CASE
                                    WHEN (liq.anio = EXTRACT(YEAR FROM NOW())) AND (EXTRACT(MONTH FROM NOW()) < 7) THEN
                                        ROUND(d.valor * (
                                            SELECT porcentaje
                                            FROM sgm_app.ctlg_descuento_emision
                                            WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                            AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                            LIMIT 1
                                        ) / 100, 2) * (-1)
                                    ELSE
                                        0.00
                                END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = liq.id AND d.rubro = 2
                            LIMIT 1
                        ) AS desc
                    "),
                    
                    DB::raw("
                        (
                            SELECT
                                CASE
                                     WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                        ROUND((liq.saldo * (
                                            SELECT ROUND((porcentaje / 100), 2) 
                                            FROM sgm_financiero.ren_intereses i
                                            WHERE i.anio = liq.anio
                                            LIMIT 1
                                        )), 2)
                                    ELSE
                                        0.00
                                    END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = liq.id AND d.rubro = 2
                            LIMIT 1
                        ) AS interes
                    "),

                    DB::raw("
                        (
                            SELECT
                                CASE
                                    WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                        ROUND((d.valor * 0.10), 2)
                                    WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                        ROUND((d.valor * 0.10), 2)
                                    ELSE
                                        0.00
                                END
                            FROM sgm_financiero.ren_det_liquidacion d
                            WHERE d.liquidacion = liq.id AND d.rubro = 2
                            LIMIT 1
                        ) AS recargos
                    "),

                      DB::raw("
                    (
                        SELECT
                            ROUND((
                                COALESCE(liq.saldo, 0)
                                +
                                COALESCE((
                                    CASE
                                        WHEN (liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) < 7) THEN
                                            ROUND(d.valor * (
                                                SELECT porcentaje
                                                FROM sgm_app.ctlg_descuento_emision
                                                WHERE num_mes = EXTRACT(MONTH FROM NOW())
                                                AND num_quincena = (CASE WHEN EXTRACT(DAY FROM NOW()) > 15 THEN 2 ELSE 1 END)
                                                LIMIT 1
                                            ) / 100, 2) * (-1)
                                        ELSE 0
                                    END
                                ), 0)
                                +
                                COALESCE((
                                    CASE
                                    WHEN (liq.anio < EXTRACT(YEAR FROM NOW())) THEN                                        
                                        ROUND((liq.saldo * (
                                            SELECT ROUND((porcentaje / 100), 2) 
                                            FROM sgm_financiero.ren_intereses i
                                            WHERE i.anio = liq.anio
                                            LIMIT 1
                                        )), 2)
                                        ELSE 0
                                    END
                                ), 0)
                                +
                                COALESCE((
                                    CASE
                                        WHEN liq.anio = EXTRACT(YEAR FROM NOW()) AND EXTRACT(MONTH FROM NOW()) > 7 THEN
                                            ROUND((d.valor * 0.10), 2)
                                        WHEN liq.anio < EXTRACT(YEAR FROM NOW()) THEN
                                            ROUND((d.valor * 0.10), 2)
                                        ELSE 0
                                    END
                                ), 0)
                            ), 2)
                        FROM sgm_financiero.ren_det_liquidacion d
                        WHERE d.liquidacion = liq.id AND d.rubro = 2
                        LIMIT 1
                    ) AS total_complemento
                "),

                    'liq.id_liquidacion'
                )
                ->whereIN('liq.predio', $id_predio)
                ->where('estado_liquidacion',2)
                ->orderBy('liq.id','desc')
                ->get();
            

        }else if($tipo=="Predio Rural"){
            $liquidacion=DB::connection('sqlsrv')->table('CARTERA_VENCIDA as cv')
            ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 'cv.CarVe_CI')
            ->leftJoin('PREDIO as P', 'p.Pre_CodigoCatastral', '=', 'cv.Pre_CodigoCatastral')
            ->select('cv.Pre_CodigoCatastral as clave_cat','cv.CarVe_FechaEmision','cv.CarVe_NumTitulo','cv.CarVe_CI'
            ,'cv.CarVe_Estado','c.Ciu_Apellidos','c.Ciu_Nombres','cv.CarVe_Nombres as nombres','cv.CarVe_ValorEmitido'
            ,'cv.CarVe_TasaAdministrativa')
            ->where('CarVe_CI', '=', $cedula)
            ->whereIn('cv.CarVe_Estado',['E'])
            // ->where('Pre_Tipo','Rural')
            ->orderby('CarVe_NumTitulo','desc')
            ->get();
            // dd($liquidacionRural);

            $titulos = DB::connection('sqlsrv')->table('TITULOS_PREDIO as t')
            ->leftJoin('CIUDADANO as c', 'c.Ciu_Cedula', '=', 't.Titpr_RUC_CI')
            ->leftJoin('PREDIO as p', 'p.Pre_CodigoCatastral', '=', 't.Pre_CodigoCatastral')
            ->select('t.Pre_CodigoCatastral as clave_cat','t.TitPr_FechaEmision as CarVe_FechaEmision',
                    't.TitPr_NumTitulo as CarVe_NumTitulo','t.Titpr_RUC_CI as CarVe_CI','t.TitPr_Estado as CarVe_Estado',
                    'c.Ciu_Apellidos','c.Ciu_Nombres','t.TitPr_Nombres as nombres',
                    't.TitPr_ValorEmitido as CarVe_ValorEmitido','t.TitPr_TasaAdministrativa as CarVe_TasaAdministrativa')
            ->where('t.Titpr_RUC_CI', '=', $cedula)
            ->where('t.TitPr_Estado', '=', 'E') // o el estado correspondiente
            ->get();

            $liquidacion = $liquidacion->merge($titulos)->sortByDesc('CarVe_NumTitulo')->values();


            foreach($liquidacion as $key=> $data){
                $anio=explode("-",$data->CarVe_NumTitulo);

                $interesMora=0;
                $anio=explode("-",$data->CarVe_NumTitulo);
                if($anio[0]!=date('Y')){
                    $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                    ->where('IntMo_Año',$anio)
                    ->select('IntMo_Valor')
                    ->first();
                    $interesMora=$consultaInteresMora->IntMo_Valor;
                }

                // $consultaInteresMora=DB::connection('sqlsrv')->table('INTERES_MORA as im')
                // ->where('IntMo_Año',$anio)
                // ->select('IntMo_Valor')
                // ->first();

                $valor=(($interesMora/100) * ($data->CarVe_ValorEmitido - $data->CarVe_TasaAdministrativa));
                
                $valor=number_format($valor,2);
                $cero=0;

                $liquidacion[$key]->porcentaje_intereses=$interesMora;
                $liquidacion[$key]->interes=$valor;
                $liquidacion[$key]->anio=$anio[0];
                $liquidacion[$key]->saldo=number_format($data->CarVe_ValorEmitido,2);
                $liquidacion[$key]->desc=number_format($cero,2);
                $liquidacion[$key]->recargos=number_format($cero,2);
                // $liquidacion[$key]->saldo=

                $total_pago=$valor +$data->CarVe_ValorEmitido;
                $liquidacion[$key]->total_complemento=number_format($total_pago,2);
            }
        }

        return ["resultado"=>$liquidacion, "error"=>false];
    }

    public function generarNoDeudor($cedula, $tipo){
        $transaction=DB::transaction(function() use ($cedula, $tipo){
            try{ 
                // $consulta=$this->buscarDetalleDeudas($cedula, $tipo);
                // if($consulta["error"]!=false){
                //     return ["mensaje"=>"Ocurrio un errror", "error"=>true];
                // }
                // $claves=[];
                // foreach($consulta['resultado'] as $data){
                //     array_push($claves,$data->clave_cat);
                // }
                // if(sizeof($consulta['resultado'])>0){
                //     $claves = array_unique($claves);
                //     $contribuyente=$consulta['resultado'][0]->nombres;
                // }else{
                //     $dataContribuyente=DB::connection('pgsql')->table('sgm_app.cat_ente as en')
                //     ->where('en.ci_ruc', $cedula)
                //     ->select(DB::raw("CONCAT(e.apellidos, ' ', e.nombres) AS nombre"))
                //     ->first();

                //     $contribuyente=$dataContribuyente->nombre;
                // }

                $claves_urbana = DB::connection('pgsql')->table('sgm_app.cat_ente as en')
                ->leftJoin('sgm_app.cat_predio_propietario as pre_pro', 'pre_pro.ente', '=', 'en.id')
                ->leftJoin('sgm_app.cat_predio as pre', 'pre_pro.predio', '=', 'pre.id')
                ->where('en.ci_ruc', $cedula)
                ->where('pre_pro.estado', 'A')
                ->pluck('pre.clave_cat')
                ->toArray();

                $clave_rural = DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
                ->where('CarVe_CI', '=', $cedula)
                ->pluck('Pre_CodigoCatastral') // Asegúrate de que este es el campo correcto
                ->toArray();

                $claves_combinadas = array_unique(array_merge($claves_urbana, $clave_rural));
                if($tipo=="Predio Rural"){
                    $dataContribuyente=DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
                    ->where('CarVe_CI', '=', $cedula)
                    ->select('CarVe_Nombres as nombre')
                    ->first();

                    $contribuyente=$dataContribuyente->nombre;
                }else{
                    $dataContribuyente=DB::connection('pgsql')->table('sgm_app.cat_ente as en')
                    ->where('en.ci_ruc', $cedula)
                    ->select(DB::raw("CONCAT(en.apellidos, ' ', en.nombres) AS nombre"))
                    ->first();

                    $contribuyente=$dataContribuyente->nombre;
                }
                    
                             

                $codigo_externo="ND_".$this->generate_string(10);
                $qrResult = Builder::create()
                ->writer(new PngWriter())
                ->data('enlinea/validacion-no-deudor/certificado/' . $codigo_externo)
                ->size(98)
                ->margin(0)
                ->backgroundColor(new Color(245, 240, 239)) // Mismo color del <td>
                ->build();

                $qr_base64 = base64_encode($qrResult->getString());
                
                
                $nombrePDF = "NoDeudor_".$codigo_externo.".pdf";// $nombrePDF    

                //primero guardamos los datos del documento
                $dias_vigencia=30;//quemado
                //guardamos los datos del documento a generar
                $fecha_hoy=date('d-m-Y');
                $fecha_vigencia=date('d-m-Y',strtotime($fecha_hoy."+".$dias_vigencia."days"));
                $codigo_externo="ND_".$this->generate_string(10);
                       
                $datos_documento=new CertificadoSolvencia();
                $datos_documento->tipo_documento='Certificado No Deudor';
                $datos_documento->fecha_generacion=$fecha_hoy;
                $datos_documento->fecha_vigencia=$fecha_vigencia;
                $datos_documento->cedula_contribuyente=$cedula;
                $datos_documento->codigo_externo=$codigo_externo;
                $datos_documento->documento=$nombrePDF;
                $datos_documento->estado='A';
                $datos_documento->documento=auth()->user()->id;
                $datos_documento->fecha_registro=date('Y-m-d H:i:s');
               

                if($datos_documento->save()){
                                       
                    //si ya existe y no ha sido borrado(descargado) lo eliminamos
                    $exists_destino = \Storage::disk('public')->exists($nombrePDF); 
                    if($exists_destino){
                        \Storage::disk('public')->delete($nombrePDF); 
                    }
                    //si no existe lo creamos          
                    $fecha=date('Y-m-d H:i:s');
                    setlocale(LC_ALL,"es_ES@euro","es_ES","esp"); //IDIOMA ESPAÑOL
                    $fecha= date('Y-m-j');
                    $fecha = strftime("%d de %B de %Y", strtotime($fecha));
                    $informacion = "GAD MUNICIPAL DEL CANTON SAN VICENTE"; 
        
                    $pdf = \PDF::loadView('nodeudor.reporte_no_deudor', ['fecha'=>$fecha,'info'=>$informacion, 'contribuyente'=>$contribuyente, 'codigo'=>$claves_combinadas,'codigo_externo'=>$codigo_externo,'qr_base64'=>$qr_base64,'cedula'=>$cedula]);
                    $pdf->setPaper('A4', 'portrait');
                        
                    $estadoarch = $pdf->stream();
                    
                    // //lo guardamos en el disco temporal
                    \Storage::disk('public')->put(str_replace("", "",$nombrePDF), $estadoarch);
                    $exists_destino = \Storage::disk('public')->exists($nombrePDF); 
                    if($exists_destino){
                        
                        return response()->json([
                            "error"=>false,
                            "pdf"=>$nombrePDF, 
                            'identificacion'=>$codigo_externo
                        ]); 
                    }else{
                        return response()->json([
                            "error"=>true,
                            "mensaje"=>'No se pudo crear el documento'
                        ]);
                        
        
                    }
                }
                else{
                    return response()->json([
                        "error"=>true,
                        "mensaje"=>'No se pudo crear el documento'
                    ]);                    

                }

            }catch(\Throwable $th){
                dd($th);
                DB::rollback();
                \Log::error("CertificadoNoDeudorController =>generar_certificado_nd =>sms => ".$th->getMessage());
                return response()->json([
                    "error"=>true,
                    "mensaje"=>'Ocurrió un error, al generar el documento'
                ]);
            }
        });
        return ($transaction);
    }

     public function verDocumento($documentName){
        try {
            $info = new \SplFileInfo($documentName);
            $extension = $info->getExtension();
            if($extension!= "pdf" && $extension!="PDF"){
                return \Storage::disk('public')->download($documentName);
            }else{
                // obtenemos el documento del disco en base 64
                $documentEncode= base64_encode(\Storage::disk('public')->get($documentName));
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

    public function generate_string($strength = 10) {

        $input = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        
        $input_length = strlen($input);
        $random_string = '';
        for($i = 0; $i < $strength; $i++) {
            $random_character = $input[mt_rand(0, $input_length - 1)];
            $random_string .= $random_character;
        }
        return $random_string;
    }

    public function guardaContribuyente(Request $request){
        DB::beginTransaction(); 
        try{
            
            $cedula=$request->cedula;
            $nombres=$request->nombres;
            $apellidos=$request->apellidos;
            $direccion=$request->direccion;
            $fnacimiento_cont=$request->fnacimiento_cont;

            $verificaCedula=PsqlEnte::where('ci_ruc',$cedula)
            ->first();
            if(!is_null($verificaCedula)){
                return [
                    'error' => true,
                    'mensaje' => 'La cedula ya existe para otro contribuyente.',
                ];
            }

            $nuevoContribuyente=new PsqlEnte;
            $nuevoContribuyente->ci_ruc=$cedula;
            $nuevoContribuyente->nombres=$nombres;
            $nuevoContribuyente->apellidos=$apellidos;
            $nuevoContribuyente->direccion=$direccion;
            $nuevoContribuyente->fecha_nacimiento=$fnacimiento_cont;
            $nuevoContribuyente->save();
            DB::commit(); // Confirmar los cambios en la BD

            return [
                'error' => false,
                'mensaje' => 'Su informacion fue ingresada exitosamente.',
            ];  

            

        } catch (\Throwable $th) {
            DB::rollback();
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return (['mensaje'=>'Ocurrió un error,intentelo más tarde '.$th,'error'=>true]); 
        } 

    }


    public function vistaRepetidos(){
        return view('emisiones.repetidos');
    }

    public function buscarRepetidos($tipo){
        $estado="E";
        if($tipo==1){
            $estado="C";
        }
        $duplicados = DB::connection('sqlsrv')->select("
            WITH Duplicados AS (
                SELECT *,
                    ROW_NUMBER() OVER (
                        PARTITION BY 
                            Pre_CodigoCatastral,
                            CarVe_RUC,
                            CarVe_CI,
                        CarVe_Nombres,
                        CarVe_Calle,
                        CarVe_numero,
                        CarVe_direccPropietario,
                        CarVe_ValTotalTerrPredio,
                        CarVe_ValTotalEdifPredio,
                        CarVe_ValCultivos,
                        CarVe_ValForestales,
                        CarVe_ValObrasInter,
                        CarVe_ValOtrasInver,
                        CarVe_ValComerPredio,
                        CarVe_RebajaHipotec,
                        CarVe_BaseImponible,
                        CarVe_RebajaGeneral,
                        CarVe_IPU,
                        CarVe_Magisterio,
                        CarVe_EducacionElemental,
                        CarVe_MedicinaRural,
                        CarVe_EstablecEducativos,
                        CarVe_SolNoEdif,
                        CarVe_ConstObsoleta,
                        CarVe_SNERecargo,
                        CarVe_ViviendaRural,
                        CarVe_FechaEmision,
                        CarVe_TituloGral,
                        CarVe_TasaAdministrativa,
                        CarVe_OtrosAdicionales,
                        CarVe_RecoleccionBasura,
                        CarVe_Bomberos,
                        CarVe_ValorEmitido,
                        CarVe_ValorTCobrado,
                        CarVe_NumTitulo,
                        CarVe_Interes,
                        CarVe_FechaRecaudacion,
                        CarVe_Estado,
                        CarVe_Observaciones,
                        Usu_usuario,
                        Carve_Valor1,
                        Carve_Valor2,
                        Carve_Recargo,
                        CarVe_Descuento
                    ORDER BY CarVe_NumTitulo DESC
                ) AS fila
                
            FROM CARTERA_VENCIDA
            WHERE CarVe_Estado = '$estado'
            )
            SELECT * FROM Duplicados WHERE fila > 1
        ");

        return ["resultado"=>$duplicados, "error"=>false];

    }

   
    public function quitarDuplicados($tipo){
        
        DB::beginTransaction(); // Iniciar la transacción
    
        try {
            $consultaDuplicado=$this->buscarRepetidos($tipo);
            if($consultaDuplicado["error"]!=false){
                return [
                    'error' => true,
                    'mensaje' => 'Ocurrió un error al consultar repetidos' 
                ];
            }

            $estado="E";
            if($tipo==1){
                $estado="C";
            }

            $txt="titulos_actualizados_".date('YmdHis').'.txt';
            $titulosActualizados = [];
            // foreach ($consultaDuplicado["resultado"] as $registro) {

            //     $fechaEmision = $registro->CarVe_FechaEmision 
            //     ? date('Y-m-d H:i:s', strtotime($registro->CarVe_FechaEmision)) 
            //     : NULL;

            //     $fechaRecaudacion = $registro->CarVe_FechaRecaudacion 
            //     ? date('Y-m-d H:i:s', strtotime($registro->CarVe_FechaRecaudacion)) 
            //     : NULL;
                
            //     $actualizados =DB::connection('sqlsrv')->table('CARTERA_VENCIDA')
            //         ->where([
            //             ['Pre_CodigoCatastral', '=', $registro->Pre_CodigoCatastral],
            //             ['CarVe_RUC', '=', $registro->CarVe_RUC],
            //             ['CarVe_CI', '=', $registro->CarVe_CI],
            //             ['CarVe_Nombres', '=', $registro->CarVe_Nombres],
            //             ['CarVe_Calle', '=', $registro->CarVe_Calle],
            //             ['CarVe_numero', '=', $registro->CarVe_numero],
            //             ['CarVe_direccPropietario', '=', $registro->CarVe_direccPropietario],
            //             ['CarVe_ValTotalTerrPredio', '=', $registro->CarVe_ValTotalTerrPredio],
            //             ['CarVe_ValTotalEdifPredio', '=', $registro->CarVe_ValTotalEdifPredio],
            //             ['CarVe_ValCultivos', '=', $registro->CarVe_ValCultivos],
            //             ['CarVe_ValForestales', '=', $registro->CarVe_ValForestales],
            //             ['CarVe_ValObrasInter', '=', $registro->CarVe_ValObrasInter],
            //             ['CarVe_ValOtrasInver', '=', $registro->CarVe_ValOtrasInver],
            //             ['CarVe_ValComerPredio', '=', $registro->CarVe_ValComerPredio],
            //             ['CarVe_RebajaHipotec', '=', $registro->CarVe_RebajaHipotec],
            //             ['CarVe_BaseImponible', '=', $registro->CarVe_BaseImponible],
            //             ['CarVe_RebajaGeneral', '=', $registro->CarVe_RebajaGeneral],
            //             ['CarVe_IPU', '=', $registro->CarVe_IPU],
            //             ['CarVe_Magisterio', '=', $registro->CarVe_Magisterio],
            //             ['CarVe_EducacionElemental', '=', $registro->CarVe_EducacionElemental],
            //             ['CarVe_MedicinaRural', '=', $registro->CarVe_MedicinaRural],
            //             ['CarVe_EstablecEducativos', '=', $registro->CarVe_EstablecEducativos],
            //             ['CarVe_SolNoEdif', '=', $registro->CarVe_SolNoEdif],
            //             ['CarVe_ConstObsoleta', '=', $registro->CarVe_ConstObsoleta],
            //             ['CarVe_SNERecargo', '=', $registro->CarVe_SNERecargo],
            //             ['CarVe_ViviendaRural', '=', $registro->CarVe_ViviendaRural],
            //             // ['CarVe_FechaEmision', '=', $fechaEmision],
            //             ['CarVe_TituloGral', '=', $registro->CarVe_TituloGral],
            //             ['CarVe_TasaAdministrativa', '=', $registro->CarVe_TasaAdministrativa],
            //             ['CarVe_OtrosAdicionales', '=', $registro->CarVe_OtrosAdicionales],
            //             ['CarVe_RecoleccionBasura', '=', $registro->CarVe_RecoleccionBasura],
            //             ['CarVe_Bomberos', '=', $registro->CarVe_Bomberos],
            //             ['CarVe_ValorEmitido', '=', $registro->CarVe_ValorEmitido],
            //             ['CarVe_ValorTCobrado', '=', $registro->CarVe_ValorTCobrado],
            //             ['CarVe_NumTitulo', '=', $registro->CarVe_NumTitulo],
            //             ['CarVe_Interes', '=', $registro->CarVe_Interes],
            //             // ['CarVe_FechaRecaudacion', '=', $fechaRecaudacion],
            //             ['CarVe_Estado', '=', $registro->CarVe_Estado],
            //             ['CarVe_Observaciones', '=', $registro->CarVe_Observaciones],
            //             ['Usu_usuario', '=', $registro->Usu_usuario],
            //             ['Carve_Valor1', '=', $registro->Carve_Valor1],
            //             ['Carve_Valor2', '=', $registro->Carve_Valor2],
            //             ['Carve_Recargo', '=', $registro->Carve_Recargo],
            //             ['CarVe_Descuento', '=', $registro->CarVe_Descuento],
            //         ])
            //         ->limit(1) // <- solo uno por grupo
                    
            //         ->update(['CarVe_Estado' => 'B', 'CarVe_EstadoOld'=>$estado]);
            //         // ->get();

            //         // $actualiza=PredioRural::

            //         if ($actualizados) {
            //             // $titulosActualizados[] = $registro->CarVe_NumTitulo;
            //             array_push($titulosActualizados, $registro->CarVe_NumTitulo);
            //         }
            // }


            foreach ($consultaDuplicado["resultado"] as $registro) {
                $titulo = $registro->CarVe_NumTitulo;
                $codigo = $registro->Pre_CodigoCatastral;
                $estadoNuevo = 'B';        // estado al que quieres cambiar
                $estadoAnterior = $estado; // puedes adaptarlo si lo tienes en otra variable

                $sql = "
                    WITH CTE AS (
                        SELECT *,
                            ROW_NUMBER() OVER (ORDER BY [CarVe_FechaEmision], [CarVe_NumTitulo]) AS rn
                        FROM [CARTERA_VENCIDA]
                        WHERE [CarVe_NumTitulo] = ? AND [Pre_CodigoCatastral] = ?
                    )
                    UPDATE CTE
                    SET [CarVe_Estado] = ?, [CarVe_EstadoOld] = ?
                    WHERE rn = 1;
                ";

                DB::connection('sqlsrv')->statement($sql, [$titulo,$codigo, $estadoNuevo, $estadoAnterior]);

                array_push($titulosActualizados, $registro->CarVe_NumTitulo);
            }
            // dd($titulosActualizados);
            // Guardar log al final
            if (!empty($titulosActualizados)) {
                $contenido = implode("\n", $titulosActualizados);
                \Storage::disk('public')->put($txt, $contenido);
                // \Storage::disk('public')->put('archivo.txt', $contenido);

            }

            return ["mensaje"=>"Informacion actualizado exitosamente", "error"=>false, "txt"=>$txt];

        } catch (\Exception $e) {
            DB::rollback(); // Revertir cambios en caso de error
            return [
                'error' => true,
                'mensaje' => 'Ocurrió un error al quitar repetido: ' . $e->getMessage(). ' Linea=> '. $e->getLine()
            ];
        }
    }

    public function descargarTxt($archivo){
        try{   
        
            $exists_destino = \Storage::disk('public')->exists($archivo); 

            if($exists_destino){
                return response()->download( storage_path('app/public/'.$archivo))->deleteFileAfterSend(true);
            }else{
                return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
            } 

        } catch (\Throwable $th) {
            // Log::error(__CLASS__." => ".__FUNCTION__." => Mensaje =>".$e->getMessage()." Linea =>".$e->getLine());
            return back()->with(['error'=>'Ocurrió un error','estadoP'=>'danger']);
        } 
    }
}