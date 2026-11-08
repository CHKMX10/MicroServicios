package com.example.commons.docs;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
/*
    CLASE PARA DOCUMENTAR EN SWAGGER, NO MANEJA ERRORES
    TE DA LA IDEA DE COMO SE VE EL JSON DE ERROR QUE DEVUELVE TU API
    ES UNA "PLANTILLA MUESTRA" PARA QUIEN LEA LA DOCUMENTACION SEPA QUE ESPERAR CUANDO ALGO FALLE

* */
@Schema(name = "Problema", description = "Formato de error de la API (RFC 9457)")//RFC 9457 ES EL FORMATO OFICIAL PARA ERRORES HTTP EN JSON
public record ProblemaDocs(

        @Schema(example = "about:blank")
        String type,

        @Schema(example = "Not Found")
        String title,

        @Schema(description = "Código HTTP", example = "404")
        int status,

        @Schema(description = "Mensaje del error", example = "Producto no encontrado con id: 99")
        String detail,

        @Schema(description = "Ruta que produjo el error", example = "/api/productos/99")
        String instance,

        @Schema(description = "Solo aparece en errores de validación",
                example = "[\"nombre: El nombre es requerido\"]")
        List<String> errores
) {
}
