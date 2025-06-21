package org.pl.staffservice.service;

import jakarta.transaction.Transactional;
import org.pl.staffservice.entity.StaffMember;
import org.pl.staffservice.entity.TimeTable;
import org.pl.staffservice.repository.StaffMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
