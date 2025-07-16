package com.veritas.asset.management.api.integration;

import com.veritas.asset.management.api.AssetManagementApiApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *  Integration tests for the {@link AssetManagementApiApplication} class.
 */
@SpringBootTest
class AssetManagementApiApplicationIT {
	@Test
	void contextLoads() {
		Assertions.assertDoesNotThrow(() -> {
			AssetManagementApiApplication.main(new String[]{});
		});
	}
}
