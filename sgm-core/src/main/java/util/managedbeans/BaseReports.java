package util.managedbeans;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author carlosloorvargas
 */
@Named
@ViewScoped
public class BaseReports implements Serializable {

    private String reportName;
    private String reportPath;
    private HashMap<String, Object> reportParameters;
    private JasperReport reporte;
    private JasperPrint reporte_view;
    private Date reportDate;
    private static final Long serialVersionUID = 1L;

    public BaseReports(String reportPath, HashMap<String, Object> reportParameters) {
        this.reportPath = reportPath;
        this.reportParameters = reportParameters;
    }

    /**
     * funcion: Genera un archivo pdf sin data source. Parametros Internos :
     * reportPath -> REPORTESWEB/test.jasper. Parametros HashMap<K,V>:
     * reportParameters.
     *
     */
    public void print2PDF_EDS() {
        try {
            String in = FacesContext.getCurrentInstance().getExternalContext().getRealPath(this.getReportPath());
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            reporte_view = JasperFillManager.fillReport(in, this.getReportParameters(), new JREmptyDataSource());
            //System.out.println("//::reporte_view " + reporte_view.getName());
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + reporte_view.getName() + ".pdf");
            ServletOutputStream servletStream = httpServletResponse.getOutputStream();
            JasperExportManager.exportReportToPdfStream(reporte_view, servletStream);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException | JRException e) {
            Logger.getLogger(BaseReports.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public HashMap<String, Object> getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(HashMap<String, Object> reportParameters) {
        this.reportParameters = reportParameters;
    }

    public JasperReport getReporte() {
        return reporte;
    }

    public void setReporte(JasperReport reporte) {
        this.reporte = reporte;
    }

    public JasperPrint getReporte_view() {
        return reporte_view;
    }

    public void setReporte_view(JasperPrint reporte_view) {
        this.reporte_view = reporte_view;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

}
