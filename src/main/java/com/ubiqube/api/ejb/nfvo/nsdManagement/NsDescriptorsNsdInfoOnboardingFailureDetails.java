package com.ubiqube.api.ejb.nfvo.nsdManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
  * The definition of the general \"ProblemDetails\" data structure from IETF RFC 7807 [19] is reproduced in this structure. Compared to the general framework defined in IETF RFC 7807 [19], the \"status\" and \"detail\" attributes are mandated to be included by the present document, to ensure that the response contains additional textual information about an error. IETF RFC 7807 [19] foresees extensibility of the \"ProblemDetails\" type. It is possible that particular APIs in the present document, or particular implementations, define extensions to define additional attributes that provide more information about the error. The description column only provides some explanation of the meaning to Facilitate understanding of the design. For a full description, see IETF RFC 7807 [19]. 
 **/
@ApiModel(description="The definition of the general \"ProblemDetails\" data structure from IETF RFC 7807 [19] is reproduced in this structure. Compared to the general framework defined in IETF RFC 7807 [19], the \"status\" and \"detail\" attributes are mandated to be included by the present document, to ensure that the response contains additional textual information about an error. IETF RFC 7807 [19] foresees extensibility of the \"ProblemDetails\" type. It is possible that particular APIs in the present document, or particular implementations, define extensions to define additional attributes that provide more information about the error. The description column only provides some explanation of the meaning to Facilitate understanding of the design. For a full description, see IETF RFC 7807 [19]. ")
public class NsDescriptorsNsdInfoOnboardingFailureDetails  {
  
  @ApiModelProperty(value = "A URI reference according to IETF RFC 3986 [5] that identifies the problem type. It is encouraged that the URI provides human-readable documentation for the problem (e.g. using HTML) when dereferenced. When this member is not present, its value is assumed to be \"about:blank\". ")
 /**
   * A URI reference according to IETF RFC 3986 [5] that identifies the problem type. It is encouraged that the URI provides human-readable documentation for the problem (e.g. using HTML) when dereferenced. When this member is not present, its value is assumed to be \"about:blank\". 
  **/
  private String type = null;

  @ApiModelProperty(value = "A short, human-readable summary of the problem type. It should not change from occurrence to occurrence of the problem, except for purposes of localization. If type is given and other than \"about:blank\", this attribute shall also be provided. A short, human-readable summary of the problem type.  It SHOULD NOT change from occurrence to occurrence of the problem, except for purposes of localization (e.g., using proactive content negotiation; see [RFC7231], Section 3.4). ")
 /**
   * A short, human-readable summary of the problem type. It should not change from occurrence to occurrence of the problem, except for purposes of localization. If type is given and other than \"about:blank\", this attribute shall also be provided. A short, human-readable summary of the problem type.  It SHOULD NOT change from occurrence to occurrence of the problem, except for purposes of localization (e.g., using proactive content negotiation; see [RFC7231], Section 3.4). 
  **/
  private String title = null;

  @ApiModelProperty(value = "The HTTP status code for this occurrence of the problem. The HTTP status code ([RFC7231], Section 6) generated by the origin server for this occurrence of the problem. ")
 /**
   * The HTTP status code for this occurrence of the problem. The HTTP status code ([RFC7231], Section 6) generated by the origin server for this occurrence of the problem. 
  **/
  private Integer status = null;

  @ApiModelProperty(required = true, value = "A human-readable explanation specific to this occurrence of the problem. ")
 /**
   * A human-readable explanation specific to this occurrence of the problem. 
  **/
  private String detail = null;

  @ApiModelProperty(value = "A URI reference that identifies the specific occurrence of the problem. It may yield further information if dereferenced. ")
 /**
   * A URI reference that identifies the specific occurrence of the problem. It may yield further information if dereferenced. 
  **/
  private String instance = null;
 /**
   * A URI reference according to IETF RFC 3986 [5] that identifies the problem type. It is encouraged that the URI provides human-readable documentation for the problem (e.g. using HTML) when dereferenced. When this member is not present, its value is assumed to be \&quot;about:blank\&quot;. 
   * @return type
  **/
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public NsDescriptorsNsdInfoOnboardingFailureDetails type(String type) {
    this.type = type;
    return this;
  }

 /**
   * A short, human-readable summary of the problem type. It should not change from occurrence to occurrence of the problem, except for purposes of localization. If type is given and other than \&quot;about:blank\&quot;, this attribute shall also be provided. A short, human-readable summary of the problem type.  It SHOULD NOT change from occurrence to occurrence of the problem, except for purposes of localization (e.g., using proactive content negotiation; see [RFC7231], Section 3.4). 
   * @return title
  **/
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public NsDescriptorsNsdInfoOnboardingFailureDetails title(String title) {
    this.title = title;
    return this;
  }

 /**
   * The HTTP status code for this occurrence of the problem. The HTTP status code ([RFC7231], Section 6) generated by the origin server for this occurrence of the problem. 
   * @return status
  **/
  @JsonProperty("status")
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public NsDescriptorsNsdInfoOnboardingFailureDetails status(Integer status) {
    this.status = status;
    return this;
  }

 /**
   * A human-readable explanation specific to this occurrence of the problem. 
   * @return detail
  **/
  @JsonProperty("detail")
  @NotNull
  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public NsDescriptorsNsdInfoOnboardingFailureDetails detail(String detail) {
    this.detail = detail;
    return this;
  }

 /**
   * A URI reference that identifies the specific occurrence of the problem. It may yield further information if dereferenced. 
   * @return instance
  **/
  @JsonProperty("instance")
  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  public NsDescriptorsNsdInfoOnboardingFailureDetails instance(String instance) {
    this.instance = instance;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsDescriptorsNsdInfoOnboardingFailureDetails {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    instance: ").append(toIndentedString(instance)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

