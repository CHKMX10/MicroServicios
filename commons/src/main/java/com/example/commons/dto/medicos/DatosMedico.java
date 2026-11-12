package com.example.commons.dto.medicos;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un medico asociado a una cita")
public record DatosMedico(

        @Schema(description = "Nombre completo del medico", example = "Jose Ramon Carranza Diaz")
        String nombre,

        @Schema(description = "Cédula profesional del medico", example = "123456789012")
        String cedulaPorfesional,

        @Schema(description = "Nombre de la especialidad medica del mmedico", example = "Cardiologia")
        String especialidad
        ) {
}
