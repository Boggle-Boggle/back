package com.boggle_boggle.bbegok;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/")
    public String initPage(){
        return "test";
    }

    @GetMapping("/test")
    public String test() {
        return "Hello, World!";
    }
}
