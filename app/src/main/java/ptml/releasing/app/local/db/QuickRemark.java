package ptml.releasing.app.local.db;

/**
 * Created by marcojacovone on 31/03/17.
 */

public class QuickRemark {

    @SuppressWarnings("unused")
    private static final String TAG = QuickRemark.class.getSimpleName();

    private int id;
    private String name;

    public QuickRemark(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
