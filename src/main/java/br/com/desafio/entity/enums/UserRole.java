package br.com.desafio.entity.enums;

public enum UserRole {
    ADMIN("admin"),
    ESTOQUISTA("estoquista");

    private String role;

    UserRole (String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
