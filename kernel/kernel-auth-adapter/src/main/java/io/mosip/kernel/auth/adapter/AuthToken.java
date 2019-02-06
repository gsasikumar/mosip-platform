package io.mosip.kernel.auth.adapter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/***********************************************************************************************************************
 * AUTH_TOKEN USED TO STORE TOKEN DETAILS
 *
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 **********************************************************************************************************************/

public class AuthToken extends UsernamePasswordAuthenticationToken {

    private String token;

    public AuthToken(String token) {
        super(null, null);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}