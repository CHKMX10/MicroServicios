package com.example.auth.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/*
* CONVERTIR ERRORES EN RESPUESTAS
*
* EXCEPCIONES PROPIAS
*
* @EXCEPTIONHANDLER
 * */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 404: el recurso no existe
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail handleNoEncontrado(RecursoNoEncontradoException e) {
        log.warn("No encontrado: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 400: el cliente mandó un dato inválido
    @ExceptionHandler(DatoInvalidoException.class)
    public ProblemDetail handleDatoInvalido(DatoInvalidoException e) {
        log.warn("Dato inválido: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 401: AGREGADAS SOLO PARA LA AUTH
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleDatoInvalido(BadCredentialsException e) {
        log.warn("Login Fallido: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "CREDENCIALES INVÁLIDAS");
    }

    // 401: AGREGADAS SOLO PARA LA AUTH
    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleDatoInvalido(UsernameNotFoundException e) {
        log.warn("Usuario no encontrado: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 409: la base de datos rechazó el dato (unique, foreign key...)
    /*@ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleIntegridad(DataIntegrityViolationException e) {
        log.warn("Integridad de datos: {}", e.getMostSpecificCause().getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "La operación viola una restricción de datos (duplicado o registro en uso.");
    }*/

    // ---------- Validaciones (sobrescriben a la clase base) ----------

    // 400: falló @Valid en el @RequestBody
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        List<String> errores = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos.");
        pd.setProperty("errores", errores);
        return handleExceptionInternal(e, pd, headers, status, request);
    }

    // 400: falló @Positive, @Min... en un @PathVariable o @RequestParam
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException e, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        List<String> errores = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Uno o más parámetros no son válidos.");
        pd.setProperty("errores", errores);
        return handleExceptionInternal(e, pd, headers, status, request);
    }

    // ---------- Red de seguridad ----------

    // 500: cualquier error que no previmos
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception e) {
        log.error("Error interno", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor. Contacte al administrador.");
    }
}