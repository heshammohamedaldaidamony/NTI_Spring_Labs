package com.example.four.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class PlainController extends AbstractController {

    public PlainController() {
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req,
                                                 HttpServletResponse res) {
        ModelAndView mav = new ModelAndView("plainTextView");
        mav.addObject("shape", "AbstractController");
        mav.addObject("resolvedBy", "BeanNameViewResolver");
        mav.addObject("hint",
                "change the view name to \"home\" and you get a JSP instead");
        return mav;
    }
}