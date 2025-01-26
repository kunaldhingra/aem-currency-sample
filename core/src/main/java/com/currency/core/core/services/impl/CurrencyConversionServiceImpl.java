package com.currency.core.core.services.impl;
import com.currency.core.core.services.CurrencyConversionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

@Component(service = CurrencyConversionService.class, immediate = true)
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyConversionServiceImpl.class);

    @Override
    public Map<String, Map<String, Object>> convert(String[] priceStrings, String[] currencies, ResourceResolver resourceResolver) {
        Map<String, Map<String, Object>> conversionResults = new HashMap<>();

        try {
            for (String priceString : priceStrings) {
                if (StringUtils.isNotBlank(priceString)) {
                    try {
                        BigDecimal price = new BigDecimal(priceString.trim());
                        for (String currencyCode : currencies) {
                            if (StringUtils.isNotBlank(currencyCode)) {
                                Resource currencyResource = resourceResolver.getResource("/etc/currencies/" + currencyCode);
                                if (currencyResource != null) {
                                    String currencyName = currencyResource.getValueMap().get("name", String.class);
                                    BigDecimal conversionFactor = currencyResource.getValueMap().get("conversionFactor", BigDecimal.class);

                                    if (currencyName != null && conversionFactor != null) {
                                        BigDecimal convertedPrice = price.multiply(conversionFactor).setScale(2, RoundingMode.HALF_UP);

                                        // Map to hold currency details for this price
                                        Map<String, Object> currencyDetails = new HashMap<>();
                                        currencyDetails.put("currencyName", currencyName);
                                        currencyDetails.put("convertedPrice", convertedPrice);

                                        // Use a composite key: price + currencyCode
                                        String key = priceString + "-" + currencyCode;
                                        conversionResults.put(key, currencyDetails);
                                    } else {
                                        LOG.warn("Missing currency details for: {}", currencyCode);
                                    }
                                } else {
                                    LOG.warn("Currency resource not found for: {}", currencyCode);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        LOG.warn("Invalid price format: {}", priceString, e);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error converting currencies: ", e);
        }

        return conversionResults;
    }

}