package com.gtawp.certgen.dto;

import com.gtawp.certgen.entity.Certificate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private Map<String, Long> summary;
    private List<Certificate> details;
}
