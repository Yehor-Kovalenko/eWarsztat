package org.pl.staffservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pl.staffservice.entity.StaffMember;
import org.pl.staffservice.repository.StaffMemberRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffMemberServiceTest {

    @Mock
    private StaffMemberRepository staffMemberRepository;

    @InjectMocks
    private StaffMemberService staffMemberService;

    @Test
    void getMemberById_whenExists() {
        StaffMember expectedUser = new StaffMember();
        expectedUser.setId(1L);
        expectedUser.setFirstName("John");

        when(staffMemberRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        Optional<StaffMember> actualUser = staffMemberService.getMemberById(1L);

        assertThat(actualUser).isPresent();
        assertThat(actualUser.get()).isEqualTo(expectedUser);
        verify(staffMemberRepository).findById(1L);
    }

    @Test
    void getMemberById_whenNotFound() {
        when(staffMemberRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<StaffMember> result = staffMemberService.getMemberById(1L);

        assertThat(result).isEmpty();
        verify(staffMemberRepository).findById(1L);
    }

    @Test
    void getAllStaffMembers() {
        List<StaffMember> users = List.of(new StaffMember(), new StaffMember());
        when(staffMemberRepository.findAll()).thenReturn(users);

        List<StaffMember> result = staffMemberService.getAllStaffMembers();

        assertThat(result.size()).isEqualTo(2);
        verify(staffMemberRepository).findAll();
    }

    @Test
    void saveStaffMember() {
        StaffMember user = new StaffMember();
        user.setFirstName("Alice");

        when(staffMemberRepository.save(user)).thenReturn(user);

        StaffMember savedUser = staffMemberService.saveStaffMember(user);

        assertThat(savedUser).isEqualTo(user);
        verify(staffMemberRepository).save(user);
    }

    @Test
    void deleteStaffMemberById_whenExists() {
        when(staffMemberRepository.existsById(1L)).thenReturn(true);

        staffMemberService.deleteStaffMemberById(1L);

        verify(staffMemberRepository).deleteById(1L);
    }

    @Test
    void deleteStaffMemberById_whenNotExists() {
        when(staffMemberRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> staffMemberService.deleteStaffMemberById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Staff member with ID 1 not found");

        verify(staffMemberRepository, never()).deleteById(any());
    }

    @Test
    void getStaffByPosition() {
        String position = "Manager";
        List<StaffMember> staffList = List.of(new StaffMember(), new StaffMember());

        when(staffMemberRepository.findByPosition(position)).thenReturn(staffList);

        List<StaffMember> result = staffMemberService.getStaffByPosition(position);

        assertThat(result.size()).isEqualTo(2);
        verify(staffMemberRepository).findByPosition(position);
    }
}
