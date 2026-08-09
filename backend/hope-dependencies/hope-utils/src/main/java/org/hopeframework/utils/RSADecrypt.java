package org.hopeframework.utils;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * @author haojiawei
 */
public class RSADecrypt {
    private static String prikey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMm6/rPD4aLGFzufSGZdbetISzSbnn885VJZ441DgMV6/gbZwbjsjB6de8iesltienubYCKTPO6zQUl3H/QEaEqNMzCTMwR8nZEcuG+KqiM8+L5QHmJZoq1Gut80DtiTLKnDQQjWziR/cWShCVO05wF0SA9uRICNXH69NQuaXTyNAgMBAAECgYAXOXEDTyTV8YpRMehK6Kyz7rHBz7yyl1crYO/inofftGY0cbNsz8bd63GJykNsY+0H9/5cXKkiLf7xL0a9eBhtcGOp+b9WkkcAju1x+ih34Fd6IXiHJn/YX8CXO/ofHpJBjN//uSajg9tfbPlNZovXLqyopA3WujMclEH3ofqNYQJBAPD7MVYemlMTKC6hyBwwORniafCXtSvAnCQbiCtADfnRGqUs+cJWVMHqkIoF3ANxqc38/TGO8r4DcaRCFhJN7LUCQQDWTZBgScZ/iHtBJf5Wz+Q0r02FZzyIEYn5Bpazh709vA0YHzxE8PxotMKApWbNIwm6uW8c1TfDRjpHhB1D2s95AkBb+EJYD83uLr2DLpNkzrK8QkEUZZHrZNMypJ9Pgl7THQ8CQyv/E1u9tnv7cUkKt3KK/ybSpy/VfRL+EYrC6aKRAkEArL0UtSiCgeYnQ5o2aw9jqTvsxq0yU5OfnUU8UDYHyi08eMcks4V0T4n0hfHM7WlivEogUCNlsVd4PpE1ba6AMQJBALp61OnDOiBAlOjWfn14DPjKvqrcunJx8EWtdX14vHOPdka8Ap6Js2PtF25Ew8j3lPmnSEH6bmmmnJiFyCqgS68=";


    public static void main(String[] args) throws Exception {
        String encrypted = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMS7mrm2R89foTEs9tKiXHvm9m3+h+ClvEwEpix3NOdyciudTs18SlnJneATgFpdcuThM/EzC7zveJ5hX6T0mV1TUz40GsdpIgYcuUXSvTd9qjsJPIjgCXPgmD8PVI8Gn782gR3eT5Xzsqy+2yVVfnjddfMu2FVHcqi5BRtsEi4hAgMBAAECgYBMmG5JV/rldmof503B/IzdgLL5aFwmzuBdCIJaVZ83bYueB4Ui4/1er+zTzUPv6upMypGamzE6wlHH+Elww/rhdu1PSu7/lX8fNvgX+9upAvkpTXX6dh72+9kB/OlbFBp2JZiu1fGzjlwhbDERl+SRzk3rrwg8xzDMPe5kPWORAQJBAP5NIrErIGiK2I95duB0s0Soz7P7yfEs82xrMw79ot8mUX+RnQitt+7viHy+etEBc5zEMXcRTOB3dznrmluqH+kCQQDGDAZRyr7+/42lalvE25cjbtsIh0IK0Z1w2FnB3HDQnSnPqx1SWy4pNHnGj8k1aEFhmubeyUg3pnDgjZVVMbF5AkEAmUtCSG5o4Nwh44si3/c5QYBJhIOZniqsqoSMiNLYZZAhvLa4ZMzjfcMjEZ95RZiMasnRz6a8IuuhEWc2617IUQJAMEi6ObK+ghj4Bb46hXIsFP1v9VJUraKRSfPVXkhKUPIgQXsseOm2BWG/1sMzgQvb9Nn5M5PLQZYYqwaZw92LsQJBANDrAWhDeSfASB2DrJ2uGRcSZYNAiaSAd+G2wKmJ54L6ZQMbtTU8u1Bulll8Vo1SR1QamxlkzbrPfI4XLSkd4Uw=";

        encrypted=(new String(Base64.getDecoder().decode(encrypted.replaceAll("/", "")), "UTF-8"));
        String result = decrypt(encrypted);
        System.out.println(result);
    }

    public static String decrypt(String cryptograph) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(prikey));
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] b = Base64.getDecoder().decode(cryptograph);
        return new String(cipher.doFinal(b));
    }
}

