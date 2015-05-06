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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.ow2.frascati.akka.fabric.peakforecast.api.ControllerSoftwareService;
import org.ow2.frascati.akka.fabric.peakforecast.api.EventNotificationAlertService;
import org.ow2.frascati.akka.fabric.peakforecast.api.ShellService;
import org.ow2.frascati.akka.fabric.peakforecast.api.SoftwareService;
import org.ow2.frascati.akka.fabric.peakforecast.util.VirtuelImage;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
//dans le jar util-core de AKKA


/** The ControllerSoftware Service  implementation. */
public class ControllerSoftwareImpl implements ControllerSoftwareService {



     private  SoftwareService logiciel;
    
    @Property private ArrayList<VirtuelImage> imageVirtuels;
    
    // SCA reference
    
	@Reference(required = true)
    private SoftwareService logicielFrascati;
	
	@Reference(required = true)
    private SoftwareService logicielNodejs;
	
	@Reference protected ShellService sh;
	
	@Reference protected EventNotificationAlertService event;
		
	
	private ActorSystem systemSoftware ;
	private ActorRef listener;
	private ActorRef controllerSoftwareSupervisor;
	
	
/** Default constructor. */
    public ControllerSoftwareImpl() {
           System.out.println("Component ControllerSoftware created.");
              
	       this.imageVirtuels =new ArrayList<VirtuelImage>();
	       //this.imagevirtuels.add(new VirtuelImage("allegrograph","allegrograph","allegrograph"));
	       //this.imagevirtuels.add(new VirtuelImage("nodejs","nodejs","nodejs"));
	       // creation et démarrage du systeme AKKA
			systemSoftware = ActorSystem.create("SystemSofware"); 
			//Creation de l'acteur Listener  qui permet d'affacher le resultat de la l'acteur ControllerSoftwareSupervisor et la durée d'execution du service
			listener = systemSoftware.actorOf(new Props(Listener.class), "Listener");
				 
    }
    
 /** implementation du service Install */   
	@SuppressWarnings("serial")
	public void install(String nomLogiciel, String listImages) {
		
		if (nomLogiciel.equals("frascati")) this.logiciel= this.logicielFrascati;
		if (nomLogiciel.equals("nodejs")) this.logiciel= this.logicielNodejs;
		
		this.imageVirtuels.clear();
		
		String tab[]= listImages.split(" ");
		for (int i = 0; i < tab.length; i++) {
			this.imageVirtuels.add(new VirtuelImage(tab[i],tab[i],tab[i]));
		}
		
		// creation de l'actor ControllerSoftwareSupervisor
		controllerSoftwareSupervisor = systemSoftware.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
			 return new ControllerSoftwareSupervisor(imageVirtuels,logiciel,sh,new Props(WorkerInstall.class),listener);
			}
		}), "ControllerVMSupervisor");		
		 
		// demarrer le déploiement
		controllerSoftwareSupervisor.tell(new MessageExecuteService());	
		
	}    
	

/** Définition des messages que nous voulons voir circulés dans le systeme AKKA*/
	
	//Message MessageExecuteService envoyé par le composant ControllerSoftware à l'acteur ControllerSoftwareSupervisor  pour lancer l'exécution du service
	static class MessageExecuteService {
	}
	
	// Message MessageWork envoyé par l'acteur ControllerSoftwareSupervisorà l'un de ses enfants acteurs Worker, 
	static class MessageWork {
		private final VirtuelImage image;
		private final SoftwareService software;
		 
		public MessageWork(VirtuelImage image, SoftwareService software) {
			this.image = image;
			this.software= software;
		}

		public VirtuelImage getImage() {
			return image;
		}

		public SoftwareService getSoftware() {
			return software;
		}
		 		
	}

	// Message MessageResultPause envoyé par un enfant acteur WorkerIntall à son parent l'acteur ControllerSoftwareSupervisor  
	// contenant le result de son travaille
	
	static class MessageResultIntaller {
		private final String result;

		public MessageResultIntaller(String result) {
			this.result = result;
		}

		public String getResult() {
			return result;
		}
		 		
	}

	// Message MessageResultWork envoyé par l'acteur ControllerSoftwareSupervisor à l'acteur listener 
	// pour l'affachage des results de ses enfants Worker et la durée du déploiement  
	
	static class MessageResultWork {
		private final ArrayList<String> results;
		private final Duration timeWork;
		 
		public MessageResultWork( ArrayList<String> results, Duration time) {
			this.results = results;
			this.timeWork =time;
		}

		public ArrayList<String> getResults() {
			return results;
		}

		public Duration getTimeWork() {
			return timeWork;
		}
		
	}

/** Définition des acteurs du systeme AKKA*/
	
// L'acteur WorkerStarter  de type Install qui a pour rôle d'installer un software donnné dans une image virtuelle donnée
	public static class WorkerInstall extends UntypedActor {
		 
		private  String startwork(VirtuelImage image, SoftwareService logiciel) {
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			System.out.println("Moi installeur "+getSelf().path().name()+"commence à "+ df.format(new java.util.Date()));
			logiciel.install(image.getHostname(), image.getUsername(), image.getPassword());
			System.out.println("Moi installeur de nom : "+getSelf().path().name()+" a fini l installation du logciel sur l image virtuel de nom "+image.getHostname()+" à "+df.format(new java.util.Date()));		
			//retournera le nom ou l'adresse IP de l'image  sur la quelle il a installé le software, qui permet de l'ajouter dans le fichier de configuration du loadbalancer
			return image.getHostname();
		}	 
		public void onReceive(Object message) {
			if (message instanceof MessageWork) {
				MessageWork installe = (MessageWork) message;
				String result = startwork(installe.getImage(), installe.getSoftware());
				getSender().tell(new MessageResultIntaller(result), getSelf());
			} else {
				unhandled(message);
			}
		}
	}
	 
// l'acteur ControllerSoftwareSupervisor
	public static class ControllerSoftwareSupervisor extends UntypedActor {
		private  int nbrWorker; //nombre de travailleur qui équivaut au nombre de machine virtuelle
		
		private ArrayList<VirtuelImage> imageVirtuels;
		private SoftwareService software;
		private ShellService sh;
		 
		private ArrayList<String> resultWokers; // permet de recuperer le result de chaque travailleur
		private int nbrResult; // Nombre des results envoyés par les travailleurs
		private final long start = System.currentTimeMillis();
		 
		private final ActorRef listener; // un listener (actor) qui permet d'afficher le résultat de la resultwokers 
		private final ActorRef supervisor; // permettant de creer les travailleurs et de les gérer
		 
		public ControllerSoftwareSupervisor(ArrayList<VirtuelImage> imageVirtuels, SoftwareService software, ShellService shell, Props typeWorker, ActorRef listener) {
			 
			this.nbrWorker = imageVirtuels.size();
			this.imageVirtuels = imageVirtuels;
			this.software = software;
			this.sh = shell;
			this.listener = listener;
			this.resultWokers = new ArrayList<String>(); 
			 
			supervisor = this.getContext().actorOf(typeWorker.withRouter(new RoundRobinRouter(this.nbrWorker)),
			"Supervisor");
		}
		 
		public void onReceive(Object message) {
			if (message instanceof MessageExecuteService) {
				
				for(VirtuelImage image : imageVirtuels){
					supervisor.tell(new MessageWork (image, software), getSelf());
				}
				
			} else if (message instanceof MessageResultIntaller) {
				    MessageResultIntaller result = (MessageResultIntaller) message;
                    //ajout de la nouvelle image virtuelle prête dans le fichier de configuration du repartiteur (Nginx)
				    System.out.println("<Actor ControllerSoftwareSupervisor>: Reception du message de l'installeur, confirmant la fin de l'installation du software(serveur) sur l'image virtuelle");
				    System.out.println("<Actor ControllerSoftwareSupervisor>: Connexion sur le serveur de repartition de charge (NGINX) pour ajouter l'image virtuelle dans son fichier de configuration et le signaler");
				    String commande="fab -f /home/daniel/scriptFabric/repartiteur/fabfile.py ajouterServeur:namehost=nginx,username=root,passworduser=root,serveur="
				    	+addressipport(result.getResult());
				    sh.system(commande);										    
				    resultWokers.add(result.getResult());
					nbrResult+= 1;
				if (nbrResult == nbrWorker) {
					// ie que chaque Worker à finit son travail
					// alors  le resultat du ControllerSoftwareSupervisor et la durée de l'excution du service est envoyer au Listener 
					Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
					listener.tell(new MessageResultWork(resultWokers, duration), getSelf());
					// Arret de la ControllerSoftwareSupervisor et tous les Worker que son supervisor gère										
					getContext().stop(getSelf());
			     }
			} else {
				unhandled(message);
			}
		}
		//une methode util
		public String addressipport(String nomImage) {		
			if(nomImage.equals("apache2"))
	        {
				return "172.17.0.21:80";
	        }
	        else if(nomImage.equals("apache3"))
	        {
	        	return "172.17.0.25:80";
	        }
	        else
	        {
	        	return "172.17.0.20:80";
	        }
		}		
		
	}
		 
// l'acteur Listener  qui permet d'affacher le result de la l'acteur ControllerSoftwareSupervisor et la durée d'execution du service
	public static class Listener extends UntypedActor { //qui a pour role d'afficher le result de la ControllerVMSupervisor
		public void onReceive(Object message) {
			if (message instanceof MessageResultWork) {
				MessageResultWork result = (MessageResultWork) message;
				
				for(String info : result.getResults()){
				  System.out.println(info);	
				}
				
				System.out.println(String.format("Durée de l'operation : \t%s",result.getTimeWork()));
			    getContext().system().shutdown(); //arret du systemsoftware AKKA
			} else {
				unhandled(message);
			}
		}
	}



	
	

}
