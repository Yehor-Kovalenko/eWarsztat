package org.pl.clientservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pl.clientservice.entity.Client;
import org.pl.clientservice.entity.Vehicle;
import org.pl.clientservice.repository.ClientRepository;
import org.pl.clientservice.repository.VehicleRepository;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    public void testAddClient() {
        Client clientToAdd = new Client(22L, "firstmae", "lastname" ,"email", "phonenumebr");
        when(clientRepository.save(clientToAdd)).thenReturn(clientToAdd);

        Client addedClient = clientService.addClient(clientToAdd);

        assertThat(addedClient).isEqualTo(clientToAdd);
        verify(clientRepository, times(1)).save(clientToAdd);
    }

    @Test
    public void testAddVehicleToClient() {
        Long clientId = 1L;
        Vehicle vehicleToAdd = new Vehicle(10L, 1L, "vin", Map.of("Detailkey", "detail"));
        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(vehicleRepository.save(vehicleToAdd)).thenReturn(vehicleToAdd);

        Vehicle addedVehicle = clientService.addVehicleToClient(clientId, vehicleToAdd);

        assertThat(addedVehicle.getClientId()).isEqualTo(clientId);
        verify(clientRepository, times(1)).existsById(clientId);
        verify(vehicleRepository, times(1)).save(vehicleToAdd);
    }

    @Test
    public void testGetVehiclesByClientId() {
        Long clientId = 1L;
        List<Vehicle> vehicles = Arrays.asList(
                new Vehicle(10L, 1L, "vin", Map.of("Detailkey", "detail")),
                new Vehicle(11L, 1L, "vin2", Map.of("Detailkey2", "detail2"))
        );
        when(vehicleRepository.findByClientId(clientId)).thenReturn(vehicles);

        List<Vehicle> result = clientService.getVehiclesByClientId(clientId);

        assertThat(vehicles).isEqualTo(result);
        verify(vehicleRepository, times(1)).findByClientId(clientId);
    }

    @Test
    public void testUpdateVehicleDetails() {
        Long vehicleId = 1L;
        Map<String, String> newDetails = new HashMap<>();
        newDetails.put("key1", "value1");
        Vehicle existingVehicle = new Vehicle(10L, 1L, "vin", new HashMap<>());
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(vehicleRepository.save(existingVehicle)).thenReturn(existingVehicle);

        Vehicle updatedVehicle = clientService.updateVehicleDetails(vehicleId, newDetails);

        assertThat(updatedVehicle.getDetails()).isEqualTo(newDetails);
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleRepository, times(1)).save(existingVehicle);
    }
}