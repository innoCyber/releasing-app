package ptml.releasing.db.models.config.response;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import ptml.releasing.db.models.base.BaseResponse;
import ptml.releasing.db.models.config.CargoType;
import ptml.releasing.db.models.config.OperationStep;
import ptml.releasing.db.models.config.Terminal;

import java.util.List;

public class ConfigurationResponse extends BaseResponse {



    @SerializedName("cargo_type")
    private List<CargoType> cargoTypeList;

    @SerializedName("operation_step")
    private List<OperationStep> operationStepList;


    @SerializedName("terminal")
    private List<Terminal> terminalList;


    public ConfigurationResponse(List<CargoType> cargoTypeList, List<OperationStep> operationStepList, List<Terminal> terminalList) {
        this.cargoTypeList = cargoTypeList;
        this.operationStepList = operationStepList;
        this.terminalList = terminalList;
    }

    public ConfigurationResponse() {
    }

    public List<CargoType> getCargoTypeList() {
        return cargoTypeList;
    }

    public void setCargoTypeList(List<CargoType> cargoTypeList) {
        this.cargoTypeList = cargoTypeList;
    }

    public List<OperationStep> getOperationStepList() {
        return operationStepList;
    }

    public void setOperationStepList(List<OperationStep> operationStepList) {
        this.operationStepList = operationStepList;
    }

    public List<Terminal> getTerminalList() {
        return terminalList;
    }

    public void setTerminalList(List<Terminal> terminalList) {
        this.terminalList = terminalList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurationResponse)) return false;
        if (!super.equals(o)) return false;

        ConfigurationResponse that = (ConfigurationResponse) o;

        if (cargoTypeList != null ? !cargoTypeList.equals(that.cargoTypeList) : that.cargoTypeList != null)
            return false;
        if (operationStepList != null ? !operationStepList.equals(that.operationStepList) : that.operationStepList != null)
            return false;
        return terminalList != null ? terminalList.equals(that.terminalList) : that.terminalList == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (cargoTypeList != null ? cargoTypeList.hashCode() : 0);
        result = 31 * result + (operationStepList != null ? operationStepList.hashCode() : 0);
        result = 31 * result + (terminalList != null ? terminalList.hashCode() : 0);
        return result;
    }
}
