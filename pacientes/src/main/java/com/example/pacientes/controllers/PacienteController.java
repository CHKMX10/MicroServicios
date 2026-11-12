package com.example.pacientes.controllers;


import com.example.commons.controller.CrudController;
import com.example.commons.docs.ProblemaDocs;

import com.example.commons.dto.pacientes.PacienteReponse;
import com.example.commons.dto.pacientes.PacienteRequest;
import com.example.pacientes.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "API Pacientes", description = "Métodos para la gestión de pacientes")
public class PacienteController extends CrudController<PacienteRequest, PacienteReponse, PacienteService> {

    public PacienteController(PacienteService service) {
        super(service);
    }

    @GetMapping("/id-paciente/{id}")
    @Operation(summary = "Obtener paciente por id sin importar el estado del registro")
    @ApiResponse(responseCode = "200", description = "Paciente sin estado encontrado")
    @ApiResponse(responseCode = "404", description = "El paciente sin estado no existe",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)))
    public ResponseEntity<PacienteReponse> obtenerPacientePorIdSinEstado(
            @PathVariable @Positive(message = "El ID debe ser positivo") Long id
    ) {
        return ResponseEntity.ok(service.obtenerPacientePorIdSinEstado(id));
    }
}
