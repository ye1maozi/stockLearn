package com.learn.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

//    @Value("${version}")
//    String version;
//
    @GetMapping("/")
    public String view(Model m) throws Exception
    {
        m.addAttribute("version","1.0.0");
        return "view";
    }
}
