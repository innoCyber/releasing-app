package ptml.releasing.db;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function3;
import ptml.releasing.api.ReleasingRemote;
import ptml.releasing.db.models.config.CargoType;
import ptml.releasing.db.models.config.OperationStep;
import ptml.releasing.db.models.config.Terminal;
import ptml.releasing.db.models.config.response.ConfigurationResponse;

public class TestDb {
    private ReleasingRemote remote;
    private ReleasingLocal local;

    public TestDb(ReleasingRemote remote, ReleasingLocal local) {
        this.remote = remote;
        this.local = local;
    }

    public  Observable<ConfigurationResponse> get(){
        return Observable.combineLatest(local.getCargoTypes(), local.getOperationSteps(), local.getTerminals(), new Function3<List<CargoType>, List<OperationStep>, List<Terminal>, ConfigurationResponse>() {
            @Override
            public ConfigurationResponse apply(List<CargoType> cargoTypes, List<OperationStep> operationSteps, List<Terminal> terminals) throws Exception {
                return new ConfigurationResponse("", true, cargoTypes, operationSteps, terminals);
            }
        });
    }
}
