<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Spring MVC Four Components Demo</title>
    <style>
        body { font-family: system-ui, sans-serif; max-width: 780px; margin: 2rem auto; line-height: 1.5; }
        h1 { border-bottom: 2px solid #333; padding-bottom: .3rem; }
        h2 { margin-top: 1.8rem; color: #444; }
        table { border-collapse: collapse; width: 100%; }
        th, td { text-align: left; padding: .4rem .6rem; border-bottom: 1px solid #ddd; }
        code { background: #f4f4f4; padding: .1rem .3rem; border-radius: 3px; }
        a { color: #06c; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>

<h1>Spring MVC — The Four Seams</h1>

<p>
    One <code>DispatcherServlet</code>, one XML file (<code>/WEB-INF/dispatcher-servlet.xml</code>).
    No <code>@EnableWebMvc</code>, no <code>&lt;mvc:annotation-driven/&gt;</code>,
    no component scan. Every strategy object is declared by hand so you can
    see all four seams at once.
</p>

<h2>Seam 1 &amp; 2 &amp; 3 — Handler routes</h2>
<table>
    <tr><th>URL</th><th>Handler shape</th><th>Mapping</th><th>Adapter</th></tr>
    <tr><td><a href="home">/home</a></td><td>AbstractController</td><td>SimpleUrlHandlerMapping</td><td>SimpleControllerHandlerAdapter</td></tr>
    <tr><td><a href="plain">/plain</a></td><td>AbstractController</td><td>SimpleUrlHandlerMapping</td><td>SimpleControllerHandlerAdapter</td></tr>
    <tr><td><a href="legacy">/legacy</a></td><td>Controller (interface)</td><td>BeanNameUrlHandlerMapping</td><td>SimpleControllerHandlerAdapter</td></tr>
    <tr><td><a href="annotated">/annotated</a></td><td>@Controller + @GetMapping</td><td>RequestMappingHandlerMapping</td><td>RequestMappingHandlerAdapter</td></tr>
    <tr><td><a href="raw">/raw</a></td><td>HttpRequestHandler</td><td>SimpleUrlHandlerMapping</td><td>HttpRequestHandlerAdapter</td></tr>
    <tr><td><a href="greet">/greet</a></td><td>GreetingHandler (ours!)</td><td>SimpleUrlHandlerMapping</td><td>GreetingHandlerAdapter (ours!)</td></tr>
    <tr><td><a href="about">/about</a></td><td>ParameterizableViewController</td><td>SimpleUrlHandlerMapping</td><td>SimpleControllerHandlerAdapter</td></tr>
    <tr><td><a href="go-home">/go-home</a></td><td>ParameterizableViewController</td><td>SimpleUrlHandlerMapping</td><td>SimpleControllerHandlerAdapter</td></tr>
</table>

<h2>Seam 4 — View resolution demos</h2>
<ul>
    <li><a href="home">/home</a> — view name <code>"home"</code> → JSP via InternalResourceViewResolver</li>
    <li><a href="plain">/plain</a> — view name <code>"plainTextView"</code> → our custom View bean via BeanNameViewResolver</li>
    <li><a href="go-home">/go-home</a> — view name <code>"goHome"</code> → RedirectView bean → HTTP 302</li>
</ul>

<h2>Diagnostics</h2>
<p><a href="diagnostics">/diagnostics</a> — dumps the three strategy lists DispatcherServlet built at startup.</p>

<p style="margin-top:2rem; color:#888; font-size:.9rem;">
    Context path: <code><%= request.getContextPath() %></code>
</p>

</body>
</html>