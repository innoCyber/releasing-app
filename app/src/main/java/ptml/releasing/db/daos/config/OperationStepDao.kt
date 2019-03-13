package ptml.releasing.db.daos.config

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Observable
import ptml.releasing.db.daos.base.BasicDAORx
import ptml.releasing.db.models.config.OperationStep


@Dao
interface OperationStepDao : BasicDAORx<OperationStep> {

    @Query("SELECT * FROM OperationStep")
    override fun getAll(): Observable<List<OperationStep>>

    @Query("SELECT * FROM OperationStep WHERE id=:id")
    override fun getItemById(id: Int): Observable<OperationStep>
}