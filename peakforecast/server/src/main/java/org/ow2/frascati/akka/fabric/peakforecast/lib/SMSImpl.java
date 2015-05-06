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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.osoa.sca.annotations.Property;
import org.ow2.frascati.akka.fabric.peakforecast.api.SMSService;


/** The SMS service implementation. */
public class SMSImpl
  implements SMSService
{

	//liste des numeros téléphones separés par le caractère espace
	@Property private String listNumerosTelephone="23774099619" ; 
	/** Default constructor. */
  public SMSImpl() {
	  System.out.println("Component SMS created.");
	}
	



	@Override
	public void send(String message) {
		
	 //info de l'utilisateur sur la plateform envoie sms Ex: (www.allsms.com)
	 String username = "fouomene" ;
	 String password  = "motdepasse";
	 String listNumeros[]= listNumerosTelephone.split(" ");
	 for (int i = 0; i < listNumeros.length; i++) {
		
		String urlRequette = "http://api.msinnovations.com/amsmodule.sendsms.v6.php?clientcode="+username+"&passcode="+password+"&XMLFlow=%3CDATA%3E%3CCLIENT%3Efouomene%3C%2FCLIENT%3E%3CMESSAGE%3E"+formatMessage(message)+"%3C%2FMESSAGE%3E%3CSMS%3E%3CMOBILEPHONE%3E"+listNumeros[i]+"%3C%2FMOBILEPHONE%3E%3C%2FSMS%3E%3C%2FDATA%3E";
 	    try {
			String reponse = get(urlRequette);
			System.out.println(reponse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   
	 }
	
	}

	
	public static String get(String url) throws IOException{
		 
		String source ="";
		URL urlPlateform = new URL(url);
		URLConnection conPlateform = urlPlateform.openConnection();
		BufferedReader in = new BufferedReader(
		new InputStreamReader(
		conPlateform.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
		source +=inputLine;
		in.close();
		return source;
	}	
 
  public String formatMessage(String message){
	  
	  String mess=message;
	  /**	  
	  espace     +
	  '        %27
	  é       %C3%A9
	  è       %C3%A8
	  ê      %C3%AA
	  à      %C3%A0  
	  &     %26
	  ù      %C3%B9
	  ô      %C3%B4
	  ;       %3B
	  ,       %2C
	  !       %21
	  :        %3A	  
	  */
	  mess.replace(" ", "+");
	  mess.replace("'", "%27");
	  mess.replace("é", "%C3%A9");
	  mess.replace("è", "%C3%A8");
	  mess.replace("ê", "%C3%AA");
	  mess.replace("à", "%C3%A0");
	  mess.replace("&", "%26");
	  mess.replace("ù", "%C3%B9");
	  mess.replace("ô", "%C3%B4");
	  mess.replace(";", "%3B");
	  mess.replace(",", "%2C");
	  mess.replace("!", "%21");
	  mess.replace(":", "%3A");
	  
	  return mess;
	  
	  
	  
  }
}
