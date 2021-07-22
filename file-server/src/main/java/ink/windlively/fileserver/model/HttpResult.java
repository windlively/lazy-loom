package ink.windlively.fileserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResult<R> {

    private String message;

    private String code;

    private boolean success;

    private R data;

    public static String DEFAULT_SUCCESS_CODE = "Y0000";

    public static String DEFAULT_FAILED_CODE = "E0000";

    public static HttpResult<?> FAILED = new HttpResult<>(null, DEFAULT_FAILED_CODE, false, null);

    public static <Z> HttpResult<Z> FAILED(String msg){
        return new HttpResult<>(msg, DEFAULT_FAILED_CODE, false, null);
    }
    
    public static <Z> HttpResult<Z> FAILED(String msg, Z data){
        return new HttpResult<>(msg, DEFAULT_FAILED_CODE, false, data);
    }

    public static HttpResult<?> SUCCESS = new HttpResult<>(null, DEFAULT_SUCCESS_CODE, true, null);

    public static <Z> HttpResult<Z> SUCCESS(Z data){
        return new HttpResult<>(null, DEFAULT_SUCCESS_CODE, true, data);
    }

    public static <Z> HttpResult<Z> SUCCESS(String msg){
        return new HttpResult<>(msg, DEFAULT_SUCCESS_CODE, true, null);
    }

    public static <Z> HttpResult<Z> SUCCESS(String msg, Z data){
        return new HttpResult<>(msg, DEFAULT_SUCCESS_CODE, true, data);
    }

}
