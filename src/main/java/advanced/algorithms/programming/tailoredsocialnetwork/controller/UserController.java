package advanced.algorithms.programming.tailoredsocialnetwork.controller;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.SearchCriteria;
import advanced.algorithms.programming.tailoredsocialnetwork.dto.user.*;
import advanced.algorithms.programming.tailoredsocialnetwork.security.JwtService;
import advanced.algorithms.programming.tailoredsocialnetwork.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = "signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void signUp(@Valid @RequestBody RegistrationDTO userDTO) {
        this.userService.signUp(userDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "activate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void activate(@RequestBody ActivationDTO activationDTO) {
        this.userService.activate(activationDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "activate/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void newActivationCode(@RequestBody EmailDTO userDTO) {
        this.userService.newActivationCode(userDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "signin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> signIn(@RequestBody AuthenticationDTO authenticationDTO) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            authenticationDTO.getUsername(),
            authenticationDTO.getPassword()
        ));

        return this.jwtService.generate(authenticationDTO.getUsername());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "signout")
    public void signOut() {
        this.jwtService.signOut();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "password/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void resetPassword(@RequestBody EmailDTO userDTO) {
        this.userService.resetPassword(userDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(path = "password/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void newPassword(@Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        this.userService.newPassword(passwordResetDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "profiles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProfileDTO getProfile(@PathVariable int id) {
        return this.userService.getProfile(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(path = "profiles/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void modifyProfile(@PathVariable int id, @Valid @RequestBody ProfileModificationDTO userDTO) {
        this.userService.modifyProfile(id, userDTO);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("profiles/{id}")
    public void deleteProfile(@PathVariable int id) {
        this.userService.deleteProfile(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "{followerId}/follow/{followedId}")
    public void followUser(@PathVariable int followerId, @PathVariable int followedId) {
        this.userService.followUser(followerId, followedId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(path = "profiles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<UserDTO> getRecommendedProfiles(@RequestBody List<SearchCriteria> criteria, Pageable pageable) {
        return this.userService.getRecommendedProfiles(criteria, pageable);
    }
}

