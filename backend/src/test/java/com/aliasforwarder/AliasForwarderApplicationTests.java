package com.aliasforwarder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/test-alias-forwarder",
    "spring.security.oauth2.client.registration.oidc-provider.client-id=test",
    "spring.security.oauth2.client.registration.oidc-provider.client-secret=test",
    "spring.security.oauth2.client.provider.oidc-provider.issuer-uri=https://test.example.com",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
})
class AliasForwarderApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context loads without errors
    }
}
