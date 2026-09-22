package lk.sliit.it3130.driver_service.dto;

import jakarta.validation.constraints.NotNull;

public class LocationUpdateRequest {

    @NotNull(message = "Latitude is required")
    private Double currentLatitude;

    @NotNull(message = "Longitude is required")
    private Double currentLongitude;

    private String serviceArea;

    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }

    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }

    public String getServiceArea() { return serviceArea; }
    public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }
}