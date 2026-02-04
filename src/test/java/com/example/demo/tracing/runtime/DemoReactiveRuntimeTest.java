package com.example.demo.tracing.runtime;

import com.example.demo.tracing.bh.BlackHole;
import com.example.demo.tracing.utils.Profiles;
import org.junit.jupiter.api.Test;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(
    properties = {
        "demo.runtime=reactive"
    }
)
@AutoConfigureTracing
@ActiveProfiles(Profiles.BATCH)
class DemoReactiveRuntimeTest {
    @MockitoBean
    private BlackHole bh;

    @Test
    void test() {
        verify(bh, timeout(5000).times(5)).traceIdAndSpanId(anyString(), anyString());
    }
}
