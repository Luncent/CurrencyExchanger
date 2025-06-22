package Controller;

import Entities.Currency;
import Exceptions.RowExists;
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
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;
    Gson gson;

    @Override
    public void init() throws ServletException {
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        super.init();
        ServletContext context = getServletContext();
        currencyService = (CurrencyService) context.getAttribute("currencyService");
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        List<Currency> currencies = null;
        String jsonAnswer;
        try {
            currencies = currencyService.getAll();
            resp.setStatus(200);
            jsonAnswer = gson.toJson(currencies);
        } catch (SQLException e) {
            resp.setStatus(500);
            jsonAnswer = "{\"message\":\"Ошибка бд\"}";
        } catch (Exception e) {
            resp.setStatus(500);
            jsonAnswer = "{\"message\":\""+e.getMessage()+"\"}";
        }
        System.out.println(jsonAnswer);
        pw.write(jsonAnswer);
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        String jsonAnswer;

        String currName = ""+(String)req.getParameter("name");
        String currCode = ""+(String)req.getParameter("code");
        String currSign = ""+(String)req.getParameter("sign");
        if(currName.trim().isEmpty() || currCode.trim().isEmpty() ||  currSign.trim().isEmpty()){
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Отсутствует поле формы\"}";
        }
        else if(currCode.length()!=3){
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Длина кода должна быть равна 3\"}";
        }
        else{
            Currency newCurrency = new Currency.Builder()
                    .setName(currName)
                    .setCode(currCode)
                    .setSign(currSign)
                    .build();
            try {
                Currency currency = currencyService.add(newCurrency);
                resp.setStatus(201);
                jsonAnswer = gson.toJson(currency);
            } catch (RowExists e) {
                resp.setStatus(409);
                jsonAnswer = "{\"message\":\"Валюта с таким кодом уже существует\"}";
            } catch (SQLException e) {
                resp.setStatus(500);
                jsonAnswer = "{\"message\":\"Ошибка бд\"}";
            } catch (Exception e) {
                resp.setStatus(500);
                jsonAnswer = "{\"message\":\""+e.getMessage()+"\"}";
            }

        }
        pw.write(jsonAnswer);
        pw.flush();
    }
}
