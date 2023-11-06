package com.xdavide9.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
/**
 * This class models properties with prefix "app" in application.properties to be used across the application.
 * @author xdavide9
 * @since 0.0.1-SNAPSHOT
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    /**
     * application name
     * @since 0.0.1-SNAPSHOT
     */
    private String name;
    /**
     * application version
     * @since 0.0.1-SNAPSHOT
     */
    private String version;
    /**
     * application description
     * @since 0.0.1-SNAPSHOT
     */
    private String description;
    /**
     * application groupId
     * @since 0.0.1-SNAPSHOT
     */
    private String groupId;
    /**
     * applicatiœn jdk
     * @since 0.0.1-SNAPSHOT
     */
    private String jdk;

    /**
     * empty constructor
     * @since 0.0.1-SNAPSHOT
     */
    public AppProperties() {}

    /**
     * getter
     * @since 0.0.1-SNAPSHOT
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * getter
     * @since 0.0.1-SNAPSHOT
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * getter
     * @since 0.0.1-SNAPSHOT
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * getter
     * @since 0.0.1-SNAPSHOT
     * @return groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * getter
     * @since 0.0.1-SNAPSHOT
     * @return jdk
     */
    public String getJdk() {
        return jdk;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     * @param version version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     * @param groupId groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * This setter is used by Spring to inject the property value.
     * It should not be called anywhere else.
     * @since 0.0.1-SNAPSHOT
     * @param jdk jdk
     */
    public void setJdk(String jdk) {
        this.jdk = jdk;
    }
}
