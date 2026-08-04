package com.vindicator.security.repository;

import com.vindicator.security.model.RemediationRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RemediationRuleRepository extends MongoRepository<RemediationRule, String> {
    Optional<RemediationRule> findByRuleId(String ruleId);
    Optional<RemediationRule> findByCveOrRulePattern(String cveOrRulePattern);
}
