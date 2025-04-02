package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;
import java.util.Date;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "User name is required")
    @Size(max = 50, message = "User name cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String userName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    @Embedded
    @Valid
    private Mobile mobile;

    @Embedded
    @Valid
    private Address address;

    @Column(length = 1000)
    private String preferences;

    @Builder.Default
    @Column(nullable = false)
    private String role = "USER";

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Mobile {
        @Pattern(regexp = "^\\+\\d{1,3}$", message = "Invalid country code")
        @Column(name = "mobile_country_code")
        private String countryCode;

        @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
        @Column(name = "mobile_number")
        private String number;
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Address {
        @NotBlank(message = "Street is required")
        @Column(name = "address_street")
        private String street;

        @NotBlank(message = "City is required")
        @Column(name = "address_city")
        private String city;

        @NotBlank(message = "State is required")
        @Column(name = "address_state")
        private String state;

        @Pattern(regexp = "^[0-9]{6}$", message = "ZIP code must be 6 digits")
        @Column(name = "address_zip_code")
        private String zipCode;
    }
}
