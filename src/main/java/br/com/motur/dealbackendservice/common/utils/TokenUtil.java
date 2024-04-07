package br.com.motur.dealbackendservice.common.utils;

/*import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;*/
import org.json.JSONObject;

import java.util.Base64;
import java.util.Date;

public class TokenUtil {

    private TokenUtil(){

    }


    /*public static DecodedJWT decodeToken(String jwtString) {
        try{
            return JWT.decode(jwtString);
        }catch(Exception e){
            return null;
        }
    }

    public static Boolean isJWTExpired(String jwtString) {
        try{
            DecodedJWT decodedJWT = decodeToken(jwtString);
            if(decodedJWT == null){
                return true;
            }
            Date expiresAt = decodedJWT.getExpiresAt();
            return expiresAt.before(new Date());
        }catch(Exception e){
            return false;
        }
    }*/

    public static String getEmail(String jwtToken){
        String[] parts = jwtToken.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        return (String) payload.get("email");
    }

    public static String getClientId(String jwtToken){
        String[] parts = jwtToken.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        return (String) payload.get("azp");
    }
    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
}
