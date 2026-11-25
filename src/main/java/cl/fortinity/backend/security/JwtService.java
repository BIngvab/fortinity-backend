package cl.fortinity.backend.security;

import cl.fortinity.backend.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import java.time.Instant;


@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;


    @Value("${app.jwt.expiration}")
    private long expiration;


    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;

    }


    public String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("fortinity")
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(expiration))
                .subject(usuario.getEmail())
                .claim("rol", usuario.getRol().name())
                .claim("usuarioId", usuario.getId())
                .build();


        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();


        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims))
                .getTokenValue();

    }
}