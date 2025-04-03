/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.managedbeans;

import com.origami.sgm.entities.VuItems;
import com.origami.sgm.services.interfaces.registro.RegistroPropiedadServices;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Anyelo
 */
@Named
@ApplicationScoped
public class AppSimpleList implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @javax.inject.Inject
    protected RegistroPropiedadServices reg;
    
    public List<VuItems> getUsoDocumentoRegistro(){
        return reg.getUsosDocumentos();
    }
    
}
