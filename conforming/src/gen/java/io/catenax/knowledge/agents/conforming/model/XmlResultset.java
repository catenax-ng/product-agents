//
// Conforming Agent Implementation
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package io.catenax.knowledge.agents.conforming.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;

/**
 * XmlResultset
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2023-03-23T11:28:11.277776230Z[GMT]")public class XmlResultset   {
  @JsonProperty("head")
  private XmlResultsetHead head = null;

  @JsonProperty("results")
  private XmlResultsetResults results = null;

  public XmlResultset head(XmlResultsetHead head) {
    this.head = head;
    return this;
  }

  /**
   * Get head
   * @return head
   **/
  @JsonProperty("head")
  @Schema(description = "")
  @Valid
  public XmlResultsetHead getHead() {
    return head;
  }

  public void setHead(XmlResultsetHead head) {
    this.head = head;
  }

  public XmlResultset results(XmlResultsetResults results) {
    this.results = results;
    return this;
  }

  /**
   * Get results
   * @return results
   **/
  @JsonProperty("results")
  @Schema(description = "")
  @Valid
  public XmlResultsetResults getResults() {
    return results;
  }

  public void setResults(XmlResultsetResults results) {
    this.results = results;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    XmlResultset xmlResultset = (XmlResultset) o;
    return Objects.equals(this.head, xmlResultset.head) &&
        Objects.equals(this.results, xmlResultset.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(head, results);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class XmlResultset {\n");
    
    sb.append("    head: ").append(toIndentedString(head)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
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
