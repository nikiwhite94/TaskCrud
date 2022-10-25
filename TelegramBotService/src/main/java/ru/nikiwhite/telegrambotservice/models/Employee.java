package ru.nikiwhite.telegrambotservice.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "employee",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "head_id")
    @NotBlank
    private Long headId;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "surname")
    @NotBlank
    private String surname;

    @Column(name = "middle_name")
    @NotBlank
    private String middleName;

    @Column(name = "email")
    @NotBlank
    @Email
    private String email;
}
