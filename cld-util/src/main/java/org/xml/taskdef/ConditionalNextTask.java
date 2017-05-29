//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.03 at 06:41:57 AM PST 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConditionalNextTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConditionalNextTask">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="condition" type="{}BinaryBoolOp" minOccurs="0"/>
 *         &lt;element name="invokeTask" type="{}TaskInvokeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConditionalNextTask", propOrder = {
    "condition",
    "invokeTask"
})
public class ConditionalNextTask {

    protected BinaryBoolOp condition;
    @XmlElement(required = true)
    protected TaskInvokeType invokeTask;

    /**
     * Gets the value of the condition property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryBoolOp }
     *     
     */
    public BinaryBoolOp getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryBoolOp }
     *     
     */
    public void setCondition(BinaryBoolOp value) {
        this.condition = value;
    }

    /**
     * Gets the value of the invokeTask property.
     * 
     * @return
     *     possible object is
     *     {@link TaskInvokeType }
     *     
     */
    public TaskInvokeType getInvokeTask() {
        return invokeTask;
    }

    /**
     * Sets the value of the invokeTask property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskInvokeType }
     *     
     */
    public void setInvokeTask(TaskInvokeType value) {
        this.invokeTask = value;
    }

}
