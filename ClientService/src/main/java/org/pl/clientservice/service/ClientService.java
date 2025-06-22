package org.pl.clientservice.service;

import lombok.RequiredArgsConstructor;
import org.pl.clientservice.entity.Client;
import org.pl.clientservice.entity.Vehicle;
import org.pl.clientservice.repository.ClientRepository;
import org.pl.clientservice.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;

    public Client addClient(Client client) {
        return clientRepository.save(client);
    }

    public Vehicle addVehicleToClient(Long clientId, Vehicle vehicle) {
        if (!clientRepository.existsById(clientId)) {
            throw new IllegalArgumentException("Client not found with ID: " + clientId);
        }
        vehicle.setClientId(clientId);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getVehiclesByClientId(Long clientId) {
        return vehicleRepository.findByClientId(clientId);
    }

    public Vehicle updateVehicleDetails(Long vehicleId, Map<String, String> newDetails) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));
        vehicle.getDetails().putAll(newDetails);
        return vehicleRepository.save(vehicle);
    }
}
