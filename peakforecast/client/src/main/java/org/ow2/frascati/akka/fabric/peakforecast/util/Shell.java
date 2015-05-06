/*
 * Shell for Java e adiGuba (http://adiguba.developpez.com)
 * adiGuba (mars 2007)
 *
 * Contact  : adiguba@redaction-developpez.com
 * Site web : http://adiguba.developpez.com/
 *
 * Ce logiciel est une librairie Java servant e simplifier l'execution
 * de programme natif ou de ligne de commande du shell depuis une
 * application Java, sans se soucier du systeme hete. 
 * 
 * Ce logiciel est regi par la licence CeCILL-C soumise au droit franeais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL-C telle que diffusee par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".
 * 
 * En contrepartie de l'accessibilite au code source et des droits de copie,
 * de modification et de redistribution accordes par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limitee.  Pour les memes raisons,
 * seule une responsabilite restreinte pese sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les concedants successifs.
 * 
 * A cet egard  l'attention de l'utilisateur est attiree sur les risques
 * associes au chargement,  e l'utilisation,  e la modification et/ou au
 * developpement et e la reproduction du logiciel par l'utilisateur etant 
 * donne sa specificite de logiciel libre, qui peut le rendre complexe e 
 * manipuler et qui le reserve donc e des developpeurs et des professionnels
 * avertis possedant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invites e charger  et  tester  l'adequation  du
 * logiciel e leurs besoins dans des conditions permettant d'assurer la
 * securite de leurs systemes et ou de leurs donnees et, plus generalement, 
 * e l'utiliser et l'exploiter dans les memes conditions de securite. 
 * 
 * Le fait que vous puissiez acceder e cet en-tete signifie que vous avez 
 * pris connaissance de la licence CeCILL-C, et que vous en avez accepte les
 * termes.
 * 
 */
package org.ow2.frascati.akka.fabric.peakforecast.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.ow2.frascati.akka.fabric.peakforecast.util.ProcessConsumer;
import org.ow2.frascati.akka.fabric.peakforecast.util.Shell;


/**
 * Cette classe represente le shell systeme.<br>
 * Elle permet de simplifier l'execution de programme externe.<br>
 * <br>
 * Cette classe utilise la classe {@linkplain ProcessConsumer} afin de gerer
 * simplement les flux d'entrees/sorties du process.
 * 
 * @author adiGuba
 * @version shell-1.0
 */
public class Shell {

	/** Commande permettant de lancer le shell sous les systemes Windows 9x. */
	private static final String[] DEFAULT_WIN9X_SHELL = {"command.com", "/C"};
	/** Commande permettant de lancer le shell sous les systemes Windows NT/XP/Vista. */
	private static final String[] DEFAULT_WINNT_SHELL = {"cmd.exe", "/C"};
	/** Commande permettant de lancer le shell sous les systemes Unix/Linux/MacOS/BSD. */
	private static final String[] DEFAULT_UNIX_SHELL = {"/bin/sh", "-c"};
	
	/** Shell du systeme courant, determine lors du chargement de la classe. */
	private static final String[] SYSTEM_SHELL = getSystemShell();
	
	/**
	 * Retourne le shell courant sous forme d'un tableau de String
	 * representant les differents parametres a executer.<br/>
	 * Le shell e utiliser depend du systeme d'exploitation et
	 * de certaine variable d'environnement (<b>%ComSpec%</b> sous Windows,
	 * <b>$SHELL</b> sous les autres systemes).
	 * @return Le tableau de parametre utile e l'execution du shell.
	 */
	private static String[] getSystemShell() {
		// On determine le shell selon deux cas : Windows ou autres :
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			// On tente de determiner le shell selon la variable d'environnement ComSpec :
			String comspec = System.getenv("ComSpec");
			if (comspec!=null) {
				return new String[] {comspec, "/C"};
			}
			// Sinon on determine le shell selon le nom du systeme :
			if (osName.startsWith("Windows 3") || osName.startsWith("Windows 95")
				|| osName.startsWith("Windows 98") || osName.startsWith("Windows ME")) {
				return DEFAULT_WIN9X_SHELL;
			}
			return DEFAULT_WINNT_SHELL;
		}
		// On tente de determiner le shell selon la variable d'environnement SHELL :
		String shell = System.getenv("SHELL");
		if (shell!=null) {
			return new String[] {shell, "-c"};
		}
		// Sinon on utilise le shell par defaut (/bin/sh)
		return DEFAULT_UNIX_SHELL;
	}
	
	/** Le tableau representant les parametres du shell. */
	private final String[] shell;
	/** Le charset associe e cette instance du shell. */
	private Charset charset = null;
	/** Le repertoire associe e cette instance du shell. */
	private File directory = null;
	/** Les variables d'environnement utilisateurs associe e ce shell. */
	private Map<String,String> userEnv = null;
	/** Indique si les variables d'environnements globales doivent etre herite. */
	private boolean systemEnvInherited = true;	
	
	/**
	 * Construit un nouveau Shell en utilisant le shell systeme.
	 */
	public Shell() {
		this.shell = Shell.SYSTEM_SHELL;
	}
	
	/**
	 * Construit un nouveau shell en utilisant la commande represente en parametre.
	 * Par exemple pour forcer l'utilisation du bash :
	 * <pre><code>Shell sh = new Shell("/bin/bash", "-c");</code></pre>
	 * @param cmds Les parametres permettant de lancer le shell.
	 */
	public Shell(String...cmds) {
		this.shell = new String[cmds.length];
		System.arraycopy(cmds, 0, this.shell, 0, this.shell.length);
	}
	
	/**
	 * Retourne le charset associe avec cette instance de shell.
	 * @return Charset.
	 */
	public Charset getCharset() {
		if (this.charset==null) {
			this.charset = Charset.defaultCharset();
		}
		return this.charset;
	}
	
	/**
	 * Modifie le charset associe avec cette instance de shell.
	 * @param charset Le nouveau charset a utiliser.
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}


	/**
	 * Modifie le charset associe avec cette instance de shell.
	 * @param charsetName Le nom du nouveau charset
	 * @throws IllegalCharsetNameException
	 * @throws UnsupportedCharsetException
	 */
	public void setCharset(String charsetName)
		throws IllegalCharsetNameException, UnsupportedCharsetException {
		this.charset = Charset.forName(charsetName);
	}


	/**
	 * Retourne une map contenant les variables d'environnements utilisateurs.
	 * Cette Map est librement modifiables afin d'ajouter/supprimer des elements.
	 * @return Map des variables d'environnements utilisateurs.
	 */
	public Map<String, String> getUserEnv() {
		if (this.userEnv==null) {
			this.userEnv = new HashMap<String, String>();
		}
		return this.userEnv;
	}


	/**
	 * Retourne le repertoire e partir duquel les commandes du shell seront lances.
	 * @return Le repertoire courant.
	 */
	public File getDirectory() {
		if (this.directory==null) {
			this.directory = new File("").getAbsoluteFile();
		}
		return this.directory;
	}

	/**
	 * Modifie le repertoire e partir duquel les commandes du shell seront lances.
	 * @param directory Le nouveau repertoire
	 * @throws IllegalArgumentException Si <code>directory</code> ne represente pas un repertoire.
	 */
	public void setDirectory(File directory) {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory");
		}
		this.directory = directory;
	}

	/**
	 * Indique si les variables d'environnements de l'application Java
	 * courante doivent etre passe aux commandes lancees par ce shell.
	 * @return <b>true</b> si les nouveaux process heritent des variables d'environnements,
	 * <b>false</b> sinon.
	 */
	public boolean isSystemEnvInherited() {
		return this.systemEnvInherited;
	}

	/**
	 * Modifie la valeur de l'attribut 'inheritSystemEnv'.
	 * @param inheritSystemEnv La nouvelle valeur de l'attribut.
	 * @see Shell#isSystemEnvInherited()
	 */
	public void setSystemEnvInherited(boolean inheritSystemEnv) {
		this.systemEnvInherited = inheritSystemEnv;
	}
	
	
	/**
	 * Creer un ProcessBuilder selon la configuration de ce shell
	 * @param args Les parametres principaux de la commande
	 * @return Un ProcessBuilder correctement initialise.
	 */
	private ProcessBuilder create(String...args) {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.directory(directory);
		if (!systemEnvInherited) {
			pb.environment().clear();
		}
		if (userEnv!=null) {
			pb.environment().putAll(userEnv);
		}
		return pb;
	}
	
	/**
	 * Cree un processus representant le shell et l'associe e une instance
	 * de ProcessConsumer.<br/>
	 * Le processus ne sera reellement demarre que lors de l'appel d'une
	 * des methodes <code>consume()</code> de la classe ProcessConsumer.<br>
	 * <br>
	 * Cela permet de lancer plusieurs commandes dans le meme shell
	 * via le flux d'entree du processus.
	 * @return Une instance de ProcessConsumer associe au processus du shell.
	 * @see ProcessConsumer#consume()
	 * @see ProcessConsumer#input(Readable)
	 */
	public ProcessConsumer shell() {
        return new ProcessConsumer(create(this.shell[0]), this.charset);
    }
	
	/**
	 * Cree un processus representant la commande du shell et l'associe
	 * e une instance de ProcessConsumer.<br/>
	 * Cette methode instancie un nouveau shell en tant que processus afin
	 * d'executer la ligne de commande en parametre. Le processus ne sera
	 * reellement demarre que lors de l'appel d'une des methodes
	 * <code>consume()</code> de la classe ProcessConsumer.<br>
	 * <br>
	 * La commande passe en parametre accepte toutes les specificitees du
	 * shell systeme (redirections, pipes, structures conditionnelles, etc.).
	 * @param commandLine La ligne de commande e executer par le shell .
	 * @return Une instance de ProcessConsumer associe e la commande.
	 * @see ProcessConsumer#consume()
	 */
    public ProcessConsumer command(String commandLine) {
    	ProcessBuilder pb = create(this.shell);
    	pb.command().add(commandLine);
    	return new ProcessConsumer(pb ,this.charset);
    }
    
    /**
     * Identique e {@link Shell#command(String)}, si ce n'est que la ligne
     * de commande est d'abord formatte en utilisant les parametres via
	 * la classe MessageFormat.<br><br>
	 * Cete methode est equivalent e la ligne suivante :<br>
	 * <pre><code>command(MessageFormat.format(commandLine, arguments))</code></pre>
     * @param commandLine La ligne de commande e formater.
     * @param arguments Les parametres du formattage.
     * @return Une instance de ProcessConsumer associe e la commande.
     * @see MessageFormat#format(String, Object...)
     * @see Shell#command(String)
     */
    public ProcessConsumer command(String commandLine, Object...arguments) {
    	return command(MessageFormat.format(commandLine, arguments));
    }
    
    /**
     * Cree un processus standard et l'associe e une instance de ProcessConsumer.
     * Le premier parametre doit obligatoirement correspondre e un nom de programme
     * existant, de la meme maniere qu'avec l'utilisation de {@linkplain Runtime#exec(String[])}.
     * <br><br>
     * Contrairement e {@link Shell#command(String)}, cette methode n'instancie
     * pas le processus du shell mais directement le programme passe en
     * premier parametre.  Le processus ne sera reellement demarre que
     * lors de l'appel d'une des methodes <code>consume()</code> de la
     * classe ProcessConsumer.<br>
     * @param args Les parametres de la commandes a executer.
     * @return Une instance de ProcessConsumer associe au process.
     */
    public ProcessConsumer exec(String...args) {
    	return new ProcessConsumer(create(args), this.charset);
    }
    
    /**
     * Retourne le nom du shell systeme.
     * C'est e dire le nom du fichier en local qui
     * return Le nom du shell.
     */
    @Override
    public String toString() {
    	return this.shell[0];
    }
    
    /**
     * Execute la ligne de commande dans le shell systeme,
     * en affichant les donnees dans les flux des sorties de l'application.
     * @param commandLine La ligne de commande e executer.
     * @return Le code de retour de la ligne de commande.
     * @throws IOException Erreur d'execution de la commande.
     * @see System#out
     * @see System#err
     */
    public static int system(String commandLine) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(Shell.SYSTEM_SHELL);
    	pb.command().add(commandLine);
    	return new ProcessConsumer(pb, null).consume();
	}
	
    /**
     * Identique e {@link Shell#system(String)}, si ce n'est que la ligne
     * de commande est d'abord formatte en utilisant les parametres via
	 * la classe MessageFormat.<br><br>
     * @param commandLine La ligne de commande e executer.
     * @return Le code de retour de la ligne de commande.
     * @throws IOException Erreur d'execution de la commande.
     * @see Shell#system(String)
     */
	public static int system(String commandLine, Object...arguments) throws IOException {
		return system(MessageFormat.format(commandLine, arguments));
	}
	
	/**
     * Execute le programme specifie en affichant les donnees dans
     * les flux des sorties de l'application.
     * @param args Les differents parametres du programme.
     * @return Le code de retour du programme.
     * @throws IOException Erreur d'execution du programme.
     * @see System#out
     * @see System#err
     */
	public static int execute(String...args) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(args);
    	return new ProcessConsumer(pb, null).consume();
	}
}
