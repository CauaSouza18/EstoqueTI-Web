package com.estoqueti.web.dto;

import java.time.LocalDateTime;

/** Corpo padrao de erro devolvido pela API REST em respostas 4xx/5xx. */
public class ApiErro {

    private LocalDateTime momento;
    private int status;
    private String mensagem;

    public ApiErro(int status, String mensagem) {
        this.momento = LocalDateTime.now();
        this.status = status;
        this.mensagem = mensagem;
    }

    public LocalDateTime getMomento() { return momento; }
    public int getStatus() { return status; }
    public String getMensagem() { return mensagem; }
}
