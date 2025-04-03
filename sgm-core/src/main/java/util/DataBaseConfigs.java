package util;

import com.origami.config.SisVars;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author carlosloorvargas
 */
public class DataBaseConfigs {

    private String path;
    private BasicDataSource ds = null;
    private DataBaseParameters dbp = null;

    public DataBaseConfigs(String path) {
        this.path = path;
    }

    public DataBaseConfigs() {

    }

    private DataBaseParameters getParameters(int tcon) {
        DataBaseParameters p;
        try {
            p = new DataBaseParameters();
            switch (tcon) {
                case 1:
                    p.setDriverClass(SisVars.driverClass);
                    p.setUserName(SisVars.userName);
                    p.setPassword(SisVars.password);
                    p.setUrl(SisVars.url);
                    p.setMinPoolSize(SisVars.minPoolSize);
                    p.setMaxPoolSize(SisVars.maxPoolSize);
                    break;
                case 2:
                    p.setDriverClass(SisVars.sqlServerDriverClass);
                    p.setUserName(SisVars.userSqlServer);
                    p.setPassword(SisVars.passwordSqlServer);
                    p.setUrl(SisVars.sqlServerUrl);
                    p.setMinPoolSize(SisVars.minPoolSize);
                    p.setMaxPoolSize(SisVars.maxPoolSize);
                    break;
                case 3:
                    p.setDriverClass(SisVars.driverClassSgmDocs);
                    p.setUserName(SisVars.userNameSgmDocs);
                    p.setPassword(SisVars.passwordSgmDocs);
                    p.setUrl(SisVars.urlSgmDocs);
                    p.setMinPoolSize(SisVars.minPoolSizeSgmDocs);
                    p.setMaxPoolSize(SisVars.maxPoolSizeSgmDocs);
                    break;
                case 4:
                    p.setDriverClass(SisVars.driverClassDbOld);
                    p.setUserName(SisVars.userNameDbOld);
                    p.setPassword(SisVars.passwordDbOld);
                    p.setUrl(SisVars.urlDbOld);
                    p.setMinPoolSize(SisVars.minPoolSizeDbOld);
                    p.setMaxPoolSize(SisVars.maxPoolSizeDbOld);
                    break;
                case 5:
                    p.setDriverClass(SisVars.driverClassDbOldAct);
                    p.setUserName(SisVars.userNameDbOldAct);
                    p.setPassword(SisVars.passwordDbOldAct);
                    p.setUrl(SisVars.urlDbOldAct);
                    p.setMinPoolSize(SisVars.minPoolSizeDbOldAct);
                    p.setMaxPoolSize(SisVars.maxPoolSizeDbOldAct);
                    break;
            }
        } catch (Exception e) {
            p = null;
            Logger.getLogger(DataBaseConfigs.class.getName()).log(Level.SEVERE, null, e);
        }
        return p;
    }

    public DataSource getDataSource(int p) {
        try {
            dbp = this.getParameters(p);
            if (dbp != null) {
                ds = new BasicDataSource();
                ds.setUrl(dbp.getUrl());
                ds.setDriverClassName(dbp.getDriverClass());
                ds.setUsername(dbp.getUserName());
                ds.setPassword(dbp.getPassword());
                ds.setMaxIdle(dbp.getMaxIdleTime());
                ds.setMaxActive(dbp.getMaxPoolSize());
                ds.setMinIdle(dbp.getMinPoolSize());
                ds.setDefaultAutoCommit(false);
            }
        } catch (Exception e) {
            Logger.getLogger(DataBaseConfigs.class.getName()).log(Level.SEVERE, null, e);
        }
        return ds;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
