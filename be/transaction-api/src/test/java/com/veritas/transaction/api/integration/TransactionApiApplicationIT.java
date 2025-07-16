package com.veritas.transaction.api.integration;

import com.veritas.transaction.api.TransactionApiApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for the {@link TransactionApiApplication} class.
 */
@SpringBootTest
class TransactionApiApplicationIT {
	@Test
	void contextLoads() {
		Assertions.assertDoesNotThrow(() -> {
			TransactionApiApplication.main(new String[]{});
		});
	}
}
