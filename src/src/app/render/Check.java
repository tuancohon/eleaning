package app.render;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.entity.User;

public class Check {
	public static boolean checkRegrex(String str)
	{
		Pattern VALID_INPUT_REGEX = Pattern.compile("[$&+,:;=\\\\\\\\?@#|/'<>.^*()%!-]",Pattern.CASE_INSENSITIVE);
		Matcher matcher = VALID_INPUT_REGEX.matcher(str);
		return matcher.find();
	}
	 public static String cleanXSS (String value) {
	        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	        value = value.replaceAll("%3C", "&lt;").replaceAll("%3E", "&gt;");
	        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
	        value = value.replaceAll("%28", "&#40;").replaceAll("%29", "&#41;");
	        value = value.replaceAll("'", "&#39;");
	        value = value.replaceAll("eval\\((.*)\\)", "");
	        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
	       // value = value.replaceAll("script", "");
	        return value;
	    }
	 public static boolean checkSQLInjectURL (String value) {
	        String regrex= "[0-9]+";
	        boolean a = Pattern.matches(regrex, value);
	        if(!Pattern.matches(regrex, value))
	        	return false;
	        return true;
	    }
	 public static boolean checkAcess (User user) {
	        if(user == null || user.getRole() == 4 )
	        	{
	        	return false;
	        	}
	        return true;
	    }
	 public static String getMd5(String input)
	    {
	        try {
	  
	            // Static getInstance method is called with hashing MD5
	            MessageDigest md = MessageDigest.getInstance("MD5");
	  
	            // digest() method is called to calculate message digest
	            //  of an input digest() return array of byte
	            byte[] messageDigest = md.digest(input.getBytes());
	  
	            // Convert byte array into signum representation
	            BigInteger no = new BigInteger(1, messageDigest);
	  
	            // Convert message digest into hex value
	            String hashtext = no.toString(16);
	            while (hashtext.length() < 32) {
	                hashtext = "0" + hashtext;
	            }
	            return hashtext;
	        } 
	  
	        // For specifying wrong message digest algorithms
	        catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException(e);
	        }
	    }
}
