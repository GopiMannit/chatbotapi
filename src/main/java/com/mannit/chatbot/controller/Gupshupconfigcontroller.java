/*
 * package com.mannit.chatbot.controller;
 * 
 * import java.time.LocalDateTime;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.http.HttpEntity; import
 * org.springframework.http.HttpHeaders; import
 * org.springframework.http.HttpMethod; import
 * org.springframework.http.HttpStatus; import
 * org.springframework.http.MediaType; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.web.client.RestClientException; import
 * org.springframework.web.client.RestTemplate; import
 * com.mannit.chatbot.config.Configvalues; import
 * com.mannit.chatbot.model.SentMessageStatus; import
 * com.mannit.chatbot.repository.Currentpatientsrepo; import
 * com.mannit.chatbot.repository.MessagestatusRepo;
 * 
 * 
 * public class Gupshupconfigcontroller {
 * 
 * private final Logger logger =
 * LoggerFactory.getLogger(Gupshupconfigcontroller.class); private final
 * RestTemplate restTemplate; private final Configvalues values;
 * 
 * @Autowired private MessagestatusRepo repo;
 * 
 * @Autowired public Gupshupconfigcontroller(RestTemplate restTemplate,
 * Configvalues values, Currentpatientsrepo app) { this.restTemplate =
 * restTemplate; this.values = values; }
 * 
 * public ResponseEntity<String> sendRequestToGupshup(String phoneNumber, String
 * status) throws RestClientException, Exception { try { HttpHeaders headers =
 * new HttpHeaders();
 * headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
 * HttpEntity<String> requestEntity = new HttpEntity<>(headers); String
 * phonenumber = "91" + phoneNumber; String fullurl2 = gupshupurl(phonenumber);
 * ResponseEntity<String> response = restTemplate.exchange(fullurl2,
 * HttpMethod.GET, requestEntity,String.class); SentMessageStatus msgstatus =
 * new SentMessageStatus(); msgstatus.setIncoming_status(status);
 * msgstatus.setPhone_number(phoneNumber); msgstatus.setMsg_status("success");
 * msgstatus.setDate(LocalDateTime.now()); repo.save(msgstatus);
 * System.out.println("Response : " + response); return response; } catch
 * (Exception e) { logger.error("{could not send the message}", e.getMessage());
 * SentMessageStatus msgstatus = new SentMessageStatus();
 * msgstatus.setIncoming_status(status); msgstatus.setPhone_number(phoneNumber);
 * msgstatus.setMsg_status("failed"); msgstatus.setDate(LocalDateTime.now());
 * repo.save(msgstatus); return new ResponseEntity<>("failed to send message",
 * HttpStatus.BAD_REQUEST); } }
 * 
 * public String gupshupurl(String PhoneNumber) {
 * 
 * String first_half = values.getGupshup_base_url(); String Second_half =
 * values.getRemaining(); String request_url = first_half + "send_to=" +
 * phoneNumber + Second_half;
 * 
 * return
 * "https://media.smsgupshup.com/GatewayAPI/rest?userid=2000231213&password=UjgMVBK$&send_to="
 * + PhoneNumber +
 * "&v=1.1&format=json&msg_type=TEXT&method=SENDMESSAGE&msg=Looks like you have one appointment today. Please Click on Yes to confirm the appointment for today.\r\n"
 * + "\r\n" + "Address: \r\n" +
 * "Dr Ahamed's Apple B-7 Doctors plaza, 1st floor, Paramount Park, Vijayanagar, Velachery main road, Chennai-600042.\r\n"
 * +
 * "Map:https://goo.gl/maps/ENkyrbxtsNzUFXSZA&isTemplate=true&header=Dr. Ahamed's Apple&footer=HOLISTIC HEALING CENTER"
 * ; }
 * 
 * 
 * 
 * }
 */