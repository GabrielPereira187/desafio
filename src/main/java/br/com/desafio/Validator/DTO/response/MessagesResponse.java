package br.com.desafio.Validator.DTO.response;

import lombok.Data;

import java.util.List;

@Data
public class MessagesResponse {

    private List<String> messages;
}
