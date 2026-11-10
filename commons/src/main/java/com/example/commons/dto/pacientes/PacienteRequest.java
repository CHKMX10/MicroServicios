package com.example.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "DTO para manejar un paciente")
public record PacienteRequest(

        @Schema(description = "Nombre del paciente", example = "Christian")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del paciente", example = "Garcia")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del paciente", example = "López")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Edad del paciente. Debe estar entre 1 y 100 años.", example = "35",
                minimum = "1", maximum = "100")
        @NotNull(message = "La edad es requerida")
        @Min(value = 1, message = "La edad mínima es de 1 año")
        @Max(value = 100, message = "La edad máxima es de 100 años")
        Short edad,

        @Schema(description = "Peso en Kg (0.1 - 200)",
                example = "50 Kg", pattern = "^[0-9]{10}$")
        @NotNull(message = "El peso es requerido")
        @DecimalMin(value = "0.1", message = "El peso minimo es de 0.1 Kg")
        @DecimalMax(value = "200.0", message = "El peso máxima es de 200.0 Kg")
        Double peso,

        @Schema(description = "Estatura en metros (1.0 - 2.0)",example = "1.75")
        @NotNull(message = "La estatura es requerida")
        @DecimalMin(value = "1.0", message = "La altura minima es de 1.0 m")
        @DecimalMax(value = "200.0", message = "La altura máxima es de 2.0 m")
        Double estatura,

        @Schema(description = "Correo electrónico del paciente", example = "christian.garcia@example.com")
        @NotBlank(message = "El email es requerido")
        @Email(message = "Debe ingresar un formato de email válido")
        @Size(min = 1, max = 100, message = "El email debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener el formato correcto (email@dominio)")
        String email,

        @Schema(description = "Número telefónico de contacto. Debe contener exactamente 10 dígitos.",
                example = "2221234567", pattern = "^[0-9]{10}$")
        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe contener solo 10 dígitos numéricos")
        String telefono,

        @Schema(description = "Dirección del paciente", example = "Av. Principal #123")
        @NotBlank(message = "La dirección es requerida")
        @Size(min = 1, max = 150, message = "La dirección debe tener entre 1 y 150 caracteres")
        String direccion
) {

}
