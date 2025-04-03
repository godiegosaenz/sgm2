package util;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author carlosloorvargas
 */
public class PhoneUtils {
    
    public static boolean getValidNumber(String number, String reg) {
        boolean flag;
        PhoneNumberUtil pnu;
        Phonenumber.PhoneNumber nProto;
        try {
            pnu = PhoneNumberUtil.getInstance();
            nProto = pnu.parse(number, reg);
            if(nProto != null) {
                flag = pnu.isPossibleNumber(nProto) == true && pnu.isValidNumber(nProto) == true;
            } else {
                flag = false;
            }
        } catch (Exception e) {
            flag = false;
            Logger.getLogger(PhoneUtils.class.getName()).log(Level.SEVERE, null, e);
        }
        return flag;
    }
    
}