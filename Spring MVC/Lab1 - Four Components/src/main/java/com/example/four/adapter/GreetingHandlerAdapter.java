package com.example.four.adapter;

import com.example.four.handler.GreetingHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

public class GreetingHandlerAdapter implements HandlerAdapter {

    public GreetingHandlerAdapter() {
    }

    @Override
    public boolean supports(Object handler) {
        return handler instanceof GreetingHandler;
    }

    @Override
    public ModelAndView handle(HttpServletRequest req,
                               HttpServletResponse res,
                               Object handler) {
        String name = req.getParameter("name");
        if (name == null || name.isBlank()) {
            name = "World";
        }

        String message = ((GreetingHandler) handler).greet(name);
        ModelAndView mav = new ModelAndView("greet");
        mav.addObject("shape", "GreetingHandler (our own interface)");
        mav.addObject("message", message);
        return mav;
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return -1L;
    }
}