package com.example.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informacion de un medico")
public record MedicoResponse(
        @Schema(description = "Identificador unico del medico", example = "1")
        Long id,

        @Schema(description = "Nombre del medico", example = "Juan Carlos Perez")
        String nombre,

        @Schema(description = "Edad del medico", example = "22")
        Short edad,

        @Schema(description = "Correo electronico del medico", example = "juan@medico.com")
        String email,

        @Schema(description = "Telefono del medico", example = "1234567890")
        String telefono,

        @Schema(description = "Cedula Profesional del medico", example = "222229777")
        String cedulaProfesioonal,

        @Schema(description = "Especidalidad del medico", example = "Cardiologia")
        String especialidad,

        @Schema(description = "Descripcion de la disponibilidad del medico", example = "Disponible para atender pacientes")
        String disponibilidad,

        @Schema(description = "Identificador de la disponibilidad del medico", example = "1")
        Long idDisponibilidad

        ) {
}
