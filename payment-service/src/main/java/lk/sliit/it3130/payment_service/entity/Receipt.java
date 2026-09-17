package lk.sliit.it3130.payment_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long paymentId;
    private Long rideId;

    private Double baseFare;
    private Double distanceCharge;
    private Double timeCharge;
    private Double surcharge;
    private Double totalAmount;

    private LocalDateTime issuedAt;

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }

    public Double getBaseFare() { return baseFare; }
    public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }

    public Double getDistanceCharge() { return distanceCharge; }
    public void setDistanceCharge(Double distanceCharge) { this.distanceCharge = distanceCharge; }

    public Double getTimeCharge() { return timeCharge; }
    public void setTimeCharge(Double timeCharge) { this.timeCharge = timeCharge; }

    public Double getSurcharge() { return surcharge; }
    public void setSurcharge(Double surcharge) { this.surcharge = surcharge; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}