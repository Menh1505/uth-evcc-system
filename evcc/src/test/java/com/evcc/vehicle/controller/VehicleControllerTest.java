package com.evcc.vehicle.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Đây là file test TÍCH HỢP cho Vehicle.
 * Nó sẽ nạp ứng dụng Spring Boot và test API.
 */
@SpringBootTest // 1. Báo cho Spring Boot "Hãy nạp cả ứng dụng lên"
@AutoConfigureMockMvc // 2. Báo "Hãy cho tôi 1 công cụ giống Postman"
public class VehicleControllerTest {

    // 3. Đây chính là "Postman" của bạn, được Spring tiêm vào
    @Autowired
    private MockMvc mockMvc;

    // 4. Đây là một ca test (giống 1 request trong Postman)
    @Test
    public void testCreateVehicle_WhenDataIsValid_ShouldReturnCreated() throws Exception {
        
        // 5. Chuẩn bị "Body" JSON
        String vehicleJson = """
            {
              "licensePlate": "51K-99988",
              "model": "Vinfast VF9 Test",
              "vinNumber": "VIN-TEST-999",
              "modelYear": 2025,
              "batteryCapacityKWh": 100.0
            }
            """;

        // 6. Gửi request POST y hệt Postman
        mockMvc.perform(
                post("/api/v1/vehicles/") // Gửi tới API này
                        .contentType(MediaType.APPLICATION_JSON) // Kiểu JSON
                        .content(vehicleJson) // Với "Body" này
            )
            // 7. Kiểm tra kết quả
            .andExpect(status().isCreated()) // Mong đợi trả về 201 Created
            .andExpect(jsonPath("$.id").exists()) // Mong đợi có "id" trong JSON trả về
            .andExpect(jsonPath("$.model").value("Vinfast VF9 Test"));
    }
}