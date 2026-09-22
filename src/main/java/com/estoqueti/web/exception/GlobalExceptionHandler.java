package com.estoqueti.web.exception;

import com.estoqueti.core.exception.DAOException;
import com.estoqueti.core.exception.RegraNegocioException;
import com.estoqueti.web.dto.ApiErro;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte as excecoes da camada de negocio (Etapa 6: RegraNegocioException,
 * DAOException) em respostas HTTP com corpo JSON consistente, para que o
 * front-end (Etapa 8) sempre receba {"mensagem": "..."} e possa exibi-la
 * no toast de erro, em vez de uma stack trace HTML padrao do servidor.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ApiErro> tratarRegraNegocio(RegraNegocioException ex) {
        return ResponseEntity.badRequest().body(new ApiErro(400, ex.getMessage()));
    }

    @ExceptionHandler(DAOException.class)
    public ResponseEntity<ApiErro> tratarDAO(DAOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErro(500, "Erro de persistencia: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErro> tratarGenerico(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErro(500, "Erro inesperado: " + ex.getMessage()));
    }
}
