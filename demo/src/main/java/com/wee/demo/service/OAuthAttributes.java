package com.wee.demo.service;

import com.wee.demo.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private OAuthAttributes(Builder builder) {
        this.attributes = builder. attributes;
        this.nameAttributeKey = builder.name;
        this.name = builder.name;
        this.email = builder.email;
    }
    public static class Builder {
        private Map<String, Object> attributes;
        private String nameAttributeKey;
        private String name;
        private String email;
        public Builder() {}
        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }
        public Builder nameAttributeKey(String nameAttributeKey) {
            this.nameAttributeKey = nameAttributeKey;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public OAuthAttributes build() {
            return new OAuthAttributes(this);
        }
    }
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        throw new IllegalArgumentException("Only kakao registration is supported");
    }
    public User toEntity() {
        return User.builder()
                .nickname(name)
                .email(email)
                .build();
    }
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return new Builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .name((String) profile.get("profile_nickname"))
                .email((String) kakaoAccount.get("account_email"))
                .build();
    }
}
