package util;

import com.origami.config.SisVars;

/**
 * @author Carlos Loor Vargas
 */
public class EmailConfigs {

    private String mailServerHost;
    private int mailServerPort;
    private String mailServerUsername;
    private String mailServerPassword;
    private boolean mailServerUseSSL;

    public EmailConfigs() {
        this.mailServerHost = SisVars.smtp_Host;
        this.mailServerPort = Integer.parseInt(SisVars.smtp_Port);
        this.mailServerUseSSL = SisVars.ssl;
        this.mailServerUsername = SisVars.correo;
        this.mailServerPassword = SisVars.pass;

    }

    public EmailConfigs(String mailServerHost, int mailServerPort, String mailServerUsername, String mailServerPassword, boolean mailServerUseSSL) {
        this.mailServerHost = mailServerHost;
        this.mailServerPort = mailServerPort;
        this.mailServerUsername = mailServerUsername;
        this.mailServerPassword = mailServerPassword;
        this.mailServerUseSSL = mailServerUseSSL;
    }

    public String getMailServerHost() {
        return mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public int getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(int mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public String getMailServerUsername() {
        return mailServerUsername;
    }

    public void setMailServerUsername(String mailServerUsername) {
        this.mailServerUsername = mailServerUsername;
    }

    public String getMailServerPassword() {
        return mailServerPassword;
    }

    public void setMailServerPassword(String mailServerPassword) {
        this.mailServerPassword = mailServerPassword;
    }

    public boolean isMailServerUseSSL() {
        return mailServerUseSSL;
    }

    public void setMailServerUseSSL(boolean mailServerUseSSL) {
        this.mailServerUseSSL = mailServerUseSSL;
    }

}
