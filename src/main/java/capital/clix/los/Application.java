package capital.clix.los;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.HandlerExceptionResolver;

@SpringBootApplication1(
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableScheduling
@ComponentScan(basePackages = {"capital.clix"})
@EnableHystrix
@EnableCircuitBreaker
public class Application {

  private static final Logger LOG = LogManager.getLogger(Application.class);

  public static void main(String[] args) {

    LOG.info("LOS service will now boot !!!!!");
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public HandlerExceptionResolver sentryExceptionResolver() {
    return new io.sentry.spring.SentryExceptionResolver();
  }

  @Bean
  public ServletContextInitializer sentryServletContextInitializer() {
    return new io.sentry.spring.SentryServletContextInitializer();
  }
}
