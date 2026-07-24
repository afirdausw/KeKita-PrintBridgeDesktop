package devyana.kekita.printbridge.api;

public class StatusData {

    private String application;
    private String version;

    public StatusData() {
    }

    public StatusData(String application, String version) {
        this.application = application;
        this.version = version;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
