package Controller;

import DTO.ExchangeDTO;
import Exceptions.MyException;
import Exceptions.NotFoundException;
import Services.CountExchangeService;
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
import java.sql.SQLException;

@WebServlet("/exchange")
public class getExchangeServlet extends HttpServlet {
    private CountExchangeService service;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        service = (CountExchangeService) context.getAttribute("countExchangeService");
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        String jsonAnswer;

        String from = ""+req.getParameter("from").trim();
        String to = ""+req.getParameter("to").trim();
        String amountStr = ""+req.getParameter("amount").trim();

        if (from.isEmpty() || to.isEmpty()) {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Коды/код валют отсутствуют\"}";
        } else if(from.length()!=3 || to.length()!=3) {
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Код валюты должен иметь длину в 3 символа\"}";
        }
        else if(!Validation.isDouble(amountStr)){
            resp.setStatus(400);
            jsonAnswer = "{\"message\":\"Неверный формат числа\"}";
        } else {
            try {
                ExchangeDTO dto = service.getExchange(from,to,Double.valueOf(amountStr));
                resp.setStatus(200);
                jsonAnswer = gson.toJson(dto);
            } catch (NotFoundException e) {
                resp.setStatus(404);
                jsonAnswer = "{\"message\":\""+e.getMessage()+"\"}";
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
