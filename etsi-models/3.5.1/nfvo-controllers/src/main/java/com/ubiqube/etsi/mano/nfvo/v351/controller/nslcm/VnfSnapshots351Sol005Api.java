/**
 *     Copyright (C) 2019-2020 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.24).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.ubiqube.etsi.mano.nfvo.v351.controller.nslcm;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ubiqube.etsi.mano.nfvo.v351.model.nslcm.ProblemDetails;
import com.ubiqube.etsi.mano.nfvo.v351.model.nslcm.VnfSnapshotInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
@RequestMapping(value = "/sol005/nslcm/v2", headers = { "Version=2.1.0" })
@RolesAllowed({ "ROLE_OSSBSS" })
public interface VnfSnapshots351Sol005Api {

	@Operation(summary = "", description = "The GET method queries information about multiple VNF snapshots. See clause 6.4.19.3.2. ", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "200 OK Shall be returned when information about zero or more VNF snapshots was queried successfully. The response body shall contain in an array the representations of zero or more \"Individual VNF snapshot\" resources, as defined in clause 6.5.2.17. If the NFVO supports alternative 2 (paging) according to clause 5.4.2.1 of ETSI GS NFV-SOL 013 for this resource, inclusion of the Link HTTP header in this response shall follow the provisions in clause 5.4.2.3 of ETSI GS NFV-SOL 013. ", content = @Content(array = @ArraySchema(schema = @Schema(implementation = VnfSnapshotInfo.class)))),
			@ApiResponse(responseCode = "400", description = "400 BAD REQUEST 400 code can be returned in the following specified cases, the specific cause has to be proper specified in the \"ProblemDetails\" structure to be returned. If the request is malformed or syntactically incorrect (e.g. if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and should include in the \"detail\" attribute more information about the source of the problem. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and should include in the \"detail\" attribute more information about the source of the problem. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. If the request contains a malformed access token, the API producer should respond with this response. The details of the error shall be returned in the WWW Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. The use of this HTTP error response code described above is applicable to the use of the OAuth 2.0 for the authorization of API requests and notifications, as defined in clauses 4.5.3.3 and 4.5.3.4. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "401", description = "401 UNAUTHORIZED If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "403", description = "403 FORBIDDEN If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided. It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "404", description = "404 NOT FOUND If the API producer did not find a current representation for the resource addressed by the URI passed in the request or is not willing to disclose that one exists, it shall respond with this response code. The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. This response code is not appropriate in case the resource addressed by the URI is a container resource which is designed to contain child resources, but does not contain any child resource at the time the request is received. For a GET request to an existing empty container resource, a typical response contains a 200 OK response code and a payload body with an empty array. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "405", description = "405 METHOD NOT ALLOWED If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "406", description = "406 NOT ACCEPTABLE If the \"Accept\" HTTP header does not contain at least one name of a content type that is acceptable to the API producer, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "500", description = "500 INTERNAL SERVER ERROR If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "503", description = "503 SERVICE UNAVAILABLE If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 for the use of the \"Retry-After\" HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "504", description = "504 GATEWAY TIMEOUT If the API producer encounters a timeout while waiting for a response from an upstream server (i.e. a server that the API producer communicates with when fulfilling a request), it should respond with this response code. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))) })
	@RequestMapping(value = "/vnf_snapshots", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<List<VnfSnapshotInfo>> vnfSnapshotsGet(
			@Parameter(in = ParameterIn.QUERY, description = "Attribute-based filtering expression according to clause 5.2 of ETSI GS NFV-SOL 013. The NFV-MANO functional entity shall support receiving this parameter as part of the URI query string. The API consumer may supply this parameter. All attribute names that appear in the FmSubscription and in data types referenced from it shall be supported by the NFV-MANO functional entity in the filter expression. ", schema = @Schema()) @Valid @RequestParam(value = "filter", required = false) String filter,
			@Parameter(in = ParameterIn.QUERY, description = "Include all complex attributes in the response. See clause 5.3 of ETSI GS NFV-SOL 013. The NFV-MANO functional entity shall support this parameter. ", schema = @Schema()) @Valid @RequestParam(value = "all_fields", required = false) String allFields,
			@Parameter(in = ParameterIn.QUERY, description = "Complex attributes to be included into the response. See clause 5.3 of ETSI GS NFV-SOL 013 for details. The NFV-MANO functional entity should support this parameter. ", schema = @Schema()) @Valid @RequestParam(value = "fields", required = false) String fields,
			@Parameter(in = ParameterIn.QUERY, description = "Complex attributes to be excluded from the response. See clause 5.3 of ETSI GS NFV-SOL 013 for details. The NFV-MANO functional entity should support this parameter. ", schema = @Schema()) @Valid @RequestParam(value = "exclude_fields", required = false) String excludeFields,
			@Parameter(in = ParameterIn.QUERY, description = "Indicates to exclude the following complex attributes from the response. See clause 5.3 of ETSI GS NFV-SOL 013 for details. The NFV-MANO functional entity shall support this parameter. The NFVO shall support this parameter. The following attributes shall be excluded from the NsInstance structure in the response body if this parameter is provided, or none of the parameters \"all_fields,\" \"fields\", \"exclude_fields\", \"exclude_default\" are provided: - vnfInstances - pnfInfo - virtualLinkInfo - vnffgInfo - sapInfo - nsScaleStatus - additionalAffinityOrAntiAffinityRules -   wanConnectionInfo", schema = @Schema()) @Valid @RequestParam(value = "exclude_default", required = false) String excludeDefault,
			@Parameter(in = ParameterIn.QUERY, description = "Marker to obtain the next page of a paged response. Shall be supported by the NFV-MANO functional entity if the entity supports alternative 2 (paging) according to clause 5.4.2.1 of ETSI GS NFV-SOL 013 for this resource. ", schema = @Schema()) @Valid @RequestParam(value = "nextpage_opaque_marker", required = false) String nextpageOpaqueMarker);

	@Operation(summary = "", description = "This method deletes an \"Individual VNF snapshot\" resource and the associated VNF snapshot information managed by the NFVO and corresponding VNFM, and any resource associated to the VNF snapshot managed by the VIM. See clause 6.4.20.3.5. ", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "204 NO CONTENT Shall be returned when the VNF snapshot resource and the associated VNF snapshot were deleted successfully. The response body shall be empty. "),
			@ApiResponse(responseCode = "400", description = "400 BAD REQUEST 400 code can be returned in the following specified cases, the specific cause has to be proper specified in the \"ProblemDetails\" structure to be returned. If the request is malformed or syntactically incorrect (e.g. if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and should include in the \"detail\" attribute more information about the source of the problem. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and should include in the \"detail\" attribute more information about the source of the problem. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. If the request contains a malformed access token, the API producer should respond with this response. The details of the error shall be returned in the WWW Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. The use of this HTTP error response code described above is applicable to the use of the OAuth 2.0 for the authorization of API requests and notifications, as defined in clauses 4.5.3.3 and 4.5.3.4. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "401", description = "401 UNAUTHORIZED If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "403", description = "403 FORBIDDEN If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided. It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "404", description = "404 NOT FOUND If the API producer did not find a current representation for the resource addressed by the URI passed in the request or is not willing to disclose that one exists, it shall respond with this response code. The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. This response code is not appropriate in case the resource addressed by the URI is a container resource which is designed to contain child resources, but does not contain any child resource at the time the request is received. For a GET request to an existing empty container resource, a typical response contains a 200 OK response code and a payload body with an empty array. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "405", description = "405 METHOD NOT ALLOWED If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "406", description = "406 NOT ACCEPTABLE If the \"Accept\" HTTP header does not contain at least one name of a content type that is acceptable to the API producer, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "409", description = "409 CONFLICT ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "500", description = "500 INTERNAL SERVER ERROR If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "503", description = "503 SERVICE UNAVAILABLE If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 for the use of the \"Retry-After\" HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "504", description = "504 GATEWAY TIMEOUT If the API producer encounters a timeout while waiting for a response from an upstream server (i.e. a server that the API producer communicates with when fulfilling a request), it should respond with this response code. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))) })
	@RequestMapping(value = "/vnf_snapshots/{vnfSnapshotInfoId}", produces = { "application/json" }, method = RequestMethod.DELETE)
	ResponseEntity<Void> vnfSnapshotsVnfSnapshotInfoIdDelete(
			@Parameter(in = ParameterIn.PATH, description = "Identifier of the \"Individual VNF snapshot\" resource. This identifier can be retrieved from the \"id\" attribute in the payload body of a response to a query to \"VNF snapshots\" resource. ", required = true, schema = @Schema()) @PathVariable("vnfSnapshotInfoId") String vnfSnapshotInfoId);

	@Operation(summary = "", description = "The GET method retrieves information about a VNF snapshot by reading an \"Individual VNF snapshot\" resource. See clause 6.4.20.3.2. ", tags = {})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "200 OK Shall be returned when information about an individual VNF snapshot was read successfully. The response body shall contain a representation of the \"Individual VNF snapshot\" resource, as defined in clause 6.5.2.17. ", content = @Content(schema = @Schema(implementation = VnfSnapshotInfo.class))),
			@ApiResponse(responseCode = "400", description = "400 BAD REQUEST 400 code can be returned in the following specified cases, the specific cause has to be proper specified in the \"ProblemDetails\" structure to be returned. If the request is malformed or syntactically incorrect (e.g. if the request URI contains incorrect query parameters or the payload body contains a syntactically incorrect data structure), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and should include in the \"detail\" attribute more information about the source of the problem. If the response to a GET request which queries a container resource would be so big that the performance of the API producer is adversely affected, and the API producer does not support paging for the affected resource, it shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and should include in the \"detail\" attribute more information about the source of the problem. If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. If the request contains a malformed access token, the API producer should respond with this response. The details of the error shall be returned in the WWW Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. The use of this HTTP error response code described above is applicable to the use of the OAuth 2.0 for the authorization of API requests and notifications, as defined in clauses 4.5.3.3 and 4.5.3.4. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "401", description = "401 UNAUTHORIZED If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "403", description = "403 FORBIDDEN If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided. It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "404", description = "404 NOT FOUND If the API producer did not find a current representation for the resource addressed by the URI passed in the request or is not willing to disclose that one exists, it shall respond with this response code. The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. This response code is not appropriate in case the resource addressed by the URI is a container resource which is designed to contain child resources, but does not contain any child resource at the time the request is received. For a GET request to an existing empty container resource, a typical response contains a 200 OK response code and a payload body with an empty array. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "405", description = "405 METHOD NOT ALLOWED If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "406", description = "406 NOT ACCEPTABLE If the \"Accept\" HTTP header does not contain at least one name of a content type that is acceptable to the API producer, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "500", description = "500 INTERNAL SERVER ERROR If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "503", description = "503 SERVICE UNAVAILABLE If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 for the use of the \"Retry-After\" HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))),
			@ApiResponse(responseCode = "504", description = "504 GATEWAY TIMEOUT If the API producer encounters a timeout while waiting for a response from an upstream server (i.e. a server that the API producer communicates with when fulfilling a request), it should respond with this response code. ", content = @Content(schema = @Schema(implementation = ProblemDetails.class))) })
	@RequestMapping(value = "/vnf_snapshots/{vnfSnapshotInfoId}", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<VnfSnapshotInfo> vnfSnapshotsVnfSnapshotInfoIdGet(
			@Parameter(in = ParameterIn.PATH, description = "Identifier of the \"Individual VNF snapshot\" resource. This identifier can be retrieved from the \"id\" attribute in the payload body of a response to a query to \"VNF snapshots\" resource. ", required = true, schema = @Schema()) @PathVariable("vnfSnapshotInfoId") String vnfSnapshotInfoId);

}
