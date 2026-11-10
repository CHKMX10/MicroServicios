package com.example.pacientes.entity;


import com.example.commons.enums.EstadoRegistro;
import com.example.commons.exceptions.ConflictoException;
import com.example.commons.utils.StringCustomUtils;
import com.example.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Entity
@Table(name = "PACIENTES")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column (name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    @Column (name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column (name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column (name = "EDAD", nullable = false)
    private Short edad;

    @Column (name = "PESO", nullable = false)
    private Double peso;

    @Column (name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column (name = "IMC", nullable = false)
    private Double imc;

    @Column (name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column (name = "NUM_EXPEDIENTE", length = 20, nullable = false)
    private String numExpediente;

    @Column (name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column (name = "DIRECCION", length = 150, nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column (name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    private static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno,
                                     Short edad, Double peso, Double estatura, String email,
                                      String telefono, String direccion ){

        StringCustomUtils.validarTamanio(nombre, 1, 50,
                "El nombre es requerido y debe tener entre 5 y 50 caracteres ");

        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50,
                "El apellido paterno es requerido y debe tener entre 5 y 50 caracteres ");

        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50,
                "El apellido materno es requerido y debe tener entre 5 y 50 caracteres ");

        StringCustomUtils.validarTamanio(email, 5, 100,
                "El email es requerido y debe tener entre 5 y 100 caracteres ");

        StringCustomUtils.validarTamanio(telefono, 10, 10,
                "El numero de telefono es requerido y debe tener 10 caracteres ");

        StringCustomUtils.validarTamanio(direccion, 5, 50,
                "El nombre es requerido y debe tener entre 5 y 150 caracteres ");

        ValoresNumericosUtils.validarRangoShort(edad, (short) 1, (short) 100,
                "La edad es requerida y debe ser positiva");

        ValoresNumericosUtils.validarDouble(peso, 0.1, 200,
                "El peso es requerido y debe ser positivo");

        ValoresNumericosUtils.validarDouble(estatura, 1.0, 200,
                "La estatura es requerida y debe ser positiva");
    }

    public String obtenerNombreCompleto(){
        return String.join(
                "",
                this.getNombre(),
                this.getApellidoPaterno(),
                this.getApellidoMaterno());
    }

    public static Double calcularImc(double peso, double estatura){
        return peso / (estatura * estatura);
    }

    public void validarNoEliminado(){
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("El paciente ya esta eliminado");
    }

    public static String generarExpediente(String telefono) {
        return telefono.trim().chars()
                .mapToObj(c -> (char) c + "X")
                .collect(Collectors.joining());
    }

    public void eliminar(){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizar(String nombre,String apellidoPaterno, String apellidoMaterno,Short edad,
                           double peso, double estatura, String email,
                           String telefono, String direccion){
        validarNoEliminado();

        validarDatos(nombre, apellidoPaterno, apellidoMaterno,edad,
                peso, estatura, email, telefono, direccion);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.imc = calcularImc(peso,estatura);
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.numExpediente=generarExpediente(telefono);
        this.direccion = direccion.trim();
    }

    public static Paciente crear(String nombre,String apellidoPaterno, String apellidoMaterno,Short edad,
                                 double peso, double estatura, String email,
                                 String telefono, String direccion){

        validarDatos(nombre,apellidoPaterno,apellidoMaterno,edad,peso,estatura,email,telefono,direccion);

        return Paciente.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .peso(peso)
                .estatura(estatura)
                .email(email.trim().toLowerCase())
                .telefono(telefono.trim())
                .direccion(direccion.trim())
                .numExpediente(generarExpediente(telefono))
                .imc(calcularImc(peso,estatura))
                .build();
    }




}
