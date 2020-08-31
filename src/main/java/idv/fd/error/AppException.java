package idv.fd.error;

public class AppException extends RuntimeException {

    private AppError error;

    public AppException() {

        super();
    }

    public AppException(String message) {

        this(null, message);
    }

    public AppException(Integer code, String message) {

        super(message);
        this.error = AppError.builder()
                .code(code)
                .msg(message)
                .build();
    }

    public AppException(Throwable cause) {

        super(cause);
        this.error = AppError.builder()
                .msg(cause.getMessage())
                .build();
    }

    public AppException(AppError error) {

        super(error.getMsg());
        this.error = error;
    }

    public AppError getError() {

        return error;
    }

    public void setError(AppError error) {

        this.error = error;
    }

    public static AppException badRequest(String msg) {

        return new AppException(AppError.badRequest(msg));
    }

    public static AppException unauthorized(String msg) {

        return new AppException(AppError.unauthorized(msg));
    }

    public static AppException forbidden(String msg) {

        return new AppException(AppError.forbidden(msg));
    }

    public static AppException notFound(String msg) {

        return new AppException(AppError.notFound(msg));
    }

    public static AppException invalidMimeType(String msg) {

        return new AppException(AppError.invalidMimeType(msg));
    }
}
