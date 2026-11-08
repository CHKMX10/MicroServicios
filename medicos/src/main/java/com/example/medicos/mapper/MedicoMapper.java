package com.example.medicos.mapper;

import com.example.commons.dto.medicos.MedicoRequest;
import com.example.commons.dto.medicos.MedicoResponse;
import com.example.commons.mapper.CommonMapper;
import com.example.medicos.entity.Medico;
import org.springframework.stereotype.Component;


@Component
public class MedicoMapper implements CommonMapper<MedicoRequest, MedicoResponse, Medico> {

    @Override
    public Medico requestAEntidad(MedicoRequest request) {
        return request == null
                ? null
                : Medico.crear(
                        request.nombre(),
                        request.apellidoPaterno(),
                        request.apellidoMaterno(),
                        request.edad(),
                        request.email(),
                        request.telefono(),
                        request.cedulaProfesional());
    }

    @Override
    public MedicoResponse entidadAResponse(Medico entidad) {
        return entidad == null
                ? null
                : new MedicoResponse(
                        entidad.getId(),
                        entidad.obtenerNombreCompleto(),
                        entidad.getEdad(),
                        entidad.getEmail(),
                        entidad.getTelefono(),
                        entidad.getCedulaProfesional(),
                        entidad.getEspecialidad().getDescripcion(),
                        entidad.getDisponibilidad().getDescripcion(),
                        entidad.getDisponibilidad().getCodigo());
    }
}
