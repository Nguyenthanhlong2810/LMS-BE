package ntlong.enums;

public enum UnitTimeEnum {

    DAY("Ngày"),
    WEEK("Tuần"),
    MONTH("Tháng"),
    YEAR("Năm");

    private String value;

    UnitTimeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
