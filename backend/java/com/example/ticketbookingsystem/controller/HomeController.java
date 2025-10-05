package com.example.ticketbookingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", ""})
    public String root() {
        return "forward:/Mybooking.html";
    }

    @GetMapping({"/index", "/index.html"})
    public String index() {
        return "forward:/Mybooking.html";
    }
}



