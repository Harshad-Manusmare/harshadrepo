package com.amrutsoftware.notificationsender.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.inject.Inject;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

@Scanned
@Component
public class IssueAssignedEventListener implements InitializingBean, DisposableBean{
	
	@ComponentImport EventPublisher eventPublisher;

	@Inject
	public IssueAssignedEventListener(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void destroy() throws Exception {
		eventPublisher.register(this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
	}
	
	@EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        Long eventTypeId = issueEvent.getEventTypeId();
        final String propertyKey = "Phone Number";
        if (EventType.ISSUE_ASSIGNED_ID.equals(eventTypeId)) {
            Issue issue = issueEvent.getIssue();
            System.out.println("\n\n\n*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%**%*%*%*%*\n\n\nInside Issue Assinged Event Listener\n\n\n*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%*%**%*%*%*%*\n\n\n");
            smsgatewayhub(ComponentAccessor.getUserPropertyManager().getPropertySet(issue.getReporter()).getString("jira.meta."+propertyKey), "Your Appointment is Sheduled with Doctor Mr/Ms/Mrs. "+issue.getAssignee().getDisplayName());
            smsgatewayhub(ComponentAccessor.getUserPropertyManager().getPropertySet(issue.getAssignee()).getString("jira.meta."+propertyKey), "You have an Appointment with Mr/Ms/Mrs."+issue.getReporter().getDisplayName());
        }
    }
	
	private void smsgatewayhub(String phoneNumber, String massage){
    	//Your authentication key
        String apikey = "E2FWGsuVmEm4tAXID0IY2g";
        //Multiple mobiles numbers separated by comma
        String mobiles = phoneNumber;
        //Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "HAR-SMS";
        //Your message to send, Add URL encoding here.
        String message = massage;
        //define route
        String channel="2";

        //Prepare Url
        URLConnection myURLConnection=null;
        URL myURL=null;
        BufferedReader reader=null;
        String encoded_message;

        //Send SMS API
        String mainUrl="https://www.smsgatewayhub.com/api/mt/SendSMS?";

        StringBuilder sbPostData= new StringBuilder(mainUrl);

        
        try
        {
        	//encoding message
        	encoded_message=URLEncoder.encode(message, "UTF-8");
        	
        	//Prepare parameter string
        	sbPostData.append("APIKey="+apikey);
        	sbPostData.append("&senderid="+senderId);
        	sbPostData.append("&channel="+channel);
        	sbPostData.append("&DCS=8&flashsms=1");
            sbPostData.append("&number="+mobiles);
            sbPostData.append("&text="+encoded_message);
            sbPostData.append("&route=1");
            
            //final string
            mainUrl = sbPostData.toString();
            
            //prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader= new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
            //reading response
            String response;
            while ((response = reader.readLine()) != null)
            //print response
            System.out.println(response);

            //finally close connection
            reader.close();
        }
        catch (IOException e)
        {
                e.printStackTrace();
        }
    }

}
