package com.veritas.asset.management.api.controller;

import com.veritas.asset.management.api.dto.AssetManagementResponse;
import com.veritas.asset.management.api.service.AssetManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class that handles HTTP requests related to asset management.
 */
@RestController
@RequestMapping("/api/asset-management")
@RequiredArgsConstructor
@Slf4j
public class AssetManagementController {

  private final AssetManagementService assetManagementService;

  /**
   *
   * Retrieves the availability status of assets based on their codes and requested amounts.
   * 
   * @param assetCode The list of asset codes to check availability for.
   * @param amount The list of amounts to check for each asset code.
   * @return The list of asset management responses containing the availability
   *         status for each asset code and amount.
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<AssetManagementResponse> isAssetAvailable(@RequestParam List<String> assetCode, @RequestParam List<Integer> amount) {
    log.info("Received asset availability check request for asset code: {} and amount: {}", assetCode, amount);
    return assetManagementService.isAssetAvailable(assetCode, amount);
  }

  /**
   * Updates the amount of an asset by asset code.
   * @param assetCode The asset code to update.
   * @param amount The amount to add (can be negative for deduction).
   * @return Success message.
   */
  @PostMapping("/update-amount")
  @ResponseStatus(HttpStatus.OK)
  public String updateAssetAmount(@RequestParam String assetCode, @RequestParam int amount) {
    log.info("Received asset amount update request for asset code: {} with amount: {}", assetCode, amount);
    assetManagementService.updateAssetAmount(assetCode, amount);
    return "Asset amount updated successfully";
  }
}
