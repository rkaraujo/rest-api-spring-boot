package software.renato.timemanager.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class } )
    public ResponseEntity<MessageResponseDTO> handleUnauthorizedException(Exception ex) {
        return new ResponseEntity<>(new MessageResponseDTO("Unauthorized"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageResponseDTO> handleValidatorException(ConstraintViolationException ex) {
        String validationMessage = ex.getConstraintViolations().stream()
                .map(violation -> "'" + violation.getMessage() + "'")
                .collect(Collectors.joining(","));
        return new ResponseEntity<>(new MessageResponseDTO(validationMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<MessageResponseDTO> handleBusinessException(BusinessException ex) {
        return new ResponseEntity<>(new MessageResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String validationMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> "'" + error.getDefaultMessage() + "'")
                .collect(Collectors.joining(","));
        return new ResponseEntity<>(new MessageResponseDTO(validationMessage), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(new MessageResponseDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(new MessageResponseDTO(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleGenericException(Exception ex) {
        LOGGER.error("Unexpected error", ex);
        return new ResponseEntity<>(new MessageResponseDTO("Generic error: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
