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
import java.util.List;
import java.util.ArrayList;

/**
 * XmlResultsetResultsResult
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2023-03-23T11:28:11.277776230Z[GMT]")public class XmlResultsetResultsResult   {
  @JsonProperty("binding")
  private List<XmlResultsetResultsResultBinding> bindings = new ArrayList<>();

  public XmlResultsetResultsResult addBinding(XmlResultsetResultsResultBinding binding) {
    this.bindings.add(binding);
    return this;
  }

  /**
   * Get binding
   * @return binding
   **/
  public List<XmlResultsetResultsResultBinding> getBindings() {
    return bindings;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    XmlResultsetResultsResult xmlResultsetResultsResult = (XmlResultsetResultsResult) o;
    return Objects.equals(this.bindings, xmlResultsetResultsResult.bindings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bindings);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class XmlResultsetResultsResult {\n");
    
    sb.append("    binding: ").append(toIndentedString(bindings)).append("\n");
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
