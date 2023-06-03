package ntlong.payload.response;

import lombok.Data;

@Data
public class RatingResponse {
    private float rate;
    private int rateFiveStar;
    private int rateFourStar;
    private int rateThreeStar;
    private int rateTwoStar;
    private int rateOneStar;
}
