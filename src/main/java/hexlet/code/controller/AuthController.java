package hexlet.code.controller;

import hexlet.code.dto.AuthReq;
import hexlet.code.util.JWTUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public final class AuthController {
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String createAuth(@RequestBody @Valid AuthReq authReq) throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(authReq.getUsername(), authReq.getPassword());
        try {
            authenticationManager.authenticate(auth);
        } catch (BadCredentialsException e) {
            throw new Exception("Неправильные email или пароль", e);
        }
        return jwtUtils.generateToken(authReq.getUsername());
    }
}
