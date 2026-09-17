package lk.sliit.it3130.payment_service.dto;

public class FareEstimateResponse {

    private Long id;
    private String pickupLocation;
    private String destinationLocation;
    private Double distanceKm;
    private Double estimatedFare;

    public FareEstimateResponse() {}

    public FareEstimateResponse(Long id, String pickupLocation, String destinationLocation,
                                 Double distanceKm, Double estimatedFare) {
        this.id = id;
        this.pickupLocation = pickupLocation;
        this.destinationLocation = destinationLocation;
        this.distanceKm = distanceKm;
        this.estimatedFare = estimatedFare;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDestinationLocation() { return destinationLocation; }
    public void setDestinationLocation(String destinationLocation) { this.destinationLocation = destinationLocation; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getEstimatedFare() { return estimatedFare; }
    public void setEstimatedFare(Double estimatedFare) { this.estimatedFare = estimatedFare; }
}