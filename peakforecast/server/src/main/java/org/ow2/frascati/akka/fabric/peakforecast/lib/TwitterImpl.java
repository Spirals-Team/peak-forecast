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

import org.osoa.sca.annotations.Property;
import org.ow2.frascati.akka.fabric.peakforecast.api.TwitterService;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


/** The Twitter service implementation. */
public class TwitterImpl
  implements TwitterService
{

     // compte twitter principal
	@Property private String username="peakforecast" ; 
	@Property private String password="motdepasse" ; 
	   
	
	/** Default constructor. */
  public TwitterImpl() {
	  System.out.println("Component Twitter created.");
	}
	
	
	@Override
	public void updateStatus(String message) {
		try {
			
			ConfigurationBuilder cb = new ConfigurationBuilder();
			//info OAuth settings de l'application twitter tweetpeakforecast enregistré par l'utilisateur @peakforecast 
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey("0PqersXnGOn7AirB13jszw")
			  .setOAuthConsumerSecret("r3qyCuhmwRI90JY04kqcYtn5yEl6yaW1UoRhAY08Q")
			  .setOAuthAccessToken("1598158057-epoKcN3Lfz405zjNZQ2rl5cx74lthvBZ93cCsfj")
			  .setOAuthAccessTokenSecret("6YwDPdjS7OpqDHb6PsmW0beOjCgjITzDGYV89gtQU");
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();
			Status status = twitter.updateStatus(message);
			System.out.println("Successfully updated the status to [" + status.getText() + "].");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

 

}
