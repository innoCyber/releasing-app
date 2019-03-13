package ptml.releasing.db.models.config;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;

@Entity
public class OperationStep  extends BaseConfig {
    @SerializedName("cargo_type_id")
    private int categoryTypeId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationStep)) return false;
        if (!super.equals(o)) return false;

        OperationStep that = (OperationStep) o;

        return categoryTypeId == that.categoryTypeId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + categoryTypeId;
        return result;
    }

    public int getCategoryTypeId() {
        return categoryTypeId;
    }

    public void setCategoryTypeId(int categoryTypeId) {
        this.categoryTypeId = categoryTypeId;
    }
}
