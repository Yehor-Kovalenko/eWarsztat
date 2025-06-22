package org.pl.staffservice.service;

import jakarta.transaction.Transactional;
import org.pl.staffservice.entity.StaffMember;
import org.pl.staffservice.entity.TimeTable;
import org.pl.staffservice.repository.StaffMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StaffMemberService {
    private final StaffMemberRepository staffMemberRepository;

    @Autowired
    public StaffMemberService(StaffMemberRepository staffMemberRepository) {
        this.staffMemberRepository = staffMemberRepository;
    }

    public Optional<StaffMember> getMemberById(Long id) throws IllegalArgumentException {
        return this.staffMemberRepository.findById(id);
    }

    public Optional<StaffMember> getMemberByEmail(String email) throws IllegalArgumentException {
        return this.staffMemberRepository.findByEmail(email);
    }

    public List<StaffMember> getAllStaffMembers() {
        return this.staffMemberRepository.findAll();
    }

    @Transactional
    public StaffMember saveStaffMember(StaffMember staffMember) {
        if (staffMember.getTimeTable() != null) {
            for (TimeTable timeTable : staffMember.getTimeTable()) {
                timeTable.setStaffMember(staffMember);
            }
        }
        return this.staffMemberRepository.save(staffMember);
    }

    @Transactional
    public void deleteStaffMemberById(Long id) {
        if (!staffMemberRepository.existsById(id)) {
            throw new IllegalArgumentException("Staff member with ID " + id + " not found");
        }
        staffMemberRepository.deleteById(id);
    }

    public List<StaffMember> getStaffByPosition(String position) {
        return staffMemberRepository.findByPosition(position);
    }

    public List<String> getVehiclesByStaffMemberId(Long staffMemberId) {
        Optional<StaffMember> staffMember = getMemberById(staffMemberId);
        return staffMember.map(StaffMember::getVehicles).orElse(new ArrayList<>());
    }

    public List<String> getVehiclesByStaffMemberEmail(String email) {
        Long id = this.getMemberByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("No staff member with such email")).getId();
        return this.getVehiclesByStaffMemberId(id);
    }

    public boolean doesStaffMemberWorkOnVehicle(Long staffMemberId, String vehicleId) {
        Optional<StaffMember> staffMember = staffMemberRepository.findById(staffMemberId);
        return staffMember.map(sm -> sm.getVehicles().contains(vehicleId))
                .orElse(false);
    }

    public boolean addVehicleToStaffMember(Long staffMemberId, String vehicleId) {
        Optional<StaffMember> optionalStaffMember = staffMemberRepository.findById(staffMemberId);

        if (optionalStaffMember.isPresent()) {
            StaffMember staffMember = optionalStaffMember.get();

            if (!staffMember.getVehicles().contains(vehicleId)) {
                staffMember.getVehicles().add(vehicleId);
                staffMemberRepository.save(staffMember);
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean removeVehicleFromStaffMember(Long staffMemberId, String vehicleId) {
        Optional<StaffMember> optionalStaffMember = staffMemberRepository.findById(staffMemberId);

        if (optionalStaffMember.isPresent()) {
            StaffMember staffMember = optionalStaffMember.get();

            boolean removed = staffMember.getVehicles().remove(vehicleId);

            if (removed) {
                staffMemberRepository.save(staffMember);
                return true;
            }
            return false;
        }
        return false;
    }
}
