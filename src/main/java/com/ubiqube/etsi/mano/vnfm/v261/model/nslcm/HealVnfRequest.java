/*
 * SOL003 - VNF Lifecycle Management interface
 * SOL003 - VNF Lifecycle Management interface definition  IMPORTANT: Please note that this file might be not aligned to the current version of the ETSI Group Specification it refers to. In case of discrepancies the published ETSI Group Specification takes precedence.  In clause 4.3.2 of ETSI GS NFV-SOL 003 v2.4.1, an attribute-based filtering mechanism is defined. This mechanism is currently not included in the corresponding OpenAPI design for this GS version. Changes to the attribute-based filtering mechanism are being considered in v2.5.1 of this GS for inclusion in the corresponding future ETSI NFV OpenAPI design. Please report bugs to https://forge.etsi.org/bugzilla/buglist.cgi?component=Nfv-Openapis&list_id=61&product=NFV&resolution=
 *
 * OpenAPI spec version: 1.1.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.ubiqube.etsi.mano.vnfm.v261.model.nslcm;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

/**
 * HealVnfRequest
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2019-06-13T10:04:39.223+02:00")
public class HealVnfRequest {
	@JsonProperty("cause")
	private String cause = null;

	@JsonProperty("additionalParams")
	private Map<String, String> additionalParams = null;

	public HealVnfRequest cause(final String cause) {
		this.cause = cause;
		return this;
	}

	/**
	 * Indicates the reason why a healing procedure is required.
	 *
	 * @return cause
	 **/
	@JsonProperty("cause")
	@ApiModelProperty(value = "Indicates the reason why a healing procedure is required. ")
	public String getCause() {
		return cause;
	}

	public void setCause(final String cause) {
		this.cause = cause;
	}

	public HealVnfRequest additionalParams(final Map<String, String> additionalParams) {
		this.additionalParams = additionalParams;
		return this;
	}

	/**
	 * Additional parameters passed by the NFVO as input to the healing process,
	 * specific to the VNF being healed, as declared in the VNFD as part of
	 * \&quot;HealVnfOpConfig\&quot;.
	 *
	 * @return additionalParams
	 **/
	@JsonProperty("additionalParams")
	@ApiModelProperty(value = "Additional parameters passed by the NFVO as input to the healing process, specific to the VNF being healed, as declared in the VNFD as part of \"HealVnfOpConfig\". ")
	public Map<String, String> getAdditionalParams() {
		return additionalParams;
	}

	public void setAdditionalParams(final Map<String, String> additionalParams) {
		this.additionalParams = additionalParams;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("class HealVnfRequest {\n");

		sb.append("    cause: ").append(toIndentedString(cause)).append("\n");
		sb.append("    additionalParams: ").append(toIndentedString(additionalParams)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(final java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
