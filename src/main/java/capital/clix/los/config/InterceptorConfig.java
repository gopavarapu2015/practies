package capital.clix.los.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import capital.clix.los.interceptor.RequestValidateInterceptor;

// @Configuration
public class InterceptorConfig implements WebMvcConfigurer {

  @Autowired
  RequestValidateInterceptor requestValidateInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestValidateInterceptor).addPathPatterns("/los/v1/loan/search");
  }
}
