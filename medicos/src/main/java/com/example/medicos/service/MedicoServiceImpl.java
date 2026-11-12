package com.example.medicos.service;

import com.example.commons.clients.CitaClient;
import com.example.commons.dto.medicos.MedicoRequest;
import com.example.commons.dto.medicos.MedicoResponse;
import com.example.commons.enums.DisponibilidadMedico;
import com.example.commons.enums.EspecialidadMedico;
import com.example.commons.enums.EstadoRegistro;
import com.example.commons.exceptions.ConflictoException;
import com.example.commons.exceptions.RecursoNoEncontradoException;
import com.example.commons.service.CrudService;
import com.example.medicos.entity.Medico;
import com.example.medicos.mapper.MedicoMapper;
import com.example.medicos.repositories.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;

    private final MedicoMapper medicoMapper;

    private final CitaClient citaClient;

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Listando a todos los medicos activos00000");

        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse).toList();
    }

    @Override
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(obtenerMedicoActivoPorId(id));
    }

    @Override
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Buscando medico sin estado con ID: ", id);

        return medicoMapper.entidadAResponse(medicoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "Medico sin estado no encontrado con ID: " + id
                )
        ));
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoPorId(idMedico);

        log.info("Actualizando disponibilidad del medico con ID: {}", idMedico);

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico
                .obtenerDisponibilidadPorCodigo(idDisponibilidad);

        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();

        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        medicoRepository.saveAndFlush(medico);

        log.info("Disponibilidad del medico con ID {} cambio de {} a {}",
                idMedico, disponibilidadAnterior, nuevaDisponibilidad
        );
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {

        log.info("Registrando nuevo médico: {}", request.nombre());

        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(request);

        medico.actualizarEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(
                        request.idEspecialidad())
        );

        medicoRepository.saveAndFlush(medico);

        log.info("Nuevo medico registrado: {}", medico.obtenerNombreCompleto());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {

        Medico medico = obtenerMedicoActivoPorId(id);

        validarMedicoTieneCitasAgendadas(id);

        validarCambiosUnicos(request, id);

        log.info("Actualizando médico con ID: {}", id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad())
        );

        medicoRepository.saveAndFlush(medico);

        log.info("Medico con ID: {} actualizado correctamente", id);

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void eliminar(Long id) {

        Medico medico = obtenerMedicoActivoPorId(id);

        log.info("Elimar Medico con ID: {}", id);

        validarMedicoTieneCitasAgendadas(id);

        medico.eliminar();

        medicoRepository.saveAndFlush(medico);

        log.info("Se eliminó el medico exitosamente ");
    }

    private Medico obtenerMedicoActivoPorId(Long id){

        log.info("Obteniendo medico activo con ID: {}", id);

        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Médico activo no encontrado con ID: " + id));
    }

    private void validarDatosUnicos(MedicoRequest request){
        log.info("Obteniendo email unico");

        if(medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException(
                    "Ya existe un medico activo con el Email: " + request.email()
            );

        log.info("Obteniendo telefono unico");

        if(medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException(
                    "Ya existe un medico activo con el Telefono: " + request.telefono()
            );

        log.info("Obteniendo cedula profesional unica");

        if(medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException(
                    "Ya existe un medico activo registrado con la cedula profesional: "
                            + request.cedulaProfesional()
            );
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id){
        log.info("Validando cambio en email unico");

        if(medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException(
                    "Ya existe un medico activo registrado con el Email: " + request.email()
            );

        log.info("Validando cambio en telefono");

        if(medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException(
                    "Ya existe un medico activo registrado con el telefono: " + request.telefono()
            );

        log.info("Validando cambio cedula profesional unica");

        if(medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException(
                    "Ya existe un medico activo registrado con la cedula profesional: "
                            + request.cedulaProfesional()
            );
    }

    private void validarMedicoTieneCitasAgendadas(Long id){
        log.info("Validando si el medico con ID: {}"+
                "tiene citas agendadas en servicio remoto", id);

        citaClient.medicoTieneCitasAsignadas(id);
    }


}
