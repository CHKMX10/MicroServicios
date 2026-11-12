package com.example.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un paciente")
public record PacienteReponse(

        @Schema(description = "Identificador unico del paciente", example = "1")
        Long id,

        @Schema(description = "Nombre del paciente", example = "Juan Carlos Perez")
        String nombre,

        @Schema(description = "Edad del paciente", example = "22")
        Short edad,

        @Schema(description = "Peso del paciente", example = "30.5")
        Double peso,

        @Schema(description = "Estatura del paciente", example = "27.9")
        Double estatura,

        @Schema(description = "IMC del paciente", example = "29.4")
        Double imc,

        @Schema(description = "Email del paciente", example = "Juan_Carlos_Perez@gmail.com")
        String email,

        @Schema(description = "Telefono del paciente", example = "1234567890")
        String telefono,

        @Schema(description = "Direccion del paciente", example = "Av. Revolucion 123")
        String direccion,

        @Schema(description = "Numero de expediente del paciente", example = "aa2a2a15ss1ad5sdfeff4f")
        String numExpediente

        ) {
}
