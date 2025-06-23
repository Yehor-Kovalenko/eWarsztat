package org.pl.clientservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pl.clientservice.entity.Client;
import org.pl.clientservice.entity.Vehicle;
import org.pl.clientservice.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ClientController.class)
@Import(ClientControllerTest.MockConfig.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ClientService clientService() {
            return Mockito.mock(ClientService.class);
        }
    }

    @Test
    void testAddClient() throws Exception {
        Client client = new Client(22L, "firstmae", "lastname" ,"email", "phonenumebr");
        when(clientService.addClient(any(Client.class))).thenReturn(client);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(client.getId()));
    }

    @Test
    void testAddVehicleToClient() throws Exception {
        Long clientId = 1L;
        Vehicle vehicle = new Vehicle(10L, 1L, "vin", Map.of("Detailkey", "detail"));
        vehicle.setClientId(clientId);
        when(clientService.addVehicleToClient(eq(clientId), any(Vehicle.class))).thenReturn(vehicle);

        mockMvc.perform(post("/api/clients/{clientId}/vehicles", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(clientId));
    }

    @Test
    void testGetVehiclesByClient() throws Exception {
        Long clientId = 1L;
        List<Vehicle> vehicles = Arrays.asList(new Vehicle(10L, 1L, "vin", Map.of("Detailkey", "detail")), new Vehicle(11L, 1L, "vin", Map.of("Detailkey", "detail")));
        when(clientService.getVehiclesByClientId(clientId)).thenReturn(vehicles);

        mockMvc.perform(get("/api/clients/{clientId}/vehicles", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(vehicles.size()));
    }

    @Test
    void testUpdateVehicleDetails() throws Exception {
        Long vehicleId = 1L;
        Map<String, String> details = Map.of("color", "blue");
        Vehicle vehicle = new Vehicle(10L, 1L, "vin", new HashMap<>());
        vehicle.setDetails(new HashMap<>(details));
        when(clientService.updateVehicleDetails(eq(vehicleId), anyMap())).thenReturn(vehicle);

        mockMvc.perform(put("/api/clients/vehicles/{vehicleId}/details", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.color").value("blue"));
    }
}