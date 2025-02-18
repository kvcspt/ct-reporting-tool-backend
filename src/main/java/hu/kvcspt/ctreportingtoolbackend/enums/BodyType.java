package hu.kvcspt.ctreportingtoolbackend.enums;

public enum BodyType {
    KNEE("KNEE"),
    ABDOMEN("ABDOMEN"),
    CHEST("CHEST");

    private final String value;

    BodyType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
