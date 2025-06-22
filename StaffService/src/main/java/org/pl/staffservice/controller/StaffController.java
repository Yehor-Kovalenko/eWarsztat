package org.pl.staffservice.controller;

import org.pl.staffservice.entity.StaffMember;
import org.pl.staffservice.entity.TimeTable;
import org.pl.staffservice.service.StaffMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffMemberService staffMemberService;

    @Autowired
    public StaffController(StaffMemberService staffMemberService) {
        this.staffMemberService = staffMemberService;
    }
    @GetMapping("")
    public ResponseEntity<?> getAllStaffMembers() {
        List<StaffMember> staffMembers = this.staffMemberService.getAllStaffMembers();
        return ResponseEntity.ok(staffMembers);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> getStaffMember(@PathVariable Long id) {
        Optional<StaffMember> staffMember = staffMemberService.getMemberById(id);
        return staffMember.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/email")
    public ResponseEntity<?> getStaffMember(@RequestBody String email) {
        Optional<StaffMember> staffMember = staffMemberService.getMemberByEmail(email);
        return staffMember.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/new")
    public ResponseEntity<?> createStaffMember(@RequestBody StaffMember staffMember) {
        StaffMember newMember = staffMemberService.saveStaffMember(staffMember);
        return ResponseEntity.ok(newMember);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaffMemberById(@PathVariable Long id) {
        staffMemberService.deleteStaffMemberById(id);
        return ResponseEntity.noContent().build();
    }

    //staff availability
    @PostMapping("/{id}/availability")
    public ResponseEntity<?> getStaffAvailability(@PathVariable Long id) {
        Optional<StaffMember> staff = staffMemberService.getMemberById(id);
        return staff.map(member -> ResponseEntity.ok(member.getTimeTable()))
                .orElse(ResponseEntity.notFound().build());
    }

    //get staff by position
    @GetMapping("/role/{position}")
    public ResponseEntity<?> getStaffByRole(@PathVariable String position) {
        List<StaffMember> staff = staffMemberService.getStaffByPosition(position);
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<Long> getStaffMemberByVehicle(@PathVariable String vehicleId) {
        Long staffMemberId = staffMemberService.getStaffMemberIdByVehicleId(vehicleId);

        if (staffMemberId != null) {
            return ResponseEntity.ok(staffMemberId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/vehicles")
    public ResponseEntity<?> getStaffMemberVehicles(@RequestBody String email) {
        try {
            List<String> vehicles = staffMemberService.getVehiclesByStaffMemberEmail(email);
            Map<String, List<String>> response = new HashMap<>();
            response.put("vehicles", vehicles);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("No employee found");
        }
    }

    @GetMapping("/{staffMemberId}/vehicles/{vehicleId}")
    public ResponseEntity<?> addVehicle(@PathVariable Long staffMemberId, @PathVariable String vehicleId) {
        boolean added = staffMemberService.addVehicleToStaffMember(staffMemberId, vehicleId);
        if (added) {
            return ResponseEntity.ok()
                    .body(Map.of("message", "Vehicle " + vehicleId + " successfully added to staff member " + staffMemberId,
                            "success", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Failed to add vehicle. Either staff member not found or vehicle already assigned.",
                            "success", false));
        }
    }

    @DeleteMapping("/{staffMemberId}/vehicles/{vehicleId}")
    public ResponseEntity<?> removeVehicle(@PathVariable Long staffMemberId, @PathVariable String vehicleId) {
        boolean removed = staffMemberService.removeVehicleFromStaffMember(staffMemberId, vehicleId);

        if (removed) {
            return ResponseEntity.ok()
                    .body(Map.of("message", "Vehicle " + vehicleId + " successfully removed from staff member " + staffMemberId,
                            "success", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Failed to remove vehicle. Either staff member not found or vehicle not assigned.",
                            "success", false));
        }
    }

    @GetMapping("/{staffMemberId}/vehicles")
    public ResponseEntity<?> getStaffMemberVehicles(@PathVariable Long staffMemberId) {
        List<String> vehicles = staffMemberService.getVehiclesByStaffMemberId(staffMemberId);

        return ResponseEntity.ok()
                .body(Map.of("staffMemberId", staffMemberId,
                        "vehicles", vehicles,
                        "count", vehicles.size()));
    }
}
