package ptml.releasing.damages.model;

/**
 * Created by marcojacovone on 31/03/17.
 */

public class Damage {

    @SuppressWarnings("unused")
    private static final String TAG = Damage.class.getSimpleName();

    private int id;
    private String name;
    private int containerType; // 0 = small, 1 = large
    private String position; // 4 characters: F/B for zone + FRO, TOP, ... for position

    public int getContainerType() {
        return containerType;
    }

    public void setContainerType(int containerType) {
        this.containerType = containerType;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Damage(int id, String name, int containerType, String position) {
        this.id = id;
        this.name = name;
        this.containerType = containerType;
        this.position = position;
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
