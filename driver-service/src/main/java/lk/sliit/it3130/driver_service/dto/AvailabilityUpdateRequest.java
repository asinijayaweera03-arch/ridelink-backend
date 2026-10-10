package lk.sliit.it3130.driver_service.dto;

import jakarta.validation.constraints.NotNull;
import lk.sliit.it3130.driver_service.enums.AvailabilityStatus;

public class AvailabilityUpdateRequest {

    @NotNull(message = "Availability status is required")
    private AvailabilityStatus availabilityStatus;

    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) { this.availabilityStatus = availabilityStatus; }
}