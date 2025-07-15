package com.veritas.asset.management.api.service;

import com.veritas.asset.management.api.dto.AssetManagementResponse;
import com.veritas.asset.management.api.repository.AssetManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class that provides operations for managing assets.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssetManagementService {

  private final AssetManagementRepository assetManagementRepository;

  /**
   *
   * Checks the availability of assets based on their codes and requested amounts.
   * 
   * @param assetCode The list of asset codes to check.
   * @param amount The list of amounts to check for each asset code.
   * @return A list of AssetManagementResponse objects indicating the availability
   *         of each asset for the requested amount.
   */
  @Transactional(readOnly = true)
  @SneakyThrows
  @Cacheable("assetAvailability")
  public List<AssetManagementResponse> isAssetAvailable(List<String> assetCode, List<Integer> amount) {
    log.info("Checking asset availability");
    var assets = assetManagementRepository.findByAssetCodeIn(assetCode);
    return assets.stream().map(asset -> {
      int idx = assetCode.indexOf(asset.getAssetCode());
      int requestedAmount = (idx >= 0 && idx < amount.size()) ? amount.get(idx) : 1;
      return AssetManagementResponse.builder()
        .assetCode(asset.getAssetCode())
        .isAssetAvailable(asset.getValue() >= requestedAmount)
        .build();
    }).toList();
  }
}
