/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.reportes;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import util.ApplicationContextUtils;
import util.CmisUtil;
import util.HiberUtil;
import util.Utils;
import util.managedbeans.BaseReports;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class ReportesView extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;

    @Inject
    private ServletSession servletSession;

    private JRBeanCollectionDataSource dataSource;
    private Map parametros;
    private CmisUtil cmis;
    private int read = 0;
    private HashMap<String, Object> params = new HashMap<>();

    private static final String UPLOAD_FOLDER = SisVars.rutaRepotiorioFichas;

    @PostConstruct
    public void init() {

    }

    public void descargarImagenArregloBytes(byte[] bytes) throws IOException {
        HttpServletResponse response;
        OutputStream outputStream = null;
        try {
            if (bytes == null) {
                return;
            }

            response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.reset();

            response.setContentType("image/JPEG");
            response.setCharacterEncoding("UTF-8");
            //response.setHeader("Content-disposition", "attachment; filename="+servletSession.getNombreReporte()+".pdf");
            response.setHeader("Content-disposition", "attachment; filename=inspeccion" + servletSession.getNombreReporte() + ".jpeg");
            response.setContentLength(bytes.length);

            outputStream = response.getOutputStream();

            outputStream.write(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            FacesContext.getCurrentInstance().responseComplete();
        }

    }

    public HttpServletResponse getResponse() {
        try {
            HttpServletResponse response;
            response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadPDFarregloBytesConsecutive(byte[] bytes, HttpServletResponse response, String ruta) throws IOException {
        OutputStream outputStream = null;
        try {
            byte[] byteStream = bytes;

            if (byteStream == null) {
                return;
            }
            writeBytesToFileNio(byteStream, ruta + servletSession.getNombreReporte() + ".pdf");
            writeBytesToFile(byteStream, ruta + servletSession.getNombreReporte() + ".pdf");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            FacesContext.getCurrentInstance().responseComplete();
        }
    }

    private static void writeBytesToFileClassic(byte[] bFile, String fileDest) {

        FileOutputStream fileOuputStream = null;

        try {
            fileOuputStream = new FileOutputStream(fileDest);
            fileOuputStream.write(bFile);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOuputStream != null) {
                try {
                    fileOuputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeBytesToFileNio(byte[] bFile, String fileDest) {

        try {
            Path path = Paths.get(fileDest);
            Files.write(path, bFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeBytesToFile(byte[] bFile, String fileDest) {

        try (FileOutputStream fileOuputStream = new FileOutputStream(fileDest)) {
            fileOuputStream.write(bFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void descargarPDFarregloBytes(byte[] bytes) throws IOException {
        HttpServletResponse response;
        OutputStream outputStream = null;

        try {
            byte[] byteStream = bytes;

            if (byteStream == null) {
                return;
            }

            response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.reset();

            response.setContentType("application/pdf");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + servletSession.getNombreReporte() + ".pdf");
            response.setContentLength(byteStream.length);

            outputStream = response.getOutputStream();

            outputStream.write(byteStream, 0, byteStream.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
                servletSession.borrarDatos();
            }
            FacesContext.getCurrentInstance().responseComplete();
        }
    }

    public InputStream descargarByteArrayDesdeAlfrescoPorURL(String url) {

        ContentStream fileStream = null;

        try {
            cmis = (CmisUtil) ApplicationContextUtils.getBean("cmisUtil");
            if (cmis != null) {
                fileStream = cmis.getDocument(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cmis = null;
        }
        if (fileStream != null) {
            return fileStream.getStream();
        } else {
            return null;
        }
    }

    public void generarReporte() throws SQLException, JRException, IOException {

        parametros = servletSession.getParametros();
        JasperPrint jasperPrint = null;
        HttpServletResponse response;
        OutputStream outputStream = null;
        byte[] byteStream = null;

//        String ruta = JsfUti.getRealPath("//reportes//" + servletSession.getNombreReporte() + ".jasper");
        InputStream ruta = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("//reportes//" + servletSession.getNombreReporte() + ".jasper");
        Utils.reemplazarRutaSubReportes(params);
        try {
            if (servletSession.getTieneDatasource()) {
                Session sess = HiberUtil.getSession();
                SessionImplementor sessImpl = (SessionImplementor) sess;
                Connection conn = sessImpl.getJdbcConnectionAccess().obtainConnection();
                byteStream = JasperRunManager.runReportToPdf(ruta, parametros, dataSource);
                conn.close();
            } else {
                dataSource = new JRBeanCollectionDataSource(new ArrayList());
                //jasperPrint = JasperFillManager.fillReport(ruta, parametros, dataSource);
                byteStream = JasperRunManager.runReportToPdf(ruta, parametros, dataSource);
                dataSource = null;
            }

            params.put("archivoByteArray", byteStream);
            params.put("tipoArchivoByteArray", "application/pdf");
            params.put("nombreArchivoByteArray", servletSession.getNombreReporte());

            this.completeTask(this.getTaskId(), params);
            servletSession.borrarDatos();

            response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.reset();

            response.setContentType("application/pdf");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=reporte.pdf");
            response.setContentLength(byteStream.length);
            outputStream = response.getOutputStream();

            outputStream.write(byteStream, 0, byteStream.length);
            //JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);  
            //servletOutputStream.flush();

        } catch (JRException e) {
            Logger.getLogger(BaseReports.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            FacesContext.getCurrentInstance().responseComplete();
        }

    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public JRBeanCollectionDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(JRBeanCollectionDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map getParametros() {
        return parametros;
    }

    public void setParametros(Map parametros) {
        this.parametros = parametros;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

}
