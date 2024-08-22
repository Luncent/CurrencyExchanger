package Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EncodingFilter {
    public static void setupEncoding(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addHeader("Content-type","application/json;chartset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
    }
}
