package ptml.releasing.db.daos.base;



import java.util.List;

import androidx.room.Dao;
import io.reactivex.Flowable;
import io.reactivex.Observable;


@Dao
public interface BasicDAORx<Entity> extends BasicDAO<Entity> {
    Observable<List<Entity>> getAll();

    Observable<Entity> getItemById(int id);


}
