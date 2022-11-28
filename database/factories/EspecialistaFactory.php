<?php

namespace Database\Factories;

use Illuminate\Database\Eloquent\Factories\Factory;


/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Especialista>
 */
class EspecialistaFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition()
    {
        return [
            'persona_id' => $this->faker->numberBetween(1, 5),
            'correo' => $this->faker->email(),
            'telefono' => '09'.$this->faker->randomNumber(8, true),
            'especialidades_id' => 1,
            'titulo' => $this->faker->jobTitle(),
            'activo' => true,
        ];
    }
}
