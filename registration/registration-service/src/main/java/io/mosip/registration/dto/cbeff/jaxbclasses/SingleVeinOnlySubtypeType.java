//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.12.06 at 02:49:01 PM IST 
//


package io.mosip.registration.dto.cbeff.jaxbclasses;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SingleVeinOnlySubtypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SingleVeinOnlySubtypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LeftVein"/>
 *     &lt;enumeration value="RightVein"/>
 *     &lt;enumeration value="Palm"/>
 *     &lt;enumeration value="BackOfHand"/>
 *     &lt;enumeration value="Wrist"/>
 *     &lt;enumeration value="Reserved1"/>
 *     &lt;enumeration value="Reserved2"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SingleVeinOnlySubtypeType")
@XmlEnum
public enum SingleVeinOnlySubtypeType {

    @XmlEnumValue("LeftVein")
    LEFT_VEIN("LeftVein"),
    @XmlEnumValue("RightVein")
    RIGHT_VEIN("RightVein"),
    @XmlEnumValue("Palm")
    PALM("Palm"),
    @XmlEnumValue("BackOfHand")
    BACK_OF_HAND("BackOfHand"),
    @XmlEnumValue("Wrist")
    WRIST("Wrist"),
    @XmlEnumValue("Reserved1")
    RESERVED_1("Reserved1"),
    @XmlEnumValue("Reserved2")
    RESERVED_2("Reserved2");
    private final String value;

    SingleVeinOnlySubtypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SingleVeinOnlySubtypeType fromValue(String v) {
        for (SingleVeinOnlySubtypeType c: SingleVeinOnlySubtypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
