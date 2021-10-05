package com.example.cqrs.app.rest;

import an.awesome.pipelinr.Pipeline;
import com.example.cqrs.app.service.Ping;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
public class PipelinerController {

    private Pipeline pipeline;

    @ResponseBody
    @GetMapping("testPipe")
    public String testPipe(){
        return new Ping("PONGA PONGA").execute(pipeline);
    }
}
