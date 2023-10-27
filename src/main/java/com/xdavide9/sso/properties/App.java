package com.xdavide9.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class App {
    private String name;
    private String version;
    private String description;
    private String groupId;
    private String jdk;

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getJdk() {
        return jdk;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setJdk(String jdk) {
        this.jdk = jdk;
    }
}
