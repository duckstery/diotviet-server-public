package diotviet.server;

import diotviet.server.entities.Customer;
import diotviet.server.entities.Product;
import diotviet.server.templates.Customer.CustomerInteractRequest;
import diotviet.server.templates.Product.ProductInteractRequest;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableAsync
public class ServerApplication {

    /**
     * Server's props
     */
    @Value("${server.port}")
    private int serverPort;

    public static void main(String[] args) {
        // Set default TimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SpringApplication.run(ServerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printReady() {
        String line = StringUtils.repeat("-", 50);

        System.out.println(line);
        System.out.println("\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80 Application is ready! \uD83D\uDE80\uD83D\uDE80\uD83D\uDE80");
        try {
            System.out.printf("✅ Host: http://%s:%s%n", InetAddress.getLocalHost().getHostAddress(), serverPort);
        } catch (UnknownHostException e) {
            System.out.println("⚠️ Host: unknown");
        }

        System.out.println(line);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Config
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        // Skip Group when map from ProductInteractRequest to Product
        modelMapper.addMappings(new PropertyMap<ProductInteractRequest, Product>() {
            @Override
            protected void configure() {
                skip(destination.getGroups());
            }
        });

        // Skip Group when map from CustomerInteractRequest to Customer
        modelMapper.addMappings(new PropertyMap<CustomerInteractRequest, Customer>() {
            @Override
            protected void configure() {
                skip(destination.getGroups());
            }
        });

        return modelMapper;
    }
}
