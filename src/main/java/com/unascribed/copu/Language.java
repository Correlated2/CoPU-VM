package com.unascribed.copu;

import java.util.HashMap;
import java.util.Set;

/**
 * Since the compiler won't always run in Minecraft, it needs its own localization solution.
 * Welcome to my compiler localization. Default keys are defined for en_US, but you can re-
 * define keys at any time.
 *
 */
public class Language {
	private static Language enUS = createEnUS();
	private static Language current = enUS;
	
	private HashMap<String, String> localizationKeys = new HashMap<>();
	
	public Language() {}
	
	public Set<String> allKeys() {
		return localizationKeys.keySet();
	}
	
	
	/**
	 * Defines the localized String that a localization key will translate to.
	 * @param key The localization key
	 * @param newValue A localized string
	 */
	public void define(String key, String newValue) {
		localizationKeys.put(key, newValue);
	}
	
	/**
	 * Grab a localized String from a localization key.
	 * @param key A key, such as "err.notEnoughArgs"
	 * @return A localized String in this language.
	 */
	public String localize(String key) {
		if (!localizationKeys.containsKey(key)) return key;
		return localizationKeys.get(key);
	}
	
	protected static Language createEnUS() {
		Language en = new Language();
		
		//All the default keys. This could get messy.
		en.define("err.validate.notEnoughArgs", "Not enough arguments.");
		en.define("err.validate.tooManyArgs",   "Too many arguments.");
		en.define("err.validate.destOperandRequired", "This argument only accepts certain registers (R0-R7, F0-F3, PG0-PG1)"); //and X and Y >:)
		
		return en;
	}
	
	public static Language getCurrent() { return current; }
	public static void setCurrent(Language lang) { current = lang; }
}
