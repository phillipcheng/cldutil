//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.31 at 04:24:33 AM PDT 
//


package org.cldutil.xml.taskdef;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LoginType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LoginType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="loginClickStream" type="{}ClickStreamType" maxOccurs="unbounded"/>
 *         &lt;element name="redirectedURL" type="{}RedirectType" maxOccurs="unbounded"/>
 *         &lt;element name="gotchaURL" type="{}ValueType" minOccurs="0"/>
 *         &lt;element name="credential" type="{}CredentialType" maxOccurs="unbounded"/>
 *         &lt;element name="loginSuccessCondition" type="{}BinaryBoolOp" minOccurs="0"/>
 *         &lt;element name="loginGotchaCondition" type="{}BinaryBoolOp" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoginType", propOrder = {
    "loginClickStream",
    "redirectedURL",
    "gotchaURL",
    "credential",
    "loginSuccessCondition",
    "loginGotchaCondition"
})
public class LoginType {

    @XmlElement(required = true)
    protected List<ClickStreamType> loginClickStream;
    @XmlElement(required = true)
    protected List<RedirectType> redirectedURL;
    protected ValueType gotchaURL;
    @XmlElement(required = true)
    protected List<CredentialType> credential;
    protected BinaryBoolOp loginSuccessCondition;
    protected BinaryBoolOp loginGotchaCondition;

    /**
     * Gets the value of the loginClickStream property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the loginClickStream property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLoginClickStream().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClickStreamType }
     * 
     * 
     */
    public List<ClickStreamType> getLoginClickStream() {
        if (loginClickStream == null) {
            loginClickStream = new ArrayList<ClickStreamType>();
        }
        return this.loginClickStream;
    }

    /**
     * Gets the value of the redirectedURL property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the redirectedURL property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRedirectedURL().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RedirectType }
     * 
     * 
     */
    public List<RedirectType> getRedirectedURL() {
        if (redirectedURL == null) {
            redirectedURL = new ArrayList<RedirectType>();
        }
        return this.redirectedURL;
    }

    /**
     * Gets the value of the gotchaURL property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getGotchaURL() {
        return gotchaURL;
    }

    /**
     * Sets the value of the gotchaURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setGotchaURL(ValueType value) {
        this.gotchaURL = value;
    }

    /**
     * Gets the value of the credential property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the credential property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCredential().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CredentialType }
     * 
     * 
     */
    public List<CredentialType> getCredential() {
        if (credential == null) {
            credential = new ArrayList<CredentialType>();
        }
        return this.credential;
    }

    /**
     * Gets the value of the loginSuccessCondition property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryBoolOp }
     *     
     */
    public BinaryBoolOp getLoginSuccessCondition() {
        return loginSuccessCondition;
    }

    /**
     * Sets the value of the loginSuccessCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryBoolOp }
     *     
     */
    public void setLoginSuccessCondition(BinaryBoolOp value) {
        this.loginSuccessCondition = value;
    }

    /**
     * Gets the value of the loginGotchaCondition property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryBoolOp }
     *     
     */
    public BinaryBoolOp getLoginGotchaCondition() {
        return loginGotchaCondition;
    }

    /**
     * Sets the value of the loginGotchaCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryBoolOp }
     *     
     */
    public void setLoginGotchaCondition(BinaryBoolOp value) {
        this.loginGotchaCondition = value;
    }

}
