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
import org.osoa.sca.annotations.Reference;
import org.ow2.frascati.akka.fabric.peakforecast.api.ShellService;
import org.ow2.frascati.akka.fabric.peakforecast.api.SoftwareService;


/** The software service implementation. */
public class SoftwareFrascati
  implements SoftwareService
{    
 
	/**
	 * 
	 */
	// le fichier contenant les scripts Fabric du logiciel 
	@Property private String pathFabFile ="/home/daniel/scriptFabric/frascati/fabfile.py"; 
   
	 // SCA reference 
    @Reference protected ShellService sh;
    
    /** Default constructor. */
    public SoftwareFrascati() {
        System.out.println("Component SoftwareFrascati created.");
    }
    

    /** SoftwareServices implementations. */

    public final void install(String host, String user, String password) {
					  		   
    	String instruction ="fab "+"-f "+pathFabFile+" install:namehost="+host+",username="+user+",passworduser="+password;
		 sh.system(instruction);
		
	}

    public final void configuration(String host, String user, String password) {
		 System.out.println("Methode Configuration of software Frascati");
		
	}

    public final  void start(String host, String user, String password) {
    	 System.out.println("Methode Start of software Frascati");
		
	}

    public final void test(String host, String user, String password) {
    	 System.out.println("Methode test of software Frascati");			
	}

    public final  void stop(String host, String user, String password) {
    	System.out.println("Methode Stop of software Frascati");
		
	}

    public final  void unconfiguration(String host, String user, String password) {
    	 System.out.println("Methode UnConfiguration of software Frascati");
	}

    public final void uninstall(String host, String user, String password) {
    	 System.out.println("Methode UnInstall of software Frascati");
	}

}
