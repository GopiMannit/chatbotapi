/*
 * package com.mannit.chatbot.controller;
 * 
 * import java.time.LocalDateTime; import java.time.format.DateTimeFormatter;
 * import java.util.ArrayList; import java.util.Arrays; import java.util.List;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.ModelAttribute; import
 * org.springframework.web.bind.annotation.PostMapping; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * com.mannit.chatbot.config.Configvalues; import
 * com.mannit.chatbot.model.PatientDetails; import
 * com.mannit.chatbot.repository.Pdetailsrepository;
 * 
 * 
 * 
 * @Controller public class PatientSurvey {
 * 
 * Logger logger = LoggerFactory.getLogger(PatientSurvey.class);
 * 
 * @Autowired private Pdetailsrepository repository;
 * 
 * @Autowired private Configvalues values;
 * 
 * 
 * @GetMapping("/getformdetails") public String getsurveyform(Model themodel) {
 * PatientDetails details =new PatientDetails(); List<String>concerns
 * =values.getOptions(); themodel.addAttribute("options", concerns);
 * themodel.addAttribute("details",details); return "patientsurvey"; }
 * 
 * @PostMapping("/getpatientdetails") public String
 * appointmentsdetails(@ModelAttribute("details") PatientDetails details){
 * logger.info("<starting appointmentdetails method>"); LocalDateTime
 * current_time= LocalDateTime.now(); //System.out.println(current_time);
 * DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
 * String current_date =current_time.format(formatter);
 * logger.info("<the current date {}>",current_date); PatientDetails p = new
 * PatientDetails(); p.setFrom(details.getFrom()); p.setName(details.getName());
 * p.setPhone_number(details.getPhone_number());
 * p.setConcern(details.getConcern()); p.setDate(current_date);
 * p.setComment(details.getComment()); repository.save(p);
 * logger.info("saved to mongo db");
 * logger.info("</Ending appointmentdetails method>"); return "Thanks"; }
 * 
 * 
 * 
 * 
 * }
 */