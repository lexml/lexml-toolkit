package br.gov.lexml.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import br.gov.lexml.borda.dao.JPAUtil;


public class CloseEntityManagerFilter implements Filter {

//    private static final Logger logger = Logger.getLogger(AbstractDAO.class.getName());
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) 
        throws IOException, ServletException {
        
        try {
            chain.doFilter(request, response);
        }
        finally {
            JPAUtil.closeEntityManager();
        }
    }

    public void init(final FilterConfig arg0) throws ServletException {
        // Desnecessário
    }

    public void destroy() {
        // Desnecessário
    }

}
