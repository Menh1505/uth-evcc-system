package com.evcc.booking.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.evcc.booking.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSearchCriteria {

    private UUID userId;
    private Long vehicleId;
    private Long contractId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private BookingStatus status;
    private Integer limit;
    private Integer offset;

    private String sortBy; // "startTime", "endTime", "createdAt", etc.
    private String sortDirection; // "ASC", "DESC"
}
