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
    public static boolean isDouble(String rateToCheck){
        try{
            Double rate = Double.valueOf(rateToCheck);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
