package org.pl.clientservice.controller;

import lombok.RequiredArgsConstructor;
import org.pl.clientservice.entity.Client;
import org.pl.clientservice.entity.Vehicle;
import org.pl.clientservice.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    @Autowired
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> addClient(@RequestBody Client client) {
        return ResponseEntity.ok(clientService.addClient(client));
    }

    @PostMapping("/{clientId}/vehicles")
    public ResponseEntity<Vehicle> addVehicleToClient(@PathVariable Long clientId, @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(clientService.addVehicleToClient(clientId, vehicle));
    }

    @GetMapping("/{clientId}/vehicles")
    public ResponseEntity<List<Vehicle>> getVehiclesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.getVehiclesByClientId(clientId));
    }

    @PutMapping("/vehicles/{vehicleId}/details")
    public ResponseEntity<Vehicle> updateVehicleDetails(@PathVariable Long vehicleId,
                                                        @RequestBody Map<String, String> newDetails) {
        return ResponseEntity.ok(clientService.updateVehicleDetails(vehicleId, newDetails));
    }
}
