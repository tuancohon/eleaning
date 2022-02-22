package app.render;

//import java.io.UnsupportedEncodingException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Arrays;
 
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
public class AES256 {
//	private static SecretKeySpec secretKey;
//    private static byte[] key;
// 
//    public static void setKey(String myKey) 
//    {
//        MessageDigest sha = null;
//        try {
//            key = myKey.getBytes("UTF-8");
//            sha = MessageDigest.getInstance("SHA-1");
//            key = sha.digest(key);
//            key = Arrays.copyOf(key, 16); 
//            System.out.println("hello1" + key);
//            secretKey = new SecretKeySpec(key, "AES");
//            System.out.println("hello2" + secretKey);
//        } 
//        catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } 
//        catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
// 
//    public static String encrypt(String strToEncrypt, String secret) 
//    {
//        try
//        {
//            setKey(secret);
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            System.out.println("hello" + cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
//            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
//        } 
//        catch (Exception e) 
//        {
//            System.out.println("Error while encrypting: " + e.toString());
//        }
//        return null;
//    }
// 
//    public static String decrypt(String strToDecrypt, String secret) 
//    {
//        try
//        {
//            setKey(secret);
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
//            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
//        } 
//        catch (Exception e) 
//        {
//            System.out.println("Error while decrypting: " + e.toString());
//        }
//        return null;
//    }
	//private static final String SECRET_KEY = "my_super_secret_key_ho_ho_ho";
	  private static final String SALT = "AnToanUngDungWebVaCoSoDuLieu";
	 
	  public static String encrypt(String strToEncrypt, String SECRET_KEY) {
	    try {
	      byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	      IvParameterSpec ivspec = new IvParameterSpec(iv);
	 
	      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	      KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
	      SecretKey tmp = factory.generateSecret(spec);
	      SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	 
	      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	      cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
	      return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
	    } catch (Exception e) {
	      System.out.println("Error while encrypting: " + e.toString());
	    }
	    return null;
	  }
	  public static String decrypt(String strToDecrypt, String SECRET_KEY) {
		    try {
		      byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		      IvParameterSpec ivspec = new IvParameterSpec(iv);
		 
		      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		      KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
		      SecretKey tmp = factory.generateSecret(spec);
		      SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
		 
		      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		      cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
		      return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		    } catch (Exception e) {
		      System.out.println("Error while decrypting: " + e.toString());
		    }
		    return null;
		  }
}