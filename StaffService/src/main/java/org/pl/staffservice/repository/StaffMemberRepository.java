package org.pl.staffservice.repository;

import org.pl.staffservice.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, Long> {
    List<StaffMember> findByPosition(String position);
    Optional<StaffMember> findByEmail(String email);
    Optional<StaffMember> findFirstByVehiclesContaining(String vehicleId);
}
