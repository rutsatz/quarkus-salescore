package com.salescore.infrastructure.service.exception.handler;

import com.salescore.infrastructure.service.exception.Error;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Status.NOT_FOUND)
                .entity(toErrorList())
                .build();
    }

    private List<Error> toErrorList() {
        return List.of(new Error("Resource not found"));
    }
}
