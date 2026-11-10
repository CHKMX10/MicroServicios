package com.example.pacientes.service;

import com.example.commons.dto.pacientes.PacienteReponse;
import com.example.commons.dto.pacientes.PacienteRequest;
import com.example.commons.enums.EspecialidadMedico;
import com.example.commons.enums.EstadoRegistro;
import com.example.commons.exceptions.ConflictoException;
import com.example.commons.exceptions.RecursoNoEncontradoException;
import com.example.pacientes.entity.Paciente;
import com.example.pacientes.mapper.PacienteMapper;
import com.example.pacientes.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PacienteServiceImpl implements PacienteService{

    private final PacienteRepository pacienteRepository;

    private final PacienteMapper pacienteMapper;

    @Override
    public PacienteReponse obtenerPacientePorIdSinEstado(Long id) {
        log.info("Listando a todos los pacientes sin estado......");

        return pacienteMapper.entidadAResponse(pacienteRepository.findById(id).
                orElseThrow(()-> new RecursoNoEncontradoException("No existe un paciente con el ID: " +id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteReponse> listar() {
        log.info("Listando a todos los pacientes activos");

        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(pacienteMapper::entidadAResponse).toList();
    }

    @Override
    public PacienteReponse obtenerPorId(Long id) {
        return pacienteMapper.entidadAResponse(obtenerPacienteActivoPorId(id));
        }

    @Override
    public PacienteReponse registrar(PacienteRequest request) {

        log.info("Registrando nuevo paciente: {}", request.nombre());

        validarDatosUnicos(request);

        Paciente paciente = pacienteMapper.requestAEntidad(request);

        pacienteRepository.saveAndFlush(paciente);

        log.info("Nuevo paciente registrado: {}", paciente.obtenerNombreCompleto());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteReponse actualizar(PacienteRequest request, Long id) {
        return null;
    }

    @Override
    public void eliminar(Long id) {

    }

    private void validarDatosUnicos(PacienteRequest request){
        log.info("Validando email unico");
        if(pacienteRepository.existByEmailIgnoreCaseAndEstadoRegistro(request.email(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un paciente activo registrado con el Email:" + request.email());

        log.info("Validando telefono unico");
        if(pacienteRepository.existByTelefonoAndEstadoRegistro(request.telefono(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un paciente activo registrado con el telefono: "+ request.telefono());
    }

    private void validarCambiosUnicos(PacienteRequest request, Long id){
        log.info("Validando email unico");
        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un paciente activo registrado con el email: "+request.email());

        log.info("Validando telefono unico");
        if (pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(request.telefono(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un paciente activo registrado con el télefono: " + request.telefono());
    }

    private Paciente obtenerPacienteActivoPorId(Long id) {

        log.info("Obteniendo paciente activo con ID: {}", id);

        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente activo no encontrado con id: " + id));
    }

    private Paciente obtenerPacienteNoActivoPorId(Long id) {

        log.info("Obteniendo paciente no activo con ID: {}", id);

        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente activo no encontrado con id: " + id));
    }
}
