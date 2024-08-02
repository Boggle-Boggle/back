package com.boggle_boggle.bbegok;

import com.boggle_boggle.bbegok.dto.base.DataResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/guest")
public class TestController {

    @GetMapping("/")
    public String initPage(){
        return "test";
    }

    @GetMapping("/test")
    @ResponseBody
    public DataResponseDto<String> test() {
        return DataResponseDto.of("HELLO, WORLD!");
    }
}
