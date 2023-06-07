//
// Conforming Agent Implementation
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.conforming.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * CxWarning
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2023-03-23T11:28:11.277776230Z[GMT]")public class CxWarning   {
  @JsonProperty("source-tenant")
  private String sourceTenant = null;

  @JsonProperty("source-asset")
  private String sourceAsset = null;

  @JsonProperty("target-tenant")
  private String targetTenant = null;

  @JsonProperty("target-asset")
  private String targetAsset = null;

  @JsonProperty("problem")
  private String problem = null;

  @JsonProperty("context")
  private String context = null;

  public CxWarning sourceTenant(String sourceTenant) {
    this.sourceTenant = sourceTenant;
    return this;
  }

  /**
   * Get sourceTenant
   * @return sourceTenant
   **/
  @JsonProperty("source-tenant")
  @Schema(description = "")
  public String getSourceTenant() {
    return sourceTenant;
  }

  public void setSourceTenant(String sourceTenant) {
    this.sourceTenant = sourceTenant;
  }

  public CxWarning sourceAsset(String sourceAsset) {
    this.sourceAsset = sourceAsset;
    return this;
  }

  /**
   * Get sourceAsset
   * @return sourceAsset
   **/
  @JsonProperty("source-asset")
  @Schema(description = "")
  public String getSourceAsset() {
    return sourceAsset;
  }

  public void setSourceAsset(String sourceAsset) {
    this.sourceAsset = sourceAsset;
  }

  public CxWarning targetTenant(String targetTenant) {
    this.targetTenant = targetTenant;
    return this;
  }

  /**
   * Get targetTenant
   * @return targetTenant
   **/
  @JsonProperty("target-tenant")
  @Schema(description = "")
  public String getTargetTenant() {
    return targetTenant;
  }

  public void setTargetTenant(String targetTenant) {
    this.targetTenant = targetTenant;
  }

  public CxWarning targetAsset(String targetAsset) {
    this.targetAsset = targetAsset;
    return this;
  }

  /**
   * Get targetAsset
   * @return targetAsset
   **/
  @JsonProperty("target-asset")
  @Schema(description = "")
  public String getTargetAsset() {
    return targetAsset;
  }

  public void setTargetAsset(String targetAsset) {
    this.targetAsset = targetAsset;
  }

  public CxWarning problem(String problem) {
    this.problem = problem;
    return this;
  }

  /**
   * Get problem
   * @return problem
   **/
  @JsonProperty("problem")
  @Schema(description = "")
  public String getProblem() {
    return problem;
  }

  public void setProblem(String problem) {
    this.problem = problem;
  }

  public CxWarning context(String context) {
    this.context = context;
    return this;
  }

  /**
   * Get context
   * @return context
   **/
  @JsonProperty("context")
  @Schema(description = "")
  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CxWarning cxWarning = (CxWarning) o;
    return Objects.equals(this.sourceTenant, cxWarning.sourceTenant) &&
        Objects.equals(this.sourceAsset, cxWarning.sourceAsset) &&
        Objects.equals(this.targetTenant, cxWarning.targetTenant) &&
        Objects.equals(this.targetAsset, cxWarning.targetAsset) &&
        Objects.equals(this.problem, cxWarning.problem) &&
        Objects.equals(this.context, cxWarning.context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceTenant, sourceAsset, targetTenant, targetAsset, problem, context);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CxWarning {\n");
    
    sb.append("    sourceTenant: ").append(toIndentedString(sourceTenant)).append("\n");
    sb.append("    sourceAsset: ").append(toIndentedString(sourceAsset)).append("\n");
    sb.append("    targetTenant: ").append(toIndentedString(targetTenant)).append("\n");
    sb.append("    targetAsset: ").append(toIndentedString(targetAsset)).append("\n");
    sb.append("    problem: ").append(toIndentedString(problem)).append("\n");
    sb.append("    context: ").append(toIndentedString(context)).append("\n");
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
