package ptml.releasing.db.models.config;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import ptml.releasing.db.models.base.BaseModel;

@Entity
public class BaseConfig extends BaseModel {

    @SerializedName("value")
    private String value;



    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseConfig)) return false;
        if (!super.equals(o)) return false;

        BaseConfig that = (BaseConfig) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
