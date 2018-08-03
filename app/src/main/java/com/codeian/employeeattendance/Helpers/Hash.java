package com.codeian.employeeattendance.Helpers;

import android.content.Context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Hash {

    private static Context context;

    public Hash(){
        this.context=context;
    }

    public static final String genHash(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString().toUpperCase(Locale.US); // return md5

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Boolean matchPass(String givenPass){
        Boolean isMatch = false;
        String adminPass = Settings.INSTANCE.getAdminPass();
        String tempPass = genHash(givenPass);

        if (adminPass.equals(tempPass)){
            isMatch = true;
        }

        return isMatch;
    }
}
