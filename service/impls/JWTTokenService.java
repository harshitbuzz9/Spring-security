package com.bridge.herofincorp.service.impls;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class JWTTokenService {
    private final RSAPrivateKey privateKey;
    private final @Getter JSONObject jwksPublic;
    private final @Getter JSONObject openIdConfig;
    private final RSAPublicKey publicKey;

    private static final String FILE_JWT_PRIVATE_KEY = "/secret.pem";
    private static final String FILE_JWKS = "/jwks.json";
    private static final String FILE_OPNEID = "/openid.json";
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${mobile-app}")
    private String mobileAudience;
    @Value("${web-app}")
    private String webAudience;

    public JWTTokenService() {
        this.privateKey = privateKey();
        this.jwksPublic = jwksPublic();
        this.publicKey = publicKey();
        this.openIdConfig = openIdConfig();
    }

    private final RSAPrivateKey privateKey() {
        String privateKeyContent;
        try {
            InputStream stream = JWTTokenService.class.getResourceAsStream(FILE_JWT_PRIVATE_KEY);
            StringBuffer sb = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str + System.lineSeparator());
            }
            privateKeyContent = sb.toString();
            privateKeyContent = privateKeyContent
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replace(System.lineSeparator(), "");
            System.out.println (privateKeyContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // ******************
        // Create private key
        // ******************
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                java.util.Base64 .getDecoder().decode(privateKeyContent)
        );

        try {
            return (RSAPrivateKey)keyFactory.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private final JSONObject jwksPublic () {
        try {
            InputStream stream = JWTTokenService.class.getResourceAsStream(FILE_JWKS);
            StringBuffer sb = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str + System.lineSeparator());
            }
            String jwksContent = sb.toString();
            System.out.println (jwksContent);
            JSONObject json = new JSONObject(jwksContent);
            return json;
        } catch (IOException | JSONException e) {
            throw new RuntimeException (e);
        }
    }
    private final JSONObject openIdConfig () {
        try {
            InputStream stream = JWTTokenService.class.getResourceAsStream(FILE_OPNEID);
            StringBuffer sb = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str + System.lineSeparator());
            }
            String jwksContent = sb.toString();
            System.out.println (jwksContent);
            return new JSONObject(jwksContent);
        } catch (IOException | JSONException e) {
            throw new RuntimeException (e);
        }

    }

    private final RSAPublicKey publicKey () {
        try {
            JSONObject jwks = this.jwksPublic.getJSONArray("keys").getJSONObject(0);
            RSAKey rsaPublicKey = new RSAKey.Builder (new Base64URL(jwks.getString("n")), new Base64URL(jwks.getString("e")))
                    .algorithm(new Algorithm(jwks.getString("alg")))
                    .keyUse(new KeyUse(jwks.getString("use")))
                    .keyID(jwks.getString("kid")).build();
            return rsaPublicKey.toRSAPublicKey();
        } catch (JSONException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public SignedJWT getJWT (String sub, List<String> roles, String userType, String issuer, String aud, int expirationInMins) {
        JSONObject jwks = this.jwksPublic.getJSONArray("keys").getJSONObject(0);
        JWSSigner signer = new RSASSASigner(this.privateKey);
        JWTClaimsSet claimset = new JWTClaimsSet.Builder()
                .subject(sub)
                .claim("roles",roles)
                .claim("userType",userType)
                .issuer(issuer)
                .audience(aud)
                .expirationTime(new Date(new Date().getTime() + ((long) expirationInMins * 60 * 1000)))
                .build();
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .keyID(jwks.getString("kid"))
                        .build(), claimset);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return signedJWT;
    }

    public SignedJWT verifySignature (String jwt) {
        boolean signatureVerified = false;
        SignedJWT signedJWT = null;
        try {
            signedJWT = SignedJWT.parse(jwt);
            JWSVerifier verifer = new RSASSAVerifier(this.publicKey);
            signatureVerified = signedJWT.verify(verifer);
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException ("Invalid JWT");
        }
        if (!signatureVerified) {
            throw new RuntimeException ("Invalid JWT");
        }
        return signedJWT;
    }

    public boolean validateToken(String token, UserDetails userDetails) throws ParseException {
        JWTClaimsSet claims = this.verifySignature(token).getJWTClaimsSet();
        if(claims.getIssuer().equals(this.issuer)){
            if (claims.getAudience().contains(this.webAudience)||claims.getAudience().contains(this.mobileAudience)){
                return !new Date().after(claims.getExpirationTime()) && claims.getSubject().equals(userDetails.getUsername());
            }
        }
        return false;
    }
    @SuppressWarnings("unchecked")
    public String expireToken(String token) throws ParseException {
        JWTClaimsSet claims = this.verifySignature(token).getJWTClaimsSet();
        List<String > roles = (List<String>) claims.getClaim("roles");
        String userType = claims.getClaim("userType").toString();
        String audience = claims.getAudience().get(0);
        return this.getJWT (claims.getSubject(), roles, userType, claims.getIssuer(), audience, 0).serialize();
    }
}