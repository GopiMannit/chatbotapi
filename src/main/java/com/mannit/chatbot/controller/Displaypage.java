package com.mannit.chatbot.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mannit.chatbot.model.CurrentPatient;
import com.mannit.chatbot.model.Noappointment;
import com.mannit.chatbot.model.QueriedPatient;
import com.mannit.chatbot.repository.Currentpatientsrepo;
import com.mannit.chatbot.repository.Noappointmentrepo;
import com.mannit.chatbot.repository.QuriedpRepo;

@Controller
public class Displaypage {

    @Autowired
    private Currentpatientsrepo repo;
    @Autowired
    private Noappointmentrepo no_app_repo;
    @Autowired
    private QuriedpRepo quried_repo;

    @GetMapping(value = "/")
    public String getcurrentpatients(Model model) throws ParseException {
        LocalDateTime current_time = LocalDateTime.now();
        String formattedDate = convertDateFormat(current_time.toString(), "yyyy-MM-dd", "MM/dd/yyyy");
        System.out.println("formated date------------s" + formattedDate);
        List<CurrentPatient> cp = repo.findByDate(formattedDate);
        List<QueriedPatient> qp = quried_repo.findByDate(formattedDate);
        List<Noappointment> np = no_app_repo.findByDate(formattedDate);
        List<Noappointment> uniqueNp = removeDuplicates(np, Noappointment::getTimestamp);
        List<CurrentPatient> uniqueCp = removeDuplicates(cp, CurrentPatient::getTimestamp);
        List<QueriedPatient> uniqueQp = removeDuplicates(qp, QueriedPatient::getTimestamp);

        model.addAttribute("currentpatients", uniqueCp);
        model.addAttribute("queriedpatients", uniqueQp);
        model.addAttribute("noappointment", uniqueNp);
        return "index.html";
    }

    @GetMapping(value = "/getbydate")
    public String getbydate(@RequestParam("date") String date, Model model) throws ParseException {
        String formattedDate = convertDateFormat(date, "yyyy-MM-dd", "MM/dd/yyyy");
        List<CurrentPatient> cp = repo.findByDate(formattedDate);
        System.out.println("the data" + repo.findByDate(formattedDate));
        List<QueriedPatient> qp = quried_repo.findByDate(formattedDate);
        List<Noappointment> np = no_app_repo.findByDate(formattedDate);
        System.out.println(np);
        List<Noappointment> uniqueNp = removeDuplicates(np, Noappointment::getTimestamp);
        List<CurrentPatient> uniqueCp = removeDuplicates(cp, CurrentPatient::getTimestamp);
        List<QueriedPatient> uniqueQp = removeDuplicates(qp, QueriedPatient::getTimestamp);
        model.addAttribute("currentpatients", uniqueCp);
        model.addAttribute("queriedpatients", uniqueQp);
        model.addAttribute("noappointment", uniqueNp);
        return "index.html";
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

    private static <T> List<T> removeDuplicates(List<T> list,
            java.util.function.Function<T, String> timestampExtractor) {
        return list.stream()
                .collect(Collectors.toMap(timestampExtractor, item -> item, (existing, replacement) -> existing))
                .values().stream().collect(Collectors.toList());
    }

}
