package ptml.releasing

import io.reactivex.Observable
import io.reactivex.internal.operators.observable.ObservableFromCallable
import ptml.releasing.db.Local
import ptml.releasing.db.models.config.CargoType
import ptml.releasing.db.models.config.OperationStep
import ptml.releasing.db.models.config.Terminal
import java.io.File
import java.lang.IllegalStateException

class FakeReleasingDb : Local{

    val terminalList = mutableMapOf<Int, Terminal>()
    val cargoTypeList = mutableMapOf<Int, CargoType>()
    val operationStepList = mutableMapOf<Int, OperationStep>()

    override fun insertTerminals(list: List<Terminal>) =  Observable.fromCallable {
        for (mutableEntry in list) {
            terminalList[mutableEntry.id] = mutableEntry
        }
    }

    override fun insertTerminal(terminal: Terminal) =  Observable.fromCallable { terminalList[terminal.id] = terminal
        Unit}


    override fun updateTerminals(list: List<Terminal>) =  Observable.fromCallable {
        for (mutableEntry in list) {
            terminalList[mutableEntry.id] = mutableEntry
        }
    }

    override fun getTerminal(id: Int): Observable<Terminal> {
        return Observable.create {
            try {
                val terminal = terminalList.get(id)
                if (terminal != null) {
                    it.onNext(terminal)
                }else{
                    it.onError(IllegalStateException("Terminal is null"))
                }
            }catch (e:Exception){
                it.onError(e)
            }
        }
    }

    override fun getTerminals(): Observable<List<Terminal>> {
        return Observable.create {
            try {
                val list = mutableListOf<Terminal>()
                for (mutableEntry in terminalList.values) {
                    list.add(mutableEntry)
                }
                it.onNext(list)

            }catch (e:Exception){
                it.onError(e)
            }

        }
    }

    override fun deleteTerminal(terminal: Terminal) = ObservableFromCallable{terminalList.remove(terminal.id)
    Unit}

    override fun deleteTerminals(list: List<Terminal>) = ObservableFromCallable{
        for (terminal in list) {
            terminalList.remove(terminal.id)
        }
    }

    override fun insertCargoTypes(list: List<CargoType>) =  Observable.fromCallable {
        for (mutableEntry in list) {
            cargoTypeList[mutableEntry.id] = mutableEntry
        }
    }

    override fun insertCargoType(cargoType: CargoType) =  Observable.fromCallable { cargoTypeList[cargoType.id] = cargoType
        Unit}


    override fun updateCargoTypes(list: List<CargoType>) =  Observable.fromCallable {
        for (mutableEntry in list) {
            cargoTypeList[mutableEntry.id] = mutableEntry
        }
    }

    override fun getCargoType(id: Int): Observable<CargoType> {
        return Observable.create {
            try {
                val cargoType = cargoTypeList.get(id)
                if (cargoType != null) {
                    it.onNext(cargoType)
                }else{
                    it.onError(IllegalStateException("CargoType is null"))
                }
            }catch (e:Exception){
                it.onError(e)
            }
        }
    }

    override fun getCargoTypes(): Observable<List<CargoType>> {
        return Observable.create {
            try {
                val list = mutableListOf<CargoType>()
                for (mutableEntry in cargoTypeList.values) {
                    list.add(mutableEntry)
                }
                it.onNext(list)

            }catch (e:Exception){
                it.onError(e)
            }

        }
    }

    override fun deleteCargoType(cargoType: CargoType) = ObservableFromCallable{cargoTypeList.remove(cargoType.id)
        Unit}

    override fun deleteCargoTypes(list: List<CargoType>) = ObservableFromCallable{
        for (cargoType in list) {
            cargoTypeList.remove(cargoType.id)
        }
    }

    override fun insertOperationSteps(list: List<OperationStep>) =  Observable.fromCallable {
        for (mutableEntry in list) {
            operationStepList[mutableEntry.id] = mutableEntry
        }
    }

    override fun insertOperationStep(operationStep: OperationStep) =  Observable.fromCallable { operationStepList[operationStep.id] = operationStep
        Unit}


    override fun updateOperationSteps(list: List<OperationStep>) =  Observable.fromCallable {
        for (mutableEntry in list) {
            operationStepList[mutableEntry.id] = mutableEntry
        }
    }

    override fun getOperationStep(id: Int): Observable<OperationStep> {
        return Observable.create {
            try {
                val operationStep = operationStepList.get(id)
                if (operationStep != null) {
                    it.onNext(operationStep)
                }else{
                    it.onError(IllegalStateException("OperationStep is null"))
                }
            }catch (e:Exception){
                it.onError(e)
            }
        }
    }

    override fun getOperationSteps(): Observable<List<OperationStep>> {
        return Observable.create {
            try {
                val list = mutableListOf<OperationStep>()
                for (mutableEntry in operationStepList.values) {
                    list.add(mutableEntry)
                }
                it.onNext(list)

            }catch (e:Exception){
                it.onError(e)
            }

        }
    }

    override fun deleteOperationStep(operationStep: OperationStep) = ObservableFromCallable{operationStepList.remove(operationStep.id)
        Unit}

    override fun deleteOperationSteps(list: List<OperationStep>) = ObservableFromCallable{
        for (operationStep in list) {
            operationStepList.remove(operationStep.id)
        }
    }


    private fun getJson(path: String): String {
        val uri = this.javaClass.classLoader.getResource(path)
        val file = File(uri.path)
        return String(file.readBytes())
    }
}
