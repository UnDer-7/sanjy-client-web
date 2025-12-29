package br.com.gorillaroxo.sanjy.client.web;

import br.com.gorillaroxo.sanjy.client.web.config.SanjyClientWebConfigProp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties(SanjyClientWebConfigProp.class)
@EnableFeignClients(basePackages = "br.com.gorillaroxo.sanjy.client.web.client")
public class SanjyClientWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SanjyClientWebApplication.class, args);
    }

}
