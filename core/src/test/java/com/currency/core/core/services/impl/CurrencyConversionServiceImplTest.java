package com.currency.core.core.services.impl;

import com.currency.core.core.services.CurrencyConversionService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyConversionServiceImplTest {

    private CurrencyConversionService currencyConversionService;

    @Mock
    private ResourceResolver resourceResolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyConversionService = new CurrencyConversionServiceImpl();
    }

    @Test
    void testConvert_ValidInput() {
        // Arrange
        String[] priceStrings = {"100", "200"};
        String[] currencies = {"USD", "EUR"};

        // Mock resources for USD
        Resource usdResource = mock(Resource.class);
        ValueMap usdValueMap = mock(ValueMap.class);
        when(resourceResolver.getResource("/etc/currencies/USD")).thenReturn(usdResource);
        when(usdResource.getValueMap()).thenReturn(usdValueMap);
        when(usdValueMap.get("name", String.class)).thenReturn("US Dollar");
        when(usdValueMap.get("conversionFactor", BigDecimal.class)).thenReturn(new BigDecimal("1.1"));

        // Mock resources for EUR
        Resource eurResource = mock(Resource.class);
        ValueMap eurValueMap = mock(ValueMap.class);
        when(resourceResolver.getResource("/etc/currencies/EUR")).thenReturn(eurResource);
        when(eurResource.getValueMap()).thenReturn(eurValueMap);
        when(eurValueMap.get("name", String.class)).thenReturn("Euro");
        when(eurValueMap.get("conversionFactor", BigDecimal.class)).thenReturn(new BigDecimal("0.9"));

        // Act
        Map<String, Map<String, Object>> results = currencyConversionService.convert(priceStrings, currencies, resourceResolver);

        // Assert
        assertEquals(4, results.size());

        // Validate USD conversion for 100
        Map<String, Object> usdDetails = results.get("100-USD");
        assertEquals("US Dollar", usdDetails.get("currencyName"));
        assertEquals(new BigDecimal("110.00"), usdDetails.get("convertedPrice"));

        // Validate EUR conversion for 100
        Map<String, Object> eurDetails = results.get("100-EUR");
        assertEquals("Euro", eurDetails.get("currencyName"));
        assertEquals(new BigDecimal("90.00"), eurDetails.get("convertedPrice"));

        // Validate USD conversion for 200
        Map<String, Object> usdDetails200 = results.get("200-USD");
        assertEquals("US Dollar", usdDetails200.get("currencyName"));
        assertEquals(new BigDecimal("220.00"), usdDetails200.get("convertedPrice"));

        // Validate EUR conversion for 200
        Map<String, Object> eurDetails200 = results.get("200-EUR");
        assertEquals("Euro", eurDetails200.get("currencyName"));
        assertEquals(new BigDecimal("180.00"), eurDetails200.get("convertedPrice"));
    }

    @Test
    void testConvert_InvalidPriceFormat() {
        // Arrange
        String[] priceStrings = {"abc"};
        String[] currencies = {"USD"};

        // Act
        Map<String, Map<String, Object>> results = currencyConversionService.convert(priceStrings, currencies, resourceResolver);

        // Assert
        assertEquals(0, results.size());
    }

    @Test
    void testConvert_MissingCurrencyResource() {
        // Arrange
        String[] priceStrings = {"100"};
        String[] currencies = {"XYZ"};

        // Mock missing resource
        when(resourceResolver.getResource("/etc/currencies/XYZ")).thenReturn(null);

        // Act
        Map<String, Map<String, Object>> results = currencyConversionService.convert(priceStrings, currencies, resourceResolver);

        // Assert
        assertEquals(0, results.size());
    }
}
