package com.ubiqube.etsi.mano.nfvo.v351.model.nslcm;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.ubiqube.etsi.mano.nfvo.v351.model.nslcm.KeyValuePairs;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * This type represents attribute modifications that were performed on an \&quot;Individual VNF instance\&quot; resource when changing the current VNF package. The attributes that can be included consist of those requested to be modified explicitly in the \&quot;ChangeCurrentVnfPkgRequest\&quot; data structure, and additional attributes of the \&quot;VnfInstance\&quot; data structure that were modified implicitly during the operation. The \&quot;ModificationsTriggeredByVnfPkgChange\&quot; data type shall comply with the provisions defined in table 6.5.3.79-1. 
 */
@Schema(description = "This type represents attribute modifications that were performed on an \"Individual VNF instance\" resource when changing the current VNF package. The attributes that can be included consist of those requested to be modified explicitly in the \"ChangeCurrentVnfPkgRequest\" data structure, and additional attributes of the \"VnfInstance\" data structure that were modified implicitly during the operation. The \"ModificationsTriggeredByVnfPkgChange\" data type shall comply with the provisions defined in table 6.5.3.79-1. ")
@Validated


public class ModificationsTriggeredByVnfPkgChange   {
  @JsonProperty("vnfConfigurableProperties")
  private KeyValuePairs vnfConfigurableProperties = null;

  @JsonProperty("metadata")
  private KeyValuePairs metadata = null;

  @JsonProperty("extensions")
  private KeyValuePairs extensions = null;

  @JsonProperty("vnfdId")
  private String vnfdId = null;

  @JsonProperty("vnfProvider")
  private String vnfProvider = null;

  @JsonProperty("vnfProductName")
  private String vnfProductName = null;

  @JsonProperty("vnfSoftwareVersion")
  private String vnfSoftwareVersion = null;

  @JsonProperty("vnfdVersion")
  private String vnfdVersion = null;

  public ModificationsTriggeredByVnfPkgChange vnfConfigurableProperties(KeyValuePairs vnfConfigurableProperties) {
    this.vnfConfigurableProperties = vnfConfigurableProperties;
    return this;
  }

  /**
   * Get vnfConfigurableProperties
   * @return vnfConfigurableProperties
   **/
  @Schema(description = "")
  
    @Valid
    public KeyValuePairs getVnfConfigurableProperties() {
    return vnfConfigurableProperties;
  }

  public void setVnfConfigurableProperties(KeyValuePairs vnfConfigurableProperties) {
    this.vnfConfigurableProperties = vnfConfigurableProperties;
  }

  public ModificationsTriggeredByVnfPkgChange metadata(KeyValuePairs metadata) {
    this.metadata = metadata;
    return this;
  }

  /**
   * Get metadata
   * @return metadata
   **/
  @Schema(description = "")
  
    @Valid
    public KeyValuePairs getMetadata() {
    return metadata;
  }

  public void setMetadata(KeyValuePairs metadata) {
    this.metadata = metadata;
  }

  public ModificationsTriggeredByVnfPkgChange extensions(KeyValuePairs extensions) {
    this.extensions = extensions;
    return this;
  }

  /**
   * Get extensions
   * @return extensions
   **/
  @Schema(description = "")
  
    @Valid
    public KeyValuePairs getExtensions() {
    return extensions;
  }

  public void setExtensions(KeyValuePairs extensions) {
    this.extensions = extensions;
  }

  public ModificationsTriggeredByVnfPkgChange vnfdId(String vnfdId) {
    this.vnfdId = vnfdId;
    return this;
  }

  /**
   * Get vnfdId
   * @return vnfdId
   **/
  @Schema(description = "")
  
    public String getVnfdId() {
    return vnfdId;
  }

  public void setVnfdId(String vnfdId) {
    this.vnfdId = vnfdId;
  }

  public ModificationsTriggeredByVnfPkgChange vnfProvider(String vnfProvider) {
    this.vnfProvider = vnfProvider;
    return this;
  }

  /**
   * If present, this attribute signals the new value of the \"vnfProvider\" attribute in \"VnfInstance\". If present, this attribute (which depends on the value of the \"vnfdId\" attribute) was modified implicitly during the related operation, and contains a copy of the value of the related attribute from the VNFD in the VNF Package identified by the \"vnfdId\" attribute. 
   * @return vnfProvider
   **/
  @Schema(description = "If present, this attribute signals the new value of the \"vnfProvider\" attribute in \"VnfInstance\". If present, this attribute (which depends on the value of the \"vnfdId\" attribute) was modified implicitly during the related operation, and contains a copy of the value of the related attribute from the VNFD in the VNF Package identified by the \"vnfdId\" attribute. ")
  
    public String getVnfProvider() {
    return vnfProvider;
  }

  public void setVnfProvider(String vnfProvider) {
    this.vnfProvider = vnfProvider;
  }

  public ModificationsTriggeredByVnfPkgChange vnfProductName(String vnfProductName) {
    this.vnfProductName = vnfProductName;
    return this;
  }

  /**
   * If present, this attribute signals the new value of the \"vnfProductName\" attribute in \"VnfInstance\". If present, this attribute (which depends on the value of the \"vnfdId\" attribute) was modified implicitly during the related operation, and contains a copy of the value of the related attribute from the VNFD in the VNF Package identified by the \"vnfdId\" attribute. 
   * @return vnfProductName
   **/
  @Schema(description = "If present, this attribute signals the new value of the \"vnfProductName\" attribute in \"VnfInstance\". If present, this attribute (which depends on the value of the \"vnfdId\" attribute) was modified implicitly during the related operation, and contains a copy of the value of the related attribute from the VNFD in the VNF Package identified by the \"vnfdId\" attribute. ")
  
    public String getVnfProductName() {
    return vnfProductName;
  }

  public void setVnfProductName(String vnfProductName) {
    this.vnfProductName = vnfProductName;
  }

  public ModificationsTriggeredByVnfPkgChange vnfSoftwareVersion(String vnfSoftwareVersion) {
    this.vnfSoftwareVersion = vnfSoftwareVersion;
    return this;
  }

  /**
   * Get vnfSoftwareVersion
   * @return vnfSoftwareVersion
   **/
  @Schema(description = "")
  
    public String getVnfSoftwareVersion() {
    return vnfSoftwareVersion;
  }

  public void setVnfSoftwareVersion(String vnfSoftwareVersion) {
    this.vnfSoftwareVersion = vnfSoftwareVersion;
  }

  public ModificationsTriggeredByVnfPkgChange vnfdVersion(String vnfdVersion) {
    this.vnfdVersion = vnfdVersion;
    return this;
  }

  /**
   * Get vnfdVersion
   * @return vnfdVersion
   **/
  @Schema(description = "")
  
    public String getVnfdVersion() {
    return vnfdVersion;
  }

  public void setVnfdVersion(String vnfdVersion) {
    this.vnfdVersion = vnfdVersion;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModificationsTriggeredByVnfPkgChange modificationsTriggeredByVnfPkgChange = (ModificationsTriggeredByVnfPkgChange) o;
    return Objects.equals(this.vnfConfigurableProperties, modificationsTriggeredByVnfPkgChange.vnfConfigurableProperties) &&
        Objects.equals(this.metadata, modificationsTriggeredByVnfPkgChange.metadata) &&
        Objects.equals(this.extensions, modificationsTriggeredByVnfPkgChange.extensions) &&
        Objects.equals(this.vnfdId, modificationsTriggeredByVnfPkgChange.vnfdId) &&
        Objects.equals(this.vnfProvider, modificationsTriggeredByVnfPkgChange.vnfProvider) &&
        Objects.equals(this.vnfProductName, modificationsTriggeredByVnfPkgChange.vnfProductName) &&
        Objects.equals(this.vnfSoftwareVersion, modificationsTriggeredByVnfPkgChange.vnfSoftwareVersion) &&
        Objects.equals(this.vnfdVersion, modificationsTriggeredByVnfPkgChange.vnfdVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vnfConfigurableProperties, metadata, extensions, vnfdId, vnfProvider, vnfProductName, vnfSoftwareVersion, vnfdVersion);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModificationsTriggeredByVnfPkgChange {\n");
    
    sb.append("    vnfConfigurableProperties: ").append(toIndentedString(vnfConfigurableProperties)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
    sb.append("    extensions: ").append(toIndentedString(extensions)).append("\n");
    sb.append("    vnfdId: ").append(toIndentedString(vnfdId)).append("\n");
    sb.append("    vnfProvider: ").append(toIndentedString(vnfProvider)).append("\n");
    sb.append("    vnfProductName: ").append(toIndentedString(vnfProductName)).append("\n");
    sb.append("    vnfSoftwareVersion: ").append(toIndentedString(vnfSoftwareVersion)).append("\n");
    sb.append("    vnfdVersion: ").append(toIndentedString(vnfdVersion)).append("\n");
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
