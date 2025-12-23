package ir.tejaratBank.core.CoreBank;

import java.math.BigInteger;

public class BankingUtils {

    private static final String TEJARAT_BANK_CODE = "018";
    private static final String COUNTRY_CODE = "1827";

    public static String calculateCheckDigit(String baseNumber) {
        int s = 0;
        boolean alternate = false;
        for (int i = baseNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(baseNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n = (n % 10) + 1;
            }
            s += n;
            alternate = !alternate;

        }
        int checkDigit = (10 - (s % 10)) % 10;
        return String.valueOf(checkDigit);
    }

//

    public static String generateIBAN(String accountNumber) {
        String bban = TEJARAT_BANK_CODE + String.format("%019d" , Long.parseLong(accountNumber));
        String tempIban = bban + COUNTRY_CODE + "00";
        BigInteger bi = new BigInteger(tempIban);
        int mod97 = bi.mod(new BigInteger("97")).intValue();
        int checkDigits = 98 - mod97;
        String checkDigitsStr = String.format("%02d", checkDigits);
        return "IR" + checkDigitsStr + bban;
    }
}
