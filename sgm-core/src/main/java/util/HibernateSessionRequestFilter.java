/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.origami.session.UserSession;
import com.origami.sgm.acl.AccessLevelRequest;
import com.origami.sgm.acl.AclUtils;
import com.origami.sgm.acl.RespuestaAcceso;
import com.origami.sgm.services.ejbs.SisEjb;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.HibernateException;

/**
 *
 * @author Fernando
 */
@WebFilter(filterName = "HibernateSessionRequestFilter", urlPatterns = {"/*"})
public class HibernateSessionRequestFilter implements Filter {

    private static final boolean debug = false;
    public static ThreadLocal<String> holamundo = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "thread"; //To change body of generated methods, choose Tools | Templates.
        }
    };
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (debug) {
            log("HibernateSessionRequestFilter:doFilter()");
        }

        Boolean aclOk = checkAcl(request, response);
        if (Boolean.TRUE.equals(aclOk)) {
            executeChain(request, response, chain);
        } // end if aclOk==true
        else {
            // redirigir a pagina de acceso denegado
            redirectToDenied(request, response);
        }
    }

    protected void executeChain(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        doBeforeProcessing(request, response);

        Boolean soyIniciadorSession = false;
        if (!HibernateUtil.yaIniciada.get()) {
            soyIniciadorSession = true;
            HibernateUtil.yaIniciada.set(true);
        }

        Throwable problem = null;
        try {

            chain.doFilter(request, response);

            // flush y do commit si hay session-transaccion activa
            HiberUtil.flushAndCommit();
        } catch (HibernateException ex) {
            HiberUtil.rollback();
            ex.printStackTrace();
            // do rollback
        } catch (Throwable t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
            t.printStackTrace();
        } finally {
//            Session sess = HibernateUtil.getSessionFactory().getCurrentSession();
//            sess.close();
//            ManagedSessionContext.unbind(HibernateUtil.getSessionFactory());
            HiberUtil.rollbackOnlyCheck();
            HiberUtil.closeSession();
            HibernateUtil.yaIniciada.set(false);
        }

        doAfterProcessing(request, response);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    public HibernateSessionRequestFilter() {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("HibernateSessionRequestFilter:DoBeforeProcessing");
        }

//        HttpServletRequest sr = (HttpServletRequest) request;
//        System.out.println("> " + sr.getRequestURI());
        // ejecutar reglas de accesos
        // Write code here to process the request and/or response before
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log items on the request object,
        // such as the parameters.
        /*
         for (Enumeration en = request.getParameterNames(); en.hasMoreElements(); ) {
         String name = (String)en.nextElement();
         String values[] = request.getParameterValues(name);
         int n = values.length;
         StringBuffer buf = new StringBuffer();
         buf.append(name);
         buf.append("=");
         for(int i=0; i < n; i++) {
         buf.append(values[i]);
         if (i < n-1)
         buf.append(",");
         }
         log(buf.toString());
         }
         */
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("HibernateSessionRequestFilter:DoAfterProcessing");
        }

        // Write code here to process the request and/or response after
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log the attributes on the
        // request object after the request has been processed. 
        /*
         for (Enumeration en = request.getAttributeNames(); en.hasMoreElements(); ) {
         String name = (String)en.nextElement();
         Object value = request.getAttribute(name);
         log("attribute: " + name + "=" + value.toString());

         }
         */
        // For example, a filter might append something to the response.
        /*
         PrintWriter respOut = new PrintWriter(response.getWriter());
         respOut.println("<P><B>This has been appended by an intrusive filter.</B>");
         */
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("HibernateSessionRequestFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("HibernateSessionRequestFilter()");
        }
        StringBuffer sb = new StringBuffer("HibernateSessionRequestFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    protected Boolean checkAcl(ServletRequest request, ServletResponse response) {
        AccessLevelRequest alReq = new AccessLevelRequest();

        HttpSession session = ((HttpServletRequest) request).getSession();
        session.setMaxInactiveInterval(60 * 120);
        UserSession us1 = (session != null) ? (UserSession) session.getAttribute("userSession") : null;
        // setear user_id, null si no es usuario logueado(guest)
        if (us1 == null || us1.getUserId() == null || us1.getUserId() < 0L) {
            alReq.setIdUser(null);
        } else {
            //set id y username
            alReq.setIdUser(us1.getUserId());
            alReq.setUsuario(us1.getName());
            alReq.setEsSuperUser(us1.getEsSuperUser());
            // setear los roles:
            alReq.setRolesIds(us1.getRoles());
        }
        // setear la url unificada (sin domain, sin contextname, sin /faces/)
        alReq.setUrlAcceso(AclUtils.getUnifiedUrl((HttpServletRequest) request));
        RespuestaAcceso resp = SisEjb.aclCacheServ().checkAccessLevel(alReq);

        return resp.getAccessGranted();
    }

    protected void redirectToDenied(ServletRequest request, ServletResponse response) {
        HttpServletRequest hreq = (HttpServletRequest) request;
        try {
            ((HttpServletResponse) response).sendRedirect(hreq.getContextPath() + "/faces/denied.xhtml");

        } catch (IOException ex) {
            Logger.getLogger(HibernateSessionRequestFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
