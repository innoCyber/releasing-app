package ptml.releasing.damages.model;

/**
 * Created by marcojacovone on 14/04/17.
 */

public class AssignedDamage extends Damage {

    private String damageRemarks;
    private int damageCount;
    private int size; // 0 = low, 1 = high
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public AssignedDamage(int id, String name, String damageRemarks, int damageCount, int containerType, String position, String location, int size) {
        super(id, name, containerType, position);
        this.damageRemarks = damageRemarks;
        this.damageCount = damageCount;
        this.size = size;
        this.location = location;
    }

    public String getDamageRemarks() {
        return damageRemarks;
    }

    public void setDamageRemarks(String damageRemarks) {
        this.damageRemarks = damageRemarks;
    }

    public int getDamageCount() {
        return damageCount;
    }

    public void setDamageCount(int damageCount) {
        this.damageCount = damageCount;
    }
}
