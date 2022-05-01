package com.absence.auth.utilities;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Component
public class PasswordHashUtil {

    private static String staticSalt;
    private static Integer staticIterations;
    private static Integer staticKeyLength;

    @Value("${password.hash.salt}")
    public void setSaltStatic(String salt){
        PasswordHashUtil.staticSalt = salt;
    }

    @Value("${password.hash.iterations}")
    public void setIterationsStatic(Integer iterations){
        PasswordHashUtil.staticIterations = iterations;
    }

    @Value("${password.hash.key-length}")
    public void setKeyLengthStatic(Integer keyLength){
        PasswordHashUtil.staticKeyLength = keyLength;
    }

    public static String generate(String password) {
        try {
            char[] passwordChars = password.toCharArray();
            byte[] saltBytes = staticSalt.getBytes();
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( passwordChars, saltBytes, staticIterations, staticKeyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] hashedBytes = key.getEncoded( );
            return Hex.encodeHexString(hashedBytes);
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

}
