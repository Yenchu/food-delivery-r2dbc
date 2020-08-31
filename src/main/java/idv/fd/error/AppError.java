package idv.fd.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppError {

    private Integer code;
    private Integer status;
    private String msg;

    public static AppError badRequest(String msg) {

        return AppError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .msg(msg)
                .build();
    }

    public static AppError unauthorized(String msg) {

        return AppError.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .msg(msg)
                .build();
    }

    public static AppError forbidden(String msg) {

        return AppError.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .msg(msg)
                .build();
    }

    public static AppError notFound(String msg) {

        return AppError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .msg(msg)
                .build();
    }

    public static AppError invalidMimeType(String msg) {

        return AppError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .msg(msg)
                .build();
    }

    public static AppError internalServerError(String msg) {

        return AppError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .msg(msg)
                .build();
    }

}
