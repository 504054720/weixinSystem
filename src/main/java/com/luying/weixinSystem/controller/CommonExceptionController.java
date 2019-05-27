package com.luying.weixinSystem.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionController {

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(){
        return "error/500";
    }
}
