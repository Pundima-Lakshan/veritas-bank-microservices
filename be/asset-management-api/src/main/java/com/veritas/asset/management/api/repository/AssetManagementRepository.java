package com.veritas.asset.management.api.repository;

import com.veritas.asset.management.api.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for storing assets in the database.
 * It provides CRUD operations and other database-related functionality.
 */
public interface AssetManagementRepository extends JpaRepository<Asset, Long> {
  List<Asset> findByAssetCodeIn(List<String> assetCode);
}
