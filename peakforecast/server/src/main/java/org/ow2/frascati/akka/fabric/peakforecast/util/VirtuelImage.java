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
 *         
 */

package org.ow2.frascati.akka.fabric.peakforecast.util;


public  class VirtuelImage {
		
	private  String hostname;
	private  String username;
	private  String password;
	
	public VirtuelImage() {
		
	}
	public VirtuelImage(String hostname, String username, String password) {
		
		this.hostname=hostname;
		this.username=username;
		this.password=password;
		
	}
	public String getHostname() {
		return hostname;
	}
	public  String getUsername() {
		return username;
	}
	public  String getPassword() {
		return password;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public  void setPassword(String password) {
		this.password = password;
	}
	

}
