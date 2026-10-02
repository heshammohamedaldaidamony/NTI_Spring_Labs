package com.example.four.handler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AnnotatedController {

    public AnnotatedController() {
    }

    @GetMapping("/annotated")
    public String annotated(@RequestParam(defaultValue = "42") int answer,
                            Model model) {
        model.addAttribute("shape", "@Controller + @GetMapping");
        model.addAttribute("answer", answer);
        model.addAttribute("note",
                "The int was parsed for you by RequestParamMethodArgumentResolver.");
        return "annotated";
    }
}