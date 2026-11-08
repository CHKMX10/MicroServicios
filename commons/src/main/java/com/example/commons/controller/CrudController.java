package com.example.commons.controller;

import com.example.commons.service.CrudService;
import com.example.commons.docs.ProblemaDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//--------------es una plantilla
@RequiredArgsConstructor
@Validated

@ApiResponse(responseCode = "400", description = "Datos o parámetros inválidos",
        content = @Content(mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemaDocs.class)))
@ApiResponse(responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemaDocs.class)))
public class CrudController<RQ , RS , S extends CrudService<RQ , RS>> {//clase generica s es service

    protected final S service;//protected lo usa su hijo

    @GetMapping
    @Operation(summary = "Listar registros")
    @ApiResponse(responseCode = "200" , description = "Listado obtenido")
    public ResponseEntity<List<RS>> listar(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")//CONSULTA POR MEDIO DEL ID
    @Operation(summary = "Consulta por ID")
    @ApiResponse(responseCode = "200", description = "Registro encontrado")
    @ApiResponse(responseCode = "404", description = "El registro no existe",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)))
    public ResponseEntity<RS> obtenerPorId(

            @Parameter(description = "Id del registro", example = "1")
            @PathVariable @Positive(message = "El ID debe ser psoitivo") Long id

    ){
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping//CREACION POR MEDIO DE INSERCION
    @Operation(summary = "Agregar registro", description = "Agregar los registros en la DB")
    @ApiResponse(responseCode = "201", description = "Resgistro exitoso")
    @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)))
    public ResponseEntity<RS> registrar(

            @Valid @RequestBody RQ request

    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));

    }

    @PutMapping("/{id}")//
    @Operation(summary = "Actualizar", description = "Actualizar un registro")
    @ApiResponse(responseCode = "200", description = "Registro actualizado")
    @ApiResponse(responseCode = "404", description = "El registro no existe",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)))
    public ResponseEntity<RS> actualizar(

            @Parameter(description = "Id del registrp", example = "1")
            @PathVariable @Positive (message = "El ID debe ser positivo") Long id,

            @Valid @RequestBody RQ request
    ){
        return ResponseEntity.ok(service.actualizar(request, id));
    }

    @DeleteMapping("/{id}")//ELIMINACION CON ID COMO PARAMETRO
    @Operation(summary = "Eliminar un registro")
    @ApiResponse(responseCode = "204", description = "Registro eliminado")
    @ApiResponse(responseCode = "404", description = "El registro no existe",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)))
    @ApiResponse(responseCode = "409", description = "Conflicto con los datos enviados",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDocs.class)))

    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Id del registro", example = "1")
            @PathVariable @Positive (message = "El ID debe ser positivo") Long id
    ){
        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
