package ptml.releasing.db.daos.base;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface BasicDAOLive<Entity> extends BasicDAO<Entity>
{
    LiveData<List<Entity>> getAll();

    LiveData<Entity> getItemById(int id);
}
