import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtils {

	public static String concat2Strings(String first, String second) {
		return first + second;
	}
	
	public static String concat3Strings(String first, String second, String third) {
		return first + second + third;
	}
	
    public static String getSHA(String input) 
    { 
        try { 
            // Static getInstance method is called with hashing SHA 
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
  
            // digest() method called 
            // to calculate message digest of an input 
            // and return array of byte 
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
            System.out.println("Exception thrown for incorrect algorithm: " + e); 
            return null; 
        } 
    } 
    
	public static String concatAndHashString(String first, String second) {
		String concat = first;
		if(second != null) {
			concat = HashUtils.concat2Strings(first, second);	
		}
		
		String hash = HashUtils.getSHA(concat);
		
		return hash;
	}
	
	public static String concatAndHashString(String first, String second, String third) {
		String concat = first;
		
		if(third == null) {
			return concatAndHashString(first, second);
		}
		
		concat = concat3Strings(first, second, third);
		String hash = HashUtils.getSHA(concat);
		
		return hash;
	}
	
	public static String XOR(String str, String key) {
		return encode(str, key);
	}
	
	public static String getStringFromXOR(String str, String key) {
		return decode(str, key);
	}
	
	public static int xorNumber(int number1, int number2){ 
		return number1 ^ number2;
	}
	
    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }
    
    
	// xor 2 string
    private static String encode(String s, String key) {
        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
    }
	
    private static String base64Encode(byte[] bytes) {
        String s = Base64.getEncoder().encodeToString(bytes);
        return s.replaceAll("\\s", "");
    }

    
    // get string from xor
    private static String decode(String s, String key) {
        return new String(xorWithKey(base64Decode(s), key.getBytes()));
    }

    private static byte[] base64Decode(String s) {
        try {          
            return Base64.getDecoder().decode(s);
        } catch (Exception e) {
        	return null;
        }
    }
	
}
