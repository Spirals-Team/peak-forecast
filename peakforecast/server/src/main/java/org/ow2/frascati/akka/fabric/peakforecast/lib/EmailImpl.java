/**
 * PeakForecast
 * Copyright (C) 2009-2012 INRIA, University of Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
	Contact: contact-adam@lifl.fr
	Author: Daniel Fouomene
	Contributor(s): Romain Rouvoy, Lionel Seinturier        
 */
package org.ow2.frascati.akka.fabric.peakforecast.lib;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.osoa.sca.annotations.Property;
import org.ow2.frascati.akka.fabric.peakforecast.api.EmailService;


/** The Email service implementation. */
public class EmailImpl
  implements EmailService
{

	//liste des emails separés par le caractère espace
	@Property private String listEmails="info@fouomene.com starbebe2003@yahoo.fr" ; 
	/** Default constructor. */
  public EmailImpl() {
	  System.out.println("Component Email created.");
	}
	



	@Override
	public void send(String message) {
		
		try {
			Email email = new SimpleEmail();
			email.setHostName("smtp.googlemail.com");
			email.setSmtpPort(465);
			//email.setAuthenticator(new DefaultAuthenticator("username", "password"));
			email.setAuthenticator(new DefaultAuthenticator("fouomenedaniel@gmail.com", "motdepasse"));
			email.setSSLOnConnect(true);
			email.setFrom("fouomenedaniel@gmail.com");
			email.setSubject("Alert PeakForecast");
			email.setMsg(message);
			String listtabmail[]= listEmails.split(" ");
			for (int i = 0; i < listtabmail.length; i++) {
				
				email.addTo(listtabmail[i]);
		   
			 }			
			email.send();	
			System.out.println("Message Email envoyé !!!");
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
