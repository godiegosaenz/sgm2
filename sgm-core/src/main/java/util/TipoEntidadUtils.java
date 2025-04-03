package util;

import java.io.Serializable;

/**
 * @author CarlosLoorVargas
 * @Date 28/10/2013
 * @version 1.0
 */
public class TipoEntidadUtils implements Serializable {

    private boolean natural = false;
    private boolean juridica = false;
    private boolean nacExt = false;
    private int tPersona;
    private String tDocumento;

    public static final Long serialVersionUID = 1L;

    public TipoEntidadUtils() {
    }

    public boolean getNatural() {
        return natural;
    }

    public void setNatural(boolean natural) {
        this.natural = natural;
        this.settPersona(1);
    }

    public boolean getJuridica() {
        return juridica;
    }

    public void setJuridica(boolean juridica) {
        this.juridica = juridica;
        this.settPersona(2);
    }

    public boolean getNacExt() {
        return nacExt;
    }

    public void setNacExt(boolean nacExt) {
        this.nacExt = nacExt;
    }

    public int gettPersona() {
        return tPersona;
    }

    public void settPersona(int tPersona) {
        this.tPersona = tPersona;
    }

    public String gettDocumento() {
        return tDocumento;
    }

    public void settDocumento(String tDocumento) {
        this.tDocumento = tDocumento;
    }

}
