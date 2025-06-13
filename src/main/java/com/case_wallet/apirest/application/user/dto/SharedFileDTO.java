package com.case_wallet.apirest.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedFileDTO {
    private UUID id;
    private String name;
    private String url;
    private String type;
} 