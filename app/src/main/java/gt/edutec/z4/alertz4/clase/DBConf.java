package gt.edutec.z4.alertz4.clase;

public class DBConf {

    private static DBConf conf = null;

    private String url;
    private String usuario;
    private String password;
    private String dbName;

    public static DBConf getInstance(){
        if (conf == null) {
            conf = new DBConf();
        }
        return conf;
    }

    public DBConf() {
    }

    public DBConf(String url, String usuario, String password, String dbName) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
        this.dbName = dbName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getConnection(){
        return String.format("%s?=%s&%s&%s", this.url, this.usuario, this.password, this.dbName);
    }
}
