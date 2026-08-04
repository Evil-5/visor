package com.vindicator.security.service;

import com.vindicator.security.dto.IngestScanRequest;
import com.vindicator.security.model.ScanJob;
import com.vindicator.security.model.SecurityFinding;
import com.vindicator.security.repository.ScanJobRepository;
import com.vindicator.security.repository.SecurityFindingRepository;
import com.vindicator.security.scanner.ScannerReportParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScanIngestionService {

    private static final Logger log = LoggerFactory.getLogger(ScanIngestionService.class);

    private final List<ScannerReportParser> parsers;
    private final SecurityFindingRepository findingRepository;
    private final ScanJobRepository scanJobRepository;
    private final MongoTemplate mongoTemplate;

    public ScanIngestionService(List<ScannerReportParser> parsers,
                                SecurityFindingRepository findingRepository,
                                ScanJobRepository scanJobRepository,
                                MongoTemplate mongoTemplate) {
        this.parsers = parsers;
        this.findingRepository = findingRepository;
        this.scanJobRepository = scanJobRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public ScanJob ingest(IngestScanRequest request) {
        log.info("Ingesting scan report for asset {} from scanner {}", request.getAsset(), request.getScanner());
        ScannerReportParser parser = parsers.stream()
                .filter(p -> p.supports(request.getScanner()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No parser supported for scanner: " + request.getScanner()));

        List<SecurityFinding> parsedFindings = parser.parse(request.getAsset(), request.getRawReportJson());
        int ingestedCount = 0;

        for (SecurityFinding finding : parsedFindings) {
            Optional<SecurityFinding> existing = findingRepository.findByAssetAndCveOrRuleIdAndPackageOrFile(
                    finding.getAsset(),
                    finding.getCveOrRuleId(),
                    finding.getPackageOrFile()
            );

            if (existing.isPresent()) {
                Query query = Query.query(Criteria.where("id").is(existing.get().getId()));
                Update update = new Update()
                        .set("lastSeenAt", Instant.now())
                        .set("status", "OPEN")
                        .set("installedVersion", finding.getInstalledVersion())
                        .set("fixedVersion", finding.getFixedVersion());

                mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), SecurityFinding.class);
            } else {
                findingRepository.save(finding);
            }
            ingestedCount++;
        }

        ScanJob job = ScanJob.builder()
                .jobId(UUID.randomUUID().toString())
                .asset(request.getAsset())
                .scannersRun(List.of(request.getScanner()))
                .findingsCount(ingestedCount)
                .status("COMPLETED")
                .startedAt(Instant.now())
                .completedAt(Instant.now())
                .createdBy("system")
                .build();

        return scanJobRepository.save(job);
    }
}
