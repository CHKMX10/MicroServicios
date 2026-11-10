package com.example.citas.service;


import com.example.citas.dto.CitaRequest;
import com.example.citas.dto.CitaResponse;
import com.example.citas.entity.Cita;
import com.example.citas.enums.EstadoCita;
import com.example.citas.mapper.CitaMapper;
import com.example.citas.repositories.CitaRepository;
import com.example.commons.enums.EstadoRegistro;
import com.example.commons.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService{

    private final CitaRepository citaRepository;

    private final CitaMapper citaMapper;

    @Override
    public List<CitaResponse> listar() {

        log.info("Listando todas las citas activas...");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(citaMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        return citaMapper.entidadAResponse(obtenerCitaActivaOException(id));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva Cita");

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada");

        return citaMapper.entidadAResponse(cita);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {

        Cita cita = obtenerCitaActivaOException(id);

        log.info("Actualizando Cita con ID: {}", id);

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        log.info("Cita actualizada");

        return citaMapper.entidadAResponse(cita);
    }


    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {

        Cita cita = obtenerCitaActivaOException(idCita);

        log.info("Actualizar el estado de la cita con ID: {}", idCita);

        cita.actualizarEstadoCita(
                EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita)
        );

        citaRepository.saveAndFlush(cita);

        log.info("Estado de la cita {} actualizado a {}", idCita, idEstadoCita);
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaActivaOException(id);

        log.info("Eliminando cita con ID: {}", id);

        cita.eliminar();

        log.info("Cita ha sido marcada como eliminada con ID: {}", id);
    }

    public Cita obtenerCitaActivaOException(Long id){

        log.info("Buscando cita con ID: {}", id);

        return citaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Cita no encontrada con ID: "+id));
    }


}
