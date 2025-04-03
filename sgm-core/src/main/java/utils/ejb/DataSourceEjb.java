package utils.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.sql.DataSource;
import util.DataBaseConfigs;
import utils.ejb.interfaces.DatabaseLocal;

/**
 * @author carlosloorvargas
 */
@Singleton(name = "dataSource")
@Lock(LockType.READ)
public class DataSourceEjb implements DatabaseLocal {

    private DataBaseConfigs dbc = null;
    private DataSource ds = null;
    private DataSource mds = null;
    private DataSource sgmDocsds = null;
    private DataSource dbOld = null;
    private DataSource dbOldAct = null;

    @PostConstruct
    protected void init() {
        dbc = new DataBaseConfigs();
        ds = dbc.getDataSource(1);
        mds = dbc.getDataSource(2);
        sgmDocsds = dbc.getDataSource(3);
        dbOld = dbc.getDataSource(4);
        dbOldAct = dbc.getDataSource(5);
    }

    @Override
    public DataSource getDataSource() {
        return ds;
    }

    @Override
    public DataSource getMsqlDataSource() {
        return mds;
    }

    @Override
    public DataSource getSgmDocsDataSource() {
        return sgmDocsds;
    }

    @Override
    public DataSource getDbOldDataSource() {
        return dbOld;
    }

    @Override
    public DataSource getDbOldActDataSource() {
        return dbOldAct;
    }

}
