package com.veritas.transaction.api.client;

import com.veritas.transaction.api.dto.AssetManagementResponse;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * A Feign client interface for interacting with the Asset Management API.
 */
@FeignClient(name = "asset-management-api")
public interface AssetManagementClient {

  @Retry(name = "asset-management")
  /**
   *
   * Retrieves asset availability information from the Asset Management API.
   * 
   * @param assetCode The list of asset codes to check availability for.
   * @param amount The list of amounts to check for each asset code.
   * @return A list of AssetManagementResponse objects containing asset
   *         availability information.
   */
  @GetMapping("/api/asset-management")
  List<AssetManagementResponse> checkAssetAvailability(@RequestParam List<String> assetCode, @RequestParam List<Integer> amount);

  /**
   * Updates the amount of an asset by asset code.
   * @param assetCode The asset code to update.
   * @param amount The amount to add (can be negative for deduction).
   */
  @PostMapping("/api/asset-management/update-amount")
  void updateAssetAmount(@RequestParam("assetCode") String assetCode, @RequestParam("amount") int amount);
}
