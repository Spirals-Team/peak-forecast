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

import org.osoa.sca.annotations.Reference;
import org.ow2.frascati.akka.fabric.peakforecast.api.EmailService;
import org.ow2.frascati.akka.fabric.peakforecast.api.EventNotificationAlertService;
import org.ow2.frascati.akka.fabric.peakforecast.api.SMSService;
import org.ow2.frascati.akka.fabric.peakforecast.api.TwitterService;


/** The EventNotificationAlert service implementation. */
public class EventNotificationAlertImpl
  implements EventNotificationAlertService
{


    // SCA reference    
	@Reference protected TwitterService twitter;
	@Reference protected SMSService sms;
	@Reference protected EmailService email;	
	
	/** Default constructor. */
  public EventNotificationAlertImpl() {
	  System.out.println("Component EventNotificationAlert created.");
	}
	



	@Override
	public void send(String message) {
		twitter.updateStatus(message);
		sms.send(message);
		email.send(message);
		
	}

}
