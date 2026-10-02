package com.example.four.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class HomeController extends AbstractController {

    public HomeController() {
        setSupportedMethods("GET", "HEAD");
        setCacheSeconds(0);
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req,
                                                 HttpServletResponse res) {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("shape", "AbstractController");
        mav.addObject("note",
                "Template Method: handleRequest() ran the method and cache checks, "
                        + "then delegated to handleRequestInternal().");
        return mav;
    }
}