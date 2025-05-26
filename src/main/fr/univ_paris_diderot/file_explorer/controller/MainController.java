package fr.univ_paris_diderot.file_explorer.controller;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import fr.univ_paris_diderot.file_explorer.model.Settings;
import fr.univ_paris_diderot.utils.*;

/**
 * Controlleur principale de l'application
 * @author Franc Zobo Nomo
 * @version 1.0
 */
public class MainController {

	// --------------------------------- ---------- Variables ------------ ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	
	/** L'application est en route */
	public MutableLiveData<Boolean> isOpenLiveData = new MutableLiveData<>("isOpen" , true);
	/** Le répertoire courrant */
	public MutableLiveData<String> currentFolderData = new MutableLiveData<>("currentFolder", null);
	/** Le répertoire sélectionné */
	public MutableLiveData<String> selectedFolderData = new MutableLiveData<>("selectedFolder", null);
	/** Le fichier courant */
	public MutableLiveData<File> currentFileData = new MutableLiveData<>("currentFile", null);
	/** Le contenu du ficheir courant */
	public MutableLiveData<Object> currentFileContentData = new MutableLiveData<>("currentFileContent", null);
	/** Le chemin du fichier selectioner */
	public MutableLiveData<String> selectedFileData = new MutableLiveData<>("selectedFile", null);
	/** L'arborescence de fichier courante */
	public MutableLiveData<LinkedList<File[]>> filesTreeData =  new MutableLiveData<>("filesTree", null);
	/** Le nouveau favoris a ajouter */
	public MutableLiveData<String> newFavoritesData = new MutableLiveData<>("newFavorites", null);
	/** La nouvelle extension d'image a ajouter */
	public MutableLiveData<String> newImageExtData = new MutableLiveData<>("newImageExt", null);
	/** La nouvelle extension de texte a ajouter */
	public MutableLiveData<String> newTxtExtData = new MutableLiveData<>("newTextExt", null);
	/** Le nouveau favoris a ajouter */
	public MutableLiveData<String> delFavoritesData = new MutableLiveData<>("deleteFavorites", null);
	/** La nouvelle extension d'image a ajouter */
	public MutableLiveData<String> delImageExtData = new MutableLiveData<>("deleteImageExt", null);
	/** La nouvelle extension de texte a ajouter */
	public MutableLiveData<String> delTxtExtData = new MutableLiveData<>("deleteTextExt", null);
	/** La nouvelle recherche de texte */
	public MutableLiveData<String> searchRequestData = new MutableLiveData<>("searchRequest", null);
	/** La réponse de la recherche */
	public MutableLiveData<File[]> searchResponseData = new MutableLiveData<File[]>("searchResponse", null);

	/** La destination pour le déplacement d'un fichier*/
	public MutableLiveData<String> destinationFolderData = new MutableLiveData<>("destinationFolder", null);
	/** La destination pour le déplacement d'un fichier*/
	public MutableLiveData<String> fileToMovedData = new MutableLiveData<>("fileToMoved", null);

	/** Le fichier à rennomer */
	public MutableLiveData<String> fileToRenameData = new MutableLiveData<>("fileToRename", null);
	/** Le nouveau nom du fichier à rennomer */
	public MutableLiveData<String> newFileNameData = new MutableLiveData<>("newFileName", null);

	/** Le fichier à copier */
	public MutableLiveData<String> fileToDeleteData = new MutableLiveData<>("fileToDelete", null);

	/** Les fichiers caché peuvent être affichés */
	public MutableLiveData<Boolean> isHiddenVisibleData;
	/** Liste des favoris */
	public MutableLiveData<List<String>> currentFavoritesData;
	/** Liste des extensions de fichiers pour le visualiseur d'image */
	public MutableLiveData<List<String>> currentImagesExtensionsData;
	/** Liste des extensions de fichiers pour le visualiseur de texte */
	public MutableLiveData<List<String>> currentTextExtensionsData;

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ---------- Création ------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Contructeurs d'un controlleurs
	 * @param settings Les paramètre par défaut
	 */
	public MainController(Settings settings) {

		isHiddenVisibleData = new MutableLiveData<>("isHiddenVisible", settings.getShowHiddenFiles());
		currentFavoritesData = new MutableLiveData<>("currentFavorites", settings.getFavorites());
		currentImagesExtensionsData = new MutableLiveData<>("currentImagesExtensions", settings.getImageExtensions());
		currentTextExtensionsData = new MutableLiveData<>("currentTextExtensions", settings.getTextExtensions());

		Log.d("Created " +  Util.printObject(this) );
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

}
