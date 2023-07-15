package pl.programodawca.drivergear;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
public class DriverGearApplication {

//    @Bean
//    public WebMvcConfigurer webMvcConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addResourceHandlers(ResourceHandlerRegistry registry) {
//                registry.addResourceHandler("/css/**")
//                        .addResourceLocations("classpath:/static/css/");
//                registry.addResourceHandler("/js/**")
//                        .addResourceLocations("classpath:/static/js/");
//                registry.addResourceHandler("/webjars/**")
//                        .addResourceLocations("/webjars/")
//                        .resourceChain(false);
//            }
//        };
//    }
//


    public static void main(String[] args) {
        SpringApplication.run(DriverGearApplication.class, args);
    }

//    @Bean
//    public LayoutDialect layoutDialect() {
//        return new LayoutDialect();
//    }

}
