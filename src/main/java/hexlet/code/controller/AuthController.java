package hexlet.code.controller;

import hexlet.code.dto.AuthReq;
import hexlet.code.util.JWTUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@AllArgsConstructor
public final class AuthController {
    private JWTUtils jwtUtils;
    private AuthenticationManager authenticationManager;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String createAuth(@RequestBody @Valid AuthReq authReq) throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(authReq.getUsername(), authReq.getPassword());
        authenticationManager.authenticate(auth);
        return jwtUtils.generateToken(authReq.getUsername());
    }
}
