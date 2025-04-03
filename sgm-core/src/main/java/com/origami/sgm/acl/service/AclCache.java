/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.acl.service;

import com.origami.sgm.acl.AccessLevelRequest;
import com.origami.sgm.acl.RespuestaAcceso;
import com.origami.sgm.acl.entity.AclUrl;
import com.origami.sgm.entities.AclRol;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Fernando
 */
@Local
public interface AclCache {
 
    Boolean checkAccess(String url, Long idUser, List<Long> rolesIds);

    void clear();
    
    public Boolean getEnabled();

    public void setEnabled(Boolean enabled);

    public List<AclRol> getRoles_url(AclUrl urlEnt);

    public List<AclRol> getRoles_url(Long idUrl);

    public RespuestaAcceso checkAccessLevel(AccessLevelRequest alreq);
    
}
