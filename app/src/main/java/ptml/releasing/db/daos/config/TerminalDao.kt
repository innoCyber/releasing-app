package ptml.releasing.db.daos.config

import androidx.room.Query
import io.reactivex.Observable
import ptml.releasing.db.daos.base.BasicDAORx
import ptml.releasing.db.models.config.Terminal

interface TerminalDao : BasicDAORx<Terminal> {
    @Query("SELECT * FROM Terminal")
    override fun getAll(): Observable<List<Terminal>>

    @Query("SELECT * FROM Terminal WHERE id=:id")
    override fun getItemById(id: Int): Observable<Terminal>
}