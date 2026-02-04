package com.example.demo.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import reactor.core.publisher.Hooks;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EnableConfigurationProperties(DemoProperties.class)
public class DemoTracingApplication {

	public static void main(String[] args) {
		Hooks.enableAutomaticContextPropagation();
//		ContextRegistry.getInstance().registerThreadLocalAccessor(
//			new ThreadLocalAccessor<Span>() {
//				@Override public Object key() { return MyContext.class; }
//				@Override public MyContext getValue() { return MyContext.current(); }
//				@Override public void setValue(MyContext value) { MyContext.set(value); }
//				@Override public void reset() { MyContext.clear(); }
//			}
//		);
		SpringApplication.run(DemoTracingApplication.class, args);
	}

}
