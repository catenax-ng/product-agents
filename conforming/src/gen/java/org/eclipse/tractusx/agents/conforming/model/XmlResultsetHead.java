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
 * XmlResultsetHead
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2023-03-23T11:28:11.277776230Z[GMT]")public class XmlResultsetHead   {
  @JsonProperty("variable")
  private List<XmlResultsetHeadVariable> variables = new ArrayList<>();

  public XmlResultsetHead addVariable(XmlResultsetHeadVariable variable) {
    this.variables.add(variable);
    return this;
  }

  /**
   * Get variables
   * @return variables
   **/
  public List<XmlResultsetHeadVariable> getVariables() {
    return variables;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    XmlResultsetHead xmlResultsetHead = (XmlResultsetHead) o;
    return Objects.equals(this.variables, xmlResultsetHead.variables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(variables);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class XmlResultsetHead {\n");
    
    sb.append("    variable: ").append(toIndentedString(variables)).append("\n");
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
