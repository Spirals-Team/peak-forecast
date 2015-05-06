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
import org.ow2.frascati.akka.fabric.peakforecast.api.ControllerKubectlService;
import org.ow2.frascati.akka.fabric.peakforecast.api.EventNotificationAlertService;
import org.ow2.frascati.akka.fabric.peakforecast.api.ShellService;


/** The EventNotificationAlert service implementation. */
public class ControllerKubectlImpl
  implements ControllerKubectlService
{


    // SCA property    
    
    @Property private String pathFabFile ="/home/daniel/scriptFabric/kubernetes/fabfile.py"; // le fichier contenant les scripts Fabric de mnipulation d'un cluster Kubernetes 
    
    // SCA reference
    
	@Reference protected ShellService sh;
	
	@Reference protected EventNotificationAlertService event;	
	
	/** Default constructor. */
  public ControllerKubectlImpl() {
	  System.out.println("Component ControllerKubectlImp created.");
	}
	

  /** implementation du service kubectl Resize replication controller */  
	@Override
	public void kubectlResize(String nameReplicationController, int newSize) {
		
		System.out.println(" Resize Replication controller name "+nameReplicationController+" with new size "+newSize);
		
		String commande="fab -f "+pathFabFile+" kubectlresize:namehost=192.168.1.2,username=daniel,passworduser=celine,namereplicationcontroller="
				+nameReplicationController+",newsize="+newSize;
		    sh.system(commande);
		    
		   // event.send("Resize Replication controller name "+nameReplicationController+" with new size "+newSize);
		    		
		
	}

}
