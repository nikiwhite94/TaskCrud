package ru.nikiwhite.employeeservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "department",
        uniqueConstraints = @UniqueConstraint(columnNames = {"head_id"}))
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @Column(name = "head_id")
    private long headId;

    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
}
