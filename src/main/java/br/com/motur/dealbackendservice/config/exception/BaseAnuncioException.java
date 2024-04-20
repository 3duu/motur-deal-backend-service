package br.com.motur.dealbackendservice.config.exception;
import lombok.Data;


@Data
public class BaseAnuncioException extends RuntimeException {

    private final DefaultErrorCode falha;
    private final Boolean sqsRollback;
    public BaseAnuncioException( final DefaultErrorCode falha) {
        super(falha.getMessage());
        this.falha = falha;
        this.sqsRollback = true;
    }
    public BaseAnuncioException(final DefaultErrorCode falha, Boolean roolback) {
        this.falha = falha;
        this.sqsRollback = roolback;
    }
    public BaseAnuncioException(final DefaultErrorCode falha, final Throwable e) {
        super(e);
        this.falha = falha;
        this.sqsRollback = true;
    }
    public BaseAnuncioException(final DefaultErrorCode falha, final Throwable e, Boolean rollback) {
        super(e);
        this.falha = falha;
        this.sqsRollback = rollback;
    }
}
