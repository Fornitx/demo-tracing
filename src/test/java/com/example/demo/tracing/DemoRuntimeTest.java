package com.example.demo.tracing;

import com.example.demo.tracing.utils.Profiles;
import org.junit.jupiter.api.Test;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureTracing
@ActiveProfiles(Profiles.BATCH)
class DemoRuntimeTest {
	@Test
	void test() {
	}
}
