package io.mosip.kernel.auth.jwtBuilder;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.entities.BasicTokenDto;
import io.mosip.kernel.auth.entities.MosipUser;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.TimeToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenGenerator {

    @Autowired
    MosipEnvironment mosipEnvironment;

    private Claims getBasicClaims(MosipUserDto mosipUser) {
        Claims claims = Jwts.claims().setSubject(mosipUser.getUserId());
        claims.put("mobile", mosipUser.getMobile());
        claims.put("mail", mosipUser.getMail());
        claims.put("role", mosipUser.getRole());

        return claims;
    }

    private String buildToken(Claims claims) {
        String secret = mosipEnvironment.getJwtSecret();
        String token_base = mosipEnvironment.getTokenBase();
        int token_expiry = mosipEnvironment.getTokenExpiry();

        long currentTimeInMs = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeInMs);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .signWith(SignatureAlgorithm.HS512, secret);


        if (token_expiry >= 0) {
            long expTimeInMs = currentTimeInMs + token_expiry;
            builder.setExpiration(new Date(expTimeInMs));
        }

        return token_base.concat(builder.compact());
    }

    public String generateForOtp(MosipUserDto mosipUser, Boolean isOtpVerifiedYet) {
        Claims claims = getBasicClaims(mosipUser);
        claims.put("isOtpRequired", true);
        claims.put("isOtpVerified", isOtpVerifiedYet);
        return buildToken(claims);
    }
    
    public String refreshTokenForOTP(MosipUserDto mosipUser) {
    	Claims claims = getBasicClaims(mosipUser);
        claims.put("isOtpRequired", true);
        claims.put("isOtpVerified", true);
		return buildRefreshTokenOTP(claims);
	}

	private String buildRefreshTokenOTP(Claims claims) {
        String secret = mosipEnvironment.getJwtSecret();
        String token_base = mosipEnvironment.getTokenBase();
        long token_expiry = mosipEnvironment.getRefreshTokenExpiry();

        long currentTimeInMs = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeInMs);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .signWith(SignatureAlgorithm.HS512, secret);


        if (token_expiry >= 0) {
            long expTimeInMs = currentTimeInMs + token_expiry;
            builder.setExpiration(new Date(expTimeInMs));
        }

        return token_base.concat(builder.compact());
    }

	public BasicTokenDto basicGenerate(MosipUserDto mosipUser) {
		BasicTokenDto basicTokenDto = new BasicTokenDto();
		 Claims claims = Jwts.claims().setSubject(mosipUser.getUserId());
	        claims.put("mobile", mosipUser.getMobile());
	        claims.put("mail", mosipUser.getMail());
	        claims.put("role", mosipUser.getRole());
	        claims.put("lang", mosipUser.getLangCode());
	        TimeToken token = getToken(claims);
	        String refreshToken = buildRefreshToken(claims);
	        basicTokenDto.setAuthToken(token.getToken());
	        basicTokenDto.setRefreshToken(refreshToken);
	        basicTokenDto.setExpiryTime(token.getExpTime());
		return basicTokenDto;
	}
	public BasicTokenDto basicGenerateOTPToken(MosipUserDto mosipUser,boolean otpVerified) {
		BasicTokenDto basicTokenDto = new BasicTokenDto();
		 Claims claims = Jwts.claims().setSubject(mosipUser.getUserId());
	        claims.put("mobile", mosipUser.getMobile());
	        claims.put("mail", mosipUser.getMail());
	        claims.put("role", mosipUser.getRole());
	        claims.put("lang", mosipUser.getLangCode());
	        claims.put("isOtpRequired", true);
	        claims.put("isOtpVerified", otpVerified);
	        TimeToken token = getToken(claims);
	        String refreshToken = buildRefreshToken(claims);
	        basicTokenDto.setAuthToken(token.getToken());
	        basicTokenDto.setRefreshToken(refreshToken);
	        basicTokenDto.setExpiryTime(token.getExpTime());
		return basicTokenDto;
	}

	private String buildToken(Claims claims, long exptime) {
        String secret = mosipEnvironment.getJwtSecret();
        String token_base = mosipEnvironment.getTokenBase();
        int token_expiry = mosipEnvironment.getTokenExpiry();

        long currentTimeInMs = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeInMs);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .signWith(SignatureAlgorithm.HS512, secret);


        if (token_expiry >= 0) {
        	exptime = currentTimeInMs + token_expiry;
            builder.setExpiration(new Date(exptime));
        }

        return token_base.concat(builder.compact());
    }
	
	private TimeToken getToken(Claims claims) {
		TimeToken timeToken = new TimeToken();
		long exptime=0;
        String secret = mosipEnvironment.getJwtSecret();
        String token_base = mosipEnvironment.getTokenBase();
        int token_expiry = mosipEnvironment.getTokenExpiry();

        long currentTimeInMs = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeInMs);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .signWith(SignatureAlgorithm.HS512, secret);


        if (token_expiry >= 0) {
        	exptime = currentTimeInMs + token_expiry;
            builder.setExpiration(new Date(exptime));
        }
        timeToken.setToken(token_base.concat(builder.compact()));
        timeToken.setExpTime(exptime);
        return timeToken;
    }

	public String refreshToken(MosipUserDto mosipUser) {
		 Claims claims = Jwts.claims().setSubject(mosipUser.getUserId());
	        claims.put("mobile", mosipUser.getMobile());
	        claims.put("mail", mosipUser.getMail());
	        claims.put("role", mosipUser.getRole());
	        claims.put("lang", mosipUser.getLangCode());
		return buildRefreshToken(claims);
	}
	
	 private String buildRefreshToken(Claims claims) {
	        String secret = mosipEnvironment.getJwtSecret();
	        String token_base = mosipEnvironment.getTokenBase();
	        long token_expiry = mosipEnvironment.getRefreshTokenExpiry();

	        long currentTimeInMs = System.currentTimeMillis();
	        Date currentDate = new Date(currentTimeInMs);

	        JwtBuilder builder = Jwts.builder()
	                .setClaims(claims)
	                .setIssuedAt(currentDate)
	                .signWith(SignatureAlgorithm.HS512, secret);


	        if (token_expiry >= 0) {
	            long expTimeInMs = currentTimeInMs + token_expiry;
	            builder.setExpiration(new Date(expTimeInMs));
	        }

	        return token_base.concat(builder.compact());
	    }

	public TimeToken generateNewToken(String existingToken) {
		Claims claims = getClaims(existingToken);
		return getToken(claims);
	}

	private Claims getClaims(String token) {
        String token_base = mosipEnvironment.getTokenBase();
        String secret = mosipEnvironment.getJwtSecret();

        if (token == null || !token.startsWith(token_base)) {
            throw new RuntimeException("Invalid Token");
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token.substring(token_base.length()))
                    .getBody();

            return claims;
        } catch (Exception e) {
            throw new RuntimeException("Invalid Token");
        }
    }
}
