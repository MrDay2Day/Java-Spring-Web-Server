package org.file.middleware.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * To apply an interceptor to specific routes in Spring Boot, you need to configure the InterceptorRegistry in your WebMvcConfigurer implementation. Instead of using registry.addInterceptor(myInterceptor).addPathPatterns("/**"); which applies the interceptor to all routes, you'll specify the exact URL patterns you want to intercept.
 *
 * Explanation of Changes:
 *
 *  addPathPatterns("/api/**", "/admin/**", "/specific/route"):
 *      This line specifies the URL patterns that the MyInterceptor should intercept.
 * "/api/**": This pattern will intercept all requests that start with /api/. The ** wildcard matches any number of path segments.
 *      "/admin/**": This pattern will intercept all requests that start with /admin/.
 *      "/specific/route": This pattern will only intercept requests that exactly match /specific/route.
 *      You can add as many patterns as you need, separated by commas.
 * 2. Excluding Routes:
 *
 * If you want to apply the interceptor to most routes but exclude certain ones, you can use excludePathPatterns():
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private MainInterceptor mainInterceptor;
    @Autowired
    private AuthInterceptor authInterceptor;
     @Autowired
     private CookieInterceptor cookieInterceptor;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/secure/**");

        registry.addInterceptor(mainInterceptor)
                .addPathPatterns("/api/**", "/admin/**", "/specific/route")
                .excludePathPatterns("/public/**", "/login", "/register");

        /* Adding Additional Interceptor patterns */
         registry.addInterceptor(cookieInterceptor)
                 .addPathPatterns("/*");

    }
}
