package com.vindicator.security.repository;

import com.vindicator.security.model.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AssetRepository extends MongoRepository<Asset, String> {
    Optional<Asset> findByAssetId(String assetId);
    Optional<Asset> findByName(String name);
}
