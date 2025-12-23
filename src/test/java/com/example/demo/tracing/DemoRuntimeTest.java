package com.example.demo.tracing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.example.demo.tracing.RuntimeConfig.BATCH_PROFILE;

@SpringBootTest
@AutoConfigureTracing
@ActiveProfiles(BATCH_PROFILE)
class DemoRuntimeTest {
	@Test
	void test() {
	}
}
