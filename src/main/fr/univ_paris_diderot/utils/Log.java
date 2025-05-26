package fr.univ_paris_diderot.utils;

import java.lang.Thread;
import java.sql.Timestamp;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * {@code Log} Permet d'afficher des informations de Logging sur la sortie standard (d'erreur si nécessaire)
 * ou de stocker ces même infromations dans un fichier spécifique.
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * 
 */
public final class Log{

	/** @hidden */
	private Log(){}

	//  --------------------------------- ----- ----- Varaibles ----- ----- --------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Le nom du répertoire de débuggage.
	 */
	private static final String LOG_DIR_NAME = "log";
	/**
	 * Le fichier de debuggage.
	 */
	private static FileOutputStream LOG_FILE_OUTPUTSTREAM = null;

	/**
	 * L'abbréviation pour les informations de debuggage.
	 */
	private static final String DEBUG_ABBR = "DBG";
	/**
	 * L'abbréviation pour les informations d'erreur.
	 */
	private static final String ERROR_ABBR = "ERR";
	/**
	 * L'abbréviation pour les informations verbeuse.
	 */
	private static final String VERBOSE_ABBR = "VRB";
	/**
	 * L'abbréviation pour les informations standard.
	 */
	private static final String INFO_ABBR = "INF";

	/**
	 * L'espacement entre les champs de débuggage.
	 */
	private static final String FIELD_SPACE = "   ";

	/**
	 * Fin d'un champs au cas où celui est trop longs.
	 */
	private static final String FIELD_TRAILLING = "...";

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ------------ Classes ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Mode définit le type d'information de Logging
	 * 
	 * @author Franc Zobo Nomo
	 * @since 1.0
	 * 
	 */
	private static enum Mode{

		/**
		 * Mode représente le type d'information à afficher.
		 */
		DEBUG{
			@Override
			public String toString(){return DEBUG_ABBR;}
		},
		/**
		 * Infoomation d'erreur.
		 */
		ERROR{
			@Override
			public String toString(){return ERROR_ABBR;}
		},
		/**
		 * Information verbeuse.
		 */
		VERBOSE{
			@Override
			public String toString(){return VERBOSE_ABBR;}
		},
		/**
		 * Information standard.
		 */
		INFO{
			@Override
			public String toString(){return INFO_ABBR;}
		}
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- --- Initialisaion & fermeture --- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Crée un fichier servant à stocké toutes les informations de Logging.
	 * 
	 */
	public static void init(){

		Thread.currentThread().setName("Main");
		File logDirFile =  Util.getFileFromRessource(LOG_DIR_NAME);
		Date now = new Date();

		Log.i("Initialization ...");

		if ( (logDirFile == null) || (!logDirFile.exists()) || (!logDirFile.isDirectory())){
			Log.e("File structure has changed");
			Log.e("Cannot start Log module");
			Log.i("... Initialization failed");
			return;
		}

		String logFileName = String.format("ProgramTrace[%1$tY-%1$tm-%1$td]", now);
		short counter = 0;
		File logFile;
		String logFilePath;

		do {
			logFilePath = (counter > 0)?
				String.format("%s/%s(%d).log", LOG_DIR_NAME, logFileName, counter + 1):
				String.format("%s/%s.log", LOG_DIR_NAME, logFileName);
			logFile = new File(logFilePath);
			counter++;
		} while(logFile != null && logFile.exists());

		try{

			logDirFile.createNewFile();
			Log.d("File " + logFilePath + ": created");
			FileOutputStream os = new FileOutputStream(logFile);
			Log.i("... Initialization succeed");
			LOG_FILE_OUTPUTSTREAM = os;
		} catch(Exception e){

			Log.e("File " + logFilePath + " can't be created");
			Log.i("... Initialization failed");
			return;
		}
	}

	/**
	 * 
	 * Ferme le fichier servant à stocker des informations de Logging.
	 */
	public static void close(){

		if (LOG_FILE_OUTPUTSTREAM != null){
			
			try {
				LOG_FILE_OUTPUTSTREAM.close();
				Log.i("Log closed");
			} catch (Exception e) {
				Log.e("Log File: can not be closed");
			}
		}
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//



	// --------------------------------- ------------ Affichage ---------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//


	/**
	 * 
	 * Affiche les informations sur la sortie standard (d'erreur dans le cas échéant)
	 * ou stocke les informations dans le fichier référencé par {@link Log#LOG_FILE_OUTPUTSTREAM LOG_FILE_OUTPUTSTREAM}.
	 * 
	 * <p>
	 * Les informations sont constitué de plusieurs champs:
	 * </p>
	 * 
	 * <ul>
	 * <li>Le temps</li>
	 * <li>Le type</li>
	 * <li>Le thread</li>
	 * <li>La classe</li>
	 * <li>Le message</li>
	 * </ul>
	 * 
	 * 
	 * @param mod type d'inforamtion de logging
	 * @param mess message associé à l'information
	 * @see Log#fieldCreator(String, int, boolean) fieldCreator()
	 * @see Log#getClassName() getClassName()
	 */
	private static void print(Mode mod, String mess){
		String data;
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		String time_field = String.format("%1$tH:%1$tM:%1$tS.%1$tL", timestamp);
		String mode_field = "[" + mod + "]";
		String context_field = fieldCreator(Thread.currentThread().getName(), 10, true);
		String class_field = fieldCreator(getClassName(), 50, false);
		String message_field = mess;

		data = 	time_field + FIELD_SPACE +
				mode_field + FIELD_SPACE +
				context_field + FIELD_SPACE +
				class_field + ":" + FIELD_SPACE +
				message_field + "\n";

		try {

			LOG_FILE_OUTPUTSTREAM.write(data.getBytes());
			if (mod == Mode.ERROR) System.err.print(data);
		} catch (Exception e) {

			if (mod != Mode.ERROR) System.out.print(data);
			else System.err.print(data);
		}
	}

	/**
	 * 
	 * Imprime le message spécifier en tant qu'information de débuggage.
	 * 
	 * @param mess message associé à l'information
	 * @see Log#print(Mode, String) print()
	 * 
	 */
	public static void d(String mess){

		print(Mode.DEBUG, mess);
	}

	/**
	 * 
	 * Imprime le message spécifier en tant qu'information d'erreur.
	 * 
	 * @param mess message associé à l'information
	 * @see Log#print(Mode, String) print()
	 * 
	 */
	public static void e(String mess){

		print(Mode.ERROR, mess);
	}

	/**
	 * 
	 * Imprime le message spécifier en tant qu'information verbeuse.
	 * 
	 * @param mess message associé à l'information
	 * @see Log#print(Mode, String) print()
	 * 
	 */
	public static void v(String mess){

		print(Mode.VERBOSE, mess);
	}

	/**
	 * 
	 * Imprime le message spécifier en tant qu'information standard.
	 * 
	 * @param mess message associé à l'information
	 * @see Log#print(Mode, String) print()
	 * 
	 */
	public static void i(String mess){
	
		print(Mode.INFO, mess);
	}

	/**
	 * 
	 * Diminue ou augment une chaîne pour correspondre à la taille demandé.
	 * 
	 * @param content la chaîne à manipulé
	 * @param width la taille souhaité
	 * @param at_end diminué à la fin
	 * @return la chaîne à la taille souhaité
	 * 
	 */
	private static String fieldCreator(String content, int width, boolean at_end){

		String subField;

		if (content.length() > width){

			if (at_end)
				subField = content.substring(0, width - FIELD_TRAILLING.length()) + FIELD_TRAILLING;
			else 
				subField = FIELD_TRAILLING + content.substring(content.length() - width + FIELD_TRAILLING.length(), content.length());
		}

		else {

			String format = "%1$-" + String.valueOf(width) + "s";
			subField = String.format(format, content);
		}

		return subField;
	}

	/**
	 * 
	 * Renvoie le nom de la classe ayant appéler une méthode de Log.
	 * 
	 * @return la classe d'un objet ayant fait appel à une méthode de Log
	 */
	private static String getClassName(){
		try {
			throw new Exception();
		} catch (Exception e) {
			return e.getStackTrace()[3].getClassName();
		}
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

}