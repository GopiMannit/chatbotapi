package com.mannit.chatbot.controllerTest;


import com.mannit.chatbot.config.Configvalues;
import com.mannit.chatbot.controller.DefaultMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class DefaultMessageTest {

    private MockMvc mockMvc;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Configvalues configvalues;



    @InjectMocks
    private DefaultMessage defaultMessage;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(defaultMessage).build();
    }

    @Test
    @DisplayName("Test for processCDRWebhook method - Success")
    void testProcessCDRWebhook_Success() throws Exception {
        String requestBody = "{}";

        // Mock external service calls
        when(restTemplate.postForLocation(any(String.class), any()))
                .thenReturn(null);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/public/webhook/cdr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

    }

    
    @Test
    @DisplayName("Test for handleCDRWebhook method - Not Found")
    void testHandleCDRWebhook_NotFound() throws Exception {
        String requestBody = "{}";

        when(restTemplate.postForLocation(any(String.class), any()));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/public/webhook/cdr/invalidHandle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andReturn();



    }
}
