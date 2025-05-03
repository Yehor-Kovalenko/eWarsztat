package org.pl.staffservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staff_member")
public class StaffMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String position;
    private double salary;

    public List<TimeTable> getTimeTable() {
        return timeTable;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "staffMember", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimeTable> timeTable;

    @Override
    public String toString() {
        return "StaffMember{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                '}';
    }
}
