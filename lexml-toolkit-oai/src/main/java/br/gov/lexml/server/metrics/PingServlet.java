package br.gov.lexml.server.metrics;

import com.sun.mail.iap.Response;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class PingServlet extends HttpServlet {
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        Writer w = response.getWriter();
        w.write("pong");
        w.close();
        response.setStatus(Response.OK);
    }
}
