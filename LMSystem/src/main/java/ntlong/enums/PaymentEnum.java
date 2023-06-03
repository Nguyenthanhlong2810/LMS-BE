package ntlong.enums;

public enum PaymentEnum {
    UNPAID("Chưa Thanh Toán"), PAY_SUCCESS("Đã thanh toán"), PAY_ERROR("Thanh toán lỗi");
    private String value;

    PaymentEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
