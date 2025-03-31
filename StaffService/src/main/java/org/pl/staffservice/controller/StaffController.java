package org.pl.staffservice.controller;

import org.pl.staffservice.entity.StaffMember;
import org.pl.staffservice.service.StaffMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffMemberService staffMemberService;

    @Autowired
    public StaffController(StaffMemberService staffMemberService) {
        this.staffMemberService = staffMemberService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllStaffMembers() {
        List<StaffMember> staffMembers = this.staffMemberService.getAllStaffMembers();
        return ResponseEntity.ok(staffMembers);
    }

    @GetMapping("/staffMember")
    public ResponseEntity<?> getStaffMember(@RequestBody Long id) {
        StaffMember staffMember = this.staffMemberService.getMemberById(id).orElse(null);
        if (staffMember == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Staff member with this id could not be found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            return ResponseEntity.ok(staffMember);
        }
    }

    @DeleteMapping("/staffMember")
    public ResponseEntity<?> deleteStaffMemberById(@RequestBody Long id) {
        try{
            this.staffMemberService.deleteStaffMemberById(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
