// File: com/project/edusync/uis/model/dto/AddressDto.java
package com.project.edusync.uis.model.dto;

import com.project.edusync.uis.model.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO representing a user's address, including its type.
 */
public record AddressDTO(
        Long addressId,

        @NotNull(message = "Address type is required.")
        AddressType addressType,

        @NotBlank(message = "Address line 1 is required.")
        @Size(max = 255)
        String line1,

        @Size(max = 255)
        String line2,

        @NotBlank(message = "City is required.")
        @Size(max = 100)
        String city,

        @NotBlank(message = "State/Province is required.")
        @Size(max = 100)
        String state,

        @NotBlank(message = "Postal code is required.")
        @Size(max = 20)
        String postalCode,

        @NotBlank(message = "Country is required.")
        @Size(max = 100)
        String country
) {}