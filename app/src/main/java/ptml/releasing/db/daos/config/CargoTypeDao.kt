package ptml.releasing.db.daos.config

import androidx.room.Query
import io.reactivex.Observable
import ptml.releasing.db.daos.base.BasicDAORx
import ptml.releasing.db.models.config.CargoType


interface CargoTypeDao : BasicDAORx<CargoType> {

    @Query("SELECT * FROM CargoType")
    override fun getAll(): Observable<List<CargoType>>

    @Query("SELECT * FROM CargoType WHERE id=:id")
    override fun getItemById(id: Int): Observable<CargoType>
}