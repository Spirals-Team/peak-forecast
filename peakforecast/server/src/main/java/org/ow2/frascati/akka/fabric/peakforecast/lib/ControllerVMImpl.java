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
import java.io.*;
import java.nio.*;
import java.util.*;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.ow2.frascati.akka.fabric.peakforecast.api.ControllerVMService;
import org.ow2.frascati.akka.fabric.peakforecast.api.EventNotificationAlertService;
import org.ow2.frascati.akka.fabric.peakforecast.api.ShellService;
import org.ow2.frascati.akka.fabric.peakforecast.util.VirtuelImage;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import akka.util.Duration; //dans le jar util-core de AKKA



/** The ControllerVM Service  implementation. */
public class ControllerVMImpl implements ControllerVMService {

    
    @Property private String pathFabFile ="/home/daniel/scriptFabric/VM/fabfile.py"; // le fichier contenant les scripts Fabric de mnipulation d'une VM 
    @Property private ArrayList<VirtuelImage> imageVirtuels;
    
    // SCA reference
    
	@Reference protected ShellService sh;
	
	@Reference protected EventNotificationAlertService event;
		
	
	private ActorSystem systemVm ;
	private ActorRef listener;
	private ActorRef controllerVmSupervisor;

	
	
/** Default constructor. */
    public ControllerVMImpl() {
           System.out.println("Component ControllerVM created.");
              
	       this.imageVirtuels =new ArrayList<VirtuelImage>();
	       //this.imagevirtuels.add(new VirtuelImage("allegrograph","allegrograph","allegrograph"));
	       //this.imagevirtuels.add(new VirtuelImage("nodejs","nodejs","nodejs"));
	       // creation et démarrage du systeme AKKA
			systemVm = ActorSystem.create("SystemVM"); 
			//Creation de l'acteur Listener  qui permet d'affacher le resultat de la l'acteur ControllerVMSupervisor et la durée d'execution du service
			listener = systemVm.actorOf(new Props(Listener.class), "Listener");

				 
    }
    
 /** implementation du service Pause */   
	@SuppressWarnings("serial")
	public void pause(String listImages) {
		
	
		//Retrait des VM  dans le fichier de configuration du repartiteur (Nginx)
	    System.out.println("Connexion sur le serveur de repartition de charge (NGINX) pour retirer les VM dans son fichier de configuration et le signaler");
		
	    this.imageVirtuels.clear();
		String tab[]= listImages.split(" ");
		for (int i = 0; i < tab.length; i++) {
			this.imageVirtuels.add(new VirtuelImage(tab[i],tab[i],tab[i]));		
		    //PB gestion des acces concurents sur le fichier a revoir(solution passer plustot la liste des VM)
			String commande="fab -f /home/daniel/scriptFabric/repartiteur/fabfile.py retirerServeur:namehost=172.17.0.30,username=root,passworduser=root,serveur="
		    	+addressipport(tab[i]);
		    sh.system(commande);
			
		}
				
		// creation de l'actor ControllerVMSupervisor
		controllerVmSupervisor = systemVm.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
			 return new ControllerVMSupervisor(imageVirtuels,pathFabFile,sh,new Props(WorkerPauseVM.class),listener);
			}
		}), "ControllerVMSupervisor");		
		 
		// démarrer l'exécution
		controllerVmSupervisor.tell(new MessageExecuteService());	
		
	}    
	
	 /** implementation du service Create container */   
		@SuppressWarnings("serial")
		public void createContainer(String listImages) {

 			System.out.println("nom des container à créer "+listImages);
                 	
		this.imageVirtuels.clear();
					String tab[]= listImages.split(" ");
					for (int i = 0; i < tab.length; i++) {
						this.imageVirtuels.add(new VirtuelImage(tab[i],tab[i],tab[i]));	
					}
									
			// creation de l'actor ControllerVMSupervisor
			controllerVmSupervisor = systemVm.actorOf(new Props(new UntypedActorFactory() {
				public UntypedActor create() {
				 return new ControllerVMSupervisor(imageVirtuels,pathFabFile,sh,new Props(WorkerCreateContainer.class),listener);
				}
			}), "ControllerVMSupervisor");		
			 
			// démarrer l'exécution
			controllerVmSupervisor.tell(new MessageExecuteService());	
			
		}   
		
		 /** implementation du service Create container */   
			@SuppressWarnings("serial")
			public void startContainer(String listImages) {
			this.imageVirtuels.clear();
					String tab[]= listImages.split(" ");
					for (int i = 0; i < tab.length; i++) {
						this.imageVirtuels.add(new VirtuelImage(tab[i],tab[i],tab[i]));	
					}
							
				// creation de l'actor ControllerVMSupervisor
				controllerVmSupervisor = systemVm.actorOf(new Props(new UntypedActorFactory() {
					public UntypedActor create() {
					 return new ControllerVMSupervisor(imageVirtuels,pathFabFile,sh,new Props(WorkerStartContainer.class),listener);
					}
				}), "ControllerVMSupervisor");		
				 
				// démarrer l'exécution
				controllerVmSupervisor.tell(new MessageExecuteService());	
				
			} 

			
			 /** implementation du service Create container */   
				@SuppressWarnings("serial")
				public void stopContainer(String listImages) {
								
			this.imageVirtuels.clear();
					String tab[]= listImages.split(" ");
					for (int i = 0; i < tab.length; i++) {
						this.imageVirtuels.add(new VirtuelImage(tab[i],tab[i],tab[i]));	
					}
					// creation de l'actor ControllerVMSupervisor
					controllerVmSupervisor = systemVm.actorOf(new Props(new UntypedActorFactory() {
						public UntypedActor create() {
						 return new ControllerVMSupervisor(imageVirtuels,pathFabFile,sh,new Props(WorkerStopContainer.class),listener);
						}
					}), "ControllerVMSupervisor");		
					 
					// démarrer l'exécution
					controllerVmSupervisor.tell(new MessageExecuteService());	
					
				} 

	//une methode util
	public String addressipport(String nomImage) {		
		
		if(nomImage.equals("apache2"))
        {
			return "172.17.0.26:80";
        }
        else if(nomImage.equals("apache3"))
        {
        	return "172.17.0.27:80";
        }
        else
        {
        	return "172.17.0.25:80";
        }
	}

/** Définition des messages que nous voulons voir circulés dans le systeme AKKA*/
	
	//Message MessageExecuteService envoyé par le composant ControllerSoftware à l'acteur ControllerVMSupervisor  pour lancer l'exécution du service
	static class MessageExecuteService {
	}
	
	// Message MessageWork envoyé par l'acteur ControllerSoftwareSupervisorà l'un de ses enfants acteurs Worker, 
	static class MessageWork {
		private final VirtuelImage image;
		private final String pathFabFile;
		private final ShellService sh;
		 
		public MessageWork(VirtuelImage image,String pathFabFile,ShellService shell) {
			this.image = image;
			this.pathFabFile=pathFabFile;
			this.sh=shell;
		}
		public VirtuelImage getImage() {
			return image;
		}
		public String getPathFabfile() {
			return pathFabFile;
		}
		public ShellService getSh() {
			return sh;
		}		 		
	}

	// Message MessageResultPause envoyé par un enfant acteur WorkerStarter à son parent l'acteur ControllerVMSupervisor  
	// contenant le result de son travaille
	
	static class MessageResultPause {
		private final String result;

		public MessageResultPause(String result) {
			this.result = result;
		}

		public String getResult() {
			return result;
		}
		 		
	}

	// Message MessageResultCreateContainer envoyé par un enfant acteur WorkerStarter à son parent l'acteur ControllerVMSupervisor  
	// contenant le result de son travaille
	
	static class MessageResultCreateContainer {
		private final String result;

		public MessageResultCreateContainer(String result) {
			this.result = result;
		}

		public String getResult() {
			return result;
		}
		 		
	}

	// Message MessageResultStartContainer envoyé par un enfant acteur WorkerStarter à son parent l'acteur ControllerVMSupervisor  
	// contenant le result de son travaille
	
	static class MessageResultStartContainer {
		private final String result;

		public MessageResultStartContainer(String result) {
			this.result = result;
		}

		public String getResult() {
			return result;
		}
		 		
	}

	// Message MessageResultStopContainer envoyé par un enfant acteur WorkerStarter à son parent l'acteur ControllerVMSupervisor  
	// contenant le result de son travaille
	
	static class MessageResultStopContainer {
		private final String result;

		public MessageResultStopContainer(String result) {
			this.result = result;
		}

		public String getResult() {
			return result;
		}
		 		
	}
	// Message MessageResultWork envoyé par l'acteur ControllerVMSupervisor à l'acteur listener 
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

		public Duration getTimework() {
			return timeWork;
		}
		
	}

/** Définition des acteurs du systeme AKKA*/
	
	// L'acteur WorkerPauseVM  de type Pause qui a pour rôle de mettre en pause une image virtuelle donnée
	public static class WorkerPauseVM extends UntypedActor {
		 
		private  String startwork(VirtuelImage image,String pathFabFile,ShellService sh) {
			
			 DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			 System.out.println("Moi Pauseur "+getSelf().path().name()+"commence à "+ df.format(new java.util.Date()));
			 String instruction ="fab "+"-f "+pathFabFile+" pause:namehost=172.17.0.30,username=daniel,passworduser=celine,namevm="+image.getHostname();
			 sh.system(instruction);
			System.out.println("Moi Pauseur de nom : "+getSelf().path().name()+" a fini la mise en pause de la VM de nom "+image.getHostname()+" à "+df.format(new java.util.Date()));		
			//retournera le nom ou l'adresse IP de la VM qu'il a demarré,
			return image.getHostname();
		}	 
		public void onReceive(Object message) {
			if (message instanceof MessageWork) {
				MessageWork work = (MessageWork) message;
				String result = startwork(work.getImage(), work.getPathFabfile(), work.getSh());
				getSender().tell(new MessageResultPause(result), getSelf());
			} else {
				unhandled(message);
			}
		}
	}
	
	// L'acteur WorkerCreateContainer  de type create qui a pour rôle de create une container donné
		public static class WorkerCreateContainer extends UntypedActor {
			 
			private  String startwork(VirtuelImage image,String pathFabFile,ShellService sh) {
				
				 DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				 System.out.println("Moi Createur "+getSelf().path().name()+"commence à "+ df.format(new java.util.Date()));
				 
                               String instruction ="fab "+"-f "+pathFabFile+" createcontainer:namehost=172.17.0.30,username=daniel,passworduser=celine,namevm="+image.getHostname();                               
                                sh.system(instruction);
				System.out.println("Moi Createur de nom : "+getSelf().path().name()+" a fini la creation du container de nom "+image.getHostname()+" à "+df.format(new java.util.Date()));		
                                
				//retournera le nom ou l'adresse IP de qu'il a demarré,
				return image.getHostname();
			}	 
			public void onReceive(Object message) {
				if (message instanceof MessageWork) {
					MessageWork work = (MessageWork) message;
					String result = startwork(work.getImage(), work.getPathFabfile(), work.getSh());
					getSender().tell(new MessageResultCreateContainer(result), getSelf());
				} else {
					unhandled(message);
				}
			}
		}
	 
		// L'acteur WorkerStartContainer  de type start qui a pour rôle de démarrer un container donné
		public static class WorkerStartContainer extends UntypedActor {
			 
			private  String startwork(VirtuelImage image,String pathFabFile,ShellService sh) {
				
				 DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				 System.out.println("Moi Démarreur "+getSelf().path().name()+"commence à "+ df.format(new java.util.Date()));
				 String instruction ="fab "+"-f "+pathFabFile+" startcontainer:namehost=172.17.0.30,username=daniel,passworduser=celine,namevm="+image.getHostname();
				 sh.system(instruction);
				System.out.println("Moi démarreur de nom : "+getSelf().path().name()+" a fini le démarrage du container de nom "+image.getHostname()+" à "+df.format(new java.util.Date()));		
				//retournera le nom ou l'adresse IP de qu'il a demarré,
				return image.getHostname();
			}	 
			public void onReceive(Object message) {
				if (message instanceof MessageWork) {
					MessageWork work = (MessageWork) message;
					String result = startwork(work.getImage(), work.getPathFabfile(), work.getSh());
					getSender().tell(new MessageResultStartContainer(result), getSelf());
				} else {
					unhandled(message);
				}
			}
		}

		// L'acteur WorkerStopContainer  de type stop qui a pour rôle de stopper un container donné
		public static class WorkerStopContainer extends UntypedActor {
			 
			private  String startwork(VirtuelImage image,String pathFabFile,ShellService sh) {
				
				 DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				 System.out.println("Moi stoppeur "+getSelf().path().name()+"commence à "+ df.format(new java.util.Date()));
				 
                                // recuperation de l id du container
                                String reponseInspect=sh.commandAsString("fab "+"-f "+pathFabFile+" idcontainer:namehost=172.17.0.30,username=daniel,passworduser=celine,namevm="+image.getHostname()); 
                                String tab[]= reponseInspect.split("\"");
				for (int i = 0; i < tab.length; i++) {		
	                          System.out.println(i+"==="+tab[i]);		
				}		


                                String instruction ="fab "+"-f "+pathFabFile+" stopcontainer:namehost=172.17.0.30,username=daniel,passworduser=celine,namevm="+image.getHostname();
				sh.system(instruction);
				System.out.println("Moi stoppeur de nom : "+getSelf().path().name()+" a fini le stoppage du container de nom "+image.getHostname()+" à "+df.format(new java.util.Date()));		
				//retournera le nom ou l'adresse IP de qu'il a demarré,
				return image.getHostname();
			}	 
			public void onReceive(Object message) {
				if (message instanceof MessageWork) {
					MessageWork work = (MessageWork) message;
					String result = startwork(work.getImage(), work.getPathFabfile(), work.getSh());
					getSender().tell(new MessageResultStopContainer(result), getSelf());
				} else {
					unhandled(message);
				}
			}
		}

			
		
		
// l'acteur ControllerVMSupervisor
	public static class ControllerVMSupervisor extends UntypedActor {
		private  int nbrWorker; //nombre de travailleur qui équivaut au nombre de machine virtuelle
		
		private ArrayList<VirtuelImage> imageVirtuels;
		private ShellService sh;
		private String pathFabFile;
		 
		private ArrayList<String> resultWokers; // permet de recuperer le result de chaque travailleur
		private int nbreResult; // Nombre des results envoyés par les travailleurs
		private final long start = System.currentTimeMillis();
		 
		private final ActorRef listener; // un listener (actor) qui permet d'afficher le résultat de la resultwokers 
		private final ActorRef supervisor; // permettant de creer les travailleurs et de les gérer
		 
		public ControllerVMSupervisor(ArrayList<VirtuelImage> imageVituels,String pathFabFile,ShellService shell, Props typeWorker, ActorRef listener) {
			 
			this.nbrWorker = imageVituels.size();
			this.imageVirtuels = imageVituels;
			this.sh = shell;
			this.pathFabFile=pathFabFile;
			this.listener = listener;
			this.resultWokers = new ArrayList<String>(); 
			 
			supervisor = this.getContext().actorOf(typeWorker.withRouter(new RoundRobinRouter(this.nbrWorker)),
			"Supervisor");
		}
		 
		public void onReceive(Object message) {
			if (message instanceof MessageExecuteService) {


			        System.out.println("Generation des scripts de creation des containers");			
                                int portExpose = 5000;	
				for(VirtuelImage image : imageVirtuels){
                                        portExpose=portExpose+1;
		                        genereScriptFileContainer(image.getHostname(), portExpose); 	
				}
				
				for(VirtuelImage image : imageVirtuels){
					supervisor.tell(new MessageWork (image,pathFabFile,sh), getSelf());
				}
				
			} else if (message instanceof MessageResultPause) {
				    MessageResultPause result = (MessageResultPause) message;
				    System.out.println("<Actor ControllerVMSupervisor>: Reception du message d'un Pauseur, confirmant la mise en pause du container");										    
				    resultWokers.add(result.getResult());
					nbreResult+= 1;
				if (nbreResult == nbrWorker) {
					// ie que chaque Worker à finit son travail
					// alors  le resultat du ControllerVMSupervisor et la durée de l'excution du service est envoyer au Listener 
					Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
					listener.tell(new MessageResultWork(resultWokers, duration), getSelf());
					// Arret de la ControllerVMSupervisor et tous les Worker que son supervisor gère										
					getContext().stop(getSelf());
			     }
			} else if (message instanceof MessageResultCreateContainer) {
				    MessageResultCreateContainer result = (MessageResultCreateContainer) message;
				    System.out.println("<Actor ControllerVMSupervisor>: Reception du message d'un createur, confirmant la  creation du container");										    
				    resultWokers.add(result.getResult());
					nbreResult+= 1;
				if (nbreResult == nbrWorker) {
					// ie que chaque Worker à finit son travail
					// alors  le resultat du ControllerVMSupervisor et la durée de l'excution du service est envoyer au Listener 
					Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
					listener.tell(new MessageResultWork(resultWokers, duration), getSelf());
			         System.out.println("<Actor ControllerVMSupervisor>: Demarrage des containers et enregistrement chez le Loadbalancer");       
  
				for(VirtuelImage image : imageVirtuels){
				 System.out.println("<Actor ControllerVMSupervisor>: Demarrage du container "+image.getHostname());			
				 String instruction ="fab "+"-f "+pathFabFile+" startcontainer:namehost=172.17.0.30,username=daniel,passworduser=celine,namevm="+image.getHostname();
				 sh.system(instruction);
		                 			
				//ajout du nouveau container prête dans le fichier de configuration du repartiteur (Nginx)
			
				System.out.println("<Actor ControllerVMSupervisor>: Connexion sur le serveur de repartition de charge (NGINX) pour ajouter l'adresse ip du container dans son fichier de configuration et le signaler");
				    String commande="fab -f /home/daniel/scriptFabric/repartiteur/fabfile.py ajouterServeur:namehost=172.17.0.30,username=daniel,passworduser=celine,serveur="
				    	+addressipport(image.getHostname());
				    sh.system(commande);

				}




					// Arret de la ControllerVMSupervisor et tous les Worker que son supervisor gère										
					getContext().stop(getSelf());
                                        


			     }
			} else if (message instanceof MessageResultStartContainer) {
				    MessageResultStartContainer result = (MessageResultStartContainer) message;
				    System.out.println("<Actor ControllerVMSupervisor>: Reception du message d'un demarreur, confirmant le démarrage d'un container");						
//ajout du nouveau container prête dans le fichier de configuration du repartiteur (Nginx)
			
				    System.out.println("<Actor ControllerVMSupervisor>: Connexion sur le serveur de repartition de charge (NGINX) pour ajouter l'adresse ip du container dans son fichier de configuration et le signaler");
				    String commande="fab -f /home/daniel/scriptFabric/repartiteur/fabfile.py ajouterServeur:namehost=172.17.0.30,username=daniel,passworduser=celine,serveur="
				    	+addressipport(result.getResult());
				    sh.system(commande);									    
				    resultWokers.add(result.getResult());
					nbreResult+= 1;
				if (nbreResult == nbrWorker) {
					// ie que chaque Worker à finit son travail
					// alors  le resultat du ControllerVMSupervisor et la durée de l'excution du service est envoyer au Listener 
					Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
					listener.tell(new MessageResultWork(resultWokers, duration), getSelf());
					// Arret de la ControllerVMSupervisor et tous les Worker que son supervisor gère										
					getContext().stop(getSelf());
			     }
			} else if (message instanceof MessageResultStopContainer) {
				    MessageResultStopContainer result = (MessageResultStopContainer) message;
				    System.out.println("<Actor ControllerVMSupervisor>: Reception du message d'un stoppeur, confirmant l'arret d'un container");						
//retrait du container dans le fichier de configuration du repartiteur (Nginx)
			
				    System.out.println("<Actor ControllerVMSupervisor>: Connexion sur le serveur de repartition de charge (NGINX) pour retirer l'image virtuelle dans son fichier de configuration et le signaler");
				    String commande="fab -f /home/daniel/scriptFabric/repartiteur/fabfile.py retirerServeur:namehost=172.17.0.30,username=daniel,passworduser=celine,serveur="
				    	+addressipport(result.getResult());
				    sh.system(commande);									    
				    resultWokers.add(result.getResult());
					nbreResult+= 1;
				if (nbreResult == nbrWorker) {
					// ie que chaque Worker à finit son travail
					// alors  le resultat du ControllerVMSupervisor et la durée de l'excution du service est envoyer au Listener 
					Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
					listener.tell(new MessageResultWork(resultWokers, duration), getSelf());
					// Arret de la ControllerVMSupervisor et tous les Worker que son supervisor gère										
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
					return "172.17.0.26:80";
			}
			else if(nomImage.equals("apache3"))
			{
				return "172.17.0.27:80";
			}
			else
			{
				return "172.17.0.25:80";
			}
		}

		public void copyFile(String source, String dest) {		
		
			java.nio.channels.FileChannel in = null; // canal d'entrée
			java.nio.channels.FileChannel out = null; // canal de sortie
			 
			try {
			  // Init
			  in = new FileInputStream(source).getChannel();
			  out = new FileOutputStream(dest).getChannel();
			 
			  // Copie depuis le in vers le out
			  in.transferTo(0, in.size(), out);
			} catch (Exception e) {
			  e.printStackTrace(); // n'importe quelle exception
			} finally { // finalement on ferme
			  if(in != null) {
			  	try {
				  in.close();
				} catch (IOException e) {}
			  }
			  if(out != null) {
			  	try {
				  out.close();
				} catch (IOException e) {}
			  }
			}
                }

		public void genereScriptFileContainer(String containerName, int portExpose)            {		
                           try {
					   
                                                File file = new File("/home/daniel/"+containerName);
						Thread.sleep(300);
                                                if (file.mkdirs()) {

						  System.out.println("Creation du fichier clientsonde.conf");
	                                          PrintWriter clientsondeconffile = new PrintWriter(new FileWriter("/home/daniel/"+containerName+"/clientsonde.conf"));
						  clientsondeconffile.println("[program:clientsonde]");
                                                  clientsondeconffile.println("command=python /clientsonde.py "+portExpose);
	                                          clientsondeconffile.close();

                                                  System.out.println("Creation du fichier Dockerfile");
                                                  PrintWriter Dockerfile = new PrintWriter(new FileWriter("/home/daniel/"+containerName+"/Dockerfile"));
						  Dockerfile.println("FROM fouomene/ApacheZeromq");
						  Dockerfile.println("EXPOSE 80");     
						  Dockerfile.println("EXPOSE 22");
                                                  Dockerfile.println("EXPOSE "+portExpose);
						  Dockerfile.println("ADD index.html /var/www/mediawiki/index.html");
						  Dockerfile.println("ADD LocalSettings.php /var/www/mediawiki/LocalSettings.php");     
						  Dockerfile.println("RUN chmod 777 /var/www/mediawiki/LocalSettings.php");
                                                  
                                                  Dockerfile.println("ADD clientsonde.py clientsonde.py");
                                                  Dockerfile.println("ADD clientsonde.conf /etc/supervisor/conf.d/clientsonde.conf");
                                                  Dockerfile.println("ADD start start");
						  Dockerfile.println("RUN chmod 777 start");     
						  Dockerfile.println("CMD [\"sh\" ,\"start\"]");
	                                          Dockerfile.close();

                                                  System.out.println("Copie des fichiers Dockerfile clientsonde.py, index.html, LocalSettings.php, start");
                                                  copyFile("/home/daniel/containerapache/start","/home/daniel/"+containerName+"/start");
                                                  copyFile("/home/daniel/containerapache/clientsonde.py","/home/daniel/"+containerName+"/clientsonde.py");
                                                  copyFile("/home/daniel/containerapache/index.html","/home/daniel/"+containerName+"/index.html");
                                                  copyFile("/home/daniel/containerapache/LocalSettings.php","/home/daniel/"+containerName+"/LocalSettings.php");


			      

						} else {
						System.out.println("Echec de creation de dossiers : " + file.getPath());
						}

                                        } catch (Exception e) {
					  e.printStackTrace();
					}            
		
			}
	
	}		
		
		 
// l'acteur Listener  qui permet d'affacher le result de la l'acteur ControllerVMSupervisor et la durée d'execution du service
	public static class Listener extends UntypedActor { //qui a pour role d'afficher le result de la ControllerVMSupervisor
		public void onReceive(Object message) {
			if (message instanceof MessageResultWork) {
				MessageResultWork result = (MessageResultWork) message;
				
				for(String info : result.getResults()){
				  System.out.println(info);	
				}
				
				System.out.println(String.format("Durée de l'operation : \t%s",result.getTimework()));
			    getContext().system().shutdown(); //arret du systemvm AKKA
			} else {
				unhandled(message);
			}
		}
	}



	
	

}
