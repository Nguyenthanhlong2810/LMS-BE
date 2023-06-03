package ntlong.model;

public enum NewsContentType {
    NORMAL("Thông thường"),
    VIDEO("Tin video"),
    COURSE_LINKED("Liên kết khóa học"),
    EVENT_LINKED("Liên kết sự kiện");

    public final String type;

    private NewsContentType(String type){
        this.type = type;
    }
}