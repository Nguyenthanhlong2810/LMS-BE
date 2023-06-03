package ntlong.common;

import ntlong.exception.CustomException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    private Util() {
    }

    public static String getDateString(Date date,String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static Date getDateWithoutTimeUsingFormat(Date date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd-MM-yyyy");
            return formatter.parse(formatter.format(date));
        }catch (ParseException e){
            return date;
        }
    }


}
