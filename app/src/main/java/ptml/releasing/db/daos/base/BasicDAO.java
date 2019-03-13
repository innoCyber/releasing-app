package ptml.releasing.db.daos.base;



import java.util.Collection;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;


@Dao
public interface BasicDAO<Entity> {



    /* Inserts */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insert(final Entity entity);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(final Collection<Entity> entities);

    /* Deletes */
    @Delete
    void delete(final Entity entity);

    @Delete
    void delete(final Collection<Entity> entities);

    /*@Delete
    int deleteAll();*/

    /* Updates */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(final Entity entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(final Collection<Entity> entities);


}
