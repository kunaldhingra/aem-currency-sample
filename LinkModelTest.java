package com.currency.core.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

class LinkModelTest {

    @InjectMocks
    private LinkModel linkModel;

    @Mock
    private Resource resource;

    @Mock
    private PageManager pageManager;

    @Mock
    @Inject
    private Page page;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        // Mocking resource and page manager
        when(resource.getPath()).thenReturn("/content/mysite/home");
        when(pageManager.getContainingPage("/content/mysite/home")).thenReturn(page);

        // Mocking page behavior
        when(page.getNavigationTitle()).thenReturn("Home Navigation Title");
        when(page.getTitle()).thenReturn("Home Title");

        linkModel.resource = resource;
        linkModel.pageManager = pageManager;
        linkModel.title = "Home Navigation Title";
        when(pageManager.getContainingPage("/content/mysite/home")).thenReturn(page);


        // Initialize the model
        linkModel.init();
    }


    @Test
    void testInitAndGetLink() {
        // Verify that the link is initialized with the correct value
        assertEquals("/content/mysite/home.html", linkModel.getLink());
    }

    @Test
    void testGetTitleWithPageNavigationTitle() {
        assertEquals("Home Navigation Title", linkModel.getTitle());

    }

    @Test
    void testGetTitleWhenPageIsNull() {
        // Mock no page found
        when(pageManager.getContainingPage("/content/mysite/home")).thenReturn(null);

        // Verify fallback to injected JCR title
        linkModel.title = "Fallback Title";
        assertEquals("Fallback Title", linkModel.getTitle());
    }
}
