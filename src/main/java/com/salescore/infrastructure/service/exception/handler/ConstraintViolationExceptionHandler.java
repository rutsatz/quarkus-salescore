package com.salescore.infrastructure.service.exception.handler;

import com.salescore.infrastructure.service.exception.Error;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return Response.status(Status.BAD_REQUEST)
                .entity(toErrorList(exception))
                .build();
    }

    private List<Error> toErrorList(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .map(Error::new)
                .collect(Collectors.toList());
    }
}
