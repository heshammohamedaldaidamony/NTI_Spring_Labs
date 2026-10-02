package com.example.four.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LegacyController implements Controller {

    public LegacyController() {
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest req,
                                      HttpServletResponse res) {
        String raw = req.getParameter("times");

        int times;
        try {
            times = (raw != null && !raw.isBlank()) ? Integer.parseInt(raw) : 3;
        } catch (NumberFormatException ex) {
            times = 3;
        }
        times = Math.max(0, Math.min(times, 20));

        ModelAndView mav = new ModelAndView("legacy");
        mav.addObject("shape", "Controller (interface)");
        mav.addObject("times", times);
        mav.addObject("echo", "ping ".repeat(times));
        return mav;
    }
}