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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.ow2.frascati.akka.fabric.peakforecast.api.ControllerSoftwareServicePortType;
import org.ow2.frascati.akka.fabric.peakforecast.api.ControllerVMServicePortType;
import org.ow2.frascati.akka.fabric.peakforecast.api.ShellService;
import org.ow2.frascati.akka.fabric.peakforecast.api.ControllerKubectlServicePortType;
import org.ow2.frascati.akka.fabric.peakforecast.api.EventNotificationAlertServicePortType;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.util.Duration;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;



public class Monitor implements Runnable {
	
     // SCA property    
    
     @Property private String nameReplicationController = "mediawiki-controller";
     
     // SCA reference
   			
	 private ControllerSoftwareServicePortType controllerSoftwareService;   
	 @Reference 
	  public final void setControllerSoftwareService(ControllerSoftwareServicePortType service)
	  {
	    this.controllerSoftwareService = service;
	  }
	 
	 private ControllerVMServicePortType controllerVmService;
	 @Reference 
	  public final void setControllerVMService(ControllerVMServicePortType service)
	  {
	    this.controllerVmService = service;
	  }
	 
	 private ControllerKubectlServicePortType controllerKubectlService;
	 @Reference 
	  public final void setControllerKubectlService(ControllerKubectlServicePortType service)
	  {
	    this.controllerKubectlService = service;
	  }

	 private EventNotificationAlertServicePortType eventNotificationAlertService;
	 @Reference 
	  public final void setEventNotificationAlertService(EventNotificationAlertServicePortType service)
	  {
	    this.eventNotificationAlertService = service;
	  }	 
	 
	 @Reference protected ShellService sh;
	 
	  private ActorSystem systemMonitor ;
	  private ActorRef collect; //retourne le nombre de requettes par intervalle de 30 secondes
	  private ActorRef analyse; //en cas de pic, retourne le nbre image virtuel pour absorber le pic
	  private ActorRef decide; //prend une décision et envoie l'action à exécuter à l'acteur Action ( EX Déclenche le déployement du logiciel sur les différentes VM)
	  private ActorRef action; // exécute l'action entreprise à après la décision
	  
	/** Default constructor. */
    public Monitor() {
        System.out.println("Component Monitor created.");

    }

    @Init
    public final void init()
    {
      System.out.println("Monitor initialized");

    }
    
    /** Run the Monitor. */
    public final void run() {
    	    	  	
		systemMonitor = ActorSystem.create("MonitorSystem"); 
		//creation de l'actor Action
		action = systemMonitor.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
			 return new Action(controllerSoftwareService,controllerVmService,controllerKubectlService,nameReplicationController);
			}
		}), "Actionneur");	
		//creation de l'actor Decide
		decide = systemMonitor.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
			 return new Decide(action,sh,eventNotificationAlertService);
			}
		}), "Decideur");	
		//creation l'actor Analyse
		analyse = systemMonitor.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
			 return new Analyse(decide,sh,eventNotificationAlertService);
			}
		}), "Analyseur");
		
		//creation de l'actor Collect des traces de la version Japonais de Wikipedia
//		collect = systemMonitor.actorOf(new Props(new UntypedActorFactory() {
//			public UntypedActor create() {
//			 return new CollectTraceWikipedia(analyse,sh);
//			}
//		}), "Collect");

		//creation de l'actor Collect en temps réel
		collect = systemMonitor.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
			 return new CollectRealTime(analyse,sh);
			}
		}), "Collect");
		
		
		  System.out.println("START");
		  //lecture du nombre de requettes tous les 30 secondes
		  Cancellable cancellable = systemMonitor.scheduler().schedule(Duration.Zero(),Duration.create(30, "seconds"),collect, new Interval(30));				  
		  try {
			  Thread.sleep(30000000);
			} catch (InterruptedException e) {
				System.out.println(e.getStackTrace());
				return;
			}
		 
		  cancellable.cancel();
		  System.out.println("END");   	
    }
 
    
/** Définition des messages que nous voulons voir circulés dans le systeme AKKA*/
	
	//Message Interval envoyé par le composant client Monitor à l'acteur collect  pour la lecture des traces par intervalle de 30 seconde par Ex
	static class Interval {
		private final int value; //en seconde
				
		public Interval(int val) {			
			this.value = val;
		}
		
		public int getValue() {
			return value;
		}

		
	}
	
	// Message Requette envoyé par l'acteur Collect à l'acteur Analyseur  par interval de 30 seconde
	static class Requette {
		private final int nbre;
		private final String reponseTime;

		public Requette(int nbre, String reponseTime) {
			this.nbre = nbre;
			this.reponseTime = reponseTime;
		}
		
		public int getNbre() {
			return nbre;
		}

		public String getReponseTime() {
			return reponseTime;
		}
		 		
	}	
	// Message MachineAjout envoyé par l'acteur Analyseur à l'acteur Décideur  pour prendre une décision
	static class MachineAjout {
		private final int nbre;
		
		public MachineAjout (int nbre) {
			this.nbre = nbre;
		}
		
		public int getNbre() {
			return nbre;
		}
		 		
	}
	
	// Message MachineRetrait envoyé par l'acteur Analyseur à l'acteur Décideur  pour prendre une décision
	static class MachineRetrait {
		private final int nbre;
		
		public MachineRetrait (int nbre) {
			this.nbre = nbre;
		}
		
		public int getNbre() {
			return nbre;
		}
		 		
	}
	// Message SofwareVM  envoyé par l'acteur Decideur à l'acteur Action  pour executer l'action
	static class SoftwareVM {
		private String action;// action est soit, install, uninstall, start, stop, reconfig, replication (dans un cluster kubernetes)..
		private String nomLogiciel;
	    private String listImages; //liste des VM
		private int newSize = -1; // nombre de replica
	    
		public SoftwareVM(String action, String nomLogiciel, String listImages, int newSize) {
			this.action = action;
			this.nomLogiciel = nomLogiciel;
			this.listImages = listImages;
			this.newSize = newSize;
		}	 
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public String getNomLogiciel() {
			return nomLogiciel;
		}
		public void setNomLogiciel(String nomLogiciel) {
			this.nomLogiciel = nomLogiciel;
		}
		public String getListImages() {
			return listImages;
		}
		public void setListImages(String listImages) {
			this.listImages = listImages;
		}	
	    public int getNewSize() {
			return newSize;
		}
		public void setNewSize(int newSize) {
			this.newSize = newSize;
		}
		 		
	}
	
// l'acteur Collect des traces japonais de wikipedia
	public static class CollectTraceWikipedia extends UntypedActor {
		private ShellService sh;
		private int indexList = 1765; 
		private List<DBObject> list;
		private final ActorRef analyse; 
		
		
		public CollectTraceWikipedia(ActorRef analyse, ShellService shell) {
			 this.analyse = analyse;	       
	         this.sh = shell;

			 try {  
                //nbre de requettes sauvegardé par interval de 30 secondes 				 
	    		Mongo m = new Mongo();
	    		DB db = m.getDB( "trace10" );	   						
	    		DBCollection collTime = db.getCollection("thirtyseconde");				        		
	    		DBCursor cursor = collTime.find().sort(new BasicDBObject( "debutdate" , 1 ));
	    		this.list = cursor.toArray();
		    	 }
	    	   catch (Exception e) {
	    	            System.out.println(e.toString());
	    	   }
		}
		 
		public void onReceive(Object message) {

			if (message instanceof Interval) {				
				    System.out.println(" <Actor Collect>: Envoie du nombre de requetes et tu temps de reponse à une requette");
															
					//String reponsetime = sh.commandAsString("siege -d1 -r1 -c1 http://192.168.0.6/mediawiki/index.php/Accueil");
					String reponsetime= " ";
					//Envoie du nbre de requettes sauvegardé par interval de 30 secondes
					analyse.tell(new Requette(((Integer)list.get(indexList).get("nbreRequete")).intValue(), reponsetime), getSelf());
					indexList+= 1;	
									
			} else {
				unhandled(message);
			}
		}
	}
 

	

// l'acteur Collect en temps réel
	public static class CollectRealTime extends UntypedActor {
		private ShellService sh;
		private final ActorRef analyse; 
		private Date dateDebut;
		private Date dateFin;
		private int nbreMessage = 0;
		private DateFormat df; 
		
		public CollectRealTime(ActorRef analyse, ShellService shell) {
			 this.analyse = analyse;	       
	         this.sh = shell;
	         this.df = new SimpleDateFormat("dd/MMM/yyyy':'hh:mm:ss", Locale.ENGLISH);
		}
		 
		public void onReceive(Object message) {

			if (message instanceof Interval) {				
				Interval interval = (Interval) message;
				nbreMessage++;
				if (nbreMessage==1){					
						
					 System.out.println("Intialisation de la date et l'heure");  
					 this.dateDebut= new Date();
					
				}
			    else {		
					
				    this.dateFin= new Date();
				    System.out.println("<Actor Collect>: Envoie du nombre de requetes et tu temps de reponse à une requette");
				    System.out.println("Entre l'interval de temps dateDebut="+df.format(this.dateDebut)+" et dateFin="+df.format(this.dateFin));							
															
					//String reponsetime = sh.commandAsString("siege -d1 -r1 -c1 http://192.168.0.6/mediawiki/index.php/Accueil");
					String reponseTime= " ";	
					
	                //Copie des n dernières lignes(traces) du fichier access.log du loadbalancer 
					//dans le fichier temporaire tmp (/var/log/nginx/tmp)	
					String commande="fab -f /home/daniel/scriptFabric/log/fabfile.py tail:namehost=192.168.1.2,username=daniel,passworduser=celine,nbreligne=10000";				
					String result = sh.commandAsString(commande);				

					//Envoie du nbre de requettes en temps réel par interval à l'actor analyse 
					analyse.tell(new Requette(nbreReqTrafic(this.dateDebut,this.dateFin,"/var/log/nginx/tmp"),reponseTime), getSelf());	
					this.dateDebut=this.dateFin;	
			    			    
			   }	

				
			} else {
				unhandled(message);
			}
		}
		
		//Nombre de requettes du trafic pendant un interval de temps
		public int nbreReqTrafic(Date dateDebut, Date dateFin, String pathFile) {	
            
			// il faut une pause de milliseconde pour être sûr que les dernieres ligne de de access.log sont transferées dans le fichier tmp								
			//avant la lecture
			try {
				  Thread.sleep(300);
				} catch (InterruptedException e) {
					System.out.println(e.getStackTrace());
					
				}
			int nbreReq=0;
			try {				
				Date dateReq;				
			    BufferedReader input = new BufferedReader(new FileReader(new File(pathFile)));
				if (input == null){ 
					System.out.println("Fichier non trouvé: " + pathFile);
					return -1;			
				}
				else {
					
					for (String ligne = input.readLine(); ligne != null; ligne = input.readLine()) {
						
						String tab[] = ligne.split(" ");						
						String datestr=tab[3] ;					
						datestr=datestr.substring(1, datestr.length());						
                        dateReq= df.parse(datestr) ;	                        	                        
                		if (((dateDebut.compareTo(dateReq)<0)||(dateDebut.compareTo(dateReq)==0))
                				&&((dateReq.compareTo(dateFin)<0))||(dateReq.compareTo(dateFin)==0)){	                			
                		  nbreReq++;	                			
                		}							
					}
				
				}
				
				input.close();
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}catch (ParseException e) {
				e.printStackTrace();
			}
			
		return nbreReq;	
			
		}
	 	
	}	
	

	
	
	
// l'acteur Analyseur
	public static class Analyse extends UntypedActor { 
		private ShellService sh;
		private EventNotificationAlertServicePortType eventService;
		private int nbreReqFutur; //nbre de requete  future(prevision)
		private int nbreReqPrecedant; 
		private int nbreSms = 0; 
		private int[] tabTrace= new int[3]; 
		private double constanteLissage=0.9;
		private int maxReqImageVirtuel = 1200; //supposé être le nbre de requetes que peut supporter une VirtuelImage à valider
		private double coefMultiplicateur;
		ArrayList<Integer> nbreReqAvantPicDetect= new ArrayList<Integer>(); 
		ArrayList<Integer> nbreVMAjouterQuandPicDetect= new ArrayList<Integer>();  
		int nbrePicDetect=0;
		
		
		private final ActorRef decide; 
		
		public Analyse(ActorRef decide,ShellService shell,EventNotificationAlertServicePortType eventService) {
			 this.decide= decide;	 
			 this.sh = shell;
			 this.eventService = eventService;
			 for (int i = 0; i < 3; i++) {
				 tabTrace[i]=0;
			}			 
			 
		}
		
		public void insertTabTrace(int nbreReq){
			tabTrace[0]=tabTrace[1];
			tabTrace[1]=tabTrace[2];
			tabTrace[2]=nbreReq;		
		}
		
		public void onReceive(Object message) {
			if (message instanceof Requette) {								
				Requette reqCourant = (Requette) message;
				System.out.println(" <Actor Analyse>: Nbre de requettes reçues après 30 seconde  = "+ reqCourant.getNbre());
				
				//String tab[]= reqcourant.getReponsetime().split(" ");
			/*	for (int i = 0; i < tab.length; i++) {
					System.out.println( i + tab[i]);
				}*/
				
				//System.out.println(" <Actor Analyse>: Temps de reponse d'une requete à present est de "+tab[4]+" "+tab[5]);
				insertTabTrace(reqCourant.getNbre());
				nbreSms++;				
				if (nbreSms==1){
					nbreReqPrecedant=reqCourant.getNbre();
					nbreReqFutur= reqCourant.getNbre(); 
					System.out.println(" <Actor Analyse>: nbreReqPrecedant = "+ nbreReqPrecedant);
					System.out.println(" <Actor Analyse>: nbreReqFutur = "+ nbreReqFutur);
				}
				else{
					nbreReqFutur= (int) (constanteLissage*reqCourant.getNbre() + ((1-constanteLissage)*nbreReqFutur));
					System.out.println(" <Actor Analyse>: nbreReqPrecedant = "+ nbreReqPrecedant);
					coefMultiplicateur=Math.rint((double)reqCourant.getNbre()/nbreReqPrecedant);					
					System.out.println(" <Actor Analyse>: Valeur du coefficient multiplicateur = "+ coefMultiplicateur);
					System.out.println(" <Actor Analyse>: nbreReqFutur = "+ nbreReqFutur);
					if (coefMultiplicateur>=2.0){	
						
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						//eventService.send("PeakForecast-INFO: Detection d'un pic de trafic à "+ df.format(new java.util.Date()));
						System.out.println("<Actor Analyse>: Detection d'un pic de trafic à "+ df.format(new java.util.Date()));
						nbrePicDetect++;
						nbreReqAvantPicDetect.add(nbreReqPrecedant);
						int estimaTrafic = reqCourant.getNbre() + nbreReqFutur; //sur une minute						
						//int nbreImageVirtuel = (int) (estimaTrafic-(tabTrace[0]+tabTrace[1]))/maxReqImageVirtuel; //avec les VM ou Container docker on ne calcul que le nombre de ressources  ajouter
						int nbreImageVirtuel = (int) estimaTrafic/maxReqImageVirtuel; // avec les cluster kubernetes c'est le nombre de replications totals y compris ceux encours d'executions
						//nbreVMAjouterQuandPicDetect.add(nbreImageVirtuel);
						System.out.println("<Actor Analyse>: Envoie du nombre de VM neccessaire pour absorber la charge future");
						decide.tell(new MachineAjout(nbreImageVirtuel), getSelf());					
					}
					if (!nbreReqAvantPicDetect.isEmpty()){
						
						if((nbreReqAvantPicDetect.get(nbrePicDetect-1)).intValue()>=reqCourant.getNbre()){	
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							int estimaTrafic = reqCourant.getNbre() + nbreReqFutur; //sur une minute						
							//int nbreImageVirtuel = (int) (estimaTrafic-(tabTrace[0]+tabTrace[1]))/maxReqImageVirtuel; //avec les VM ou Container docker on ne calcul que le nombre de ressources  ajouter
							int nbreImageVirtuel = (int) estimaTrafic/maxReqImageVirtuel; // avec les cluster kubernetes c'est le nombre de replications totals y compris ceux encours d'executions
							//eventService.send("PeakForecast-INFO: Detection de baisse du trafic  après un pic à "+ df.format(new java.util.Date()));
							System.out.println("<Actor Analyse>: Detection de baisse du trafic  après un pic à "+ df.format(new java.util.Date()));							
							//decide.tell(new MachineRetrait((nbreVMAjouterQuandPicDetect.get(nbrePicDetect-1)).intValue()), getSelf());
							decide.tell(new MachineRetrait(nbreImageVirtuel), getSelf());
						    nbreReqAvantPicDetect.remove(nbrePicDetect-1);
						   // nbreVMAjouterQuandPicDetect.remove(nbrePicDetect-1);
						    nbrePicDetect--;						
						}
						
						
					}
					nbreReqPrecedant=reqCourant.getNbre();					
				}
				
			} else {
				unhandled(message);
			}
		}
	}	
	
// l'acteur Decideur
	public static class Decide extends UntypedActor {
		 
		private ShellService sh;
		private EventNotificationAlertServicePortType eventService;
		private final ActorRef action; 
		private String nomLogiciel;
		private String listImages;
		ArrayList<String> listNomVMAjouter= new ArrayList<String>();
		int nbrePicDetect=0;
		
		public Decide( ActorRef action, ShellService shell, EventNotificationAlertServicePortType eventService) {
			this.action=action;
			this.sh = shell;
			this.eventService = eventService;
			
			nomLogiciel ="Mediawiki";
	        listImages="apache2 apache3"; //nom des differentes images virtuelles separées par le caractère espace
		}
		 
		public void onReceive(Object message) {

			if (message instanceof MachineAjout) {
				MachineAjout machine = (MachineAjout) message;
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				System.out.println(" <Actor Decide>: Reception du nombre de VM, neccessaire pour absorber le pic de trafic FUTURE "); //machine.getNbre()			  			  	
				//System.out.println(" <Actor Decide>: Collect des temps de reponse des VM serveur en cour d'execution ");	
				//String commande="fab -f /home/daniel/scriptFabric/listeserveur/fabfile.py listeServeur:namehost=192.168.1.2,username=daniel,passworduser=celine";				
				//String result = sh.commandAsString(commande);				
				//String tab[]= result.split(" ");
				//ArrayList<String> listeServeur= new ArrayList<String>(); 
				//ArrayList<String> timeReponse= new ArrayList<String>(); 
				//ArrayList<String> ddosReponseList= new ArrayList<String>(); 
				//System.out.println(" <Actor Decide>: Liste des VM serveur en cour d'execution");
				//for (int i = 0; i < tab.length; i++) {
				//		if (tab[i].contains("172.")) { 
				//			listeServeur.add(tab[i]);
				//			System.out.println(" <Actor Decide>: VM serveur "+tab[i]);
				//		}
				//	}		
				/*for (String serveur : listeServeur) {
					String reponseTime = sh.commandAsString("siege -d1 -r1 -c1 http://"+serveur+"/mediawiki/ -v");
					String tempReponse[]= reponseTime.split(" ");
						for (int i = 0; i < tempReponse.length; i++) {
					   System.out.println("tempReponse "+i +" "+ tempReponse[i]);
				     }
					
					timeReponse.add(" <Actor Decide>: Le temps de reponse du serveur "+serveur+" est de "+tempReponse[4]+" "+tempReponse[5]);	
				}
				for (String time : timeReponse) {
					System.out.println(time);
				}*/
				System.out.println(" <Actor Decide>: Verification des attaques DDOS");				
/*				for (String serveur : listeServeur) {
					
					String reponseDDOS= sh.commandAsString("fab -f /home/daniel/scriptFabric/detectddos/fabfile.py detetedDDOS:namehost="+nomVM(serveur)+",username="+nomVM(serveur)+",passworduser="+nomVM(serveur));
					String ddosReponse[]= reponseDDOS.split(" ");
						for (int i = 0; i < ddosReponse.length; i++) {
					   System.out.println("ddosReponse "+ i+" "+ ddosReponse[i]);
				     }
					
					ddosReponseList.add(ddosReponse[19]);	
				}*/
				int attaqueDDOS=0;
				int index=0;
				ArrayList<String> listeServeurAttaquer= new ArrayList<String>(); 
				/*for (String reponse : ddosReponseList) {
					if(reponse.equals("1")) {
							attaqueDDOS=1;
							listeServeurAttaquer.add(listeServeur.get(index));
					}					
					index++;
				}*/				
				System.out.println(" <Actor Decide>: Prise de decision !!!!!!!!!");					
                if(attaqueDDOS==1){
                	String listServeurStr=" ";
    				for (String serveur : listeServeurAttaquer) {
    					listServeurStr= listServeurStr+serveur+"";
    				}					
    				System.out.println("<Actor Decide>: Detection d attaques DDOS sur le serveur(s)"+ listServeurStr+" à  "+ df.format(new java.util.Date()));
    				System.out.println("<Actor Decide>: Impossible d'allouer les ressources pour absorber le pic de trafic Future"); 
					eventService.send("PeakForecast-CRITICAL: Detection d'attaques DDOS sur le serveur(s)"+listServeurStr+" à  "+ df.format(new java.util.Date()));
					eventService.send("PeakForecast-CRITICAL: Impossible d'allouer les ressources pour absorber le pic de trafic Future");
                	
					//Script de filtrage des adresses ip des machines zombies Encour 
					
                }else{
                	
                	System.out.println("<Actor Decide>: Aucune attaque DDOS detecter");
                	System.out.println("<Actor Decide>: Allocation de ressources pour absorber le pic de trafic Future"); 
                	//eventService.send("PeakForecast-INFO: Aucune attaque DDOS detecter. Allocation de ressources pour absorber le pic de trafic Future.");
                	//int newSize = machine.getNbre();
                	int newSize = 3; //pour les tests nous mettons 3 pour le premier resize du nombre de replica
                	//action.tell(new SoftwareVM("install",nomLogiciel,listImages));	 
                	action.tell(new SoftwareVM("replication",null,null,newSize));	   				
                	//listNomVMAjouter.add(listImages);
    				nbrePicDetect++;                	                	
                }			
			
			} else {
				if (message instanceof MachineRetrait) {
					MachineRetrait machine = (MachineRetrait) message;
					System.out.println(" <Actor Decide>: Reception du nombre de VM, à mettre en pause ");	//machine.getNbre()		  			  	
					System.out.println(" <Actor Decide>: Prise de decision!!!!!!!!");	
                	//int newSize = machine.getNbre();
                	int newSize = 1; //pour les tests nous mettons 3 pour le premier resize du nombre de replica
					//action.tell(new SoftwareVM("pause","VM",listNomVMAjouter.get(nbrePicDetect-1)));
                	 action.tell(new SoftwareVM("replication",null,null,newSize));	
					//listNomVMAjouter.remove(nbrePicDetect-1);
					nbrePicDetect--;
					
					
				} else {
					unhandled(message);
				}
			}
		}

	//une methode util
	public String nomVM(String adresseipport) {		
		if(adresseipport.equals("172.17.0.8:80"))
		{
			//apache2	
			return "172.17.0.8";
		}
		else if(adresseipport.equals("172.17.0.9:80"))
		{
			//apache3
			return "172.17.0.9";
		}
		else
		{
			//apache1
                        return "172.17.0.5";
		}
	}		
	
	
	}
	
// l'acteur Action
	public static class Action extends UntypedActor {
		 
		 private ControllerSoftwareServicePortType controllerSoftwareService;
		 private ControllerVMServicePortType controllerVmService;
		 private ControllerKubectlServicePortType controllerKubectlService;
		 private String nameReplicationController;
		
		public Action(ControllerSoftwareServicePortType controllerSoftwareService, ControllerVMServicePortType controllerVmService, ControllerKubectlServicePortType controllerKubectlService, String nameReplicationController) {
	        this.controllerSoftwareService = controllerSoftwareService;
	        this.controllerVmService = controllerVmService;
	        this.controllerKubectlService = controllerKubectlService;
	        this.nameReplicationController = nameReplicationController;
		}
		 
		public void onReceive(Object message) {

			if (message instanceof SoftwareVM) {
				SoftwareVM softwarevm = (SoftwareVM) message;
				System.out.println(" <Actor Action>: Execution de la décision prise");
								
				if (softwarevm.getAction().equals("replication") && softwarevm.getNewSize()!= -1){

					System.out.println("Resize Replication controller name "+nameReplicationController+" with new size "+softwarevm.getNewSize());			  	
					int newSize = softwarevm.getNewSize();
					controllerKubectlService.kubectlResize(nameReplicationController,newSize);
				}
				
				if (softwarevm.getAction().equals("install")){
					//System.out.println("date debut"+ new java.util.Date());
					//appel du service install du serveur distant pour le déploiement 
					
					/// instruction de manipulations des VM de Virtualbox 
					///System.out.println(" <Actor Action>: Execution de l'action "+ softwarevm.getAction()+" concernant MediaWiki sur "+softwarevm.getListImages());			  	
					///System.out.println(" <Actor Action>: Deployement du logiciel sur les différentes VM");					
			         //controllerSoftwareService.install(softwarevm.getNomLogiciel(),softwarevm.getListImages());
					
					// instructions de manipulations des containers Docker
					System.out.println(" <Actor Action>: Creation des containers, Demarrage des containers et Enregistrement chez le Loadbalancer"+softwarevm.getListImages());
					controllerVmService.createContainer(softwarevm.getListImages());
					//System.out.println(" <Actor Action>: Demarrage des containers et enregistrement chez le Loadbalancer"+softwarevm.getListImages());
					//controllerVmService.startContainer(softwarevm.getListImages());
			        // s.start(softwarevm.getListimages());
			        //System.out.println("date final"+ new java.util.Date());
				}
				
				if (softwarevm.getAction().equals("pause") && softwarevm.getNomLogiciel().equals("VM")){
					//System.out.println("date debut"+ new java.util.Date());
					//appel du service install du serveur distant pour le déploiement 
					System.out.println(" <Actor Action>: Execution de l'action "+ softwarevm.getAction()+"  sur les VM "+softwarevm.getListImages());			  	
					
					/// instruction de manipulations des VM de Virtualbox 
					///System.out.println(" <Actor Action>: Mise en pause des différentes VM");
					///controllerVmService.pause(softwarevm.getListImages());
					
					// instructions de manipulations des containers Docker
					System.out.println(" <Actor Action>: Arrêt des différents des containers et Retrait chez le Loadbalancer"+softwarevm.getListImages());
					controllerVmService.stopContainer(softwarevm.getListImages());
					
			        // s.start(softwarevm.getListimages());
			        //System.out.println("date final"+ new java.util.Date());
				}
				
			} else {
				unhandled(message);
			}
		}
	}
	
    
    
}
