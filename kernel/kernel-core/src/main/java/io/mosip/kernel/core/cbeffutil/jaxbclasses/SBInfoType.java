//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.12.06 at 02:49:01 PM IST 
//


package io.mosip.kernel.core.cbeffutil.jaxbclasses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SBInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SBInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FormatOwner" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="FormatType" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SBInfoType", propOrder = {
    "formatOwner",
    "formatType"
})
public class SBInfoType {

    @XmlElement(name = "FormatOwner")
    @XmlSchemaType(name = "positiveInteger")
    protected Long formatOwner;
    @XmlElement(name = "FormatType")
    @XmlSchemaType(name = "positiveInteger")
    protected Long formatType;
	/**
	 * @return the formatOwner
	 */
	public Long getFormatOwner() {
		return formatOwner;
	}
	/**
	 * @param formatOwner the formatOwner to set
	 */
	public void setFormatOwner(Long formatOwner) {
		this.formatOwner = formatOwner;
	}
	/**
	 * @return the formatType
	 */
	public Long getFormatType() {
		return formatType;
	}
	/**
	 * @param formatType the formatType to set
	 */
	public void setFormatType(Long formatType) {
		this.formatType = formatType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formatOwner == null) ? 0 : formatOwner.hashCode());
		result = prime * result + ((formatType == null) ? 0 : formatType.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SBInfoType other = (SBInfoType) obj;
		if (formatOwner == null) {
			if (other.formatOwner != null)
				return false;
		} else if (!formatOwner.equals(other.formatOwner))
			return false;
		if (formatType == null) {
			if (other.formatType != null)
				return false;
		} else if (!formatType.equals(other.formatType))
			return false;
		return true;
	}
}
