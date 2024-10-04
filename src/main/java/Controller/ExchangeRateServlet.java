package Controller;

import Entities.Currency;
import DTO.Exchange;
import Entities.ExchangeRate;
import Utils.Validation;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name="ExchangeRateServlet", urlPatterns = {"/exchangeRates", "/exchangeRate/*", "/exchange"})
public class ExchangeRateServlet extends HttpServlet {
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
        String json;
        try {
            if (req.getServletPath().contains("exchangeRates")) {
                List<ExchangeRate> erates = ExchangeRate.getAllExRates();
                json = gson.toJson(erates);
                pw.write(json);
                pw.flush();
            }
            else if(req.getServletPath().contains("exchangeRate")){
                String codeCombo = req.getPathInfo().substring(1);
                if(codeCombo.isEmpty()){
                    resp.setStatus(400);
                    json = "{\"message\":\"Коды пары валют отсутствуют в адресе\"}";
                    pw.write(json);
                    pw.flush();
                }
                else{
                    ExchangeRate er = ExchangeRate.getCertExRate(codeCombo);
                    if(er.chkMock()==2){
                        resp.setStatus(404);
                        json = "{\"message\":\"Обменный курс для пары кодов не найден\"}";
                        pw.write(json);
                        pw.flush();
                    }
                    else{
                        json = gson.toJson(er);
                        pw.write(json);
                        pw.flush();
                    }
                }
            }
            else if(req.getServletPath().contains("exchange")){
                String baseCurCode = req.getParameter("from");
                String targetCurCode = req.getParameter("to");
                String amountStr = req.getParameter("amount");
                System.out.println(baseCurCode+" "+targetCurCode+" "+amountStr);
                if(baseCurCode == null || targetCurCode == null || amountStr==null) {
                    resp.setStatus(400);
                    json = "{\"message\":\"Отсутствует нужный параметр\"}";
                    pw.write(json);
                    pw.flush();
                }
                else{
                    BigDecimal amount;
                    if(Validation.isBigDecimal(amountStr)){
                        amount = BigDecimal.valueOf(Double.valueOf(amountStr));
                    }
                    else{
                        resp.setStatus(400);
                        json = "{\"message\":\"wrong number format\"}";
                        pw.write(json);
                        pw.flush();
                        return;
                    }
                    Exchange exchange = new Exchange(baseCurCode, targetCurCode, amount);

                    if(!exchange.checkCurrencies()){
                        resp.setStatus(404);
                        json = "{\"message\":\"Одной (или двух) валюты не существует\"}";
                        pw.write(json);
                        pw.flush();
                    }
                    else {
                        exchange.calculateExchange();
                        if(exchange.checkExchange()){
                            resp.setStatus(200);
                            json = gson.toJson(exchange);
                            pw.write(json);
                            pw.flush();
                        }
                        else{
                            resp.setStatus(404);
                            json = "{\"message\":\"Нельзя установить курс между валютами\"}";
                            pw.write(json);
                            pw.flush();
                        }
                    }
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
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getParameter("_method")!=null && req.getParameter("_method").equals("PATCH")){
            this.doPatch(req,resp);
            return;
        }
        EncodingFilter.setupEncoding(req, resp);
        PrintWriter pw = resp.getWriter();
        String json;

        try {
            if(!req.getServletPath().contains("exchangeRates")){
                resp.setStatus(404);
                json = "{\"message\":\"resource not found\"}";
                pw.write(json);
                pw.flush();
                return;
            }
            String baseCurCode = req.getParameter("baseCurrencyCode");
            String targetCurCode = req.getParameter("targetCurrencyCode");
            String rateStr = req.getParameter("rate");
            BigDecimal rate;
            if(Validation.isBigDecimal(rateStr)){
                rate = BigDecimal.valueOf(Double.valueOf(rateStr));
            }
            else{
                resp.setStatus(400);
                json = "{\"message\":\"wrong number format\"}";
                pw.write(json);
                pw.flush();
                return;
            }
            if (baseCurCode == null || baseCurCode.isEmpty() || targetCurCode == null || targetCurCode.isEmpty() || rate == null
                    || rate.compareTo(BigDecimal.valueOf(0)) != 1) {
                resp.setStatus(400);
                json = "{\"message\":\"Отсутствует нужное поле формы\"}";
                pw.write(json);
                pw.flush();
            } else {
                if (ExchangeRate.getCertExRate(baseCurCode, targetCurCode).chkMock()==0){
                    resp.setStatus(409);
                    json = "{\"message\":\"Валютная пара с таким кодом уже существует\"}";
                    pw.write(json);
                    pw.flush();
                }
                else{
                    if(Currency.getCertCurrency(baseCurCode).chkMock()!=0 || Currency.getCertCurrency(targetCurCode).chkMock()!=0){
                        resp.setStatus(404);
                        json = "{\"message\":\"Одна (или обе) валюта из валютной пары не существует в БД\"}";
                        pw.write(json);
                        pw.flush();
                    }
                    else{
                        ExchangeRate.addExRate(baseCurCode,targetCurCode,rate);
                        ExchangeRate er = ExchangeRate.getCertExRate(baseCurCode,targetCurCode);
                        resp.setStatus(201);
                        json = gson.toJson(er);
                        pw.write(json);
                        pw.flush();
                    }
                }
            }
        }catch (SQLException e){
            resp.setStatus(500);
            json = "{\"message\":\"База данных недоступна\"}";
            pw.write(json);
            pw.flush();
            e.printStackTrace();
        }
    }
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
        EncodingFilter.setupEncoding(req, resp);
        PrintWriter pw = resp.getWriter();
        String json;
        if(!req.getServletPath().contains("exchangeRate")){
            resp.setStatus(404);
            json = "{\"message\":\"resource not found\"}";
            pw.write(json);
            pw.flush();
            return;
        }
        try{
            String codeCombo = req.getPathInfo().substring(1);
            String rateStr = req.getParameter("rate");
            if(rateStr == null || Double.parseDouble(rateStr)<0){
                resp.setStatus(400);
                json = "{\"message\":\"Отсутствует нужное поле формы\"}";
                pw.write(json);
                pw.flush();
            }
            else{
                BigDecimal rate;
                if(Validation.isBigDecimal(rateStr)){
                    rate = BigDecimal.valueOf(Double.valueOf(rateStr));
                }
                else{
                    resp.setStatus(400);
                    json = "{\"message\":\"wrong number format\"}";
                    pw.write(json);
                    pw.flush();
                    return;
                }
                ExchangeRate er =  ExchangeRate.updateExRate(codeCombo, rate);
                if(er.chkMock()!=0){
                    resp.setStatus(404);
                    json = "{\"message\":\"Валютная пара отсутствует в базе данных\"}";
                    pw.write(json);
                    pw.flush();
                }
                else{
                    json = gson.toJson(er);
                    pw.write(json);
                    pw.flush();
                }
            }
        }
        catch (SQLException e){
            resp.setStatus(500);
            json = "{\"message\":\"База данных недоступна\"}";
            pw.write(json);
            pw.flush();
        }
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if(method.equals("PATCH")) {
            this.doPatch(req,resp);
        }
        else {
            super.service(req, resp);
        }
    }
}
