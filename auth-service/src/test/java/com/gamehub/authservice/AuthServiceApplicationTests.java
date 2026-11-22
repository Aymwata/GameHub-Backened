package com.gamehub.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"jwt.secret=gamehub-test-clave-ficticia-de-32-bytes",
        "spring.datasource.url=jdbc:h2:mem:auth-test",
        "eureka.client.enabled=false"})
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
