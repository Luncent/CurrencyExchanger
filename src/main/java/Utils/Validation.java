package Utils;

import java.math.BigDecimal;

public class Validation {
    public static boolean isBigDecimal(String rateToCheck){
        try{
            BigDecimal rate = BigDecimal.valueOf(Double.valueOf(rateToCheck));
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
