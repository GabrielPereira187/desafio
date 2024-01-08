package br.com.desafio.Product.controller.advice;

import br.com.desafio.Category.exception.CategoryNotFoundException;
import br.com.desafio.File.exception.EmptyFieldList;
import br.com.desafio.File.exception.FieldNotExistException;
import br.com.desafio.Product.exception.ProductNotFoundException;
import br.com.desafio.User.exception.UserNotFoundException;
import br.com.desafio.UserFieldVisibility.exception.UserVisibilityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String productNotFoundHandler(ProductNotFoundException ex){
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotFoundHandler(UserNotFoundException ex){
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String categoryNotFoundHandler(CategoryNotFoundException ex){
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(FieldNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String fieldNotExistHandler(FieldNotExistException ex){
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(EmptyFieldList.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String emptyFieldListHandler(EmptyFieldList ex){
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserVisibilityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userVisibilityNotFoundHandler(UserVisibilityNotFoundException ex){
        return ex.getMessage();
    }

}
