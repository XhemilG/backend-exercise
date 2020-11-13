package services;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import models.User;
import play.libs.concurrent.HttpExecutionContext;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class AuthenticationService {

    @Inject
    HttpExecutionContext ec;

    @Inject
    Config config;

    public CompletableFuture<HashMap<String, String>> generateToken(User user) {
        return CompletableFuture.supplyAsync(() -> {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(config.getString("encryption.private_key"));
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            JwtBuilder builder = Jwts.builder()
                    .claim("content",user)
                    .signWith(signatureAlgorithm, signingKey);

            String jwt = builder.compact();
            HashMap<String, String> result = new HashMap<>();
            result.put("token", jwt);
            
            return result;
        }, ec.current());
    }

    public CompletableFuture<Claims> parseToken(String jwt) {
        return CompletableFuture.supplyAsync(() -> Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(config.getString("encryption.private_key")))
                .parseClaimsJws(jwt)
                .getBody(), ec.current());
    }
}
