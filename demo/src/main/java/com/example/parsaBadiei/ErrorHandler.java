package com.example.parsaBadiei;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorHandler implements ErrorController {
    @GetMapping("/error")
    public String handleError(){ return "Don't know what to do now!";}

}
