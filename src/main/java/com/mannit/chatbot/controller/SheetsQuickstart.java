package com.mannit.chatbot.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.mannit.chatbot.config.Configvalues;
import com.mannit.chatbot.model.Botdelivered;
import com.mannit.chatbot.model.Botread;
import com.mannit.chatbot.model.Botsent;
import com.mannit.chatbot.model.CurrentPatient;
import com.mannit.chatbot.model.Latestdatecollection;
import com.mannit.chatbot.model.Noappointment;
import com.mannit.chatbot.model.QueriedPatient;
import com.mannit.chatbot.model.SentMessageStatus;
import com.mannit.chatbot.model.Defaultstatus;
import com.mannit.chatbot.model.Smartyupresp;
import com.mannit.chatbot.repository.Botdeliveredrepo;
import com.mannit.chatbot.repository.Botreadrepo;
import com.mannit.chatbot.repository.Botsentrepo;
import com.mannit.chatbot.repository.Currentpatientsrepo;
import com.mannit.chatbot.repository.Latestdatecollectionrepo;
import com.mannit.chatbot.repository.MessagestatusRepo;
import com.mannit.chatbot.repository.Noappointmentrepo;
import com.mannit.chatbot.repository.QuriedpRepo;
import com.mannit.chatbot.repository.Defaultstatusrepo;
import com.mannit.chatbot.repository.Smartyupresrespo;
import com.mannit.chatbot.response.ApiResponse;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;

@RestController
public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private final static Logger logger = LoggerFactory.getLogger(SheetsQuickstart.class);
    private static final int NUM_THREADS = 20;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
    @Autowired
    private Currentpatientsrepo repo;
    @Autowired
    private Noappointmentrepo no_app_repo;
    @Autowired
    private QuriedpRepo quried_repo;
    @Autowired
    private Latestdatecollectionrepo date_repo;
    @Autowired
    private Configvalues spreadsheetVal;
    @Autowired
    private MessagestatusRepo mrepo;
    @Autowired
    private Defaultstatusrepo srepo;
    @Autowired
    private Smartyupresrespo yrepo;
    @Autowired
    private Botsentrepo sentrepo;
    @Autowired
    private Botreadrepo readrepo;
    @Autowired
    private Botdeliveredrepo deliveredrepo;
    
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private String lastProcessedTimestampYesPatients = "";
    private String lastProcessedTimestampNoAppointment = "";
    private String lastProcessedTimestampCallBack = "";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        logger.info("<START in the getcredentials method>");
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        logger.info("</END in the getcredentials method>");
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return credential;
    }


   // @Scheduled(fixedRate = 600000)
    public void getsheetdata() throws GeneralSecurityException, IOException {
        logger.info("<In the getSheetdata() method start>");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        // final String spreadsheetId = "16mQkUXa6PeeH96WDFeATHPLFx_dYUUOGcXqJ9clkBek";
        final String spreadsheetId = spreadsheetVal.getSpreadsheetId();
        final String range_1 = "Yes-patients";
        final String range_2 = "No-appointment";
        final String range_3 = "Call-me-back";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();
        logger.info("<Started Reading the spreadsheet with spreadsheet id >" + spreadsheetId);
        ValueRange response_1 = service.spreadsheets().values().get(spreadsheetId, range_1).execute();
        List<List<Object>> values1 = response_1.getValues();
        lastProcessedTimestampYesPatients = processSheetData("Yes-patients", values1,
                lastProcessedTimestampYesPatients);

        ValueRange response_2 = service.spreadsheets().values().get(spreadsheetId, range_2).execute();
        List<List<Object>> values2 = response_2.getValues();
        lastProcessedTimestampNoAppointment = processSheetData("No-appointment", values2,
                lastProcessedTimestampNoAppointment);

        ValueRange response_3 = service.spreadsheets().values().get(spreadsheetId, range_3).execute();
        List<List<Object>> values3 = response_3.getValues();
        lastProcessedTimestampCallBack = processSheetData("Call-me-back", values3, lastProcessedTimestampCallBack);
    }

    private String processSheetData(String sheetName, List<List<Object>> values, String lastProcessedTimestamp) {
        boolean isFirstRow = true;
        if (values == null || values.isEmpty()) {
            System.out.println("No data found for " + sheetName);
        } else {
            logger.info("<Sheet value>" + values.size());
            for (List<Object> row : values) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                String timestamp = row.get(0).toString();

                Optional<Latestdatecollection> ct = date_repo.findById(insertDummyDocument());
                Latestdatecollection ct3 = ct.orElse(null);
                String c_date = ct3.getAppointment_lastupdated();
                String n_date = ct3.getRejected_lastupdated();
                String q_date = ct3.getQueried_lastupdated();

                // if (timestamp.compareTo(lastProcessedTimestamp) > 0) {
                if (sheetName.equals("Yes-patients") && compare(timestamp, c_date)) {
                    logger.info("<START Yes-patients>");
                    CurrentPatient cp = new CurrentPatient();
                    cp.setTimestamp(row.get(0).toString());
                    cp.setName(row.get(1).toString());
                    cp.setPhone_number(row.get(2).toString());
                    cp.setDoctor_choice(row.get(3).toString());
                    ct3.setAppointment_lastupdated(row.get(0).toString());
                    repo.save(cp);
                    logger.info("</Insert Yes-patients ::>" + cp);
                } else if (sheetName.equals("No-appointment") && compare(timestamp, n_date)) {
                    logger.info("<START Insert No-appointment>");
                    Noappointment noAppointment = new Noappointment();
                    noAppointment.setName(row.get(1).toString());
                    noAppointment.setPhone_number(row.get(2).toString());
                    noAppointment.setTimestamp(row.get(0).toString());
                    ct3.setRejected_lastupdated(row.get(0).toString());
                    no_app_repo.save(noAppointment);
                    logger.info("</End Insert No-appointment ::>" + noAppointment);
                } else if (sheetName.equals("Call-me-back") && compare(timestamp, q_date)) {
                    logger.info("<START Call-me-back>");
                    QueriedPatient qp = new QueriedPatient();
                    qp.setName(row.get(1).toString());
                    qp.setPhone_number(row.get(2).toString());
                    qp.setTimestamp(row.get(0).toString());
                    ct3.setQueried_lastupdated(row.get(0).toString());
                    quried_repo.save(qp);
                    logger.info("</End Insert Call-me-back ::>" + qp);
                }
                date_repo.save(ct3);
                lastProcessedTimestamp = timestamp;
            }
        }
        // }
        return lastProcessedTimestamp;
    }

    @RequestMapping(value = "/api/getbydate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "https://ahamedsapple.mannit.co")
    public Map<String, List<?>> getByDate(@RequestParam("date") String date) {
        String formattedDate = convertDateFormat(date, "yyyy-MM-dd", "MM/dd/yyyy");
        List<CurrentPatient> cp = repo.findByDate(formattedDate);
        List<QueriedPatient> qp = quried_repo.findByDate(formattedDate);
        List<Noappointment> np = no_app_repo.findByDate(formattedDate);
        List<Noappointment> uniqueNp = removeDuplicates(np, Noappointment::getTimestamp);
        List<CurrentPatient> uniqueCp = removeDuplicates(cp, CurrentPatient::getTimestamp);
        List<QueriedPatient> uniqueQp = removeDuplicates(qp, QueriedPatient::getTimestamp);
        List<SentMessageStatus> sentnumbers  = mrepo.findAllBydate(formattedDate);
        Map<String, List<?>> result = new HashMap<>();
        result.put("currentPatients", uniqueCp);
        result.put("queriedPatients", uniqueQp);
        result.put("noAppointments", uniqueNp);
        result.put("sentnumbers", sentnumbers);
        System.out.println(result);
        return result;
    }

    @RequestMapping(value = "/getdata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<?>> getcurrentpatient(Model model) throws ParseException {
        LocalDateTime current_time = LocalDateTime.now();
        String formattedDate = convertDateFormat(current_time.toString(), "yyyy-MM-dd", "MM/dd/yyyy");
        System.out.println("formated date------------s" + formattedDate);
        List<CurrentPatient> cp = repo.findByDate(formattedDate);
        List<QueriedPatient> qp = quried_repo.findByDate(formattedDate);
        List<Noappointment> np = no_app_repo.findByDate(formattedDate);
       // List<SentMessageStatus>sentdetails= 
        List<Noappointment> uniqueNp = removeDuplicates(np, Noappointment::getTimestamp);
        List<CurrentPatient> uniqueCp = removeDuplicates(cp, CurrentPatient::getTimestamp);
        List<QueriedPatient> uniqueQp = removeDuplicates(qp, QueriedPatient::getTimestamp);
        Map<String, List<?>> result = new HashMap<>();
        result.put("currentPatients", uniqueCp);
        result.put("queriedPatients", uniqueQp);
        result.put("noAppointments", uniqueNp);
        return result;
    }

    private static <T> List<T> removeDuplicates(List<T> list,
            java.util.function.Function<T, String> timestampExtractor) {
        return list.stream()
                .collect(Collectors.toMap(timestampExtractor, item -> item, (existing, replacement) -> existing))
                .values().stream().collect(Collectors.toList());
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

//@GetMapping("/m") 
    public boolean compare(String sheetdate, String dbdate) {
        // String dateString = "01/09/2024 10:33:42";
        String dateFormat = "MM/dd/yyyy HH:mm:ss";
//	sheetdate = "01/09/2024 10:35:42";
//	dbdate = "01/09/2024 10:33:42";

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        try {
            Date parsedDate = sdf.parse(sheetdate);
            Date parseddbdate = sdf.parse(dbdate);
            parseddbdate.compareTo(parsedDate);
            System.out.println(parsedDate.after(parseddbdate));
            boolean parsevalue = parsedDate.after(parseddbdate);
            // System.out.println(parsevalue);
            return parsevalue;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String insertDummyDocument() {
        if (date_repo.count() > 0) {
            Optional<Latestdatecollection> existingDocument = date_repo.findAll().stream().findFirst();
            // Return the ID if the existing document is found
            return existingDocument.map(Latestdatecollection::getId).orElse("No document found");
        } else {
            Latestdatecollection dummyDocument = new Latestdatecollection();
            dummyDocument.setRejected_lastupdated("01/23/2024 10:32:51");
            dummyDocument.setAppointment_lastupdated("01/23/2024 9:51:56");
            dummyDocument.setQueried_lastupdated("12/21/2023 20:00:02");
            Latestdatecollection savedDocument = date_repo.save(dummyDocument);
            return savedDocument.getId();
        }
    }

    public void getsortedlist(List<CurrentPatient> CpatientList) {
        // List<Noappointment>NpatientList,List<QueriedPatient>QpatientList
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Comparator<CurrentPatient> timestampComparator = Comparator.comparing(patient -> {
            try {
                return dateFormat.parse(patient.getTimestamp());
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date(0);
            }
        });
        Collections.sort(CpatientList, timestampComparator);

        for (CurrentPatient patient : CpatientList) {
            System.out.println(patient);
        }
    }
    @PostMapping("/api/metamsgstatus")
  public ResponseEntity<Object> getsm(@RequestBody String requestbody){
	  executorService.submit(()-> getsmartyupresponse(requestbody));
	  return apiresponse(new ApiResponse(HttpStatus.OK, "received successfully",getcurrenttime()));
  }
    
    @PostMapping("/api/replies")
  public ResponseEntity<Object> getrep(@RequestBody String requestbody){
	  executorService.submit(()-> getreplies(requestbody));
	  return apiresponse(new ApiResponse(HttpStatus.OK, "received successfully",getcurrenttime()));
  } 
    
    
    //@PostMapping("/api/metamsgstatus")
    public ResponseEntity<Object> getsmartyupresponse(String requestbody) throws JsonMappingException, JsonProcessingException{
      System.out.println("in getsmartyupresponse"+requestbody);
       String formattedDate = getcurrenttime();
 	   System.out.println(requestbody);
 	   JsonNode node =decodejson(requestbody);
 	   Defaultstatus sc=new Defaultstatus();
 	   String status =node.get("status").asText();
 	   String phone_number_id =node.get("phone_number_id").asText();
 	   String customer_number=node.get("customer_phone_number").asText();
 	   String wamid=node.get("wamid").asText();
 	   switch(status) {
 	   case "sent":
 		   Botsent sent =new Botsent();
 		   sent.setBool_status(Boolean.parseBoolean(node.get("is_message_send").asText()));
 		   sent.setPhone_no_id(phone_number_id);
 		   sent.setTimestamp(formattedDate);
 		   sent.setWamid(wamid);
 		   sent.setPhone_number(customer_number);
 		   sent.setStatus(status); 
 		   sentrepo.save(sent);
 		   break;
 	   case "delivered":
 		   Botdelivered bd= new Botdelivered();
 		   bd.setPhone_no_id(phone_number_id);
 		   bd.setBool_status(Boolean.parseBoolean(node.get("is_message_delivered").asText()));
 		   bd.setStatus(status);
 		   bd.setTimestamp(formattedDate);
 		   bd.setWamid(wamid);
 		   bd.setPhone_number(customer_number);
 		   deliveredrepo.save(bd);
 		   break;
 	   case "read":
 		   Botread br =new Botread();
 		   br.setPhone_number(customer_number);
 		   br.setStatus(status);
 		   br.setTimestamp(formattedDate);
 		   br.setWamid(wamid);
 		   readrepo.save(br);
 		   break;
 		   default:
 			Defaultstatus ds = new Defaultstatus();
 			ds.setPhone_no_id(phone_number_id);
 			ds.setPhone_number(customer_number);
 			ds.setStatus(status);
 			ds.setTimestamp(formattedDate);
 			ds.setTimestamp(formattedDate);
 			ds.setWamid(wamid);
 			srepo.save(ds);
 	   }     
 	   logger.info("{saving details}"+sc);
 	   logger.info("{received details}"+requestbody);
 	   return apiresponse(new ApiResponse(HttpStatus.OK, "received successfully",formattedDate));
    }
    
    
    public ResponseEntity<Object>getreplies( String requestbody) throws JsonMappingException, JsonProcessingException{
    System.out.println("in getreplies"+requestbody);
 	JsonNode node =decodejson(requestbody);
	long unixSeconds = node.get("received_at").asLong();
 	Date date = new java.util.Date(unixSeconds*1000L); 
 	SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
 	String timestamp = sdf.format(date);
 	String Phone_number = node.get("customer_phone_number").asText();
 	String msg =node.get("message").asText();
 	String name=node.get("customer_name").asText();
 	String phoneno_id=node.get("phone_number_id").asText();
 	String wamid=node.get("wamid").asText();
 	String currentdate =getcurrenttime();
 	saveall(timestamp,msg,Phone_number,name,phoneno_id,wamid,currentdate);
      switch(msg) {
      case "Yes":
    	 CurrentPatient pt = new CurrentPatient();
    	 pt.setPhone_number(Phone_number);
    	 pt.setName(name);
    	 pt.setTimestamp(timestamp);
    	 pt.setMsg(msg);
    	 repo.save(pt);
    	 break;
      case "No":
    	  Noappointment np =new Noappointment();
    	  np.setTimestamp(timestamp);
    	  np.setName(name);
    	  np.setPhone_number(Phone_number);
    	  no_app_repo.save(np);
    	  break;
      case "Call Me Back":
    	  QueriedPatient qp = new QueriedPatient();
    	  qp.setName(name);
    	  qp.setPhone_number(Phone_number);
    	  qp.setTimestamp(timestamp);
    	  quried_repo.save(qp);
    	  break;
      case "Duty Doctor-1000/-":
      case "Chief Doctor-4000/-":
    	  updatecurrentpatient(timestamp,msg,Phone_number);
    	  break;
      }
      return apiresponse(new ApiResponse(HttpStatus.OK, "received successfully",getcurrenttime()));
    }
    
    public JsonNode decodejson(String json) throws JsonMappingException, JsonProcessingException {
 	   ObjectMapper mapper =new ObjectMapper();
 	   JsonNode node =mapper.readTree(json);
 	   return node;
    }
    private ResponseEntity<Object> apiresponse(ApiResponse response) {
        return new ResponseEntity<>(response, response.getStatus());
    }
    private void updatecurrentpatient(String timestamp,String message,String phonenumber) {
    	List<CurrentPatient> cp2=  repo.findByDateAndPhonenumber(convertDateFormat(timestamp,"MM/dd/yyyy HH:mm:ss","MM/dd/yyyy"), phonenumber);
    	 for(CurrentPatient c:cp2) {
    		 c.setDoctor_choice(message);
    		 c.setTimestamp(timestamp);
    		 repo.save(c);
    	 }
    }
   private void saveall(String timestamp,String message,String phonenumber,String name,String phonenoid,String wamid,String currenttime) {
	   Smartyupresp re = new Smartyupresp() ;
	   re.setMsg(message);
	   re.setName(name);
	   re.setPhone_number(phonenumber);
       re.setPhone_number_id(phonenoid);
       re.setTimestamp(timestamp);
       re.setWamid(wamid);
       re.setLastupdateddate(currenttime);
       yrepo.save(re);
	   
 }
 private String getcurrenttime() {
	 Date currentDate = new Date();
     String formattedDate = convertDateFormat(currentDate.toString(), "EEE MMM dd HH:mm:ss zzz yyyy", "MM/dd/yyyy HH:mm:ss");
	 return formattedDate;
	 }


}
