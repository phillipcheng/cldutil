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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CsvTransformType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CsvTransformType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ops" type="{}TransformOp" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="outputDir" type="{}ValueType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="reverse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="transformClass" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="outputType" type="{}CsvOutputType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CsvTransformType", propOrder = {
    "ops",
    "outputDir"
})
public class CsvTransformType {

    protected List<TransformOp> ops;
    protected ValueType outputDir;
    @XmlAttribute(name = "reverse")
    protected Boolean reverse;
    @XmlAttribute(name = "transformClass")
    protected String transformClass;
    @XmlAttribute(name = "outputType")
    protected CsvOutputType outputType;

    /**
     * Gets the value of the ops property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ops property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOps().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransformOp }
     * 
     * 
     */
    public List<TransformOp> getOps() {
        if (ops == null) {
            ops = new ArrayList<TransformOp>();
        }
        return this.ops;
    }

    /**
     * Gets the value of the outputDir property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getOutputDir() {
        return outputDir;
    }

    /**
     * Sets the value of the outputDir property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setOutputDir(ValueType value) {
        this.outputDir = value;
    }

    /**
     * Gets the value of the reverse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isReverse() {
        if (reverse == null) {
            return false;
        } else {
            return reverse;
        }
    }

    /**
     * Sets the value of the reverse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReverse(Boolean value) {
        this.reverse = value;
    }

    /**
     * Gets the value of the transformClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransformClass() {
        return transformClass;
    }

    /**
     * Sets the value of the transformClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransformClass(String value) {
        this.transformClass = value;
    }

    /**
     * Gets the value of the outputType property.
     * 
     * @return
     *     possible object is
     *     {@link CsvOutputType }
     *     
     */
    public CsvOutputType getOutputType() {
        return outputType;
    }

    /**
     * Sets the value of the outputType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CsvOutputType }
     *     
     */
    public void setOutputType(CsvOutputType value) {
        this.outputType = value;
    }

}
