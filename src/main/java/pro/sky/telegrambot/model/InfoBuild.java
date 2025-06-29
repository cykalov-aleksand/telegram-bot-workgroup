package pro.sky.telegrambot.model;

public class InfoBuild {
    private String name;
    private String version;
    InfoBuild(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "{" +"\n"+
                "name: " + name + "\n"+
                "version: " + version + "\n" +
                "}";
    }
}
