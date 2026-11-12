package com.example.pacientes.service;

import com.example.commons.clients.CitaClient;
import com.example.commons.dto.pacientes.PacienteReponse;
import com.example.commons.dto.pacientes.PacienteRequest;
import com.example.commons.enums.EstadoRegistro;
import com.example.commons.exceptions.ConflictoException;
import com.example.commons.exceptions.RecursoNoEncontradoException;
import com.example.pacientes.entity.Paciente;
import com.example.pacientes.mapper.PacienteMapper;
import com.example.pacientes.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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

    private final CitaClient citaClient;

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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

        Paciente paciente = obtenerPacienteActivoPorId(id);

        validarPacienteTieneCitasAgendadas(id);

        validarCambiosUnicos(request, id);

        log.info("Actualizando paciente con ID: {}", id);

        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion()
        );

        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente con ID: {} actualizado correctamente", id);

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {

        Paciente paciente = obtenerPacienteActivoPorId(id);

        log.info("Elimar Paciente con ID: {}", id);

        validarPacienteTieneCitasAgendadas(id);

        paciente.eliminar();

        pacienteRepository.saveAndFlush(paciente);

        log.info("Se eliminó el paciente exitosamente ");
    }

    private void validarDatosUnicos(@NonNull PacienteRequest request){
        log.info("Validando email unico");

        if(pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
                    throw new ConflictoException(
                        "Ya existe un paciente activo con el Email: " + request.email()
                );

        log.info("Obteniendo telefono unico");

        if(pacienteRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
                    throw new ConflictoException(
                        "Ya existe un paciente activo con el Telefono: " + request.telefono()
                );
    }

    private void validarCambiosUnicos(PacienteRequest request, Long id){

        log.info("Validando cambio en email");

        if(pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException(
                    "Ya existe un medico activo registrado con el Email: " + request.email()
            );

        log.info("Validando cambio en telefono");

        if(pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException(
                    "Ya existe un medico activo registrado con el telefono: " + request.telefono()
            );
    }
    private void validarPacienteTieneCitasAgendadas(Long id){
        log.info("Validando si el paciente con ID: {}"+
                "tiene citas agendadas en remoto", id);

        citaClient.pacienteTieneCitasAsignadas(id);
    }

    private Paciente obtenerPacienteActivoPorId(Long id) {

        log.info("Obteniendo paciente activo con ID: {}", id);

        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Paciente activo no encontrado con id: " + id));
    }

}
