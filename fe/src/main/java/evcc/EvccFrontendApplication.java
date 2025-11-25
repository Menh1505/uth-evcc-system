package evcc;

import evcc.service.BookingLocalService;
import evcc.service.ContractLocalService;
import evcc.service.GroupLocalService;
import evcc.service.UserLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvccFrontendApplication implements CommandLineRunner {

    @Autowired
    private UserLocalService userLocalService;

    @Autowired
    private GroupLocalService groupLocalService;

    @Autowired
    private ContractLocalService contractLocalService;

    @Autowired
    private BookingLocalService bookingLocalService;

    public static void main(String[] args) {
        SpringApplication.run(EvccFrontendApplication.class, args);
        System.out.println("Hello word");
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize sample booking data
        bookingLocalService.initializeSampleBookings();
        System.out.println("Sample data initialized successfully!");
    }
}
