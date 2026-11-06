package br.com.lojinhadakatia.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registra a auditoria no prefixo de API administrativa. */
@Configuration
public class AdminAuditConfiguration implements WebMvcConfigurer {
    private final AdminAuditInterceptor interceptor;

    public AdminAuditConfiguration(AdminAuditInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/api/v1/admin/**");
    }
}
