package com.example.commons.mapper;

public interface CommonMapper<RQ, RS, E> {//REQUEST = RQ, RESPONSE = RS, ENTIDAD = E,}

   //---------------------asi como se declaran se deben ocupar-------------

    E requestAEntidad(RQ request);

    RS entidadAResponse(E entidad);
}
