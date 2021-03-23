package com.salescore.infrastructure.service.exception.handler;

import com.salescore.infrastructure.service.exception.Error;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class IllegalArgumentExceptionHandler implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response.serverError()
                .entity(toErrorList(exception))
                .build();
    }

    private List<Error> toErrorList(IllegalArgumentException exception) {
        return List.of(new Error(exception.getMessage()));
    }
}
