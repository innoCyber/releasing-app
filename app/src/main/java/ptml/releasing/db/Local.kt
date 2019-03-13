package ptml.releasing.db

import io.reactivex.Observable

import ptml.releasing.db.models.config.CargoType
import ptml.releasing.db.models.config.OperationStep
import ptml.releasing.db.models.config.Terminal

interface Local {
    fun insertTerminals(list:List<Terminal>): Observable<Unit>
    fun insertTerminal(terminal: Terminal) : Observable<Unit>
    fun updateTerminals(list: List<Terminal>): Observable<Unit>
    fun getTerminal(id: Int): Observable<Terminal>
    fun getTerminals(): Observable<List<Terminal>>
    fun deleteTerminal(terminal: Terminal): Observable<Unit>
    fun deleteTerminals(list: List<Terminal>): Observable<Unit>


    fun insertCargoTypes(list:List<CargoType>): Observable<Unit>
    fun insertCargoType(cargoType: CargoType): Observable<Unit>
    fun updateCargoTypes(list: List<CargoType>): Observable<Unit>
    fun getCargoType(id: Int): Observable<CargoType>
    fun getCargoTypes(): Observable<List<CargoType>>
    fun deleteCargoType(cargoType: CargoType): Observable<Unit>
    fun deleteCargoTypes(list: List<CargoType>): Observable<Unit>


    fun insertOperationSteps(list:List<OperationStep>): Observable<Unit>
    fun insertOperationStep(operationStep: OperationStep): Observable<Unit>
    fun updateOperationSteps(list: List<OperationStep>): Observable<Unit>
    fun getOperationStep(id: Int): Observable<OperationStep>
    fun getOperationSteps(): Observable<List<OperationStep>>
    fun deleteOperationStep(operationStep: OperationStep): Observable<Unit>
    fun deleteOperationSteps(list: List<OperationStep>): Observable<Unit>
}