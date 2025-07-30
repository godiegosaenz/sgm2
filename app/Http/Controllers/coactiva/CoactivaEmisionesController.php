<?php

namespace App\Http\Controllers\coactiva;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PsqlYearDeclaracion;
use App\Models\PsqlLiquidacion;
use App\Models\CoactTitulo;
use App\Models\CoactListadoTitulo;
use Carbon\Carbon;
use DB;
use Illuminate\Support\Facades\Gate;

class CoactivaEmisionesController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        return view('coactiva.index');
    }

    public function consultaTitulosUrbanos($tipo, $valor){
        try{
            if($tipo!=3){
                $predio_id = DB::connection('pgsql')->table('sgm_app.cat_predio')->select('id')
                ->where(function($query) use($tipo, $valor) {
                    if($tipo==0){
                        $query->where('num_predio', '=', $valor);
                    }else if($tipo==2){
                        $query->where('clave_cat', '=', $valor);
                    }else{
                        // $query->where('clave_cat', '=', $clave);
                    }
                })
                ->first();
                if(is_null($predio_id)){
                    return ["mensaje"=>"No se encontro informacion ", "error"=>true];
                }
                $valor = $predio_id->id;
            }
            
            $liquidacionUrbana = DB::connection('pgsql')->table('sgm_financiero.ren_liquidacion as liq')
            ->join('sgm_app.cat_predio', 'liq.predio', '=', 'sgm_app.cat_predio.id')
            ->leftJoin('sgm_app.cat_ente', 'liq.comprador', '=', 'sgm_app.cat_ente.id')
            ->select('liq.id','liq.id_liquidacion','liq.total_pago','liq.estado_liquidacion','liq.predio','liq.anio','liq.nombre_comprador','sgm_app.cat_predio.clave_cat','sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos','sgm_app.cat_ente.ci_ruc','num_predio', 'sgm_app.cat_ente.id as id_cont',           
            
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
                ")
            
            )
            ->where(function($query) use($tipo, $valor) {
                if($tipo!=3){
                    $query->where('predio','=',$valor);
                }else{
                    $query->where('comprador','=',$valor);
                }   
            })
            ->whereNotIn('anio',[2025])
            ->whereNotIN('estado_liquidacion',[1,3,4,5])
            ->where('estado_coactiva',1)
            ->where('notifica_coact',null)
            ->orderby('clave_cat','desc')
            ->orderBy('anio', 'desc')
            ->get();
            
            $correo="";
            if(sizeof($liquidacionUrbana)>0){
                $correo=DB::connection('pgsql')->table('sgm_app.ente_correo')
                ->where('ente',$liquidacionUrbana[0]->id_cont)
                ->pluck('email')
                ->toArray(); 
            }
                

            return ["resultado"=>$liquidacionUrbana, "correo"=>$correo, "error"=>false];

        }catch (\Exception $e) {
            return ["mensaje"=>'Ocurrio un error, intentelo mas tarde '.$e, "error"=>true];
        }

    }

    public function coactivar(Request $request){
       
        DB::beginTransaction();
        try{
            $id_liquidacion=$request->id_liquidacion;
            // Convertir a array de números:
            $ids = explode(',', $id_liquidacion);
            $ids = array_map('trim', $ids);   // quitar espacios
            $ids = array_map('intval', $ids); // convertir a enteros
            
            $verifica=PsqlLiquidacion::whereIn('id',$ids)
            ->where('estado_liquidacion',1)->get();
            if(sizeof($verifica)==0){
                return ["mensaje"=>' Las emisiones ya no se encuentran disponibles de descoactivar', "error"=>true];
            }

            $documento=$request->archivo;

            $creaCoactiva=new CoactTitulo();   
            $creaCoactiva->observacion=$request->observacion;           
            if(!is_null($documento)){
                $extension = pathinfo($documento->getClientOriginalName(), PATHINFO_EXTENSION);
                $nombreDocumento = "documento_coact_"."-" . date('Ymd') . '-' . time();
                $creaCoactiva->documento = $nombreDocumento . "." . $extension;
            }
            $creaCoactiva->estado="C";
            $creaCoactiva->id_usuario_crea=auth()->user()->id;
            
            if($creaCoactiva->save()){

               
                $contador=0;
                $liquidacion=PsqlLiquidacion::whereIn('id',$ids)
                ->where('estado_liquidacion',1)
                ->get();    
                foreach($liquidacion as $emision){
                    $coactivoTitulo=new CoactListadoTitulo();
                    $coactivoTitulo->id_coact_listado_titulos=$creaCoactiva->id;
                    $coactivoTitulo->id_ren_liquidacion=$emision->id;
                    $coactivoTitulo->estado="C";
                    $coactivoTitulo->save();

                    $emision->coactiva=true;
                    $emision->estado_coactiva=2;
                    $emision->save();

                    if(!is_null($documento)){
                        \Storage::disk('disksDocumentoRenta')->put($nombreDocumento . "." . $extension, \File::get($documento));
                        $contador++;
                    }
                }                
            
                DB::commit();
                if($contador==0){
                    DB::rollBack();
                    return ["mensaje"=>' No se Coactivo ningun titulo', "error"=>true];
                }else{
                    $txt="1 Titulo Fue Coactivado Exitosamente";
                    if($contador>1){
                        $txt="Fueron Coactivados ".$contador." Titulos Exitosamente";
                    }
                    return ["mensaje"=>$txt, "error"=>false];
                }                    
            
            }

            return ["mensaje"=>' No se Coactivo ningun titulo', "error"=>true];

        }catch (\Exception $e) {
            DB::rollBack();
            return ["mensaje"=>'Ocurrio un error, intentelo mas tarde '.$e, "error"=>true];
        }
    }

    public function vistaCoactivar(){
        return view('coactiva.coactiva');
    }

    public function buscarNotificados($tipo, $valor){
        try{
            $buscar=DB::connection('pgsql')->table('sgm_financiero.coact_titulos as t')
            ->leftJoin('sgm_app.cat_ente', 't.ente', '=', 'sgm_app.cat_ente.id')
            ->select('t.id','t.observacion_notificacion','t.id_usuario_notif','t.fecha_registro_notif',
            'sgm_app.cat_ente.nombres','sgm_app.cat_ente.apellidos','sgm_app.cat_ente.ci_ruc', 'sgm_app.cat_ente.id as id_cont')
            ->where('t.ente',$valor)
            ->where('t.estado','N')
            ->get();

            foreach($buscar as $key => $data){
                $usuarioRegistra=DB::connection('mysql')->table('users as u')
                ->leftJoin('personas as p', 'p.id', '=', 'u.idpersona')
                ->where('u.id',$data->id_usuario_notif)
                ->select('p.nombres','p.apellidos','p.cedula')
                ->first();
                if(is_null($usuarioRegistra)){
                    $buscar[$key]->nombre_usuario=$data->usuario;
                }else{
                    $buscar[$key]->nombre_usuario=$usuarioRegistra->nombres." ".$usuarioRegistra->apellidos;
                    $buscar[$key]->cedula_usuario=$usuarioRegistra->cedula;
                }
                $titulos=DB::connection('pgsql')->table('sgm_financiero.coact_listado_titulos as lt')
                ->leftJoin('sgm_financiero.ren_liquidacion as rl', 'rl.id', '=', 'lt.id_ren_liquidacion')
                ->where('id_coact_listado_titulos',$data->id)
                ->where('lt.estado','N')
                ->pluck('rl.id_liquidacion')
                ->toArray();
                $buscar[$key]->titulos=$titulos;
            }

            return ["resultado"=>$buscar, "error"=>false];

        }catch (\Exception $e) {
            return ["mensaje"=>'Ocurrio un error, intentelo mas tarde '.$e, "error"=>true];
        }        
    }

    public function detalleDeudas($idcoact){
        try{
            $titulos=DB::connection('pgsql')->table('sgm_financiero.coact_listado_titulos as lt')
            ->where('id_coact_listado_titulos',$idcoact)
            ->where('lt.estado','N')
            ->pluck('lt.id_ren_liquidacion')
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
                ->whereIN('liq.id', $titulos)
                ->where('estado_liquidacion',2)
                ->orderBy('liq.id','desc')
                ->get();
            
            return ["resultado"=>$liquidacion, "error"=>false];
        
        }catch (\Exception $e) {
            return ["mensaje"=>'Ocurrio un error, intentelo mas tarde '.$e, "error"=>true];
        }  
    }

    public function coactivarEmisiones(Request $request){
               
        DB::beginTransaction();
        try{
            $id_coact=$request->id_coact;
            $coactiva=CoactTitulo::where('id',$id_coact)
            ->where('estado','N')
            ->first();
          
            if(is_null($coactiva)){
                return ["mensaje"=>'La informacion seleccionada ya no se encuentra disponible de coactivar', "error"=>true];
            }

            $titulos_pendiente_pagos=DB::connection('pgsql')->table('sgm_financiero.coact_listado_titulos as lt')
            ->leftJoin('sgm_financiero.ren_liquidacion as rl', 'rl.id', '=', 'lt.id_ren_liquidacion')
            ->where('id_coact_listado_titulos',$id_coact)
            ->where('lt.estado','N')
            ->where('estado_liquidacion',2)
            ->get();

            if(sizeof($titulos_pendiente_pagos)==0){
                return ["mensaje"=>'La informacion seleccionada ya no se encuentra disponible de coactivar', "error"=>true];
            }

            $coactiva->estado="C";
            $coactiva->fecha_registro_coact=date('Y-m-d H:i:s');
            $coactiva->id_usuario_crea_coact=auth()->user()->id;
            $coactiva->save();

            $titulos_estado=CoactListadoTitulo::where('id_coact_listado_titulos',$id_coact)
            ->where('estado','N')->get();
            $contador=0;
            foreach($titulos_estado as $titulo){
              
                $liquidacion=PsqlLiquidacion::where('id', $titulo->id_ren_liquidacion)
                ->where('estado_liquidacion',2)
                ->first();
                if(!is_null($liquidacion)){
                    $liquidacion->coactiva=true;
                    $liquidacion->estado_coactiva=2;
                    if($liquidacion->save()){
                        $titulo->estado="C";
                        $titulo->save();
                        $contador++;
                    }
                }
            }

            if($contador>0){
                DB::commit();
                return ["mensaje"=>'Proceso realizado exitosamente', "error"=>false];

            }else{
                DB::rollBack();
                return ["mensaje"=>' No se Coactivo ningun titulo', "error"=>true];
            }
                                

        }catch (\Exception $e) {
            DB::rollBack();
            return ["mensaje"=>'Ocurrio un error, intentelo mas tarde '.$e, "error"=>true];
        }
    }

}