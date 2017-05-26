import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe Main del programma per gestire una piccola sala cinema, con prenotazioni, cancellazioni e salvataggio dello stato su file di testo.
 * 
 * @author damiano
 */
public class D4mnAsciiCinema {

    // La variabile globale sala per contenere lo stato della sala cinema. Le dimensioni sono ignote per cui non può essere istanziata subito.
    static boolean[][] sala;
    static final boolean LIBERO = true;
    static final boolean OCCUPATO = false;
    
    // La variabile Scanner per poter richiedere input all'utente
    static Scanner scanner = new Scanner(System.in);
    
    // Variabile globale contenente l'oggetto File creato nel main.
    static File fileSala;
    
    // Variabili globali per la scrittura della sala su file di testo
    static final char LIBERO_FILE = 'L';
    static final char OCCUPATO_FILE = 'O';
    
    // Variabili globali per la gestione delle operazioni
    static final char PRENOTA = 'a';
    static final char CANCELLA = 's';
    static final char ESCI = 'q';

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Presentazione del programma
        System.out.println("Sala Cinema program. ");
        System.out.print("Inserire nome file in cui è salvato/salvare lo stato della sala: ");
        
        // Prendo in input il nome del file:
        String filename = scanner.next();
        fileSala = new File(filename);
        
        // Controllo l'esistenza del file:
        if (fileSala.exists()) {
            System.out.println("Il file inserito esiste. Inizializzazione da file...");
            initDaFile();
        } else {
            System.out.println("Il file inserito non esiste. Inizializzazione da utente...");
            initDaUser();
        }
        
        // Mostro lo stato attuale della sala
        stampaSala();
        
        // Ciclo di operazioni:
        char inputUtente;
        do {
            inputUtente = menu();
            if (operazione(inputUtente)) {
                System.out.println("Operazione eseguita con successo.");
                stampaSala();
            } else {
                System.out.println("Operazione fallita.");
            }
        } while (inputUtente != ESCI);
    } // end main()

    
    /**
     * Metodo per chiedere fila nella sala. 
     * Si utilizza sia per chiedere la fila in cui prenotare che per chiedere la fila in cui si trova il posto da liberare.
     * Controlla che l'input sia legale, ovvero sia un indice che effettivamente appartiene alla sala
     * @return il numero di fila controllato.
     */
    public static int chiediFila() {
        int fila;
        do {
            System.out.print("\tfila (1 - " + (sala.length) + ") ?: ");
            fila = scanner.nextInt() - 1;
        } while (fila < 0 || fila >= sala.length);
        return fila;
    }

    /**
     * Metodo per chiedere posto nella sala. 
     * Si utilizza sia per chiedere il posto in cui prenotare che per chiedere il posto da liberare.
     * Controlla che l'input sia legale, ovvero sia un indice che effettivamente appartiene alla sala
     * @return il numero di posto controllato.
     */
    public static int chiediPosto() {
        int posto;
        do {
            System.out.print("\tposto (1 - " + (sala[0].length) + "): ");
            posto = scanner.nextInt() - 1;
        } while (posto < 0 || posto >= sala[0].length);
        return posto;
    }

    /**
     * Metodo per realizzare l'esercizio 5,3.
     * Si crea uno scrittore di file e si sovrascrive il file (se esistente, altrimenti si crea e si scrive direttamente) con lo stato attuale della sala.
     */
    public static void esci() {
        try {
            try (PrintWriter writer = new PrintWriter(fileSala)) {
                for (boolean[] fila : sala) {
                    for (int colonna = 0; colonna < fila.length; colonna++) {
                        writer.print(fila[colonna] == OCCUPATO ? OCCUPATO_FILE : LIBERO_FILE);
                    }
                    writer.println();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(D4mnAsciiCinema.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo per realizzare l'esercizio 5,2. 
     * Chiede il posto per cui cancellare la prenotazione.
     * Effettua la prenotazione, se possibile, altrimenti lancia un'eccezione.
     * @throws PrenotazioneInesistenteException se il posto era già libero.
     */
    public static void cancella() throws PrenotazioneInesistenteException {
        System.out.println("Cancellazione prenotazione:");
        int fila = chiediFila();
        int posto = chiediPosto();
        if (sala[fila][posto] == LIBERO) {
            throw new PrenotazioneInesistenteException();
        } else {
            sala[fila][posto] = LIBERO;
        }
    }

    /**
     * Metodo che realizza l'esercizio 5,1. 
     * Chiede il posto che si vuole prenotare e verifica che non sia già prenotato. 
     * Se è già prenotato lancia un eccezione, se termina correttamente significa che la prenotazione è
     * andata a buon fine.
     * @throws PostoOccupatoException quando il posto è già prenotato.
     */
    public static void prenota() throws PostoOccupatoException {
        System.out.println("Prenotazione posto:");
        int fila = chiediFila();
        int posto = chiediPosto();
        if (sala[fila][posto] == OCCUPATO) {
            throw new PostoOccupatoException();
        } else {
            sala[fila][posto] = OCCUPATO;
        }
    }

    /**
     * Metodo che realizza l'esercizio 4. Per ogni possibile input si delega il
     * compito ai metodi che realizzano l'operazione dedicata. Ogni metodo può
     * causare un eccezione specifica definita da noi. In caso di eccezione il
     * metodo ritorna false, per significare che l'operazione non è andata a
     * buon fine.
     *
     * @param input il carattere che rappresenta l'input dell'utente
     * @return true se l'operazione selezionata va a buon fine.
     */
    public static boolean operazione(char input) {
        switch (input) {
            case PRENOTA:
                try {
                    prenota();
                    return true;
                } catch (PostoOccupatoException e) {
                    System.err.print(e);
                    return false;
                }
            case CANCELLA:
                try {
                    cancella();
                    return true;
                } catch (PrenotazioneInesistenteException e) {
                    System.err.print(e);
                    return false;
                }
            case ESCI:
                esci();
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Metodo che realizza l'esercizio 3. Propone un menu all'utente e controlla
     * l'input. Quando l'input è corretto il metodo termina e passa l'input al
     * chiamante.
     *
     * @return il carattere scelto dall'utente
     */
    public static char menu() {
        System.out.println("Inserire prossima operazione:\n\t- effettua prenotazione (" + PRENOTA + ")\n\t- cancella prenotazione (" + CANCELLA + ")\n\t- salva ed esci (" + ESCI + ")");
        boolean ok;
        char scelta;
        do {
            scelta = scanner.next().charAt(0);
            ok = (scelta == PRENOTA) || (scelta == CANCELLA) || (scelta == ESCI);
            if (!ok) {
                System.out.print("Input non valido. Rifare: ");
            }
        } while (!ok);
        return scelta;
    }

    /**
     * Metodo per realizzare l'esercizio 2. Scorre il file una volta per
     * scoprire le dimensioni della sala, poi resetta il buffer per rileggere e
     * inizializzare lo stato della sala.
     *
     */
    public static void initDaFile() {
        BufferedReader reader;
        try {
            // Provo ad aprire lo stream e a leggere il file:
            reader = new BufferedReader(new FileReader(fileSala));
            
            // variabile String per leggere una riga di testo alla volta.
            String currentLine;
            
            // variabile per contare le linee e capire le dimensioni della sala:
            int numeroRighe = 0;
            int numeroColonne = 0;
            
            while ((currentLine = reader.readLine()) != null) {
                numeroRighe++;
                numeroColonne = currentLine.length();
                System.out.println("Lettura file " + fileSala.getName() + "; \t riga " + numeroRighe + ":\t" + currentLine);
            }
            
            // ho letto il file, contato le righe e le colonne, posso istanziare la matrice.
            sala = new boolean[numeroRighe][numeroColonne];

            // Adesso bisogna inizializzarne lo stato per sincronizzarlo a quello del file:
            // resetto il buffer e riparto da capo per rileggere il file:
            reader.close();
            reader = new BufferedReader(new FileReader(fileSala));
            for (int lineNum = 0; lineNum < numeroRighe; lineNum++) {
                currentLine = reader.readLine();
                for (int charNum = 0; charNum < numeroColonne; charNum++) {
                    // inizializzo lo stato di ogni posto come letto da file.
                    sala[lineNum][charNum] = (currentLine.charAt(charNum) == LIBERO_FILE ? LIBERO : OCCUPATO);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(D4mnAsciiCinema.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo che realizza l'esercizio 1. Chiede le dimensioni della sala
     * all'utente e inizializza la sala vuota.
     */
    public static void initDaUser() {
        System.out.print("Numero file di posti?  ");
        int righe = scanner.nextInt();
        System.out.print("Numero posti per fila? ");
        int colonne = scanner.nextInt();

        // istanzio la sala
        sala = new boolean[righe][colonne];
        
        // inizializzazione della sala
        for (int r = 0; r < righe; r++) {
            for (int c = 0; c < colonne; c++) {
                sala[r][c] = LIBERO;
            }
        }
    }

    /**
     * Metodo che realizza l'esercizio -1. Interfaccia grafica per presentare
     * velocemente lo stato della sala in ogni momento.
     */
    public static void stampaSala() {
        System.out.println("Stato attuale della sala:");
        System.out.print("┌");
        for (int c = 0; c < sala[0].length / 2 - 3; c++) {
            System.out.print("───");
        }
        if (sala[0].length % 2 == 1) {
            System.out.print("───┤█████████████├");
        } else {
            System.out.print("┤████████████████├");
        }
        for (int c = sala[0].length / 2 + 3; c < sala[0].length; c++) {
            System.out.print("───");
        }
        System.out.println("┐");


        for (boolean[] fila : sala) {
            System.out.print("├");
            for (int c = 0; c < fila.length; c++) {
                System.out.print("───");
            }
            System.out.println("┤");
            System.out.print("│");
            for (int c = 0; c < sala[0].length; c++) {
                System.out.print(fila[c] == OCCUPATO ? "└o┘" : "└─┘");
            }
            System.out.println("│");
        }
        System.out.print("└");
        for (int c = 0; c < sala[0].length; c++) {
            System.out.print("───");
        }
        System.out.println("┘");
        System.out.println();
    }

}

// CREO LE ECCEZIONI UTILI A GESTIRE I CASI PARTICOLARI DOVUTI AD ERRORI NELL'UTILIZZO DELL'APPLICAZIONE DA PARTE DELL'UTENTE.

/**
 * Eccezione per i tentativi di prenotazione di un posto già occupato.
 * @author damiano
 */
class PostoOccupatoException extends RuntimeException {
    PostoOccupatoException() {
        super("Il posto che si cerca di prenotare è già occupato.");
    }
}

/**
 * Eccezione per i tentativi di cancellazione di una prenotazione in un posto che è già libero.
 * @author damiano
 */
class PrenotazioneInesistenteException extends RuntimeException {
    PrenotazioneInesistenteException() {
        super("Il posto per cui si cerca di eliminare la prenotazione è già libero.");
    }
}
