package com.ubiqube.api.ejb.nfvo.nsLcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
  * This type represents information about a link port of an external VL, i.e. a port providing connectivity for the VNF to an NS VL.  
 **/
@ApiModel(description="This type represents information about a link port of an external VL, i.e. a port providing connectivity for the VNF to an NS VL.  ")
public class NsInstancesNsInstanceInstantiatedVnfInfoExtLinkPorts  {
  
  @ApiModelProperty(required = true, value = "An identifier with the intention of being globally unique. ")
 /**
   * An identifier with the intention of being globally unique. 
  **/
  private String id = null;

  @ApiModelProperty(required = true, value = "")
  @Valid
  private NsInstancesNsInstanceInstantiatedVnfInfoResourceHandle resourceHandle = null;

  @ApiModelProperty(value = "An identifier that is unique for the respective type within a VNF instance, but may not be globally unique. ")
 /**
   * An identifier that is unique for the respective type within a VNF instance, but may not be globally unique. 
  **/
  private String cpInstanceId = null;
 /**
   * An identifier with the intention of being globally unique. 
   * @return id
  **/
  @JsonProperty("id")
  @NotNull
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NsInstancesNsInstanceInstantiatedVnfInfoExtLinkPorts id(String id) {
    this.id = id;
    return this;
  }

 /**
   * Get resourceHandle
   * @return resourceHandle
  **/
  @JsonProperty("resourceHandle")
  @NotNull
  public NsInstancesNsInstanceInstantiatedVnfInfoResourceHandle getResourceHandle() {
    return resourceHandle;
  }

  public void setResourceHandle(NsInstancesNsInstanceInstantiatedVnfInfoResourceHandle resourceHandle) {
    this.resourceHandle = resourceHandle;
  }

  public NsInstancesNsInstanceInstantiatedVnfInfoExtLinkPorts resourceHandle(NsInstancesNsInstanceInstantiatedVnfInfoResourceHandle resourceHandle) {
    this.resourceHandle = resourceHandle;
    return this;
  }

 /**
   * An identifier that is unique for the respective type within a VNF instance, but may not be globally unique. 
   * @return cpInstanceId
  **/
  @JsonProperty("cpInstanceId")
  public String getCpInstanceId() {
    return cpInstanceId;
  }

  public void setCpInstanceId(String cpInstanceId) {
    this.cpInstanceId = cpInstanceId;
  }

  public NsInstancesNsInstanceInstantiatedVnfInfoExtLinkPorts cpInstanceId(String cpInstanceId) {
    this.cpInstanceId = cpInstanceId;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsInstancesNsInstanceInstantiatedVnfInfoExtLinkPorts {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    resourceHandle: ").append(toIndentedString(resourceHandle)).append("\n");
    sb.append("    cpInstanceId: ").append(toIndentedString(cpInstanceId)).append("\n");
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

