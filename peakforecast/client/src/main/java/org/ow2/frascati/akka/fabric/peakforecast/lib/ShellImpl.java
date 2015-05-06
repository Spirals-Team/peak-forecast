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
 *
	Contact: contact-adam@lifl.fr
	Author: Daniel Fouomene
	Contributor(s): Romain Rouvoy, Lionel Seinturier         
 */
package org.ow2.frascati.akka.fabric.peakforecast.lib;

import java.io.IOException;

import org.ow2.frascati.akka.fabric.peakforecast.api.ShellService;
import org.ow2.frascati.akka.fabric.peakforecast.util.Shell;


/** The shell service implementation. */
public class ShellImpl
  implements ShellService
{

	
	
	/** Default constructor. */
  public ShellImpl() {
	  System.out.println("Component Shell created.");
	}
	
	
	@SuppressWarnings("static-access")
	public int system(String commandLine) {
		
		try {
			
			return new Shell().system(commandLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("static-access")
	public int system(String commandLine, Object... arguments) {
		
		try {
			return new Shell().system(commandLine, arguments);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	 return 0;
	}

	public String commandAsString(String commandLine) {
		try {
			return new Shell().command(commandLine).consumeAsString();
		} catch (IOException e) {		
			e.printStackTrace();
		}
	 return null;
	}

	public String commandAsString(String commandLine, Object... arguments) {
		try {
			return new Shell().command(commandLine, arguments).consumeAsString();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	 return null;
	}

	public String execAsString(String... args) {
		try {
			return new Shell().exec(args).consumeAsString();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	 return null;
	}

 

}
