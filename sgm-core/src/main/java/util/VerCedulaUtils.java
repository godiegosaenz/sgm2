package util;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Carlos Loor Vargas
 * @Date 28/10/2013
 * @version 2.0
 * @description Valida la cedula en base al digito verificador
 */
public class VerCedulaUtils implements Serializable {

    public static final Long serialVersionUID = 1L;
    
    private boolean flag;
    private String cedula;
    private int tamCedula = 10;
    private int[] coeficientes = new int[]{2, 1, 2, 1, 2, 1, 2, 1, 2};
    private int numProvincias = 24;
    private int tercerDigito = 6;
    private TipoEntidadUtils teu;
    

    public VerCedulaUtils() {
        teu = new TipoEntidadUtils();
    }

    public boolean comprobarDocumento(String documento) {
        boolean val = false;
        try {
            switch (documento.length()) {
                case 10:
                    val = this.isCIValida(documento);
                    break;
                case 13:
                    val = this.isRucValido(documento);
                    break;
            }
        } catch (Throwable e) {
            val = false;
            Logger.getLogger(VerCedulaUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return val;
    }

    public boolean isCIValida(String cedula) {
        int provincia, digitoTres, digitoVer, digitoVerObt, total = 0;
        this.tercerDigito = Integer.parseInt(cedula.substring(0, 2));
        try {
            if ((cedula.length() == this.getTamCedula()) && (cedula.matches("[0-9]*"))) {
                provincia = Integer.parseInt(cedula.charAt(0) + "" + cedula.charAt(1));
                digitoTres = Integer.parseInt(cedula.charAt(2) + "");
                if (provincia > 0 && provincia <= this.getNumProvincias()
                        && digitoTres < this.getTercerDigito()) {

                    digitoVer = Integer.parseInt(cedula.charAt(9) + "");
                    for (int i = 0; i < this.getCoeficientes().length; i++) {
                        int valor = Integer.parseInt(this.getCoeficientes()[i] + "") * Integer.parseInt(cedula.charAt(i) + "");
                        total = valor >= 10 ? total + (valor - 9) : total + valor;
                    }
                    digitoVerObt = total >= 10 ? (total % 10) != 0 ? 10 - (total % 10) : (total % 10) : total;
                    if (digitoVerObt == digitoVer) {
                        flag = true;
                        this.teu.setNatural(flag);
                        this.teu.setJuridica(false);
                        this.teu.setNacExt(false);
                        this.teu.settPersona(1);
                        this.teu.settDocumento("C");
                    } else {
                        flag = false;
                        this.teu.setNatural(flag);
                    }
                }
            }
        } catch (Throwable e) {
            flag = false;
            Logger.getLogger(VerCedulaUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }

    //ruc
    public int[] coeficientes(int digito) {
        int[] coeficients = null;
        switch (digito) {
            case 6:
                //coeficientes = new int[]{2,1,2,1,2,1,2,1,2};
                coeficients = new int[]{3, 2, 7, 6, 5, 4, 3, 2};
                break;
            case 9:
                //coeficientes = new int[]{4,3,2,7,6,5,4,3,2};
                coeficients = new int[]{4, 3, 2, 7, 6, 5, 4, 3, 2};
                break;
        }
        return coeficients;
    }

    public int getvalorRUC(String ruc, int[] coefValRuc) {
        int digito, suma = 0;
        for (int i = 0; i < ruc.length() - 4; i++) {
            digito = Integer.parseInt(ruc.substring(i, i + 1)) * coefValRuc[i];
            suma += ((digito % 10) + (digito / 10));
        }
        return suma;
    }

    public boolean getValides(int suma, int digito, int verificador) {
        flag = false;
        switch (digito) {
            case 6:
                if ((suma % 10 == 0) && (suma % 10 == verificador)) {
                    flag = true;
                } else flag = (10 - (suma % 10)) == verificador;
                break;
            case 9:
                if ((suma % 10 == 0) && (suma % 10 == verificador)) {
                    flag = true;
                } else if ((10 - (suma % 10)) == verificador) {
                    flag = true;
                } else {
                    flag = true;
                }
                break;
        }
        return flag;
    }

    public boolean isRucValido(String documento) {
        flag = false;
        if ((documento.length() == 13)) {
            int[] coef;
            int suma;
            int verificador = Integer.parseInt(documento.substring(9, 10));
            tercerDigito = Integer.parseInt(documento.substring(2, 3));
            coef = this.coeficientes(tercerDigito);
            if (coef != null) {
                suma = this.getvalorRUC(documento, coef);
                flag = this.getValides(suma, tercerDigito, verificador);
                teu.setJuridica(flag);
                if (flag == true) {
                    this.teu.setNatural(false);
                    teu.settPersona(2);
                    teu.settDocumento("R");
                }
            } else {
                String x = documento.substring(0, 10);
                if (this.isCIValida(x) == true) {
                    teu.settPersona(1);
                    flag = true;
                    teu.settDocumento("R");
                    if(flag==true){
                        this.teu.setJuridica(false);
                        this.teu.setNacExt(false);
                    }
                    this.teu.setNatural(flag);
                }
            }
        }
        return flag;
    }

    public boolean validar(String documento) {
        boolean x = false;
        switch (documento.length()) {
            case 10:
                x = this.isCIValida(documento);
                break;
            case 13:
                x = this.isRucValido(documento);
                break;
        }
        return x;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public int getTamCedula() {
        return tamCedula;
    }

    public void setTamCedula(int tamCedula) {
        this.tamCedula = tamCedula;
    }

    public int[] getCoeficientes() {
        return coeficientes;
    }

    public void setCoeficientes(int[] coeficientes) {
        this.coeficientes = coeficientes;
    }

    public int getNumProvincias() {
        return numProvincias;
    }

    public void setNumProvincias(int numProvincias) {
        this.numProvincias = numProvincias;
    }

    public int getTercerDigito() {
        return tercerDigito;
    }

    public void setTercerDigito(int tercerDigito) {
        this.tercerDigito = tercerDigito;
    }

    public TipoEntidadUtils getTeu() {
        return teu;
    }

    public void setTeu(TipoEntidadUtils teu) {
        this.teu = teu;
    }

//    public static void main(String[] args) {
//        VerCedulaUtils vcu = new VerCedulaUtils();
//        vcu.comprobarDocumento("0926937442001");
//    }
}
