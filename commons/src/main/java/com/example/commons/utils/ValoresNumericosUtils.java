package com.example.commons.utils;


import com.example.commons.exceptions.DatoInvalidoException;

import java.math.BigDecimal;

public class ValoresNumericosUtils {

public static <N extends Number> void validarNumeroRequerido(N numero , String mensaje ) {
    if( numero == null)
        throw new DatoInvalidoException(mensaje);
}


    public static void validarDouble(double num, double min, double max,String mensaje ){
        validarNumeroRequerido(num, mensaje);
        if(num < min || num > max )
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarRangoPrecios(BigDecimal precioMin, BigDecimal precioMax) {
        if (precioMin != null && precioMax != null && precioMin.compareTo(precioMax) > 0)
            throw new DatoInvalidoException("El precio mínimo no puede ser mayor que el precio máximo");
    }


    public static void validarRangoShort(Short numero, short min, short max, String mensaje){
        validarNumeroRequerido(numero, mensaje);
        if(numero < min || numero > max )
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarLongPositivo(Long numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);
        if(numero < 0 )
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarPorRangoBigDecimal(BigDecimal numero, BigDecimal min, BigDecimal max, String mensaje){
        validarNumeroRequerido(numero,mensaje);

        if (numero.compareTo(min) < 0 || numero.compareTo(max) > 0) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    public static void validarEnteroPositivo( Integer numero, String mensaje){

    validarNumeroRequerido(numero, mensaje);

    if (numero <= 0)
        throw new DatoInvalidoException(mensaje);
}


public static void validarBigDecimalPositivo( BigDecimal numero, String mensaje){

    validarNumeroRequerido(numero, mensaje);

    if (numero.compareTo(BigDecimal.ZERO) <= 0)
        throw new DatoInvalidoException(mensaje);
}

    public static void validarShortPositivo(Short numero, String mensaje){

        validarNumeroRequerido(numero, mensaje);

        if(numero <= 0 )
            throw new DatoInvalidoException(mensaje);
    }
}
