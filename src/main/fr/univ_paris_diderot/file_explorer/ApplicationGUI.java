package fr.univ_paris_diderot.file_explorer;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import fr.univ_paris_diderot.file_explorer.controller.MainController;
import fr.univ_paris_diderot.file_explorer.controller.MutableLiveData.Observer;
import fr.univ_paris_diderot.file_explorer.model.Settings;
import fr.univ_paris_diderot.file_explorer.view.frame.MainWindow;
import fr.univ_paris_diderot.utils.*;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Application graphique permétant de visualiser et manipuler des fichiers.
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 */
public class ApplicationGUI {

	// --------------------------------- ----------- Variables ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	
	/** Le nom du fichier de theme par défaut */
	private static final String DEFAULT_THEME_PATH = Util.getResourcesFolder() + "/theme/default_theme.xml";
	/** Le nom du répertoire des langages */
	private static final String LANG_FILE_PATH = Util.getResourcesFolder() + "/lang/LabelsBundle";

	/** Les paramètre encadrant l'application */
	private Settings settings;
	/** Le controlleur gérant l'application */
	private MainController controller;
	/** La fenêtre graphique principale de l'application */
	private MainWindow window;
	/** L'action à effectuer demender par le controlleur */
	private Runnable action;
	/** Le verrou servant à annoncer l'application qu'une action doit être effetuer */
	public Object lock = new Object();
	/** Le statut de l'application */
	private boolean isRunning = false;

	/** Le répertoire racine */
	private static final File homeFolder;
	/** Le répertoire poubelle */
	private static final File trashFolder;

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ----------- Création ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	static{

		homeFolder = new File(System.getProperty("user.home"));
		var url = ApplicationGUI.class.getClassLoader().getResource(".trash");
		trashFolder = new File(url.getPath());
	}

	/**
	 * Contructeur d'une application
	 * @throws Exception Si l'application ne peut pas être paramétrer
	 */
	private ApplicationGUI() throws Exception{

		settings = Settings.create();
		if (settings == null) throw new Exception();

		var favFiles = settings.getFavorites();
		if (!favFiles.contains(homeFolder.getAbsolutePath()))
			favFiles.add(homeFolder.getAbsolutePath());
		
		if (! favFiles.contains(trashFolder.getAbsolutePath()))
			favFiles.add(trashFolder.getAbsolutePath());

		controller = new MainController(settings);
		observeMainController();
		Log.d("Created " + Util.printObject(this));
	}

	/**
	 * Création d'une application
	 * @return l'application créer ou imprime une erreur sur le fichier de Logging
	 */
	private static ApplicationGUI launch(){
		Log.i("Launching ...");

		try {

			ApplicationGUI app = new ApplicationGUI();
			Log.i("... Launching succeed");
			return app;
		} catch (Exception e) {

			Log.i("... Launching failed");
			return null;
		}
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//


	public static File getHomefolder() {
		return homeFolder;
	}

	public static File getTrashfolder() {
		return trashFolder;
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ------------ Lancement ---------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Lancement de l'application. 
	 * <ul>
	 * <li> Crée un fênetre principale </li>
	 * <li> Attends les évennements du controlleur </li>
	 * </ul>
	 */
	public void start(){

		Log.i(Util.printObject(this) + " starts");

		isRunning = true;
		ResourceBundle labels = ResourceBundle.getBundle(LANG_FILE_PATH, Locale.getDefault());

		try {
			// Création des valeurs par défaut

			controller.currentFavoritesData.setValueNoAlert(controller.currentFavoritesData.getValue());
			changeFileTree(homeFolder.getAbsolutePath());

			// Installation des polices
			GraphicsEnvironment GE = GraphicsEnvironment.getLocalGraphicsEnvironment();
			List<String> AVAILABLE_FONT_FAMILY_NAMES = Arrays.asList(GE.getAvailableFontFamilyNames());
			var fontDir = new File(Util.getFontsFolder());
			if (fontDir.isDirectory()){

				var fontList = fontDir.listFiles();

				for (var font: fontList){

					Font f = Font.createFont(Font.TRUETYPE_FONT, font);
					if (!AVAILABLE_FONT_FAMILY_NAMES.contains(f.getFontName())) {
						GE.registerFont(f);
					}

				}
			}
	
			// Installation du theme
			SynthLookAndFeel look = new SynthLookAndFeel();
			URL url = ApplicationGUI.class.getClassLoader().getResource(DEFAULT_THEME_PATH);
			look.load(url.openStream(), ApplicationGUI.class);
			UIManager.setLookAndFeel(look);
			
			SwingUtilities.invokeLater(() -> {
				
				// Creation de l'interface graphique
				Thread.currentThread().setName("Graphics");
				window = new MainWindow(controller, labels);
				window.setVisible(true);
			});

		} catch (Exception e) {
			e.printStackTrace();
			isRunning = false;
		}

		// Boucle principale de l'application
		while (isRunning) {

			try {
				
				synchronized(lock){
					lock.wait(); // Attente d'evennement de l'application
				}
				if (action != null) action.run();
	

			} catch (Exception e) {}
			
			if (! isRunning)
				Log.i(Util.printObject(this) + " stop");
		}
		
		close();
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ---------- Observeurs ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Gestion du lorsque les valeurs du controlleur ont changés.
	 * 
	 * @see ApplicationGUI#stop()
	 * @see ApplicationGUI#changeCurrentFile(String)
	 * @see ApplicationGUI#handleNewFavorites(String)
	 * @see ApplicationGUI#handleNewImageExtension(String)
	 * @see ApplicationGUI#handleNewTextExtension(String)
	 * @see ApplicationGUI#handleDeleteFavorites(String)
	 * @see ApplicationGUI#handleDeleteImagesExtension(String)
	 * @see ApplicationGUI#handleDeleteTextExtension(String)
	 * @see ApplicationGUI#handleShowHiddenFile(boolean)
	 * @see ApplicationGUI#handleNewSearch(String)
	 * @see ApplicationGUI#handleFileMovement(String, String)
	 * @see ApplicationGUI#handleFileRenaming(String, String)
	 * @see ApplicationGUI#handleFileRemoving(String)
	 * 
	 */
	private void observeMainController(){

		controller.isOpenLiveData.observe(new Observer<>((t) -> { if (!t) stop();}));

		controller.selectedFolderData.observe(new Observer<>((newFolderPath) -> {
			synchronized(lock){
				action = () -> changeFileTree(newFolderPath);
				lock.notify();
			}
		}));

		controller.selectedFileData.observe(new Observer<>((newFilePath) -> {
			synchronized(lock){
				action = () -> changeCurrentFile(newFilePath);
				lock.notify();
			}
		}));

		controller.newFavoritesData.observe(new Observer<>((newFav) -> {
			synchronized(lock){

				handleNewFavorites(newFav);
				lock.notify();
			}
		}));

		controller.newImageExtData.observe(new Observer<>((newExt) -> {
			synchronized(lock){

				handleNewImageExtension(newExt);
				lock.notify();
			}
		}));

		controller.newTxtExtData.observe(new Observer<>((newExt) -> {
			synchronized(lock){

				handleNewTextExtension(newExt);
				lock.notify();
			}
		}));

		controller.delFavoritesData.observe(new Observer<>((fav) -> {
			synchronized(lock){

				handleDeleteFavorites(fav);
				lock.notify();
			}
		}));

		controller.delImageExtData.observe(new Observer<>((ext) -> {
			synchronized(lock){

				handleDeleteImagesExtension(ext);
				lock.notify();
			}
		}));

		controller.delTxtExtData.observe(new Observer<>((ext) -> {
			synchronized(lock){

				handleDeleteTextExtension(ext);
				lock.notify();
			}
		}));

		controller.isHiddenVisibleData.observe(new Observer<>((show) -> {
			synchronized(lock){

				handleShowHiddenFile(show);
				lock.notify();
			}
		}));

		controller.searchRequestData.observe(new Observer<>((searh) ->{
			synchronized(lock){

				handleNewSearch(searh);
				lock.notify();
			}
		}));

		controller.fileToMovedData.observe(new Observer<>((file) -> {
			synchronized(lock){

				handleFileMovement(file, controller.destinationFolderData.getValue());
				lock.notify();
			}
		}));

		controller.newFileNameData.observe(new Observer<>((newName) -> {

			synchronized(lock){

				handleFileRenaming(controller.fileToRenameData.getValue(), newName);
				lock.notify();
			}
		}));

		controller.fileToDeleteData.observe(new Observer<>((path) -> {

			synchronized(lock){

				handleFileRemoving(path);
				lock.notify();
			}
		}));
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ---- Manipulation de fichier ---- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Change le fichier courant de l'application
	 * 
	 * @param filename le nom du fichier
	 * 
	 */
	private void changeCurrentFile(String filename){

		File f = new File(filename);

		if (f.exists()) {
			
			var ext = Util.getFileExtension(filename);

			if ( Util.inList(settings.getTextExtensions(), ext)){
				
				try {
					var fcontent = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
					controller.currentFileContentData.setValueNoAlert(fcontent);
				} catch (IOException e) {
					controller.currentFileContentData.setValueNoAlert(null);
				}
			}

			else if ( Util.inList(settings.getImageExtensions(), ext)) {

				try {
					var fcontent = ImageIO.read(f);
					controller.currentFileContentData.setValueNoAlert(fcontent);
				} catch (IOException e) {
					controller.currentFileContentData.setValueNoAlert(null);
				}
			}

			else controller.currentFileContentData.setValueNoAlert(null);

			controller.currentFileData.setValue(f);
		}
	}

	/**
	 * 
	 * Change toute l'arborescence de fichier actuelle selon un nouveau noeud
	 * 
	 * @param newFolderPath le nouveau noeud
	 * 
	 */
	private void changeFileTree(String newFolderPath){

		if (newFolderPath == null) return;

		var tree = new LinkedList<File[]>();
		var d = new File(newFolderPath);
		var exit = false;

		if (!d.isDirectory())
			return;

		while(! exit){

			if ( FilenameUtils.equals(d.getAbsolutePath(), homeFolder.getAbsolutePath()) )
				exit = true;

			tree.addFirst(d.listFiles());
			d = d.getParentFile();
		}

		controller.currentFolderData.setValue(newFolderPath);
		controller.filesTreeData.setValue(tree);

	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- -------------- Arrêt ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Arrête l'application
	 * 
	 */
	private void stop(){

		synchronized(lock){
			isRunning = false;
			lock.notifyAll();
		}
		
	}

	/**
	 * 
	 * Ferme l'application
	 * 
	 */
	private void close(){
		settings.save();
		Log.i(Util.printObject(this) + " closed");
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ----------- Principale ---------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/** 
	 * @hidden
	 * @param args nothing
	 * */
	public static void main(String[] args) {
		
		Log.init();
		Log.i("Program started");

		ApplicationGUI app = launch();
		if (app != null) app.start();

		Log.i("Program exited properly");
		Log.close();
		System.exit(0);;
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//






	// --------------------------------- --- Manipulation des paramètre -- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Gerer l'ajout d'un nouveau favoris
	 * 
	 * @param fav le nouveau favoris
	 * 
	 */
	private void handleNewFavorites(String fav){

		if (fav == "")
			return;

		File f = new File(fav);

		if (f.exists()){

			List<String> l = controller.currentFavoritesData.getValue();

			if (! l.contains(fav)){
				l.add(fav);
				controller.currentFavoritesData.setValue(l);
				settings.setFavorites(l);
			}
		}
	}

	/**
	 * 
	 * Gerer l'ajout d'une nouvelle extension d'image
	 * 
	 * @param ext la nouvelle extension
	 * 
	 */
	private void handleNewImageExtension(String ext){

		if (ext == "" || ext.length() > 10)
			return;

		List<String> l = controller.currentImagesExtensionsData.getValue();

		if (! l.contains(ext)){
			l.add(ext);
			controller.currentImagesExtensionsData.setValue(l);
			settings.setImageExtensions(l);
		}
	}

	/**
	 * 
	 * Gerer l'ajout d'une nouvelle extension de texte
	 * 
	 * @param ext la nouvelle extension
	 * 
	 */
	private void handleNewTextExtension(String ext){

		if (ext == "" || ext.length() > 10)
			return;

		List<String> l = controller.currentTextExtensionsData.getValue();

		if (! l.contains(ext)){
			l.add(ext);
			controller.currentTextExtensionsData.setValue(l);
			settings.setTextExtensions(l);
		}
	}

	/**
	 * 
	 * Gerer la suppression d'un favoris
	 * 
	 * @param fav le favoris
	 * 
	 */
	private void handleDeleteFavorites(String fav){

		if ( FilenameUtils.equals(fav, homeFolder.getAbsolutePath()) )
			return;

		if ( FilenameUtils.equals(fav, trashFolder.getAbsolutePath()) )
			return;

		List<String> l = controller.currentFavoritesData.getValue();

		if (! l.contains(fav))
			return;

		l.remove(fav);
		controller.currentFavoritesData.setValue(l);
		settings.setFavorites(l);
	}

	/**
	 * 
	 * Gerer la suppression d'une extension d'image
	 * 
	 * @param ext l'extension
	 * 
	 */
	private void handleDeleteImagesExtension(String ext){

		List<String> l = controller.currentImagesExtensionsData.getValue();

		if (! l.contains(ext))
			return;

		l.remove(ext);
		controller.currentImagesExtensionsData.setValue(l);
		settings.setImageExtensions(l);
	}

	/**
	 * 
	 * Gerer la suppression d'une extension de texte
	 * 
	 * @param ext l'extension
	 * 
	 */
	private void handleDeleteTextExtension(String ext){

		List<String> l = controller.currentTextExtensionsData.getValue();

		if (! l.contains(ext))
			return;

		l.remove(ext);
		controller.currentTextExtensionsData.setValue(l);
		settings.setTextExtensions(l);
	}

	/**
	 * 
	 * Gerer la modification de la visibilité des fichiers cachés
	 * 
	 * @param show la nouvelle visibilité
	 * 
	 */
	private void handleShowHiddenFile(boolean show){

		settings.setShowHiddenFiles(show);
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- --- Gestion de la recherche ----- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Performe la recherche via l'outil find
	 * 
	 * @param search l'exppression à rechercher
	 * 
	 */
	private void handleNewSearch(String search){

		new Thread(){

			@Override
			public void run() {
				setName("Search");

				try{
					Runtime runtime = Runtime.getRuntime();
					var cmd = "find " + controller.currentFolderData.getValue() + " -name " + search;
					Process process = runtime.exec(cmd);
		
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = null;
					ArrayList<File> l = new ArrayList<>();
		
					while ((line = input.readLine()) != null)
						l.add(new File(line));
		
					input.close();
		
					
					var arr = new File[l.size()];
					arr = l.toArray(arr);
					controller.searchResponseData.setValue(arr);
					
				}
		
				catch(Exception e){}
				
				super.run();
			}
		}.start();

		
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// ---------------------------- - Gestion des action sur les fichiers - --------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Gérer le mouvement de fichier
	 * 
	 * @param path le chemein du fichier source
	 * @param dest le chemin de la destination
	 * 
	 */
	private void handleFileMovement(String path, String dest){

		try {
			
			var from = new File(path);
			var to = new File(dest);

			if (from.exists() && to.exists() && to.isDirectory()){

				if (FilenameUtils.equals(path, dest))
					return;

				else if (FilenameUtils.equals(path, homeFolder.getAbsolutePath()))
					return;

				else if (FilenameUtils.equals(path, trashFolder.getAbsolutePath()))
					return;
	
				else if (from.isDirectory())
					FileUtils.moveDirectoryToDirectory(from, to, false);

				else
					FileUtils.moveFileToDirectory(from, to, false);

				validateFileModification(path, FilenameUtils.concat(dest, FilenameUtils.getBaseName(path)));
			}

		} catch (Exception e) {
			
		}

	}

	/** 
	 * 
	 * Gestion du rennomage de fichier
	 * 
	 * @param path le chemin vers le fichier à rennomer
	 * @param newName le nouveau nom du fichier
	 * 
	 */
	private void handleFileRenaming(String path, String newName){

		try {
		
			if (path.equals(""))
				return;

			else if (FilenameUtils.equals(path, homeFolder.getAbsolutePath()) || 
				FilenameUtils.equals(path, trashFolder.getAbsolutePath()))
				return;

			else {

				var src = new File(path);
				var dest = new File(src.getParentFile(), newName );

				if (src.isDirectory())
					FileUtils.moveDirectory(src, dest);
				else 
					FileUtils.moveFile(src, dest);

				validateFileModification(path, dest.getAbsolutePath());
			}

		} catch (Exception e) {
			
			
		}
	}

	/**
	 * 
	 * Gestion de la suppression d'un fichier.
	 * Déplacement dans le répertoire poubelle.
	 * 
	 * @param path Chemin du fichier à supprimer
	 * 
	 */
	private void handleFileRemoving(String path){

		try {
			
			if (path == null ||  path.equals(""))
				return;

			else if (FilenameUtils.equals(path, homeFolder.getAbsolutePath()) || 
				FilenameUtils.equals(path, trashFolder.getAbsolutePath()))
				return;

			else {

				File f = new File(path);

				if (! f.exists())
					return;

				else if (f.isDirectory())
					FileUtils.moveDirectoryToDirectory(f, trashFolder, false);

				else
					FileUtils.moveFileToDirectory(f, trashFolder, false);

				validateFileModification(path, FilenameUtils.concat(trashFolder.getAbsolutePath(), FilenameUtils.getBaseName(path)));
			}

		} catch (Exception e) {
			
		}
	}

	/**
	 * 
	 * Validation de la modification d'un fichier.
	 * Envoie des modification au controlleur.
	 * 
	 * @param path Chemin de l'ancien fichier
	 * @param newPath Chemin du nouveau fichier
	 * 
	 */
	private void validateFileModification(String path, String newPath){

		var favs = settings.getFavorites();
		if (favs.contains(path)){
			favs.remove(path);
			favs.add(newPath);
			controller.currentFavoritesData.setValue(favs);
		}
		
		changeFileTree(controller.currentFolderData.getValue());
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
}