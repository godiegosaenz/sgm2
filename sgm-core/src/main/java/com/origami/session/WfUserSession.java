/*
 *  Origami Solutions
 */
package com.origami.session;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author fernando
 */
public interface WfUserSession extends Serializable {

    String getDepartamento();

    Boolean getEsDirector();

    String getName();

    String getName_user();

    String getNombrePersonaLogeada();

    //Long getRolId();

    Boolean getTemp();

    String getUrlSolicitada();

    Long getUserId();

    List<Long> getRoles();

    List<Long> getDepts();
    
    String getIpClient();

}
