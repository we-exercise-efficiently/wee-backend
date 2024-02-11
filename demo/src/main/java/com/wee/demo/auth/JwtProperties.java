package com.wee.demo.auth;


public class JwtProperties {
    public final static int ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000; //1시간
    public final static int REFRESH_TOKEN_EXPIRATION_TIME = 60 * 60 * 60 * 24 * 30 * 1000; //1개월
    public final static String TOKEN_PREFIX = "Bearer ";
    public final static String ACCESS_HEADER_STRING = "Authorization";
    public final static String REFRESH_HEADER_STRING = "refresh_token";
}
