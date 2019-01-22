//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.12.06 at 02:49:01 PM IST 
//


package io.mosip.kernel.cbeffutil.jaxbclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import io.mosip.kernel.cbeffutil.common.DateAdapter;


/**
 * <p>Java class for BDBInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BDBInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ChallengeResponse" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="Index" type="{http://docs.oasis-open.org/bias/ns/biaspatronformat-1.0/}UUIDType" minOccurs="0"/>
 *         &lt;element name="FormatOwner" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="FormatType" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="Encryption" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="CreationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="NotValidBefore" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="NotValidAfter" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Type" type="{http://docs.oasis-open.org/bias/ns/biaspatronformat-1.0/}MultipleTypesType" minOccurs="0"/>
 *         &lt;element name="Subtype" type="{http://docs.oasis-open.org/bias/ns/biaspatronformat-1.0/}SubtypeType" minOccurs="0"/>
 *         &lt;element name="Level" type="{http://docs.oasis-open.org/bias/ns/biaspatronformat-1.0/}ProcessedLevelType" minOccurs="0"/>
 *         &lt;element name="ProductOwner" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="ProductType" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="Purpose" type="{http://docs.oasis-open.org/bias/ns/biaspatronformat-1.0/}PurposeType" minOccurs="0"/>
 *         &lt;element name="Quality" type="{http://docs.oasis-open.org/bias/ns/biaspatronformat-1.0/}QualityType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BDBInfoType", propOrder = {
    "challengeResponse",
    "index",
    "formatOwner",
    "formatType",
    "encryption",
    "creationDate",
    "notValidBefore",
    "notValidAfter",
    "type",
    "subtype",
    "level",
    "productOwner",
    "productType",
    "purpose",
    "quality"
})
public class BDBInfoType {

    @XmlElement(name = "ChallengeResponse")
    protected byte[] challengeResponse;
    @XmlElement(name = "Index")
    protected String index;
    @XmlElement(name = "FormatOwner")
    @XmlSchemaType(name = "positiveInteger")
    protected Long formatOwner;
    @XmlElement(name = "FormatType")
    @XmlSchemaType(name = "positiveInteger")
    protected Long formatType;
    @XmlElement(name = "Encryption")
    protected Boolean encryption;
    @XmlElement(name = "CreationDate")
    @XmlSchemaType(name = "dateTime")
    protected Date creationDate;
    @XmlElement(name = "NotValidBefore")
    @XmlSchemaType(name = "dateTime")
    protected Date notValidBefore;
    @XmlElement(name = "NotValidAfter")
    @XmlSchemaType(name = "dateTime")
    protected Date notValidAfter;
    @XmlList
    @XmlElement(name = "Type")
    protected List<SingleType> type;
    @XmlList
    @XmlElement(name = "Subtype")
    protected List<String> subtype;
    @XmlElement(name = "Level")
    @XmlSchemaType(name = "string")
    protected ProcessedLevelType level;
    @XmlElement(name = "ProductOwner",required=false)
    @XmlSchemaType(name = "positiveInteger")
    protected Long productOwner;
    @XmlElement(name = "ProductType",required=false)
    @XmlSchemaType(name = "positiveInteger")
    protected Long productType;
    @XmlElement(name = "Purpose")
    @XmlSchemaType(name = "string")
    protected PurposeType purpose;
    @XmlElement(name = "Quality")
    @XmlSchemaType(name = "integer")
    protected Integer quality;

    public void setType(List<SingleType> type) {
		this.type = type;
	}

	public void setSubtype(List<String> subtype) {
		this.subtype = subtype;
	}

	/**
     * Gets the value of the challengeResponse property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getChallengeResponse() {
        return challengeResponse;
    }

    /**
     * Sets the value of the challengeResponse property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setChallengeResponse(byte[] value) {
        this.challengeResponse = value;
    }

    /**
     * Gets the value of the index property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndex(String value) {
        this.index = value;
    }

 

    /**
     * Gets the value of the encryption property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEncryption() {
        return encryption;
    }

    /**
     * Sets the value of the encryption property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEncryption(Boolean value) {
        this.encryption = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setCreationDate(Date value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the notValidBefore property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getNotValidBefore() {
        return notValidBefore;
    }

    /**
     * Sets the value of the notValidBefore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setNotValidBefore(Date value) {
        this.notValidBefore = value;
    }

    /**
     * Gets the value of the notValidAfter property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getNotValidAfter() {
        return notValidAfter;
    }

    /**
     * Sets the value of the notValidAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setNotValidAfter(java.util.Date value) {
        this.notValidAfter = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the type property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SingleTypeType }
     * 
     * 
     */
    public List<SingleType> getType() {
        if (type == null) {
            type = new ArrayList<SingleType>();
        }
        return this.type;
    }

    /**
     * Gets the value of the subtype property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subtype property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubtype().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSubtype() {
        if (subtype == null) {
            subtype = new ArrayList<String>();
        }
        return this.subtype;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessedLevelType }
     *     
     */
    public ProcessedLevelType getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessedLevelType }
     *     
     */
    public void setLevel(ProcessedLevelType value) {
        this.level = value;
    }

    /**
     * Gets the value of the purpose property.
     * 
     * @return
     *     possible object is
     *     {@link PurposeType }
     *     
     */
    public PurposeType getPurpose() {
        return purpose;
    }

    /**
     * Sets the value of the purpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link PurposeType }
     *     
     */
    public void setPurpose(PurposeType value) {
        this.purpose = value;
    }

    /**
     * Gets the value of the quality property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQuality() {
        return quality;
    }

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

	/**
	 * @return the productOwner
	 */
	public Long getProductOwner() {
		return productOwner;
	}

	/**
	 * @param productOwner the productOwner to set
	 */
	public void setProductOwner(Long productOwner) {
		this.productOwner = productOwner;
	}

	/**
	 * @return the productType
	 */
	public Long getProductType() {
		return productType;
	}

	/**
	 * @param productType the productType to set
	 */
	public void setProductType(Long productType) {
		this.productType = productType;
	}

	/**
	 * @return the encryption
	 */
	public Boolean getEncryption() {
		return encryption;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(challengeResponse);
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((encryption == null) ? 0 : encryption.hashCode());
		result = prime * result + ((formatOwner == null) ? 0 : formatOwner.hashCode());
		result = prime * result + ((formatType == null) ? 0 : formatType.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((notValidAfter == null) ? 0 : notValidAfter.hashCode());
		result = prime * result + ((notValidBefore == null) ? 0 : notValidBefore.hashCode());
		result = prime * result + ((productOwner == null) ? 0 : productOwner.hashCode());
		result = prime * result + ((productType == null) ? 0 : productType.hashCode());
		result = prime * result + ((purpose == null) ? 0 : purpose.hashCode());
		result = prime * result + ((quality == null) ? 0 : quality.hashCode());
		result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		BDBInfoType other = (BDBInfoType) obj;
		if (!Arrays.equals(challengeResponse, other.challengeResponse))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (encryption == null) {
			if (other.encryption != null)
				return false;
		} else if (!encryption.equals(other.encryption))
			return false;
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
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		if (level != other.level)
			return false;
		if (notValidAfter == null) {
			if (other.notValidAfter != null)
				return false;
		} else if (!notValidAfter.equals(other.notValidAfter))
			return false;
		if (notValidBefore == null) {
			if (other.notValidBefore != null)
				return false;
		} else if (!notValidBefore.equals(other.notValidBefore))
			return false;
		if (productOwner == null) {
			if (other.productOwner != null)
				return false;
		} else if (!productOwner.equals(other.productOwner))
			return false;
		if (productType == null) {
			if (other.productType != null)
				return false;
		} else if (!productType.equals(other.productType))
			return false;
		if (purpose != other.purpose)
			return false;
		if (quality == null) {
			if (other.quality != null)
				return false;
		} else if (!quality.equals(other.quality))
			return false;
		if (subtype == null) {
			if (other.subtype != null)
				return false;
		} else if (!subtype.equals(other.subtype))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
   
}
