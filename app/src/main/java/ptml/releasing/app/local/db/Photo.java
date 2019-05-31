package ptml.releasing.app.local.db;

/**
 * Created by marcojacovone on 08/04/17.
 */

public class Photo {

    private int id;
    private String filename;
    private int cargoId;
    private byte[] data;

    public Photo(int id, String filename, int cargoId, byte[] data) {
        this.id = id;
        this.filename = filename;
        this.cargoId = cargoId;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getCargoId() {
        return cargoId;
    }

    public void setCargoId(int cargoId) {
        this.cargoId = cargoId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
