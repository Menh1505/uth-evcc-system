package evcc.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import evcc.dto.booking.BookingCreateRequestDto;

/**
 * Service to communicate with booking backend APIs
 */
@Service
public class BookingService {

    private final RestTemplate restTemplate;
    private final String backendUrl;

    public BookingService(RestTemplate restTemplate,
            @Value("${backend.url:http://localhost:8080}") String backendUrl) {
        this.restTemplate = restTemplate;
        this.backendUrl = backendUrl;
    }

    public Object createBooking(BookingCreateRequestDto request) {
        String url = backendUrl + "/api/bookings";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BookingCreateRequestDto> requestEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Object.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create booking: " + e.getMessage(), e);
        }
    }

    public Object getBookingById(Long id) {
        String url = backendUrl + "/api/bookings/" + id;

        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get booking: " + e.getMessage(), e);
        }
    }

    public List<Object> getUserBookings(UUID userId, LocalDate fromDate, LocalDate toDate) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(backendUrl + "/api/bookings/user/" + userId);

        if (fromDate != null) {
            uriBuilder.queryParam("fromDate", fromDate.toString());
        }
        if (toDate != null) {
            uriBuilder.queryParam("toDate", toDate.toString());
        }

        try {
            ResponseEntity<Object[]> response = restTemplate.getForEntity(
                    uriBuilder.toUriString(),
                    Object[].class
            );
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user bookings: " + e.getMessage(), e);
        }
    }

    public List<Object> getVehicleBookings(Long vehicleId, LocalDate fromDate, LocalDate toDate) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(backendUrl + "/api/bookings/vehicle/" + vehicleId);

        if (fromDate != null) {
            uriBuilder.queryParam("fromDate", fromDate.toString());
        }
        if (toDate != null) {
            uriBuilder.queryParam("toDate", toDate.toString());
        }

        try {
            ResponseEntity<Object[]> response = restTemplate.getForEntity(
                    uriBuilder.toUriString(),
                    Object[].class
            );
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get vehicle bookings: " + e.getMessage(), e);
        }
    }

    public Object updateBooking(Long id, Object updateRequest) {
        String url = backendUrl + "/api/bookings/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<>(updateRequest, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    Object.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update booking: " + e.getMessage(), e);
        }
    }

    public void cancelBooking(Long id, UUID userId, String reason) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(backendUrl + "/api/bookings/" + id)
                .queryParam("userId", userId.toString());

        if (reason != null && !reason.trim().isEmpty()) {
            uriBuilder.queryParam("reason", reason);
        }

        try {
            restTemplate.delete(uriBuilder.toUriString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to cancel booking: " + e.getMessage(), e);
        }
    }

    // Calendar service methods
    public Object getDayView(Long vehicleId, LocalDate date) {
        String url = backendUrl + "/api/calendar/vehicle/" + vehicleId + "/day";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("date", date.toString());

        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(
                    uriBuilder.toUriString(),
                    Object.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get day view: " + e.getMessage(), e);
        }
    }

    public List<Object> getWeekView(Long vehicleId, LocalDate weekStart) {
        String url = backendUrl + "/api/calendar/vehicle/" + vehicleId + "/week";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("weekStart", weekStart.toString());

        try {
            ResponseEntity<Object[]> response = restTemplate.getForEntity(
                    uriBuilder.toUriString(),
                    Object[].class
            );
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get week view: " + e.getMessage(), e);
        }
    }

    public List<Object> getMonthView(Long vehicleId, int year, int month) {
        String url = backendUrl + "/api/calendar/vehicle/" + vehicleId + "/month";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("year", year)
                .queryParam("month", month);

        try {
            ResponseEntity<Object[]> response = restTemplate.getForEntity(
                    uriBuilder.toUriString(),
                    Object[].class
            );
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get month view: " + e.getMessage(), e);
        }
    }
}
