package hexlet.code.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Duration;
import java.time.Instant;

@Component
public final class JWTUtils {
    @Autowired
    private JwtEncoder encoder;

    public String generateToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("self").issuedAt(now)
                .expiresAt(now.plus(Duration.ofHours(1))).subject(username).build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
