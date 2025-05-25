package com.sb.mini_bank.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "clients")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name can not be left blank")
    private String firstName;

    @NotBlank(message = "LastName can not be left blank")
    private String lastName;

    @NotBlank(message = "Email can not be left blank")
    @Email(message = "Email is not valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Phone Number can not be left blank")
    private String phoneNumber;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Account> accounts;

}
