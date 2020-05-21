package ptml.releasing.damages.model;

import org.jetbrains.annotations.NotNull;

import ptml.releasing.cargo_info.model.FormDamage;

/**
 * Created by marcojacovone on 14/04/17.
 */

public class ReleasingAssignedDamage extends ReleasingDamage {

    private String damageRemarks;
    private int damageCount;
    private String size; // Small or Large
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public ReleasingAssignedDamage(int id, String name, String damageRemarks, int damageCount, int containerType, String position, String location, String size) {
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

    @NotNull
    public FormDamage toFormDamage() {
        FormDamage damage = new FormDamage(getDamageCount(), getContainerType(), damageRemarks, location, size);
        damage.setId(getId());
        return damage;
    }
}
