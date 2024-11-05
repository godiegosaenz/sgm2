<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('auditoria_log', function (Blueprint $table) {
            $table->id(); // Identificador único
            $table->unsignedBigInteger('user_id')->nullable(); // ID del usuario que realizó el cambio
            $table->string('table_name'); // Nombre de la tabla afectada
            $table->unsignedBigInteger('record_id'); // ID del registro modificado
            $table->enum('operation', ['INSERT', 'UPDATE', 'DELETE']); // Tipo de operación
            $table->json('old_values')->nullable(); // Valores anteriores (como JSON)
            $table->json('new_values')->nullable(); // Nuevos valores (como JSON)
            $table->timestamp('created_at')->useCurrent(); // Marca de tiempo del cambio

            // Llaves foráneas
            $table->foreign('user_id')->references('id')->on('users')->onDelete('set null');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('auditoria_log');
    }
};
