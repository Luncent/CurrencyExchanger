package Controller;

import DTO.ExchangeRateDTO;
import Exceptions.NotFoundException;
import Exceptions.RowExists;
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
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
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
        EncodingFilter.setupEncoding(req, resp);
        PrintWriter pw = resp.getWriter();
        String jsonAnswer;

        try {
            List<ExchangeRateDTO> exchangeRateList = exchangeRateService.getAll();
            resp.setStatus(200);
            jsonAnswer = gson.toJson(exchangeRateList);
        } catch (SQLException e) {
            resp.setStatus(500);
            jsonAnswer = "{\"message\":\"Ошибка бд\"}";
        } catch (Exception e) {
            resp.setStatus(500);
            jsonAnswer = "{\"message\":\""+e.getMessage()+"\"}";
        }
        pw.write(jsonAnswer);
        pw.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EncodingFilter.setupEncoding(req, resp);
        PrintWriter pw = resp.getWriter();
        String jsonAnswer;

        String baseCurCode = (""+req.getParameter("baseCurrencyCode")).trim();
        String targetCurCode = (""+req.getParameter("targetCurrencyCode")).trim();
        String rateStr = (""+req.getParameter("rate")).trim();

        BigDecimal rate;
        if (Validation.isBigDecimal(rateStr)) {
            rate = BigDecimal.valueOf(Double.valueOf(rateStr));
        } else {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"wrong number format\"}";
            pw.write(jsonAnswer);
            pw.flush();
            return;
        }

        if (baseCurCode.isEmpty() || targetCurCode.isEmpty() ||
                rate.compareTo(BigDecimal.valueOf(0)) != 1) {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Отсутствует нужное поле формы или курс отрицателен\"}";
        } else if(baseCurCode.length()!=3 || targetCurCode.length()!=3){
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Длина кодов валют должна быть равна 3\"}";
        }
        else {
            try {
                ExchangeRateDTO exchangeRateDTO = exchangeRateService.add(baseCurCode, targetCurCode, rate);
                resp.setStatus(201);
                jsonAnswer = gson.toJson(exchangeRateDTO);
            } catch (SQLException e) {
                resp.setStatus(500);
                jsonAnswer = "{\"message\":\"Ошибка бд\"}";
            } catch (RowExists e) {
                resp.setStatus(409);
                jsonAnswer = "{\"message\":\"Курс существует\"}";
            } catch (NotFoundException e) {
                resp.setStatus(404);
                jsonAnswer = "{\"message\":\"Одна (или обе) валюта из валютной пары не существует в БД\"}";
            }
            catch (Exception ex){
                resp.setStatus(500);
                jsonAnswer = "{\"message\":\""+ex.getMessage()+"\"}";
            }
        }
        pw.write(jsonAnswer);
        pw.flush();
    }
}
