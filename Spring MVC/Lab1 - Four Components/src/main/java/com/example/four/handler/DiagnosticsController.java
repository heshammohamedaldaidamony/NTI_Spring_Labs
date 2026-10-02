package com.example.four.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.AbstractController;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticsController extends AbstractController {

    public DiagnosticsController() {
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req,
                                                 HttpServletResponse res) {
        ModelAndView mav = new ModelAndView("diagnostics");
        mav.addObject("mappings", namesOf(HandlerMapping.class));
        mav.addObject("adapters", namesOf(HandlerAdapter.class));
        mav.addObject("resolvers", namesOf(ViewResolver.class));
        return mav;
    }

    private List<String> namesOf(Class<?> type) {
        List<Object> beans = new ArrayList<>(
                obtainApplicationContext().getBeansOfType(type).values());
        AnnotationAwareOrderComparator.sort(beans);
        List<String> names = new ArrayList<>();
        for (Object bean : beans) {
            names.add(bean.getClass().getSimpleName());
        }
        return names;
    }
}