package com.vindicator.security.repository;

import com.vindicator.security.model.RemediationRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RemediationRuleRepository extends MongoRepository<RemediationRule, String> {
    Optional<RemediationRule> findByRuleId(String ruleId);
    
    @org.springframework.data.mongodb.repository.Query("{ 'cveOrRulePattern': ?0 }")
    Optional<RemediationRule> findByCveOrRulePattern(String cveOrRulePattern);
}
