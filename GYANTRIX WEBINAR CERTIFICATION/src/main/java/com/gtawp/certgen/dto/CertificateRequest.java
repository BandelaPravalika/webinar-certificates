package com.gtawp.certgen.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CertificateRequest {
    private String studentName;
    private String webinarName;
    private String email;
}
