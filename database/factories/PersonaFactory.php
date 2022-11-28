<?php

namespace Database\Factories;

use App\Models\Persona;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Persona>
 */
class PersonaFactory extends Factory
{
    protected $model = Persona::class;
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition()
    {
        return [
            'cedula' => '13'.$this->faker->randomNumber(8, true),
            'apellidos' => $this->faker->lastName(),
            'nombres' => $this->faker->name(),
            'fechaNacimiento' => $this->faker->date(),
            'estadoCivil' => 'SOLTERO/A',
            'ocupacion' => 'Ingeniero en Sistemas',
            'provincia' => 'MANABI',
            'provincia_id' => 13,
            'canton' => 'SUCRE',
            'canton_id' => 62,
            'direccion' => $this->faker->address(),
            'telefono' => '09'.$this->faker->randomNumber(8, true),
            'discapacidad' => 'NO'
        ];
    }
}
