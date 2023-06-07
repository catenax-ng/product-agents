//
// Conforming Agent Implementation
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.conforming.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * XmlResultsetResultsResultBindingLiteral
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2023-03-24T17:09:51.803733265Z[GMT]")public class XmlResultsetResultsResultBindingLiteral   {
    @JsonProperty("xml:lang")
    private String xmllang = null;

    @JsonProperty("datatype")
    private String datatype = null;

    @JsonProperty("value")
    @JacksonXmlText
    private String value = null;

    public XmlResultsetResultsResultBindingLiteral xmllang(String xmllang) {
        this.xmllang = xmllang;
        return this;
    }

    /**
     * Get xmllang
     * @return xmllang
     **/
    @JsonProperty("xml:lang")
    @Schema(description = "")
    public String getXmllang() {
        return xmllang;
    }

    public void setXmllang(String xmllang) {
        this.xmllang = xmllang;
    }

    public XmlResultsetResultsResultBindingLiteral datatype(String datatype) {
        this.datatype = datatype;
        return this;
    }

    /**
     * Get datatype
     * @return datatype
     **/
    @JsonProperty("datatype")
    @Schema(description = "")
    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public XmlResultsetResultsResultBindingLiteral value(String value) {
        this.value = value;
        return this;
    }

    /**
     * Get value
     * @return value
     **/
    @JsonProperty("value")
    @Schema(description = "")
    @JacksonXmlText()
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlResultsetResultsResultBindingLiteral xmlResultsetResultsResultBindingLiteral = (XmlResultsetResultsResultBindingLiteral) o;
        return Objects.equals(this.xmllang, xmlResultsetResultsResultBindingLiteral.xmllang) &&
                Objects.equals(this.datatype, xmlResultsetResultsResultBindingLiteral.datatype) &&
                Objects.equals(this.value, xmlResultsetResultsResultBindingLiteral.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xmllang, datatype, value);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class XmlResultsetResultsResultBindingLiteral {\n");

        sb.append("    xmllang: ").append(toIndentedString(xmllang)).append("\n");
        sb.append("    datatype: ").append(toIndentedString(datatype)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
