/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.ejb.interfaces;

import javax.ejb.Local;
import javax.sql.DataSource;
/**
 *
 * @author carlosloorvargas
 */
@Local
public interface DatabaseLocal {
    
    public DataSource getDataSource();
    
    public DataSource getMsqlDataSource();
    
    public DataSource getSgmDocsDataSource();
    
    public DataSource getDbOldDataSource();
    
    public DataSource getDbOldActDataSource();
    
}
