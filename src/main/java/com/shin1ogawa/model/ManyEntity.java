package com.shin1ogawa.model;

import java.io.Serializable;
import java.util.Set;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

@Model
public class ManyEntity implements Serializable {

	private static final long serialVersionUID = -4369015254092760067L;

	@Attribute(primaryKey = true)
	private Key key;

	private Boolean mod2;

	private Boolean mod3;

	private Boolean mod5;

	private Boolean mod7;

	private Boolean mod11;

	private Boolean mod13;

	private Boolean mod17;

	private Set<String> listProp;

	private String stringProp;

	private Long numberProp;


	/**
	 * @return the key
	 * @category accessor
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 * @category accessor
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * @return the mod2
	 * @category accessor
	 */
	public Boolean getMod2() {
		return mod2;
	}

	/**
	 * @param mod2 the mod2 to set
	 * @category accessor
	 */
	public void setMod2(Boolean mod2) {
		this.mod2 = mod2;
	}

	/**
	 * @return the mod3
	 * @category accessor
	 */
	public Boolean getMod3() {
		return mod3;
	}

	/**
	 * @param mod3 the mod3 to set
	 * @category accessor
	 */
	public void setMod3(Boolean mod3) {
		this.mod3 = mod3;
	}

	/**
	 * @return the mod5
	 * @category accessor
	 */
	public Boolean getMod5() {
		return mod5;
	}

	/**
	 * @param mod5 the mod5 to set
	 * @category accessor
	 */
	public void setMod5(Boolean mod5) {
		this.mod5 = mod5;
	}

	/**
	 * @return the mod7
	 * @category accessor
	 */
	public Boolean getMod7() {
		return mod7;
	}

	/**
	 * @param mod7 the mod7 to set
	 * @category accessor
	 */
	public void setMod7(Boolean mod7) {
		this.mod7 = mod7;
	}

	/**
	 * @return the mod11
	 * @category accessor
	 */
	public Boolean getMod11() {
		return mod11;
	}

	/**
	 * @param mod11 the mod11 to set
	 * @category accessor
	 */
	public void setMod11(Boolean mod11) {
		this.mod11 = mod11;
	}

	/**
	 * @return the mod13
	 * @category accessor
	 */
	public Boolean getMod13() {
		return mod13;
	}

	/**
	 * @param mod13 the mod13 to set
	 * @category accessor
	 */
	public void setMod13(Boolean mod13) {
		this.mod13 = mod13;
	}

	/**
	 * @return the listProp
	 * @category accessor
	 */
	public Set<String> getListProp() {
		return listProp;
	}

	/**
	 * @param listProp the listProp to set
	 * @category accessor
	 */
	public void setListProp(Set<String> listProp) {
		this.listProp = listProp;
	}

	/**
	 * @return the stringProp
	 * @category accessor
	 */
	public String getStringProp() {
		return stringProp;
	}

	/**
	 * @param stringProp the stringProp to set
	 * @category accessor
	 */
	public void setStringProp(String stringProp) {
		this.stringProp = stringProp;
	}

	/**
	 * @return the numberProp
	 * @category accessor
	 */
	public Long getNumberProp() {
		return numberProp;
	}

	/**
	 * @param numberProp the numberProp to set
	 * @category accessor
	 */
	public void setNumberProp(Long numberProp) {
		this.numberProp = numberProp;
	}

	/**
	 * @param mod17 the mod17 to set
	 * @category accessor
	 */
	public void setMod17(Boolean mod17) {
		this.mod17 = mod17;
	}

	/**
	 * @return the mod17
	 * @category accessor
	 */
	public Boolean getMod17() {
		return mod17;
	}
}
