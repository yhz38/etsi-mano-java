package com.ubiqube.api.ejb.nfvo.nsdManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;

public class NsdOnBoardingFailureNotification  {
  
  @ApiModelProperty(value = "")
  @Valid
  private SubscriptionNsdOnBoardingFailureNotificationNsdOnBoardingFailureNotification nsdOnBoardingFailureNotification = null;
 /**
   * Get nsdOnBoardingFailureNotification
   * @return nsdOnBoardingFailureNotification
  **/
  @JsonProperty("NsdOnBoardingFailureNotification")
  public SubscriptionNsdOnBoardingFailureNotificationNsdOnBoardingFailureNotification getNsdOnBoardingFailureNotification() {
    return nsdOnBoardingFailureNotification;
  }

  public void setNsdOnBoardingFailureNotification(SubscriptionNsdOnBoardingFailureNotificationNsdOnBoardingFailureNotification nsdOnBoardingFailureNotification) {
    this.nsdOnBoardingFailureNotification = nsdOnBoardingFailureNotification;
  }

  public NsdOnBoardingFailureNotification nsdOnBoardingFailureNotification(SubscriptionNsdOnBoardingFailureNotificationNsdOnBoardingFailureNotification nsdOnBoardingFailureNotification) {
    this.nsdOnBoardingFailureNotification = nsdOnBoardingFailureNotification;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NsdOnBoardingFailureNotification {\n");
    
    sb.append("    nsdOnBoardingFailureNotification: ").append(toIndentedString(nsdOnBoardingFailureNotification)).append("\n");
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

