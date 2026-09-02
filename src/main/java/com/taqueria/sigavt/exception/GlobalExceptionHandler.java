package com.taqueria.sigavt.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Recurso inexistente (404 Not Found)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetail> manejarEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ErrorDetail error = new ErrorDetail(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Solicitud o parámetro inválido (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetail> manejarMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        ErrorDetail error = new ErrorDetail(
                LocalDateTime.now(),
                "Error de validación en los datos enviados",
                errores.toString(), // mapa a String para mostrar los campos exactos
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Conflicto de integridad (409 Conflict)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetail> manejarDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        ErrorDetail error = new ErrorDetail(
                LocalDateTime.now(),
                "Conflicto de integridad. Es posible que intentes registrar un dato duplicado o eliminar un registro en uso.",
                request.getDescription(false),
                HttpStatus.CONFLICT.value()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetail> manejarGlobalException(Exception ex, WebRequest request) {
        ErrorDetail error = new ErrorDetail(
                LocalDateTime.now(),
                "Ocurrió un error interno en el servidor",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}