package com.gtawp.certgen.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class WebinarSequenceId implements Serializable {
    private String dateKey;
    private Integer id;
}
