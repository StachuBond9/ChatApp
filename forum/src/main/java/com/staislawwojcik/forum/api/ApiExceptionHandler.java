package com.staislawwojcik.forum.api;

import com.staislawwojcik.forum.api.response.ErrorResponse;
import com.staislawwojcik.forum.domain.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(exception = DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex){
        ErrorResponse errorResponse = new ErrorResponse(ex.getStatusCode(), ex.getMessage());
        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

}
