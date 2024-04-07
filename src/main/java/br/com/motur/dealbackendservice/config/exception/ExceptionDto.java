package br.com.motur.dealbackendservice.config.exception;

public class ExceptionDto {
    String code;
    String message;
    AlertType alertType;

    public ExceptionDto(String code, String message, AlertType alertType) {
        this.code = code;
        this.message = message;
        this.alertType = alertType;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public AlertType getAlertType() {
        return this.alertType;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ExceptionDto)) {
            return false;
        } else {
            ExceptionDto other = (ExceptionDto)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$code = this.getCode();
                    Object other$code = other.getCode();
                    if (this$code == null) {
                        if (other$code == null) {
                            break label47;
                        }
                    } else if (this$code.equals(other$code)) {
                        break label47;
                    }

                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                Object this$alertType = this.getAlertType();
                Object other$alertType = other.getAlertType();
                if (this$alertType == null) {
                    if (other$alertType != null) {
                        return false;
                    }
                } else if (!this$alertType.equals(other$alertType)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ExceptionDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $alertType = this.getAlertType();
        result = result * 59 + ($alertType == null ? 43 : $alertType.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getCode();
        return "ExceptionDto(code=" + var10000 + ", message=" + this.getMessage() + ", alertType=" + String.valueOf(this.getAlertType()) + ")";
    }

    public ExceptionDto() {
    }

    public enum AlertType {
        MODAL,
        ALERT;

        private AlertType() {
        }
    }
}
