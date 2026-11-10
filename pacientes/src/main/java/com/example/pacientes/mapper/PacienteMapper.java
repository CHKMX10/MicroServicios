package com.example.pacientes.mapper;

import com.example.commons.dto.pacientes.PacienteReponse;
import com.example.commons.dto.pacientes.PacienteRequest;
import com.example.commons.mapper.CommonMapper;
import com.example.pacientes.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteReponse, Paciente> {

    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        return request == null
                ? null
                : Paciente.crear(
                        request.nombre(),
                        request.apellidoPaterno(),
                        request.apellidoMaterno(),
                        request.edad(),
                        request.peso(),
                        request.estatura(),
                        request.email(),
                        request.telefono(),
                        request.direccion());
    }

    @Override
    public PacienteReponse entidadAResponse(Paciente entidad) {
        return entidad == null
                ? null
                :new PacienteReponse(
                entidad.getId(),
                entidad.obtenerNombreCompleto(),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getNumExpediente());
    }
}
