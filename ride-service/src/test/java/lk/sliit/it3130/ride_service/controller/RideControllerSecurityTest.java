package lk.sliit.it3130.ride_service.controller;

import lk.sliit.it3130.ride_service.dto.RideResponse;
import lk.sliit.it3130.ride_service.service.RideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
@Import({
        lk.sliit.it3130.ride_service.config.SecurityConfig.class,
        lk.sliit.it3130.ride_service.security.JwtAuthFilter.class
})
class RideControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RideService rideService;

    @Test
    @WithMockUser(roles = "PASSENGER")
    void passengerShouldBeAllowedToCreateRide() throws Exception {

        when(rideService.createRide(any()))
                .thenReturn(new RideResponse());

        mockMvc.perform(
                        post("/api/rides")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "riderId": 100,
                                          "pickupLocation": "Colombo",
                                          "destination": "Kandy",
                                          "pickupLatitude": 6.9271,
                                          "pickupLongitude": 79.8612,
                                          "destinationLatitude": 7.2906,
                                          "destinationLongitude": 80.6337,
                                          "distanceKm": 115.0
                                        }
                                        """)
                )
                .andExpect(status().isCreated());

        verify(rideService).createRide(any());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void driverShouldBeForbiddenFromCreatingRide() throws Exception {

        mockMvc.perform(
                        post("/api/rides")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "riderId": 100,
                                          "pickupLocation": "Colombo",
                                          "destination": "Kandy",
                                          "pickupLatitude": 6.9271,
                                          "pickupLongitude": 79.8612,
                                          "destinationLatitude": 7.2906,
                                          "destinationLongitude": 80.6337,
                                          "distanceKm": 115.0
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void driverShouldBeAllowedToAcceptRide() throws Exception {

        when(rideService.acceptRide(1L))
                .thenReturn(new RideResponse());

        mockMvc.perform(
                        patch("/api/rides/1/accept")
                )
                .andExpect(status().isOk());

        verify(rideService).acceptRide(1L);
    }

    @Test
    @WithMockUser(roles = "PASSENGER")
    void passengerShouldBeForbiddenFromAcceptingRide() throws Exception {

        mockMvc.perform(
                        patch("/api/rides/1/accept")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void driverShouldBeAllowedToStartRide() throws Exception {

        when(rideService.startRide(1L))
                .thenReturn(new RideResponse());

        mockMvc.perform(
                        patch("/api/rides/1/start")
                )
                .andExpect(status().isOk());

        verify(rideService).startRide(1L);
    }

    @Test
    @WithMockUser(roles = "PASSENGER")
    void passengerShouldBeForbiddenFromStartingRide() throws Exception {

        mockMvc.perform(
                        patch("/api/rides/1/start")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PASSENGER")
    void passengerShouldBeAllowedToCancelRide() throws Exception {

        when(rideService.cancelRide(1L, null))
                .thenReturn(new RideResponse());

        mockMvc.perform(
                        patch("/api/rides/1/cancel")
                )
                .andExpect(status().isOk());

        verify(rideService)
                .cancelRide(1L, null);
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void driverShouldBeForbiddenFromCancellingRide() throws Exception {

        mockMvc.perform(
                        patch("/api/rides/1/cancel")
                )
                .andExpect(status().isForbidden());
    }
}