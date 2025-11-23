package evcc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvccFrontendApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EvccFrontendApplication.class, args);
        System.out.println("Hello word");
    }
}
