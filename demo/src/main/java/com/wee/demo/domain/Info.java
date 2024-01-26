package com.wee.demo.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Info {
    private boolean gender;
    private double height;
    private double weight;
    private double bodyFat;
    private String goal;
    private String interest;
    private int level;
}
