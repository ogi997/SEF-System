package sample;

import javafx.scene.control.Alert;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Crypto {

    public X509Certificate getCertificate(File file) {
        X509Certificate certificate = null;
        CertificateFactory factory;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            factory = CertificateFactory.getInstance("X.509");
            certificate = (X509Certificate) factory.generateCertificate(fis);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return certificate;
    }

    public PrivateKey getPrivateKey(String privateKey){ //privateKey je putanja do generisanog kljuca .key
        Security.addProvider(new BouncyCastleProvider()); //ucitan BounceCastle
        File file = new File(privateKey);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert fis != null;
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int)file.length()];

        try {
            dis.readFully(keyBytes);
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str = new String(keyBytes);
        StringBuilder lines = new StringBuilder();
        BufferedReader br = new BufferedReader(new StringReader(str));

        String line;
        try {
            while ((line = br.readLine()) != null) {
                lines.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        String pem = new String(lines);
        pem = pem.replace("-----BEGIN RSA PRIVATE KEY-----", "");
        pem = pem.replace("-----END RSA PRIVATE KEY-----", "");

        byte[] base64DecodedData = Base64.getMimeDecoder().decode(pem.trim());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(base64DecodedData);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        PrivateKey pk = null;
        try {
            assert kf != null;
            pk = kf.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return pk;
    }

    public byte[] sign(File file, PrivateKey privateKey, String hashAlgorithm){
        FileInputStream fis;
        byte[] signatureBytes= null;
        try {
            Signature signature = Signature.getInstance(hashAlgorithm);
            signature.initSign(privateKey);
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            while( (count = fis.read(buffer)) > 0 ) {
                signature.update(buffer, 0, count);
            }
            signatureBytes = signature.sign();
            fis.close();


        }catch (IOException | InvalidKeyException | NoSuchAlgorithmException | SignatureException e){
            e.printStackTrace();
        }
        return signatureBytes;
    }
    public boolean verifySign(File file, PublicKey publicKey, String hashAlgorithm, byte[] signatureByte){
        FileInputStream fis;
        boolean sigOK = false;
        try {
            Signature signature = Signature.getInstance(hashAlgorithm);
            signature.initVerify(publicKey);
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            while( (count = fis.read(buffer)) > 0 ) {
                signature.update(buffer, 0, count);
            }
            fis.close();
            sigOK = signature.verify(signatureByte);

        }catch (IOException | InvalidKeyException | NoSuchAlgorithmException | SignatureException e){
            e.printStackTrace();
        }
     return sigOK;
    }

    public SecretKey generateAESKey(){
        //generisanje AES kljuca
        SecretKey secretKey = null;
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);
            secretKey = keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    public SecretKey generateDESKey(){
        //generisanje DES kljuca
        SecretKey secretKey = null;
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance("DES");
            keygen.init(56);
            secretKey = keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    public void symmetricEncrypt(PublicKey publicKey, File inputFile, User user){
        byte[] encryptedAESKey = new byte[(int)128]; //128 je velicina AES kljuca
        SecretKey secretKey = generateAESKey(); //AES key
       // System.out.println("TAJNI KLJUC ENKRIPCIJA:" + secretKey.toString());

        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedAESKey = rsaCipher.doFinal(secretKey.getEncoded());
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            FileInputStream fis = new FileInputStream(inputFile);
            byte[] fileContent = new byte[(int)inputFile.length()];
            fis.read(fileContent);
            fis.close();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedContent = aesCipher.doFinal(fileContent);

            File forPrivateKey = new File("/home/ognjen/IdeaProjects/KriptoProject/DigitalCertification/"+user.getUsername()+".key");
            PrivateKey privateKey = getPrivateKey(forPrivateKey.toString());
            byte[] signatureByte = sign(inputFile, privateKey, "SHA512withRSA");

            List<byte[]> list = new ArrayList<>();
            list.add(encryptedAESKey);
            list.add(encryptedContent);
            list.add(signatureByte); //bytovi potpisanog fajla
            list.add(user.getUsername().getBytes()); //ko salje fajl

            String pathToFolder = "/home/ognjen/IdeaProjects/KriptoProject/shareFolder/Folder/";
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathToFolder+inputFile.getName()));

            out.writeObject(list);
            out.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void symmetricDecrypt(PrivateKey privateKey, File inputFile, User user) throws BadPaddingException, InvalidKeyException {
        byte[] AESKey;
        byte[] fileContent;
        byte[] encodeSignature;
        byte[] whoSend;
        byte[] decriptedKey;
        SecretKey secretKey;
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(inputFile));
            List<byte[]> byteList = (List<byte[]>) in.readObject();
            in.close();
            AESKey = byteList.get(0);
            fileContent = byteList.get(1);
            encodeSignature = byteList.get(2);
            whoSend = byteList.get(3);

            String name = new String(whoSend);
            File f = new File("/home/ognjen/IdeaProjects/KriptoProject/DigitalCertification/"+name+".pem");
            X509Certificate cert = getCertificate(f);
            PublicKey publicKey = cert.getPublicKey();

            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            decriptedKey = rsaCipher.doFinal(AESKey);

            secretKey = new SecretKeySpec(decriptedKey, "AES/CBC/PKCS5Padding");

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedContent = aesCipher.doFinal(fileContent);

            String saveFile = "/home/ognjen/IdeaProjects/KriptoProject/root/"+user.getUsername()+"/"+inputFile.getName();

            FileOutputStream fos1 = new FileOutputStream(saveFile);
            fos1.write(decryptedContent);
            fos1.close();

            File fi = new File(saveFile);
            boolean isOk = verifySign(fi, publicKey,"SHA512withRSA", encodeSignature);
            if(!isOk){
                AlertBox.showDialog(Alert.AlertType.ERROR, "ERROR", "Digitalni potpis nije validan.");
                fi.delete();
            }
        }catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    //mode 0 - creating file
    // mode 1 - for uploading file
    public void desEncrypt(File inputFile, User user, int mode){
        SecretKey secretKey = generateDESKey();
        try{
            FileInputStream fis = new FileInputStream(inputFile);
            byte[] fileContent = new byte[(int)inputFile.length()];
            fis.read(fileContent);
            fis.close();

            Cipher desCipher = Cipher.getInstance("DES");
            desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedContent = desCipher.doFinal(fileContent);

            List<byte[]> list = new ArrayList<>();
            list.add(secretKey.getEncoded());
            list.add(encryptedContent);

            if(mode == 0) {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(inputFile.getAbsoluteFile()/*pathToFolder+inputFile.getName()*/));
                out.writeObject(list);
                out.close();
            }else {
                String pathToFolder = "/home/ognjen/IdeaProjects/KriptoProject/root/"+user.getUsername()+"/";
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathToFolder+inputFile.getName()));
                out.writeObject(list);
                out.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void desDecrypt(File inputFile/*, User user*/){

        byte[] fileContent;
        byte[] desKey;

        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(inputFile));
            List<byte[]> byteList = (List<byte[]>) in.readObject();
            in.close();
            desKey = byteList.get(0);
            fileContent = byteList.get(1);

            SecretKey secretKey;
            secretKey = new SecretKeySpec(desKey, "DES");
            Cipher desCipher = Cipher.getInstance("DES");
            desCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedContent = desCipher.doFinal(fileContent);

            FileOutputStream fos1 = new FileOutputStream(inputFile.getAbsoluteFile());
            fos1.write(decryptedContent);
            fos1.close();
        }catch (Exception badKey) {
            badKey.printStackTrace();
        }
    }
}
