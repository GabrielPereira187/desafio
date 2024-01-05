package br.com.desafio.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {
    public static String getToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
