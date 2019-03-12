package ptml.releasing.db.models.response.base;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class BaseResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public BaseResponse() {
    }

    public BaseResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseResponse)) return false;

        BaseResponse that = (BaseResponse) o;

        if (success != that.success) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (success ? 1 : 0);
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "BaseResponse{" +
                "message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
