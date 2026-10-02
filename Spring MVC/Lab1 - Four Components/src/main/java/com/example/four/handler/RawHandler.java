package com.example.four.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.HttpRequestHandler;

import java.io.IOException;

public class RawHandler implements HttpRequestHandler {

    public RawHandler() {
    }

    @Override
    public void handleRequest(HttpServletRequest req,
                              HttpServletResponse res) throws IOException {
        res.setContentType("text/plain;charset=UTF-8");
        res.getWriter().write(
                "shape    : HttpRequestHandler\n" +
                        "mapping  : SimpleUrlHandlerMapping\n" +
                        "adapter  : HttpRequestHandlerAdapter\n" +
                        "view     : none - this handler wrote the response itself\n\n" +
                        "HttpRequestHandlerAdapter.handle() returns null, which tells\n" +
                        "DispatcherServlet \"the response is committed, skip seam 4\".\n");
    }
}