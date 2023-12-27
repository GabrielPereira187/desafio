package br.com.desafio.DTO.Response;

import lombok.Data;

import java.util.List;

@Data
public class MessagesResponse {

    private List<String> messages;
}
