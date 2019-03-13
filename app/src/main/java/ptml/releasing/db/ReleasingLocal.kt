package ptml.releasing.db

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.internal.operators.observable.ObservableFromCallable
import ptml.releasing.db.models.config.CargoType
import ptml.releasing.db.models.config.OperationStep
import ptml.releasing.db.models.config.Terminal
import ptml.releasing.di.modules.rx.OBSERVER_ON
import ptml.releasing.di.modules.rx.SUBSCRIBER_ON
import javax.inject.Inject
import javax.inject.Named

class ReleasingLocal @Inject constructor(appDatabase: AppDatabase,
                                         @param:Named(SUBSCRIBER_ON) var subscriberOn: Scheduler,
                                         @param:Named(OBSERVER_ON) var observerOn: Scheduler) : Local {
    val terminalDao = appDatabase.terminalDao()
    val cargoTypeDao = appDatabase.cargoTypeDao()
    val operationStepDao = appDatabase.operationStepDao()

    override fun insertTerminals(list: List<Terminal>) = ObservableFromCallable { terminalDao.insert(list) }.subscribeOn(subscriberOn).observeOn(observerOn)


    override fun insertTerminal(terminal: Terminal) = ObservableFromCallable { terminalDao.insert(terminal) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun updateTerminals(list: List<Terminal>) = ObservableFromCallable { terminalDao.update(list) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun getTerminal(id: Int) = terminalDao.getItemById(id)

    override fun getTerminals() = terminalDao.all

    override fun deleteTerminal(terminal: Terminal) = ObservableFromCallable { terminalDao.delete(terminal) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun deleteTerminals(list: List<Terminal>) = ObservableFromCallable { terminalDao.delete(list) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun insertCargoTypes(list: List<CargoType>) = ObservableFromCallable { cargoTypeDao.insert(list) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun insertCargoType(terminal: CargoType) = ObservableFromCallable { cargoTypeDao.insert(terminal) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun updateCargoTypes(list: List<CargoType>) = ObservableFromCallable { cargoTypeDao.update(list) }.subscribeOn(subscriberOn).observeOn(observerOn)


    override fun getCargoType(id: Int) = cargoTypeDao.getItemById(id)

    override fun getCargoTypes() = cargoTypeDao.all


    override fun deleteCargoType(terminal: CargoType) = ObservableFromCallable { cargoTypeDao.delete(terminal) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun deleteCargoTypes(list: List<CargoType>) = ObservableFromCallable { cargoTypeDao.delete(list) }.subscribeOn(subscriberOn).observeOn(observerOn)


    override fun insertOperationSteps(list: List<OperationStep>) = ObservableFromCallable { operationStepDao.insert(list) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun insertOperationStep(terminal: OperationStep) = ObservableFromCallable { operationStepDao.insert(terminal) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun updateOperationSteps(list: List<OperationStep>) = ObservableFromCallable { operationStepDao.update(list) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun getOperationStep(id: Int) = operationStepDao.getItemById(id)

    override fun getOperationSteps() = operationStepDao.all

    override fun deleteOperationStep(terminal: OperationStep) = ObservableFromCallable { operationStepDao.delete(terminal) }.subscribeOn(subscriberOn).observeOn(observerOn)

    override fun deleteOperationSteps(list: List<OperationStep>) = ObservableFromCallable { operationStepDao.delete(list) }.subscribeOn(subscriberOn).observeOn(observerOn)

}