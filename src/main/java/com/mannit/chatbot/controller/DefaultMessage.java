package com.mannit.chatbot.controller;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mannit.chatbot.config.Configvalues;
import com.mannit.chatbot.model.SentMessageStatus;
import com.mannit.chatbot.model.Defaultstatus;
import com.mannit.chatbot.repository.Currentpatientsrepo;
import com.mannit.chatbot.repository.MessagestatusRepo;
import com.mannit.chatbot.repository.Defaultstatusrepo;
import com.mannit.chatbot.response.ApiResponse;

@RestController
@RequestMapping("/public**")
public class DefaultMessage {

    private static final int NUM_THREADS = 10;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
    private final Logger logger = LoggerFactory.getLogger(DefaultMessage.class);
    private final RestTemplate restTemplate;
    private final Configvalues values;
    private final Currentpatientsrepo app ;
    private final MessagestatusRepo repo;
    private final Defaultstatusrepo srepo;
   
    @Autowired
    public DefaultMessage(RestTemplate restTemplate, Configvalues values,Currentpatientsrepo app,MessagestatusRepo repo,Defaultstatusrepo srepo) {
        this.restTemplate = restTemplate;
        this.values = values;
        this.app=app;
        this.repo=repo;
        this.srepo=srepo;
     
    }

    @RequestMapping(value = "/webhook/cdr", method = RequestMethod.POST)
    public ResponseEntity<Object> processCDRWebhook(@RequestBody String requestBody) {
        logger.info("<Start handleCDRWebhook>");
        try {
            executorService.submit(() -> handleCDRWebhook(requestBody));
            logger.info("</End handleCDRWebhook>");
            return apiresponse(new ApiResponse(HttpStatus.OK, "Request accepted for processing"));
        } catch (Exception e) {
            handleWebhookError(e);
            return apiresponse(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something bad happened"));
        }
    }

    public ResponseEntity<Object> handleCDRWebhook(String requestBody) {
        logger.info("<Start handleCDRWebhook>");
        try {
            processWebhookRequest(requestBody);
            logger.info("</End handleCDRWebhook>");
            return apiresponse(new ApiResponse(HttpStatus.OK, "message successfully sent"));
        } catch (Exception e) {
            handleWebhookError(e);
            return apiresponse(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error or token expired"));
        }
    }

    private void processWebhookRequest(String requestBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            String direction = jsonNode.get("direction").asText();

            if ("inbound".equals(direction)) {
                String phoneNumber = jsonNode.get("from").asText();
                String status = jsonNode.get("status").asText();
                if ("missed".equalsIgnoreCase(status)) {
                    //sendRequestToApi(phoneNumber);
                    sendreqsmartyup(phoneNumber);
                }
                logger.info("<Missed call from {}>", phoneNumber);
            }
            logger.info("</End handleCDRWebhook>");
        } catch (Exception e) {
            handleWebhookError(e);
        }
    }

    private void sendRequestToApi(String phoneNumber) {
        logger.info("<started processing for Sending message>");
        addContact(phoneNumber);
        HttpHeaders headers = createApiRequestHeaders();
        UriComponentsBuilder uriBuilder = createApiUriBuilder(phoneNumber);
        Map<String, Object> requestBody = createApiRequestBody();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        Date currentDate = new Date();
        String formattedDate = convertDateFormat(currentDate.toString(), "EEE MMM dd HH:mm:ss zzz yyyy", "MM/dd/yyyy");
        try {
            //restTemplate.postForLocation(uriBuilder.toUriString(), requestEntity);
            ResponseEntity<String> responseEntity= restTemplate.postForEntity(uriBuilder.toUriString(), requestEntity,String.class);
            String responseBody = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String numbervalid = jsonNode.get("validWhatsAppNumber").asText();
            SentMessageStatus stat= new SentMessageStatus();
            stat.setPhone_number(phoneNumber);
            stat.setDate(formattedDate);
            repo.save(stat);
//          System.out.println(Boolean.getBoolean(numbervalid));
//          System.out.println(responseBody);
            logger.info("</Sent the message>");
        } catch (Exception e) {
            handleApiRequestError(e);
        }
    }

    private void addContact(String phoneNumber) {
        logger.info("<adding the number to contact>");
        HttpHeaders headers = createApiRequestHeaders();
        String uri = values.getOptin_url() + "91" + phoneNumber;
        Map<String, Object> customParam = createCustomParam(phoneNumber);
        Map<String, Object> requestBody = createAddContactRequestBody(phoneNumber, customParam);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForLocation(uri, requestEntity);
            logger.info("</number added to contact>");
        } catch (Exception e) {
            handleApiRequestError(e);
        }
    }
    private HttpHeaders createApiRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(values.getBEARER_TOKEN());
        return headers;
    }

    private UriComponentsBuilder createApiUriBuilder(String phoneNumber) {
        return UriComponentsBuilder.fromUriString(values.getApiURL()).queryParam("whatsappNumber", "91" + phoneNumber);
    }

    private Map<String, Object> createApiRequestBody() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("template_name", "betatst2");
        requestBody.put("broadcast_name", "-");
        return requestBody;
    }

    private Map<String, Object> createCustomParam(String phoneNumber) {
        Map<String, Object> customParam = new HashMap<>();
        customParam.put("name", phoneNumber);
        customParam.put("value", phoneNumber);
        
        return customParam;
    }

    private Map<String, Object> createAddContactRequestBody(String phoneNumber, Map<String, Object> customParam) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", phoneNumber);
        requestBody.put("customParams", Collections.singletonList(customParam));
        return requestBody;
    }

    private void handleWebhookError(Exception e) {
        e.printStackTrace();
        logger.info("</End handleCDRWebhook>");
        logger.error("<Client webhook error>", e.getMessage());
    }

    private void handleApiRequestError(Exception e) {
        e.printStackTrace();
        logger.info("<Server authentication error need refresh token>");
    }
    
    private ResponseEntity<Object> apiresponse(ApiResponse response) {
        return new ResponseEntity<>(response, response.getStatus());
    }
    public String convertDateFormat(String inputDateStr, String inputFormat, String outputFormat) {
        DateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
        DateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
        try {
            Date date = inputDateFormat.parse(inputDateStr);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

   //@GetMapping("/sendtosmartyup")
   public void sendreqsmartyup(String phonenumber) {
    	String url =values.getSmart_y_url();
    	String token=values.getSmart_y_token();
    	String templatename=values.getSmart_y_tmpltname();
    	String language =values.getSmart_y_lang();
    	String campaignname=values.getCampaign_name();
    	String ownername=values.getOwner_name();
    	UriComponentsBuilder builder =UriComponentsBuilder.fromUriString(url);
    	builder.queryParam("smartbanner_token", token);
    	builder.queryParam("campaign_name", campaignname);
    	builder.queryParam("owner_name", ownername);
    	builder.queryParam("mobile_numbers", "91"+phonenumber);
    	builder.queryParam("template_name", templatename);
    	builder.queryParam("language_code",language );
    	System.out.println(builder.build().toString());
    	ResponseEntity<String> response =restTemplate.getForEntity(builder.build().toString(), String.class);
    	System.out.println(response.toString());
    	Date currentDate = new Date();
        String formattedDate = convertDateFormat(currentDate.toString(), "EEE MMM dd HH:mm:ss zzz yyyy", "MM/dd/yyyy HH:mm:ss");
    	SentMessageStatus ms= new SentMessageStatus();
    	ms.setPhone_number(phonenumber);
    	ms.setDate(formattedDate);
    	repo.save(ms);
    }

   
}
