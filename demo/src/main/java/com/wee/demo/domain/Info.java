package com.wee.demo.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Info {
    private Boolean gender;
    private Double height;
    private Double weight;
    private Double bodyFat;
    private String goal;
    private String interest;
    private Integer level;
}
