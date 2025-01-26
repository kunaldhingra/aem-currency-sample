package com.currency.core.core.services;

import java.util.List;

import com.currency.core.core.Option;
import org.apache.sling.api.SlingHttpServletRequest;

import com.adobe.acs.commons.wcm.datasources.DataSourceOption;




/**
 * Audience service interface.
 */
@FunctionalInterface
public interface AudienceService {

    /**
     * Get the list of page owners
     *
     * @param request instance of SlingHttpServletRequest.class
     * @return DataSourceOption Collection of audience names
     */
    //List<OPtion> getAudienceAsOptions(SlingHttpServletREquest request);

    //P was capital in Option class and in SlingHttpServletRequest E was capital
    List<Option> getAudienceAsOptions(SlingHttpServletRequest request);
}
