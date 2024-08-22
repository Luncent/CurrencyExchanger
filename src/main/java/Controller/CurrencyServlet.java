package Controller;

import DataBase.DB;
import Model.Currency;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = {"/currencies", "/currency/*"}, name = "ERServlet")
public class CurrencyServlet extends HttpServlet {
    Gson gson;
    @Override
    public void init() throws ServletException {
        super.init();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EncodingFilter.setupEncoding(req, resp);
        PrintWriter pw = resp.getWriter();
        String urlPartWithCode = req.getPathInfo();
        String json;
        try {
            // if true->/currencies
            if (urlPartWithCode == null) {
                if(!req.getServletPath().contains("currencies")){
                    resp.setStatus(404);
                    json = "{\"message\":\"resource not found\"}";
                    pw.write(json);
                    pw.flush();
                    return;
                }
                List<Currency> currencies = Currency.getAllCurrencies();
                json = gson.toJson(currencies);
                pw.write(json);
                pw.flush();
            // /currency
            } else {
                if(!req.getServletPath().contains("currency")){
                    resp.setStatus(404);
                    json = "{\"message\":\"resource not found\"}";
                    pw.write(json);
                    pw.flush();
                    return;
                }
                String currencyCode  = urlPartWithCode.substring(1);
                if(currencyCode.isEmpty()){
                    resp.setStatus(400);
                    json = "{\"message\":\"Код валюты отсутствует в адресе\"}";
                    pw.write(json);
                }
                else {
                    Currency currency = DB.getCurrency(currencyCode);
                    if(currency.chkMock()!=0){
                        resp.setStatus(404);
                        json = "{\"message\":\"Валюта не найдена\"}";
                        pw.write(json);
                        pw.flush();
                        return;
                    }
                    json = gson.toJson(currency);
                    pw.write(json);
                    pw.flush();
                }
            }
        }
        catch(SQLException e){
            resp.setStatus(500);
            json = "{\"message\":\"База данных недоступна\"}";
            pw.write(json);
            pw.flush();
            e.printStackTrace();
        }
        catch (Exception e){
            resp.setStatus(504);
            json = "{\"message\":\""+e.getMessage()+"\"}";
            pw.write(json);
            pw.flush();
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EncodingFilter.setupEncoding(req, resp);
        PrintWriter pw = resp.getWriter();
        String json;
        if(!req.getServletPath().contains("currencies")){
            resp.setStatus(404);
            json = "{\"message\":\"resource not found\"}";
            pw.write(json);
            pw.flush();
            return;
        }
        try{
            String currName = (String)req.getParameter("name");
            String currCode = (String)req.getParameter("code");
            String currSign = (String)req.getParameter("sign");
            if(currName==null || currName.isEmpty() || currCode==null || currCode.isEmpty() || currSign==null || currSign.isEmpty()){
                resp.setStatus(400);
                json = "{\"message\":\"Отсутствует поле формы\"}";
                pw.write(json);
                pw.flush();
                return;
            }
            if(Currency.getCertCurrency(currCode).chkMock()==0){
                resp.setStatus(409);
                json = "{\"message\":\"Валюта с таким кодом уже существует\"}";
                pw.write(json);
                pw.flush();
                return;
            }
            Currency currency = new Currency(0,currCode,currName,currSign);
            DB.addCurrency(currency);
            currency = DB.getCurrency(currCode);
            json = gson.toJson(currency);
            pw.write(json);
            resp.setStatus(201);
            pw.flush();
        }
        catch(Exception e){
            resp.setStatus(500);
            json = "{\"message\":\""+e.getMessage()+"\"}";
            pw.write(json);
            pw.flush();
            e.printStackTrace();
        }
    }
}
