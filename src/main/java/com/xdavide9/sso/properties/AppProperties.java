package com.xdavide9.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * This class models properties with prefix "app" in properties files
 * to be used across the application.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    /**
     * It is the name of the application also specified in pom.xml
     */
    private String name;
    /**
     * It is the current version of the application also specified in pom.xml
     */
    private String version;
    /**
     * It is the description of the application also specified on GitHub
     */
    private String description;
    /**
     * It is the domain on which the application is hosted
     */
    private String groupId;
    /**
     * It is the java development kit version used to develop this application
     */
    private String jdk;

    @ConstructorBinding
    public AppProperties(String name,
                         String version,
                         String description,
                         String groupId,
                         String jdk) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.groupId = groupId;
        this.jdk = jdk;
    }

    // SETTERS

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

    // GETTERS

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
}
