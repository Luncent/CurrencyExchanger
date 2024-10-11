package Controller;

import Entities.Currency;
import Exceptions.NotFoundException;
import Services.CurrencyService;
import com.google.gson.Gson;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/currency/*")
public class getCurrencyServlet extends HttpServlet {
    Gson gson;
    private CurrencyService currencyService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        currencyService = (CurrencyService) context.getAttribute("currencyService");
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EncodingFilter.setupEncoding(req, resp);
        PrintWriter pw = resp.getWriter();
        String code = (""+req.getPathInfo()).trim().substring(1);
        String jsonAnswer;
        if (code.isEmpty()) {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Код валюты отсутствует в адресе\"}";
        } else if(code.length()!=3) {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Код валюты должен иметь длину в 3 символа\"}";
        } else {
            try {
                Currency currency = currencyService.getByCode(code);
                resp.setStatus(200);
                jsonAnswer = gson.toJson(currency);
            } catch (NotFoundException e) {
                resp.setStatus(404);
                jsonAnswer = "{\"message\":\"Валюта не найдена\"}";
            } catch (SQLException e) {
                resp.setStatus(500);
                jsonAnswer = "{\"message\":\"Ошибка бд\"}";
            } catch (InterruptedException e) {
                resp.setStatus(500);
                jsonAnswer = "{\"message\":\""+e.getMessage()+"\"}";
            }
        }
        pw.write(jsonAnswer);
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
