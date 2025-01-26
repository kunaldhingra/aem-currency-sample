package com.currency.core.core.servlets;

import com.currency.core.core.services.CurrencyConversionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CurrencyConversionServletTest {

    @InjectMocks
    private CurrencyConversionServlet currencyConversionServlet;

    @Mock
    private CurrencyConversionService currencyConversionService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private StringWriter responseWriter;


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getResourceResolver()).thenReturn(resourceResolver);
    }

    @Test
    void testDoGet_ValidRequest() throws Exception {
        // Mock request parameters
        when(request.getParameter("price")).thenReturn("[1]");
        when(request.getParameter("currencies")).thenReturn("USD,EUR");

        // Mock service behavior
        Map<String, Map<String, Object>> mockResults = new HashMap<>();
        Map<String, Object> usdDetails = new HashMap<>();
        usdDetails.put("currencyName", "US Dollar");
        usdDetails.put("convertedPrice", new BigDecimal("1.25"));
        mockResults.put("1.25-USD", usdDetails);

        Map<String, Object> eurDetails = new HashMap<>();
        eurDetails.put("currencyName", "Euro");
        eurDetails.put("convertedPrice", new BigDecimal("1.15"));
        mockResults.put("1.15-EUR", eurDetails);

        when(currencyConversionService.convert(new String[]{"1"}, new String[]{"USD", "EUR"}, resourceResolver))
                .thenReturn(mockResults);

        // Execute servlet
        currencyConversionServlet.doGet(request, response);

        // Verify response
        ObjectMapper mapper = new ObjectMapper();
        String expectedResponse = mapper.writeValueAsString(mockResults);

        assertEquals(expectedResponse, responseWriter.toString().trim());
        verify(response).setContentType("application/json");
        verify(response, never()).setStatus(Mockito.eq(500));
    }

    @Test
    void testDoGet_MissingParameters() throws Exception {
        // Mock missing request parameters
        when(request.getParameter("price")).thenReturn(null);
        when(request.getParameter("currencies")).thenReturn(null);

        // Execute servlet
        currencyConversionServlet.doGet(request, response);

        // Verify response
        assertEquals("Missing required parameters: price and currencies", responseWriter.toString().trim());
        verify(response).setStatus(400);
    }

    @Test
    void testDoGet_InternalServerError() throws Exception {
        // Mock request parameters
        when(request.getParameter("price")).thenReturn("[1]");
        when(request.getParameter("currencies")).thenReturn("USD,EUR");

        // Mock service exception
        when(currencyConversionService.convert(any(String[].class), any(String[].class), eq(resourceResolver)))
                .thenThrow(new RuntimeException("Service error"));


        // Execute servlet
        currencyConversionServlet.doGet(request, response);

        // Verify response
        verify(response).setStatus(500);
    }
}
