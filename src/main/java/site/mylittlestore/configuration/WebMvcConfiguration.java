package site.mylittlestore.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.mylittlestore.interceptor.OrderItemInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final OrderItemInterceptor orderItemInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(orderItemInterceptor)
                .addPathPatterns("/members/*/stores/*/storeTables/*/orders/*/orderItems/*");
    }
}
