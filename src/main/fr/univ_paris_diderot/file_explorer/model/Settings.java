package fr.univ_paris_diderot.file_explorer.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import fr.univ_paris_diderot.utils.*;

/**
 * 
 * La classe {@code Settings} sert à paramétrer une une application.
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * @see fr.univ_paris_diderot.file_explorer.ApplicationGUI Application
 * 
 */
public final class Settings  {

	// --------------------------------- ----------- Variables ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Le chemin du répertoires où se situe les fichier de configuration
	 */
	/**
	 * Le nom du fichier de configuration par défaut
	 */
	private static final String DEFAULT_PATH = "default_settings.conf";
	/**
	 * Le nom du fichier de configuration personalisé
	 */
	private static final String NORMAL_PATH = "settings.conf";
	
	/**
	 * {@code Object} servant de commutateur entre {@code Settings} et {@code Json}
	 */
	private static final Gson gson = new GsonBuilder().create();

	/** Réglage des favoris */
	private List<String> favorites;
	/** Réglage pour la visualisation des fichiers images */
	private List<String> imageExtensions;
	/** Réglage pour la visualisation des fichiers textess */
	private List<String> textExtensions;
	/** Reglage pour l'affichage des fichiers caché */
	private boolean showHiddenFiles;

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//



	// --------------------------------- --------- Setters & Getters ------ --------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Obtenir la liste des favoris.
	 * 
	 * @return {@code favorites}, La listes des favoris
	 */
	public List<String> getFavorites() {
		return favorites;
	}

	/**
	 * 
	 * @return {@code imageExtensions}
	 */
	public List<String> getImageExtensions() {
		return imageExtensions;
	}

	/**
	 * 
	 * @return {@code textExtensions}
	 */
	public List<String> getTextExtensions() {
		return textExtensions;
	}

	/**
	 * 
	 * Obtenir le statut de la gestion des fcihiers caché
	 * 
	 * @return {@code true} si les fichiers peuvent être afficher
	 */
	public boolean getShowHiddenFiles(){
		return showHiddenFiles;
	}

	/**
	 * 
	 * Changer la liste des favoris
	 * 
	 * @param l la nouvelle liste de favoris
	 */
	public void setFavorites(List<String> favorites) {
		this.favorites = favorites;
	}

	/**
	 * 
	 * Changer la liste d'extension d'image
	 * 
	 * @param ext la nouvelle liste des extensions
	 */
	public void setImageExtensions(List<String> ext) {
		this.imageExtensions = ext;
	}

	/**
	 * 
	 * Changer la liste d'extension de texte
	 * 
	 * @param ext la nouvelle liste des extensions
	 */
	public void setTextExtensions(List<String> ext) {
		this.textExtensions = ext;
	}

	/**
	 * 
	 * Changer la visibilité des fichiers caché
	 * 
	 * @param showHiddenFiles la nouvelle visibilité
	 */
	public void setShowHiddenFiles(boolean showHiddenFiles) {
		this.showHiddenFiles = showHiddenFiles;
	}


	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//






	// --------------------------------- ---------- Création ------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Créer un objet à partir d'un fichier de configuration.
	 * Prends le fichier par défaut si aucune sauvegarde n'a été précedemment réalisée.
	 * 
	 * @return Le paramètrage crée
	 */
	public static Settings create(){

		File settingsDirectory = Util.getFileFromRessource(Util.getSettingsFolderPath());
		String errMessage = "Internal breakdown, missing settings folder";

		if (settingsDirectory != null){

			File settingsFile = new File(settingsDirectory, NORMAL_PATH);

			try {

				if (! settingsFile.exists()) settingsFile = new File(settingsDirectory, DEFAULT_PATH);
				var settingsFileContent = new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8);

				Settings settings = gson.fromJson(settingsFileContent, Settings.class);
				Log.d("Created " + Util.printObject(settings) + " from " + settingsFile.getName());
				return settings;

			} catch (IOException e) {
				errMessage = "Internal breakdown, missing settings file";
			} catch(JsonSyntaxException e){
				errMessage = "Internal breakdown, incorrect parsing of settings file";
			}
			
		}

		Log.e(errMessage);
		return null;
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ---------- Sauvegarde ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Sauvegarde les valeurs actuelles de l'objet dans le fichier de sauvegarde
	 */
	public void save(){
		Log.i( Util.printObject(this) + " saving ...");

		File settingsDirectory = Util.getFileFromRessource(Util.getSettingsFolderPath());
		String errMessage = "Internal breakdown, missing settings folder";

		if (settingsDirectory != null){

			File settingsFile = new File(settingsDirectory, NORMAL_PATH);

			try {

				if (! settingsFile.exists()) {
					settingsFile.createNewFile();
					Log.d("File " + settingsFile.getName() + ": created");
				}

				FileOutputStream os = new FileOutputStream(settingsFile);
				os.write(gson.toJson(this).getBytes());
				Log.d("File " + settingsFile.getName() + ": updated");
				os.close();

				Log.i("... " +  Util.printObject(this) + " saving succeed");
				return;

			} catch (IOException e) {
				errMessage = "Setting file can't be created";
			} catch(JsonSyntaxException e){
				errMessage = "Internal breakdown, incorrect parsing of settings file";
			}
			
		}

		Log.e(errMessage);
		Log.i("... Saving failed");
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

}
