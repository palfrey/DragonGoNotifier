package net.tevp.dragon_go_countdown;

import android.util.Log;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import static android.util.Xml.Encoding.UTF_8;

public class DragonLogin {
    public static LoginResult Login(String username, String password) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://www.dragongoserver.net/login.php?quick_mode=1&userid=" + username + "&passwd=" + password);
        HttpResponse response = httpClient.execute(request);
        String content = IOUtils.toString(response.getEntity().getContent(), String.valueOf(UTF_8));
        LoginResult result = new LoginResult();
        Log.d("DragonLogin", content);
        if (content.contains("#Error")) {
            if (content.contains("wrong_userid")) {
                result.setStatus(LoginStatus.BAD_USERNAME);
            } else if (content.contains("wrong_password")) {
                result.setStatus(LoginStatus.BAD_PASSWORD);
            } else {
                result.setStatus(LoginStatus.OTHER_ERROR);
            }
        } else if (!content.contains("Ok")) {
            result.setStatus(LoginStatus.OTHER_ERROR);
        } else {
            for (Cookie cookie : httpClient.getCookieStore().getCookies()) {
                if (cookie.getName().contentEquals("cookie_sessioncode")) {
                    result.setSessionCode(cookie.getValue());
                    result.setExpiry(cookie.getExpiryDate());
                    result.setStatus(LoginStatus.SUCCESS);
                    break;
                }
            }
        }
        return result;
    }
}