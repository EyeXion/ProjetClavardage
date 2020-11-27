package app.insa.clav.Utils;

import app.insa.clav.reseau.UDPInput;

import java.util.TimerTask;

/**
 * Classe de task qui est utilisée pour attendre 1 seconde après l'envo d'un message de type 1
 * Après une seconde, on desactive les filtres de type 2 et 3 pour ne plus prendre en compte ces messages.
 */
public class TimerTaskResponseWait extends TimerTask {

    /**
     * Instance de UDPInput (meme que l'Application)
     */
    public UDPInput udpIn;

    public TimerTaskResponseWait(UDPInput udpIn){
        this.udpIn = udpIn;
    }

    /**
     * Quand la seconde s'est écoulée, on met les filtres à faux pour ne plus prendre en compte les messages de type 2 et 3
     */
    public void run(){
        udpIn.setFilterValue(2,false);
        udpIn.setFilterValue(2,false);
    }
}
