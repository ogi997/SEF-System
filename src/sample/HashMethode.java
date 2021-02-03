package sample;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashMethode {
    private static String hash(MessageDigest md, String passwordToHash){
        int i;
        md.update(passwordToHash.getBytes()); //mozda i izbaciti ovu liniju koda
        byte[] bytes = md.digest(/*passwordToHash.getBytes()*/);
        StringBuilder sb = new StringBuilder();
        for(i = 0; i < bytes.length; i++){
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    public static String SHA_512(String passwordToHash){
        String hashPassword = null;

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            hashPassword = hash(md, passwordToHash);
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace(); //lijepo da se ispisuju ove greske
        }
        return hashPassword;
    }

    public static String SHA_256(String passwordToHash){
        String hashPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hashPassword = hash(md, passwordToHash);
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace(); //lijepo da se ispisuju ove greske
        }
        return hashPassword;
    }

    public static String MD5(String passwordToHash){
        String hashPassword = null;

        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            hashPassword = hash(md, passwordToHash);
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace(); //napravi error poruke lijepo
        }
        return hashPassword;
    }

    public static boolean passwordValidation(String passwordToValidate, String hashPassword){
        final int numberOfStrings = 3;
        int i;
        boolean isValid = false;

        String[] passwords = new String[numberOfStrings];
        passwords[0] = MD5(passwordToValidate);
        passwords[1] = SHA_256(passwordToValidate);
        passwords[2] = SHA_512(passwordToValidate);

        for(i = 0; i < numberOfStrings && !isValid; i++){
            if(passwords[i].equals(hashPassword)){
                isValid = true;
            }
        }

        return isValid;
    }

}
