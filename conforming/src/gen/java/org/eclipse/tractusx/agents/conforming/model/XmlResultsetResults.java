//
// Conforming Agent Implementation
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.conforming.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.ArrayList;

/**
 * XmlResultsetResults
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2023-03-23T11:28:11.277776230Z[GMT]")public class XmlResultsetResults   {
  @JsonProperty("result")
  private List<XmlResultsetResultsResult> results = new ArrayList<>();

  public XmlResultsetResults addResult(XmlResultsetResultsResult result) {
    this.results.add(result);
    return this;
  }

  /**
   * Get result
   * @return result
   **/
  public List<XmlResultsetResultsResult> getResults() {
    return results;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    XmlResultsetResults xmlResultsetResults = (XmlResultsetResults) o;
    return Objects.equals(this.results, xmlResultsetResults.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(results);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class XmlResultsetResults {\n");
    
    sb.append("    result: ").append(toIndentedString(results)).append("\n");
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
