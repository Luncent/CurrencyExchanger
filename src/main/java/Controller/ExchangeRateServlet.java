package Controller;

import DTO.ExchangeRateDTO;
import Entities.Currency;
import Exceptions.MyException;
import Exceptions.NotFoundException;
import Services.CurrencyService;
import Services.ExchangeRateService;
import Utils.Validation;
import com.google.gson.Gson;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    Gson gson;
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        exchangeRateService = (ExchangeRateService) context.getAttribute("exchangeRateService");
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        String codeCombo = (""+req.getPathInfo()).trim().substring(1);
        String jsonAnswer;
        if (codeCombo.isEmpty()) {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Коды валют отсутствуют\"}";
        } else if(codeCombo.length()!=6) {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Код валюты должен иметь длину в 3 символа\"}";
        } else {
            String baseCode = codeCombo.substring(0, 3);
            String targetCode = codeCombo.substring(3);
            try {
                ExchangeRateDTO dto = exchangeRateService.getByCurrenciesCodes(baseCode, targetCode);
                resp.setStatus(200);
                jsonAnswer = gson.toJson(dto);
            } catch (NotFoundException e) {
                resp.setStatus(404);
                jsonAnswer = "{\"message\":\"Курс не найден\"}";
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        String jsonAnswer;

        String method = (""+req.getParameter("_method")).trim();
        if(method.equals("PATCH")){
            doPatch(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        String codeCombo = (""+req.getPathInfo()).trim().substring(1);
        String jsonAnswer;

        String rateStr = (""+req.getParameter("rate")).trim();
        BigDecimal rate;
        System.out.println(rateStr);
        if (Validation.isDouble(rateStr)) {
            System.out.println(rateStr);
            rate = BigDecimal.valueOf(Double.valueOf(rateStr));
        } else {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"wrong number format\"}";
            pw.write(jsonAnswer);
            pw.flush();
            return;
        }
        if (codeCombo.isEmpty()) {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Коды валют отсутствуют\"}";
        } else if(codeCombo.length()!=6) {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Код валюты должен иметь длину в 3 символа\"}";
        } else {
            String baseCode = codeCombo.substring(0, 3);
            String targetCode = codeCombo.substring(3);
            try {
                ExchangeRateDTO dto = exchangeRateService.update(baseCode,targetCode,rate);
                resp.setStatus(200);
                jsonAnswer = gson.toJson(dto);
            } catch (NotFoundException e) {
                resp.setStatus(404);
                jsonAnswer = "{\"message\":\"Курс не существует\"}";
            } catch (SQLException e) {
                resp.setStatus(500);
                jsonAnswer = "{\"message\":\"Ошибка бд\"}";
            } catch (Exception e) {
                resp.setStatus(400);
                jsonAnswer = "{\"message\":\""+e.getMessage()+"\"}";
            }
        }
        pw.write(jsonAnswer);
        pw.flush();
    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getMethod().equals("PATCH")){
            this.doPatch(req,resp);
        } else {
            super.service(req, resp);
        }
    }
}
