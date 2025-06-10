<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Spatie\Permission\Models\Role;

class RolesSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        Role::create(['name' => 'administrador']);

        $recaudacion = Role::create(['name' => 'recaudacion']);
        $recaudacion->givePermissionTo('menu de administracion');
        $recaudacion->givePermissionTo('Lista clientes');
        $recaudacion->givePermissionTo('Ingresar clientes');
        $recaudacion->givePermissionTo('menu de tesoreria');
        $recaudacion->givePermissionTo('Exoneracion tercera edad');
        $recaudacion->givePermissionTo('Lista de exoneraciones');
        $recaudacion->givePermissionTo('Remision de interes');
        $recaudacion->givePermissionTo('Lista de remisiones');
        $recaudacion->givePermissionTo('Impresion de titulos urbanos');
        $recaudacion->givePermissionTo('Impresion de titulos rurales');
        $recaudacion->givePermissionTo('Reporte de liquidacion');

        $rentas = Role::create(['name' => 'rentas']);
        $rentas->givePermissionTo('menu de rentas');
        $rentas->givePermissionTo('Catastro contribuyentes');
        $rentas->givePermissionTo('Lista de patentes');
        $rentas->givePermissionTo('Declarar patentes');
        $rentas->givePermissionTo('Impuestos transito');
        $rentas->givePermissionTo('Lista de impuestos transito');
        $rentas->givePermissionTo('Impuestos transito');
        $rentas->givePermissionTo('Reporte transito');


        $tesoreria = Role::create(['name' => 'tesoreria']);
        $tesoreria->givePermissionTo('menu de administracion');
        $tesoreria->givePermissionTo('Lista clientes');
        $tesoreria->givePermissionTo('Ingresar clientes');
        $tesoreria->givePermissionTo('menu de tesoreria');
        $tesoreria->givePermissionTo('Exoneracion tercera edad');
        $tesoreria->givePermissionTo('Lista de exoneraciones');
        $tesoreria->givePermissionTo('Remision de interes');
        $tesoreria->givePermissionTo('Lista de remisiones');
        $tesoreria->givePermissionTo('Impresion de titulos urbanos');
        $tesoreria->givePermissionTo('Impresion de titulos rurales');
        $tesoreria->givePermissionTo('Reporte de liquidacion');

        $tesoreria->givePermissionTo('menu de rentas');
        $tesoreria->givePermissionTo('Catastro contribuyentes');
        $tesoreria->givePermissionTo('Lista de patentes');
        $tesoreria->givePermissionTo('Declarar patentes');
        $tesoreria->givePermissionTo('Impuestos transito');
        $tesoreria->givePermissionTo('Lista de impuestos transito');
        $tesoreria->givePermissionTo('Impuestos transito');
        $tesoreria->givePermissionTo('Reporte transito');

        $coactiva = Role::create(['name' => 'coactiva']);
        $coactiva->givePermissionTo('menu de tesoreria');
        $coactiva->givePermissionTo('Impresion de titulos urbanos');
        $coactiva->givePermissionTo('Impresion de titulos rurales');
        $coactiva->givePermissionTo('Reporte de liquidacion');

        $financiero = Role::create(['name' => 'Finaciero']);
        $financiero->givePermissionTo('menu de administracion');
        $financiero->givePermissionTo('Lista clientes');
        $financiero->givePermissionTo('Ingresar clientes');
        $financiero->givePermissionTo('menu de tesoreria');
        $financiero->givePermissionTo('Lista de exoneraciones');
        $financiero->givePermissionTo('Lista de remisiones');
        $financiero->givePermissionTo('Impresion de titulos urbanos');
        $financiero->givePermissionTo('Impresion de titulos rurales');
        $financiero->givePermissionTo('Reporte de liquidacion');

    }
}
