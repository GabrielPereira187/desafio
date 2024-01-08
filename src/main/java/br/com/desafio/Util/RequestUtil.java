package br.com.desafio.Util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {
    public static String getToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
