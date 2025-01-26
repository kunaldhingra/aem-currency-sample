package com.currency.core.core.services;

import org.apache.sling.api.resource.ResourceResolver;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;


public interface CurrencyConversionService {
      Map<String, Map<String, Object>> convert(String[] priceStrings, String[] currencies, ResourceResolver resourceResolver);

      //Map<String, Map<String, Object>> convert(Double[] doubles, String[] currencies, ResourceResolver resourceResolver);
}
