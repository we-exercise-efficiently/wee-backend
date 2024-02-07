package com.wee.demo.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Info {
    private Boolean gender;
    private Double height;
    private Double weight;
    private Double bodyFat;
    private String goal;
    private String interest;
    private Integer level;
}
