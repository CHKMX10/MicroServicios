package com.example.commons.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos del pacientea asociado a una cita")
public record DatosPaciente(

        @Schema(description = "Nombre completo del paciente", example = "Jose Ramon Carranza Diaz")
        String nombre,

        @Schema(description = "Numero de expediente del paciente", example = "12345678901dfbdbd")
        String numExpediente,

        @Schema(description = "Edad del paciente", example = "11 años")
        String edad,

        @Schema(description = "Peso del paciente", example = "100.6 kg")
        String peso,

        @Schema(description = "Estatura del paciente", example = "2.00 m")
        String estatura,

        @Schema(description = "IMC del paciente", example = "21.3 peso normal")
        String imc,

        @Schema(description = "Numero telefonico del paciente", example = "1234567890")
        String telefono
) {
}
