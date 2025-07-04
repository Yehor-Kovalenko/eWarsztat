package org.pl.staffservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
    private String email;
    private String position;
    private double salary;
    @ElementCollection
    @CollectionTable(name =  "staff_member_vehicles", joinColumns = @JoinColumn(name = "staff_member_id"))
    @Column(name = "vehicle_id")
    private List<String> vehicles = new ArrayList<>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "staffMember", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimeTable> timeTable;

    @Override
    public String toString() {
        return """
       StaffMember{
           id=%d,
           firstName='%s',
           lastName='%s',
           phoneNumber='%s',
           position='%s',
           salary=%.2f
       }
       """.formatted(id, firstName, lastName, phoneNumber, position, salary);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public List<TimeTable> getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(List<TimeTable> timeTable) {
        this.timeTable = timeTable;
    }
}
