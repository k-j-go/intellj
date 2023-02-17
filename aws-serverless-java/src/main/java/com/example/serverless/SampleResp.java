package com.example.serverless;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SampleResp {
    private LocalDateTime localDateTime;
    private String status;
}
