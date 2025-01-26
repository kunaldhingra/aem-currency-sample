package com.currency.core.core.services.impl;

//Unused import
//import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.currency.core.core.Option;
import com.currency.core.core.services.AudienceService;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.osgi.PropertiesUtil;

import com.adobe.acs.commons.wcm.datasources.DataSourceOption;
import org.osgi.service.component.annotations.Activate;


@Component(label = "Audience Service", description = "Get Configurations",
    immediate = true)
@Service(AudienceService.class)
@Properties({@Property(name = "rootPath", label = "Root Path",
    value = DEFAULT_PATH)})
public class AudienceServiceImpl implements AudienceService {

    public static final String DEFAULT_PATH = "etc/default";

    private String rootPath = DEFAULT_PATH;

    public List getAudienceAsOptions(SlingHttpServletRequest request) {
        List list = new ArrayList<>();
        Resource resource = request.getResourceResolver().getResource(rootPath);
        if (resource != null) {
            //String array (String[]) was give in list
            List<String> audienceList = getAudiences(resource);
            for (String audienceName : audienceList) {
                list.add(new Option(audienceName, audienceName));
            }
        }
        return list;
    }

    /**
     * Returns resource name and page title of the childNodes of a specific resource.
     *
     * @param resource
     */
    private static List<String> getAudiences(Resource resource) {
        List<String> list = new ArrayList<>();
        Iterator<Resource> childNodes = resource.listChildren();
        while (childNodes.hasNext()) {
            //getTitle() is not available we can use jcr:title or  childNodes.next().getName()
            list.add(new String[]{childNodes.next().getTitle(), childNodes.next().getName()});
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */


    @Activate
    public void activate(Map<String, Object> properties) {
        rootPath = PropertiesUtil.toString(properties.get("rootPath"), null);
    }
}