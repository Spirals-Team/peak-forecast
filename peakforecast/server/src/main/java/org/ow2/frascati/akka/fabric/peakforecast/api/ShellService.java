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
 *         
 */
package org.ow2.frascati.akka.fabric.peakforecast.api;

import java.io.IOException;
import java.text.MessageFormat;

import org.osoa.sca.annotations.Service;
import org.ow2.frascati.akka.fabric.peakforecast.util.ProcessConsumer;
import org.ow2.frascati.akka.fabric.peakforecast.util.Shell;

/**
 * A basic service used 
 */
@Service
public interface ShellService {
	
	
	/**
     * Execute la ligne de commande dans le shell systeme,
     * en affichant les donnees dans les flux des sorties de l'application.
     * @param commandLine La ligne de commande  executer.
     * @return Le code de retour de la ligne de commande.
     * @throws IOException Erreur d'execution de la commande.
     * @see System#out
     * @see System#err
     */
     int system(String commandLine);
	
    /**
     * Identique {@link Shell#system(String)}, si ce n'est que la ligne
     * de commande est d'abord formatte en utilisant les parametres via
	 * la classe MessageFormat.<br><br>
     * @param commandLine La ligne de commande executer.
     * @return Le code de retour de la ligne de commande.
     * @throws IOException Erreur d'execution de la commande.
     * @see Shell#system(String)
     */
     int system(String commandLine, Object...arguments);
    
	/**
	 * Cree un processus representant la commande du shell et l'associe
	 * une instance de ProcessConsumer.<br/>
	 * Cette methode instancie un nouveau shell en tant que processus afin
	 * d'executer la ligne de commande en parametre. Le processus ne sera
	 * reellement demarre que lors de l'appel d'une des methodes
	 * <code>consume()</code> de la classe ProcessConsumer.<br>
	 * <br>
	 * La commande passe en parametre accepte toutes les specificitees du
	 * shell systeme (redirections, pipes, structures conditionnelles, etc.).
	 * @param commandLine La ligne de commande executer par le shell .
	 * @return Une chaine de caractere contenant la sortie standard du process.
	 * @throws IOException 
	 * @see ProcessConsumer#consume()
	 */
     String commandAsString(String commandLine);
    
    /**
     * Identique  {@link Shell#command(String)}, si ce n'est que la ligne
     * de commande est d'abord formatte en utilisant les parametres via
	 * la classe MessageFormat.<br><br>
	 * Cete methode est equivalent  la ligne suivante :<br>
	 * <pre><code>command(MessageFormat.format(commandLine, arguments))</code></pre>
     * @param commandLine La ligne de commande formater.
     * @param arguments Les parametres du formattage.
     * @return Une chaine de caractere contenant la sortie standard du process.
     * @throws IOException 
     * @see MessageFormat#format(String, Object...)
     * @see Shell#command(String)
     */
     String commandAsString(String commandLine, Object...arguments)  ;
    
    /**
     * Cree un processus standard et l'associee une instance de ProcessConsumer.
     * Le premier parametre doit obligatoirement correspondre un nom de programme
     * existant, de la meme maniere qu'avec l'utilisation de {@linkplain Runtime#exec(String[])}.
     * <br><br>
     * Contrairement {@link Shell#command(String)}, cette methode n'instancie
     * pas le processus du shell mais directement le programme passe en
     * premier parametre.  Le processus ne sera reellement demarre que
     * lors de l'appel d'une des methodes <code>consume()</code> de la
     * classe ProcessConsumer.<br>
     * @param args Les parametres de la commandes a executer.
     * @return Une chaine de caractere contenant la sortie standard du process.
     * @throws IOException 
     */
     String execAsString(String...args);
    
	
}
