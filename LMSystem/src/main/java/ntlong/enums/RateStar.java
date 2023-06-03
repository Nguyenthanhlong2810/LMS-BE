package ntlong.enums;

import lombok.Getter;

@Getter
public enum RateStar {

    ONE_STAR(1),

    TWO_STAR(2),

    THREE_STAR(3),

    FOUR_STAR(4),

    FIVE_STAR(5);

    RateStar(int value){
        this.value = value;
    }
    private int value;
}
