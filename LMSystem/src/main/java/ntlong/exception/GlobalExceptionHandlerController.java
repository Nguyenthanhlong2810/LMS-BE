package ntlong.exception;

import ntlong.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public ErrorAttributes errorAttributes() {
        // Hide exception field in the return object
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                return super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults().excluding(ErrorAttributeOptions.Include.EXCEPTION));
            }
        };
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse>  handleCustomException(CustomException ex, WebRequest request) {
        log.error(ex.getMessage());
        BaseResponse baseResponse = new BaseResponse(ex.getHttpStatus().value(), ex.getMessage(), request.getDescription(true));
        return new ResponseEntity<>(baseResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
    }
    @ExceptionHandler(UploadFailException.class)
    public ResponseEntity<BaseResponse> handleExceptionUpload(UploadFailException ex, WebRequest request) {
        log.error(ex.getMessage());
        BaseResponse message = new BaseResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(true));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
    @ExceptionHandler({Exception.class,
            CreateFailException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<BaseResponse> handleException(Exception ex, WebRequest request) {
        log.error(ex.getMessage());
        BaseResponse message = new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getCause().getMessage(),null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    @ExceptionHandler({NewsNotExistsException.class,
            UserNotFoundException.class}
    )
    public void handleNotFoundException(HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.NOT_FOUND.value(), "Not Found");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<BaseResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.error(ex.getMessage());
        BaseResponse message = new BaseResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getDescription(true));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse> handleRuntimeException(BusinessException ex, WebRequest request) {
        log.error(ex.getMessage());
        BaseResponse message = new BaseResponse(
                ex.getCode().value(),
                ex.getMessage(),
                ex.getMessage());
        return ResponseEntity.status(ex.getCode().value()).body(message);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<BaseResponse> handleExceptionUpload(MaxUploadSizeExceededException ex, WebRequest request) {
        log.error(ex.getMessage());
        BaseResponse message = new BaseResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),"Vượt quá giới hạn tải nội dung : 200MB" , request.getDescription(true));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
