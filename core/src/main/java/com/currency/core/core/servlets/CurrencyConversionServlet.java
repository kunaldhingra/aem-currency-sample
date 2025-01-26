package com.currency.core.core.servlets;

import com.currency.core.core.services.CurrencyConversionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

@Component(service = Servlet.class, immediate = true)
@SlingServletPaths(value = "/bin/data/currencies")
public class CurrencyConversionServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyConversionServlet.class);

    @Reference
    private CurrencyConversionService currencyConversionService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        try {
            String priceStr = request.getParameter("price");
            String currenciesStr = request.getParameter("currencies");

            if (StringUtils.isBlank(priceStr) || StringUtils.isBlank(currenciesStr)) {
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setContentType("text/plain");
                response.getWriter().write("Missing required parameters: price and currencies");
                return;
            }

            String[] price = priceStr.replace("[", "").replace("]", "").split(",");
            String[] currencies = currenciesStr.split(",");

            Map<String, Map<String, Object>> convertedPrices =  currencyConversionService.convert(price, currencies, request.getResourceResolver());

            response.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), convertedPrices);

        } catch (Exception e) {
            LOG.error("Error processing currency conversion request: ", e);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

}