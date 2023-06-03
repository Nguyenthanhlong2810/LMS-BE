package ntlong.enums;

public enum StatusCourseEnum {
    NOT_STARTED("Chưa bắt đầu"), UNCOMPLETED("Đang học"), COMPLETED("Hoàn thành")
    , OVER_DUE("Quá hạn");
    private String value;

    StatusCourseEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
