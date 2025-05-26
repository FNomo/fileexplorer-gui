package fr.univ_paris_diderot.utils;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


/**
 * 
 * {@code Util} contient des méthodes utiles.
 * 
 * @author Franc Zobo Nomo
 * @version 1.0
 * 
 */
public final class Util {

	/** Le nom du répertoire de ressource */
	private static final String RESOURCES_FOLDER_PATH = "res";
	/** Le repertoire des polices */
	private static final String FONT_FOLDER_PATH = RESOURCES_FOLDER_PATH + "/fonts";
	/** Le nom du répertoire des paramètre */
	private static final String SETTINGS_FOLDER_PATH = RESOURCES_FOLDER_PATH + "/settings";
	/** Le nom du répertoire des images */
	private static final String IMAGES_FOLDER_PATH = RESOURCES_FOLDER_PATH + "/images";

	/** @hidden */
	private Util(){}



	// --------------------------------- ------- Setters & Getters ------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * 
	 * Obtenir le chemin du repertoire de ressource
	 * 
	 * @return le chemin du répertoire de ressource
	 */
	public static String getResourcesFolder(){

		return RESOURCES_FOLDER_PATH;
	}

	/**
	 * 
	 * Obtenir le chemin du repertoire de polices
	 * 
	 * @return le chemin du répertoire de polices
	 */
	public static String getFontsFolder(){

		return FONT_FOLDER_PATH;
	}

	/**
	 * Obtenir le chemin du répertoire de paramètre.
	 * @return {@code SETTINGS_FOLDER_PATH}
	 */
	public static String getSettingsFolderPath() {
		return SETTINGS_FOLDER_PATH;
	}

	/**
	 * Obtenir le chemin du répertoire des images
	 * @return {@code IMAGES_FOLDER_PATH}
	 */
	public static String getImagesFolderPath() {
		return IMAGES_FOLDER_PATH;
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	
	/**
	 * 
	 * Renvoie un fichier spécifier par un chemin.
	 * 
	 * @param path le chemin menant au fichier
	 * @return Le fichier identifié par le chemin spécifier
	 * 
	 */
	public static File getFileFromRessource(String path){

		try{
			URL fileUrl = Util.class.getClassLoader().getResource(path);
			return new File(fileUrl.toURI());
		}
		catch(Exception e){
			var message = "File " + path + " can't be loaded";
			Log.e(message);
			return null;
		}
		
	}

		/**
		 * Traduit un objet en chaîne de caractères.
		 * @param <T> la classe de l'objet à traduire
		 * @param obj l'objet à traduire
		 * @return la rééprésentation de l'objet  en chaîne de caractères.
		 */
	public static final <T extends Object> String printObject(T obj){
		return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
	}

	/**
	 * Redimensionne une icone dans une dimension spécifier
	 * @param icon l'image à modifier
	 * @param dim la dimension requise
	 * @return une {@code ImageIcon} crée à partir de {@code icon} au dimension souhaité
	 */
	public static final ImageIcon resizeIcon(ImageIcon icon, Dimension dim){

		if (icon == null) return null;
		var img = icon.getImage();
		img = img.getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH);
		return new ImageIcon(img);
	}

	/**
	 * Obtenir l'extension d'un nom de fichier
	 * @param fname Le nom d'un fichier
	 * @return L'extension d'un nom de fichier
	 */
	public static final String getFileExtension(String fname){

		/**var realname = (fname.startsWith("."))? fname.replaceFirst("\\.", ""): fname;
		var array = realname.split("\\.");
		
		return (array.length < 2)? "": array[array.length - 1];*/
		return FilenameUtils.getExtension(fname);
	}

	/**
	 * Determine si un objet est dans un tableau
	 * @param <T> Le Type du tableau
	 * @param arr Le tableau à parcourir
	 * @param obj L'objet que l'on cherche
	 * @return {@code true} si l'objet est dans le tableau sinon {@code false}
	 */
	public static final <T extends Object> boolean inArray(T[] arr, T obj){

		for (var i: arr)
			if (i.equals(obj)) return true;

		return false;
	}

	/**
	 * Identique à {@code inArray} à l'exption que l'on parcourir une liste
	 * @see Util#inArray inArray()
	 * @param <T> Le Type de la liste
	 * @param l La liste à parcourir
	 * @param obj L'objet que l'on cherche
	 * @return {@code true} si l'objet est dans la liste sinon {@code false}
	 */
	public static final <T extends Object> boolean inList(List<T> l, T obj){

		return inArray( l.toArray(), obj);
	}

	/**
	 * Obtenir la taille en octet d'un fichier
	 * @param f le fichier à vérifier
	 * @return une chaîne de caractère informant la taille du fichier
	 */
	public static final String getSizeOf(File f){

		var size = FileUtils.sizeOf(f);

		if (size > 1000000000) 
			return (size / 1000000000.0) + " (Giga) ";

		else if (size > 1000000) 
			return (size / 1000000.0) + " (Mega) ";

		else if (size > 100) 
			return (size / 100.0) + " (Kilo) ";

		else 
			return size + " ";
	}

	/***
	 * Obtenir la date de création du fihcier
	 * @param f le fichier à consulté
	 * @return une représentation sous forme de chaîne de caractère de la date de création du fichier
	 */
	public static final Date getCreationDate(File f){
		
		try {

			FileTime creationTime = (FileTime) Files.getAttribute(f.toPath(), "creationTime");
			return new Date(creationTime.toMillis());
			
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Obtenir la dimension optimale pour une redimension
	 * @param img L'image à modifier
	 * @param askedWidth la largeur souhaitée
	 * @param askedHeight La hauteru souhaitée
	 * @return La dimension optimale correspondant au proportion.
	 */
	public static final Dimension getScaleDimension(Image img, int askedWidth, int askedHeight){
		if (img == null) return null;

		int w = img.getWidth(null);
		int h = img.getHeight(null);

		if (w > askedWidth){
			float ratio = (float) askedWidth / w;
			return new Dimension(askedWidth,  (int) (h * ratio));
		}

		else if (h > askedHeight){
			float ratio = (float) askedHeight / h;
			return new Dimension(Math.round(w * ratio), askedHeight);
		}

		else return new Dimension(w, h);
	}

	/**
	 * 
	 * Le chemin du fichier d'icone
	 * 
	 * @param name le nom de l'icone
	 * @param size la taille de l'icone
	 * @param c la couleur de l'icone
	 * 
	 * @return Le chemin du fichier d'icone
	 * 
	 */
	public static final URL getIconPath(String name, int size, String c){


		
		String path = "res/images/icon_" + name;
		if (size > 0) path += "_" + size + "px"; 
		if (!c.equals("")) path += "_" + c;
		path += ".png";
		
		var url = Util.class.getClassLoader().getResource( path );
		return url;
	}

}
