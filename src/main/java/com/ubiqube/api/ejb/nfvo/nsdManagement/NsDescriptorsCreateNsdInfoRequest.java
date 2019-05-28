package com.ubiqube.api.ejb.nfvo.nsdManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
  * \"This type creates a completely new NS descriptor resource.\" 
 **/
@ApiModel(description="\"This type creates a completely new NS descriptor resource.\" ")
public class NsDescriptorsCreateNsdInfoRequest  {
  
  @ApiModelProperty(value = "This type represents a list of key-value pairs. The order of the pairs in the list is not significant. In JSON, a set of key- value pairs is represented as an object. It shall comply with the provisions  defined in clause 4 of IETF RFC 7159.  ")
 /**
   * This type represents a list of key-value pairs. The order of the pairs in the list is not significant. In JSON, a set of key- value pairs is represented as an object. It shall comply with the provisions  defined in clause 4 of IETF RFC 7159.  
  **/
  private Object userDefinedData = null;
 /**
   * This type represents a list of key-value pairs. The order of the pairs in the list is not significant. In JSON, a set of key- value pairs is represented as an object. It shall comply with the provisions  defined in clause 4 of IETF RFC 7159.  
   * @return userDefinedData
  **/
  @JsonProperty("userDefinedData")
  public Object getUserDefinedData() {
    return userDefinedData;
  }

  public void setUserDefinedData(Object userDefinedData) {
    this.userDefinedData = userDefinedData;
  }

  public NsDescriptorsCreateNsdInfoRequest userDefinedData(Object userDefinedData) {
    this.userDefinedData = userDefinedData;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsDescriptorsCreateNsdInfoRequest {\n");
    
    sb.append("    userDefinedData: ").append(toIndentedString(userDefinedData)).append("\n");
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

