package fr.univ_paris_diderot.file_explorer.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

import fr.univ_paris_diderot.utils.*;

/**
 * Variable mutable destinée à change
 * @author Franc Zobo Nomo
 * @version 1.0
 */
public class MutableLiveData<T>  {

	// --------------------------------- ----------- Classes ------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Observeur d'une variable mutable. Effectue une action lorsque celle-ci change.
	 * @author Franc Zobo Nomo
	 * @version 1.0
	 */
	public static class Observer<T>{

		/** Action à effectuer lors du changement */
		Consumer<T> onChangeAction;
		/** Indicateur de l'état de l'observeur */
		private boolean active = true;

		/**
		 * Constructeur de la classe
		 * @param onChangeAction action à effectuer
		 */
		public Observer(Consumer<T> onChangeAction){
			this.onChangeAction = onChangeAction;
		}

		/**
		 * 
		 * @return {@code true} si l'observeur est toujours actif
		 */
		public boolean isActive(){
			return active;
		}

		/***
		 * Changement de l'état de l'observeur
		 * @param active le nouvel état
		 */
		protected void setActive(boolean active){
			this.active = active;
		}

		/**
		 * Changement de valeur de la variable
		 * @param t La nouvelle valeur
		 */
		public void onChanged(T t){

			onChangeAction.accept(t);
		}
		
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//





	// --------------------------------- ----------- Variables ----------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/** Liste des observeurs rattaché à la variable */
	private LinkedList<Observer<T>> observers = new LinkedList<MutableLiveData.Observer<T>>();
	/** Nom de la variable */
	private String name;
	/** La valeur de la variable */
	private T value;

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ---------- Création ------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Constructeur d'une variable mutable
	 * @param name le nom de la variable
	 * @param value la valeur de la variable
	 */
	public MutableLiveData(String name, T value){

		this.name = name + "Data";
		this.value = value; 
		Log.d("Created " +  Util.printObject(this) + "(" + this.name +") with value [" + value + "]");
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//




	// --------------------------------- ------- Setters & Getters ------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Obtenir la valeur de la variable
	 * @return la valeur
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Change la valeur de la variable et alerte les observeurs.
	 * @param value la nouvelle valeur
	 * @see MutableLiveData#setValueNoAlert(Object) setValueNoAlert()
	 */
	public synchronized void setValue(T value) {

		setValueNoAlert(value);
		new Thread(() ->notifyObservers()).start();
	}

	/**
	 * Change la valeur de la variable
	 * @param value la nouvelle valeur
	 */
	public synchronized void setValueNoAlert(T value){
		Log.d( name + " value becomes [" + value + "]");
		this.value = value;
	}

	/**
	 * Ajoute un observeur à la liste
	 * @param observer le nouveau observeur
	 */
	public void observe(Observer<T> observer){

		observers.add(observer);
		Log.d( name + " new observer added " + Util.printObject(observer));
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	

	

	// -------------------------------- --- Notification des observeurs --- --------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//

	/**
	 * Notification de tous les observeurs présents dans la liste.
	 * Supprime les observeurs non-actifs de la liste.
	 */
	private void notifyObservers(){

		Log.i( name + " notifying observers ...");
		Iterator<Observer<T>> iterator = observers.iterator();
		
		while (iterator.hasNext()) {
			
			Observer<T> observer = iterator.next();

			if (observer.isActive())
				observer.onChanged(value);
			else 
				iterator.remove();
		}

		Log.i("... " +  name + " notifying observers done");
	}

	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	// --------------------------------- --------------------------------- ---------------------------------//
	
}
