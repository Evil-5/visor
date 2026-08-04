package com.vindicator.security;

import com.vindicator.security.repository.AssetRepository;
import com.vindicator.security.repository.RemediationRuleRepository;
import com.vindicator.security.repository.ScanJobRepository;
import com.vindicator.security.repository.SecurityFindingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration," +
                "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration"
})
class VindicatorSecurityApplicationTests {

    @MockBean
    private SecurityFindingRepository findingRepository;

    @MockBean
    private ScanJobRepository scanJobRepository;

    @MockBean
    private AssetRepository assetRepository;

    @MockBean
    private RemediationRuleRepository remediationRuleRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    @Test
    void contextLoads() {
        // Verified Spring context loads cleanly with mocked database/auth infrastructure in test profile
    }
}
