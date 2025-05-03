package org.pl.staffservice.controller;

import org.pl.staffservice.entity.StaffMember;
import org.pl.staffservice.entity.TimeTable;
import org.pl.staffservice.service.StaffMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}
