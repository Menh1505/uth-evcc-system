package evcc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class EvccFrontendApplicationTest {

    @Test
    public void contextLoads() {
        // Test để đảm bảo Spring context load thành công
    }
}