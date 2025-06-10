<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Spatie\Permission\Models\Permission;

class PermissionsSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {

        $permissions = [
            'menu de administracion',

            'Lista clientes',
            'Ingresar clientes',

            'menu de tesoreria',

            'Exoneracion tercera edad',
            'Lista de exoneraciones',
            'Remision de interes',
            'Lista de remisiones',
            'Impresion de titulos urbanos',
            'Impresion de titulos rurales',
            'Reporte de liquidacion',

            'menu de rentas',

            'Catastro contribuyentes',
            'Lista de patentes',
            'Declarar patentes',
            'Lista de impuestos transito',
            'Impuestos transito',
            'Reporte transito',

            'menu de configuracion',

            'Lista de usuarios',
            'Crear usuario',
            'Lista de empleados',
            'Ingreso de empleados',
            'Crear Roles',

            'menu de reportes',
            'menu de analitica',
        ];

        foreach ($permissions as $permission) {
            Permission::firstOrCreate([
                'name' => $permission,
                'guard_name' => 'web',
            ]);
        }

    }
}
