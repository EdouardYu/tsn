package advanced.algorithms.programming.tailoredsocialnetwork.controller.advice;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.ErrorEntity;
import advanced.algorithms.programming.tailoredsocialnetwork.service.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        AlreadyUsedException.class,
        ValidationCodeException.class,
        UsernameNotFoundException.class,
        NotYetEnabledException.class,
        LockedException.class,
        PostNotFoundException.class
    })
    public @ResponseBody ErrorEntity handleBadRequestException(RuntimeException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorEntity handleValidationExceptions(MethodArgumentNotValidException e) {
        log.warn(String.valueOf(e));
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList()
            .stream().findFirst()
            .orElse(e.getMessage());

        return new ErrorEntity(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadCredentialsException.class})
    public @ResponseBody ErrorEntity handleBadCredentialsException(BadCredentialsException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.BAD_REQUEST.value(), "Invalid email or password");
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public @ResponseBody ErrorEntity handleBadRequestException(HttpMessageNotReadableException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid request format: " + e.getMessage()
        );
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler({AlreadyProcessedException.class})
    public @ResponseBody ErrorEntity handleAlreadyProcessedException(AlreadyProcessedException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
        SignatureException.class,
        MalformedJwtException.class,
        ExpiredJwtException.class
    })
    public @ResponseBody ErrorEntity handleSignatureExceptionException(RuntimeException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public @ResponseBody ErrorEntity handleAccessDeniedException(AccessDeniedException e) {
        log.warn(String.valueOf(e));
        return new ErrorEntity(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RuntimeException.class})
    public @ResponseBody ErrorEntity handleRuntimeException(RuntimeException e) {
        log.error(String.valueOf(e));
        return new ErrorEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
    }
}
