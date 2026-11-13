package com.example.citas.controller;


import com.example.citas.dto.CitaRequest;
import com.example.citas.dto.CitaResponse;
import com.example.citas.service.CitaService;
import com.example.commons.controller.CrudController;
import com.example.commons.docs.ProblemaDocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API Citas", description = "Métodos para la gestión de citas")
public class CitaController extends CrudController<CitaRequest, CitaResponse, CitaService>{


    public CitaController(CitaService service) {
        super(service);
    }

    @Operation(summary = "Actualizar estado de la cita",
            description = "Actualizar el estado de una cita utilizando" +
            "el identificador de la cita y el identificador del nuevo estado.")
    @ApiResponse(responseCode = "204", description = "Disponibilidad de la cita actualizada")
    @ApiResponse(responseCode = "409", description = "La disponibilidad de la cita no puede " +
            "actualizarse por regla de negocio",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)))
    @PutMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
            @PathVariable @Positive(message = "El IdCita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "El idEstado debe ser positivo") Long idEstado
    ) {
        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verificar si un paciente tiene citas confirmadas o en curso",
            description = "Actualizar el estado de una cita utilizando" +
                    "el identificador de la cita y el identificador del nuevo estado.")
    @ApiResponse(responseCode = "204", description = "Disponibilidad de la cita actualizada")
    @ApiResponse(responseCode = "409", description = "La disponibilidad de la cita no puede " +
            "actualizarse por regla de negocio",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)))
    @GetMapping("/id-paciente/{idPaciente}/citas-asignadas")
    public ResponseEntity<Void> pacienteTieneCitas(
            @PathVariable @Positive(message = "El IdCita debe ser positivo") Long idPaciente
    ) {
        service.pacienteTieneCitas(idPaciente);
        return ResponseEntity.noContent().build();
    }


    @ApiResponse(responseCode = "204", description = "Medico no tiene citas asignadas")
    @ApiResponse(responseCode = "409", description = "Medico tiene citas asignadas y no se puede " +
            "modificar ni eliminar",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)))
    @GetMapping("/id-medico/{idMedico}/citas-asignadas")
    public ResponseEntity<Void> medicoTieneCitasAsignadas(
            @Parameter(description = "Id del medico", example = "1")
            @PathVariable @Positive(message = "El idMedico debe ser positivo") Long idMedico
    ){
        service.medicoTieneCitasAsignadas(idMedico);
        return ResponseEntity.noContent().build();
    }

    @ApiResponse(responseCode = "204", description = "El paciente no tiene citas asignadas")
    @ApiResponse(responseCode = "409", description = "El paciente tiene citas asignadas",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)
            )
    )
    @GetMapping("/id-medico/{idMedico}/validar-eliminacion")
    public ResponseEntity<Void> validarEliminacionMedico(
            @Parameter(description = "ID del paciente", example = "1")
            @PathVariable @Positive(message = "El ID del medico debe ser positivo")
            Long idMedico
    ) {
        service.validarEliminacionMedico(idMedico);
        return ResponseEntity.noContent().build();
    }

}
