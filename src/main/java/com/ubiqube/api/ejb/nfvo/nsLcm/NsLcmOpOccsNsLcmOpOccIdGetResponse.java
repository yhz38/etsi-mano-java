package com.ubiqube.api.ejb.nfvo.nsLcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;

public class NsLcmOpOccsNsLcmOpOccIdGetResponse {
  
  @ApiModelProperty(value = "")
  @Valid
  private NsLcmOpOccsNsLcmOpOcc nsLcmOpOcc = null;
 /**
   * Get nsLcmOpOcc
   * @return nsLcmOpOcc
  **/
  @JsonProperty("NsLcmOpOcc")
  public NsLcmOpOccsNsLcmOpOcc getNsLcmOpOcc() {
    return nsLcmOpOcc;
  }

  public void setNsLcmOpOcc(NsLcmOpOccsNsLcmOpOcc nsLcmOpOcc) {
    this.nsLcmOpOcc = nsLcmOpOcc;
  }

  public NsLcmOpOccsNsLcmOpOccIdGetResponse nsLcmOpOcc(NsLcmOpOccsNsLcmOpOcc nsLcmOpOcc) {
    this.nsLcmOpOcc = nsLcmOpOcc;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsLcmOpOccsNsLcmOpOccIdGetResponse {\n");
    
    sb.append("    nsLcmOpOcc: ").append(toIndentedString(nsLcmOpOcc)).append("\n");
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

