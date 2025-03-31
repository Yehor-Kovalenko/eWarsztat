package org.pl.staffservice.service;

import org.pl.staffservice.entity.StaffMember;
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

    public Optional<StaffMember> getMemberById(Long id) {
        return this.staffMemberRepository.findById(id);
    }

    public List<StaffMember> getAllStaffMembers() {
        return this.staffMemberRepository.findAll();
    }

    public StaffMember addStaffMember(StaffMember staffMember) {
        return this.staffMemberRepository.save(staffMember);
    }

    public void deleteStaffMemberById(Long id) {
        this.staffMemberRepository.deleteById(id);
    }
}
