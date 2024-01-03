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
            'canton' => 'SAN VICENTE',
            'canton_id' => 62,
            'direccion' => 'Leonidas Plaza - Cdla Marianita del Jesus',
            'telefono' => '0939120904',
            'discapacidad' => 'NO'
        ]);
        DB::table('personas')->insert([
            'cedula' => '1723733729',
            'apellidos' => 'Zambrano Solorzano',
            'nombres' =>'Silvia Marlene',
            'fechaNacimiento' => '1990-03-04',
            'estadoCivil' => 'CASADO/A',
            'ocupacion' => 'Tecnologia en administracion de empresas',
            'provincia' => 'MANABI',
            'provincia_id' => 13,
            'canton' => 'SAN VICENTE',
            'canton_id' => 62,
            'direccion' => 'San Vicente - Santa Martha',
            'telefono' => '0969963569',
            'discapacidad' => 'NO'
        ]);
    }
}
