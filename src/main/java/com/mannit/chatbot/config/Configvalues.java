package com.mannit.chatbot.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("classpath:application.properties")
@Component
public class Configvalues {

    @Value("${spring.frontend.options.concern}")
    private List<String> options;

    @Value("${watti.auth.token}")
    private String BEARER_TOKEN;

    @Value("${watti.api.url}")
    private String apiURL;

    @Value("${watti.api.addcontact.url}")
    private String Optin_url;

    @Value("${spring.google.spreadsheetId}")
    private String spreadsheetId;
    @Value("${smartyuppies.auth.token}")
    private String smart_y_token;
    @Value("${smartyuppies.api.url}")
    private String smart_y_url;
    @Value("${smartyuppies.languagecode}")
    private String smart_y_lang;
    @Value("${smartyuppies.template.name}")
    private String smart_y_tmpltname;
    @Value("${smartyuppies.campaign_name}")
    private String campaign_name;
    @Value("${smartyuppies.owner.name}")
    private String owner_name;
    
    
    public String getCampaign_name() {
		return campaign_name;
	}
	public void setCampaign_name(String campaign_name) {
		this.campaign_name = campaign_name;
	}
	public String getOwner_name() {
		return owner_name;
	}
	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}
	public String getSmart_y_token() {
		return smart_y_token;
	}
	public void setSmart_y_token(String smart_y_token) {
		this.smart_y_token = smart_y_token;
	}
	public String getSmart_y_url() {
		return smart_y_url;
	}
	public void setSmart_y_url(String smart_y_url) {
		this.smart_y_url = smart_y_url;
	}
	public String getSmart_y_lang() {
		return smart_y_lang;
	}
	public void setSmart_y_lang(String smart_y_lang) {
		this.smart_y_lang = smart_y_lang;
	}
	public String getSmart_y_tmpltname() {
		return smart_y_tmpltname;
	}
	public void setSmart_y_tmpltname(String smart_y_tmpltname) {
		this.smart_y_tmpltname = smart_y_tmpltname;
	}
	public String getOptin_url() {
        return Optin_url;
    }
    public void setOptin_url(String optin_url) {
        Optin_url = optin_url;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getBEARER_TOKEN() {
        return BEARER_TOKEN;
    }

    public void setBEARER_TOKEN(String bEARER_TOKEN) {
        BEARER_TOKEN = bEARER_TOKEN;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getSpreadsheetId() {
        return spreadsheetId;
    }

    public void setSpreadsheetId(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

}
