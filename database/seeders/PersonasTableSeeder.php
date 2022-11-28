<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class PersonasTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        DB::table('personas')->insert([
            'cedula' => '1314801349',
            'apellidos' => 'Bermudez Saenz',
            'nombres' =>'Diego Andres',
            'fechaNacimiento' => '1992-03-16',
            'estadoCivil' => 'SOLTERO/A',
            'ocupacion' => 'Ingeniero en Sistemas',
            'provincia' => 'MANABI',
            'provincia_id' => 13,
            'canton' => 'SUCRE',
            'canton_id' => 62,
            'direccion' => 'Leonidas Plaza - Cdla Marianita del Jesus',
            'telefono' => '0939120904',
            'discapacidad' => 'NO'
        ]);
        DB::table('personas')->insert([
            'cedula' => '1314801340',
            'apellidos' => 'Parraga Saenz',
            'nombres' =>'Juan',
            'fechaNacimiento' => '1992-03-16',
            'estadoCivil' => 'SOLTERO/A',
            'ocupacion' => 'Ingeniero en Sistemas',
            'provincia' => 'MANABI',
            'provincia_id' => 13,
            'canton' => 'SUCRE',
            'canton_id' => 62,
            'direccion' => 'Leonidas Plaza - Cdla Marianita del Jesus',
            'telefono' => '0939120904',
            'discapacidad' => 'NO'
        ]);
    }
}
