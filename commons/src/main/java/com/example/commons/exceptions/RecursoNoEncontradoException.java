package com.example.commons.exceptions;


/*
 * CONVERTIR ERRORES EN RESPUESTAS
 *
 * EXCEPCIONES PROPIAS
 *
 * @EXCEPTIONHANDLER
 * */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
