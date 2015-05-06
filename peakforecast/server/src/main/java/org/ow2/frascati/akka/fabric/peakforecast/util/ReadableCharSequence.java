/*
 * Shell for Java � adiGuba (http://adiguba.developpez.com)
 * adiGuba (mars 2007)
 *
 * Contact  : adiguba@redaction-developpez.com
 * Site web : http://adiguba.developpez.com/
 *
 * Ce logiciel est une librairie Java servant � simplifier l'ex�cution
 * de programme natif ou de ligne de commande du shell depuis une
 * application Java, sans se soucier du syst�me h�te. 
 * 
 * Ce logiciel est r�gi par la licence CeCILL-C soumise au droit fran�ais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL-C telle que diffus�e par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".
 * 
 * En contrepartie de l'accessibilit� au code source et des droits de copie,
 * de modification et de redistribution accord�s par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limit�e.  Pour les m�mes raisons,
 * seule une responsabilit� restreinte p�se sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les conc�dants successifs.
 * 
 * A cet �gard  l'attention de l'utilisateur est attir�e sur les risques
 * associ�s au chargement,  � l'utilisation,  � la modification et/ou au
 * d�veloppement et � la reproduction du logiciel par l'utilisateur �tant 
 * donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � 
 * manipuler et qui le r�serve donc � des d�veloppeurs et des professionnels
 * avertis poss�dant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invit�s � charger  et  tester  l'ad�quation  du
 * logiciel � leurs besoins dans des conditions permettant d'assurer la
 * s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement, 
 * � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�. 
 * 
 * Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez 
 * pris connaissance de la licence CeCILL-C, et que vous en avez accept� les
 * termes.
 * 
 */
package org.ow2.frascati.akka.fabric.peakforecast.util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;

/**
 * Cette classe permet simplement d'encapsuler un CharSequence
 * afin de l'utiliser comme un Readable.
 * 
 * @see Readable
 * @see CharSequence
 * 
 * @author adiGuba
 * @version shell-1.0
 */
public class ReadableCharSequence implements Readable, Closeable {

	/** Le CharSequence depuis lequel les donn�es seront lues. */
	private final CharSequence cs;
	/** Position courante dans le CharSequence. */
	private int pos = 0;
	
	/**
	 * Construit un ReadableCharSequence qui lit les donn�es
	 * dans l'objet CharSequence en param�tre. 
	 * @param cs Le CharSequence source des donn�es.
	 */
	public ReadableCharSequence(CharSequence cs) {
		this.cs = cs;
		if (this.cs==null) {
			throw new NullPointerException("null");
		}
	}
	
	/**
	 * Ferme le flxu courant, et evenutellement le
	 * CharSequence si celui-ci impl�mente Closeable.
	 * @see Closeable
	 */
	public void close() throws IOException {
		if (this.cs instanceof Closeable) {
			((Closeable)this.cs).close();
		}
		this.pos = -1;
	}

	/**
	 * Lit lesflux depuis le CharSequence vers le CharBuffer en param�tre.
	 */
	public int read(CharBuffer cb) throws IOException {
		if (this.pos < 0) {
			throw new ClosedChannelException();
		}
		int remaining = this.cs.length()-pos;
		if (remaining>0) {
			int len = Math.min( cb.remaining() , remaining);
			cb.append(this.cs, pos, len);
			this.pos += len;
			return len;
		}
		return -1;
	}
}
