package ntlong.enums;

public enum TypeAssignEnum {

    FREE("MIỄN PHÍ"), PAID("ĐÃ MUA");
    private String value;

    TypeAssignEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
