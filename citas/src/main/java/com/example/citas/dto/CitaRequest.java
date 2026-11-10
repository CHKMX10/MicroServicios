package com.example.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Schema(description = "Datos necesarios para registrar o actualizar una cita")
public record CitaRequest(

        @Schema(description = "Datos necesarios para registrar o actualizar una cita")
        @NotNull(message = "El ID del paciente es requerido")
        @Positive(message = "El ID del paciente debe ser positivo")
        Long idPaciente,

        @Schema(description = "Identificador del medico", example = "5", minimum = "1")
        @NotNull(message = "El ID del médico es requerido")
        @Positive(message = "El ID del médico debe ser positivo")
        Long idMedico,

        @Schema(
                description = "Fecha y hora programada para la cita. Debe ser una fecha y hora actual o futura",
                example = "25/09/2026 10:30",
                type = "string",
                format = "date-time"
        )
        @NotNull(message = "La fecha de la cita es requerida")
        @FutureOrPresent(message = "La fecha de la cita debe ser futura")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,

        @Schema(
                description = "Descripcion de los sintomas que presenta el paciente",
                example = "El paciente presenta fiebre, dolor en el abdomen y fiebre desde hace 2 dias",
                minLength = 20,
                maxLength = 500
        )
        @NotBlank(message = "Los sintomas son requeridos")
        @Size(min = 20, max = 500,
                message = "La descripcion de los sintomas debe tener entre 20 y 500 caracteres")
        String sintomas

        ) {
}
