<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class ProvinciasTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        DB::table('provincias')->insert(['nombre' => 'AZUAY']);
        DB::table('provincias')->insert(['nombre' => 'BOLIVAR']);
        DB::table('provincias')->insert(['nombre' => 'CAÑAR']);
        DB::table('provincias')->insert(['nombre' => 'CARCHI']);
        DB::table('provincias')->insert(['nombre' => 'COTOPAXI']);
        DB::table('provincias')->insert(['nombre' => 'CHIMBORAZO']);
        DB::table('provincias')->insert(['nombre' => 'EL ORO']);
        DB::table('provincias')->insert(['nombre' => 'ESMERALDAS']);
        DB::table('provincias')->insert(['nombre' => 'GUAYAS']);
        DB::table('provincias')->insert(['nombre' => 'IMBABURA']);
        DB::table('provincias')->insert(['nombre' => 'LOJA']);
        DB::table('provincias')->insert(['nombre' => 'LOS RIOS']);
        DB::table('provincias')->insert(['nombre' => 'MANABI']);
        DB::table('provincias')->insert(['nombre' => 'MORONA SANTIAGO']);
        DB::table('provincias')->insert(['nombre' => 'NAPO']);
        DB::table('provincias')->insert(['nombre' => 'PASTAZA']);
        DB::table('provincias')->insert(['nombre' => 'PICHINCHA']);
        DB::table('provincias')->insert(['nombre' => 'TUNGURAHUA']);
        DB::table('provincias')->insert(['nombre' => 'ZAMORA CHINCHIPE']);
        DB::table('provincias')->insert(['nombre' => 'GALAPAGOS']);
        DB::table('provincias')->insert(['nombre' => 'SUCUMBIOS']);
        DB::table('provincias')->insert(['nombre' => 'ORELLANA']);
        DB::table('provincias')->insert(['nombre' => 'SANTO DOMINGO DE LOS TSACHILAS']);
        DB::table('provincias')->insert(['nombre' => 'SANTA ELENA']);

    }
}
