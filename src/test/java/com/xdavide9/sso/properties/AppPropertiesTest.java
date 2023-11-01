package com.xdavide9.sso.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// integration test to see if app properties are correctly injected in AppProperties POJO

@SpringBootTest()
class AppPropertiesTest {

    @Autowired
    private AppProperties underTest;
    @Test
    void itShouldInjectAppProperties() {
        // given
        String name = "sso";
        String version = "0.0.1-SNAPSHOT";
        String description = "sso";
        String jdk = "21";
        String groupId = "com.xdavide9";
        // when
        String returnedName = underTest.getName();
        String returnedVersion = underTest.getVersion();
        String returnedDescription = underTest.getDescription();
        String returnedJdk = underTest.getJdk();
        String returnedGroupId = underTest.getGroupId();
        // then
        assertThat(returnedName).isEqualTo(name);
        assertThat(returnedVersion).isEqualTo(version);
        assertThat(returnedDescription).isEqualTo(description);
        assertThat(returnedJdk).isEqualTo(jdk);
        assertThat(returnedGroupId).isEqualTo(groupId);
    }
}