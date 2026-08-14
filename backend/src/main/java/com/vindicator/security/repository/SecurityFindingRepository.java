package com.vindicator.security.repository;

import com.vindicator.security.model.SecurityFinding;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SecurityFindingRepository extends MongoRepository<SecurityFinding, String> {
    Optional<SecurityFinding> findByFindingId(String findingId);
    
    @org.springframework.data.mongodb.repository.Query("{ 'asset': ?0, 'cveOrRuleId': ?1, 'packageOrFile': ?2 }")
    Optional<SecurityFinding> findByAssetAndCveOrRuleIdAndPackageOrFile(String asset, String cveOrRuleId, String packageOrFile);
    List<SecurityFinding> findByAssetAndStatus(String asset, String status);
    List<SecurityFinding> findByStatus(String status);
    List<SecurityFinding> findBySeverityAndStatus(String severity, String status);
    long countByAssetAndStatusAndSeverity(String asset, String status, String severity);
}
