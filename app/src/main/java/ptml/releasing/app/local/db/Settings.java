package ptml.releasing.app.local.db;

import ptml.releasing.configuration.models.CargoType;

/**
 * Created by marcojacovone on 31/03/17.
 */
public class Settings {

    @SuppressWarnings("unused")
    private static final String TAG = Settings.class.getSimpleName();

    public enum OperationMode {
        SHIP_SIDE(0),
        PARKING_SLOT(1);

        private int code;

        OperationMode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static OperationMode enumFromCode(int code) {
            switch(code) {
                case 0: return SHIP_SIDE;
                default: return PARKING_SLOT;
            }
        }

        @Override
        public String toString() {
            switch(code) {
                case 0: return "Ship Side";
                default: return "Parking Slot";
            }
        }
    }

    public enum Terminal {
        MAIN(0),
        SATELLITE_CAR_PARK(1),
        TERMINAL_2(2),
        DEPOT(3);

        private int code;

        Terminal(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        /**
         *
         * @return
         */
        public int getExternalCode() {
            if(code == 0)
                return 3;
            else if(code == 1)
                return 1;
            else if(code == 2)
                return 2;
            else if(code == 3)
                return 4;
            return 0;
        }

       public static Terminal enumFromCode(int code) {
            switch(code) {
                case 0: return MAIN;
                case 1: return SATELLITE_CAR_PARK;
                case 2: return TERMINAL_2;
                case 3: return DEPOT;
                default: return MAIN;
            }
        }

        @Override
        public String toString() {
            switch(code) {
                case 0: return "Main";
                case 1: return "Satellite Car Park";
                case 2: return "Terminal 2";
                case 3: return "Depot";
                default: return "Main";
            }
        }
    }

    private int id;
    private OperationMode operationMode;
    private CargoType cargoType;
    private Terminal terminal;
    private int parkingStep;

    private boolean enableScan;
    private String wsBase;
    private String imagesUrl;
    private String imagesPath;
    private String imagesUsername;
    private String imagesPassword;
    private String currentPrinter;
    private String currentPrinterName;
    private String labelCpclData;

    public boolean isDeviceEnabled() {
        return deviceEnabled;
    }

    public void setDeviceEnabled(boolean deviceEnabled) {
        this.deviceEnabled = deviceEnabled;
    }

    private boolean deviceEnabled;

    public Settings(int id, OperationMode operationMode, CargoType cargoType, Terminal terminal, int parkingStep, boolean enableScan, String wsBase, String imagesUrl, String imagesPath, String imagesUsername, String imagesPassword,
                    String currentPrinter, String currentPrinterName, String labelCpclData) {
        this.id = id;
        this.operationMode = operationMode;
        this.cargoType = cargoType;
        this.terminal = terminal;
        this.parkingStep = parkingStep;
        this.enableScan = enableScan;
        this.wsBase = wsBase;
        this.imagesUrl = imagesUrl;
        this.imagesPath = imagesPath;
        this.imagesUsername = imagesUsername;
        this.imagesPassword = imagesPassword;
        this.deviceEnabled = false;
        this.currentPrinter = currentPrinter;
        this.currentPrinterName = currentPrinterName;
        this.labelCpclData = labelCpclData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OperationMode getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(OperationMode operationMode) {
        this.operationMode = operationMode;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public void setCargoType(CargoType cargoType) {
        this.cargoType = cargoType;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public String getWsBase() {
        return wsBase;
    }

    public void setWsBase(String wsBase) {
        this.wsBase = wsBase;
    }

    public String getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(String imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public String getImagesPath() {
        return imagesPath;
    }

    public void setImagesPath(String imagesPath) {
        this.imagesPath = imagesPath;
    }

    public String getImagesUsername() {
        return imagesUsername;
    }

    public void setImagesUsername(String imagesUsername) {
        this.imagesUsername = imagesUsername;
    }

    public String getImagesPassword() {
        return imagesPassword;
    }

    public void setImagesPassword(String imagesPassword) {
        this.imagesPassword = imagesPassword;
    }

    public int getParkingStep() {
        return parkingStep;
    }

    public void setParkingStep(int parkingStep) {
        this.parkingStep = parkingStep;
    }

    public boolean isEnableScan() {
        return enableScan;
    }

    public void setEnableScan(boolean enableScan) {
        this.enableScan = enableScan;
    }

    public String getCurrentPrinter() {
        return currentPrinter;
    }

    public void setCurrentPrinter(String currentPrinter) {
        this.currentPrinter = currentPrinter;
    }

    public String getCurrentPrinterName() {
        return currentPrinterName;
    }

    public void setCurrentPrinterName(String currentPrinterName) {
        this.currentPrinterName = currentPrinterName;
    }

    public String getLabelCpclData() {
        return labelCpclData;
    }

    public void setLabelCpclData(String labelCpclData) {
        this.labelCpclData = labelCpclData;
    }
}
