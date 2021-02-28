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

package com.ubiqube.etsi.mano.vnfm.v261.controller.vnflcm.sol003;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ubiqube.etsi.mano.model.ProblemDetails;
import com.ubiqube.etsi.mano.vnfm.v261.model.nslcm.LccnSubscription;
import com.ubiqube.etsi.mano.vnfm.v261.model.nslcm.LccnSubscriptionRequest;

import io.swagger.annotations.ApiParam;

@RequestMapping(value = { "/sol003/vnflcm/v1/subscriptions", "/sol002/vnflcm/v1/subscriptions" })
public interface VnfLcmSubscriptions261Sol003Api {

	@io.swagger.annotations.ApiOperation(value = "", notes = "Query Subscription Information  The GET method queries the list of active subscriptions of the functional block that invokes the method. It can be used e.g. for resynchronization after error situations. ", response = LccnSubscription.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "OK The list of subscriptions was queried successfully. The response body shall contain the representations of all active subscriptions of the functional block that invokes the method. ", response = LccnSubscription.class),
			@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  Specifically in case of this task resource, the reason can also be that the task is not supported for the VNF LCM operation occurrence represented by the parent resource, and that the task resource consequently does not exist. The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the sourceof the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 405, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable If the \"Accept\" HTTP header does not contain at least one name of a content type that is acceptable to the API producer, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case.         ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the \"Retry-After\" HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	@GetMapping(consumes = { "application/json" }, produces = { "application/json" })
	ResponseEntity<List<LccnSubscription>> subscriptionsGet(final MultiValueMap<String, String> requestParams, @Valid final String nextpageOpaqueMarker);

	@io.swagger.annotations.ApiOperation(value = "", notes = "Subscribe  The POST method creates a new subscription. Creation of two subscription resources with the same callbackURI and the same filter can result in performance degradation and will provide duplicates of notifications to the NFVO, and might make sense only in very rare use cases. Consequently, the VNFM may either allow creating a subscription resource if another subscription resource with the same filter and callbackUri already exists (in which case it shall return the “201 Created” response code), or may decide to not create a duplicate subscription resource (in which case it shall return a “303 See Other” response code referencing the existing subscription resource with the same filter and callbackUri). ", response = LccnSubscription.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 201, message = "Created The subscription was created successfully. The response body shall contain a representation of the created subscription resource. The HTTP response shall include a \"Location\" HTTP header that points to the created subscription resource. ", response = LccnSubscription.class),
			@io.swagger.annotations.ApiResponse(code = 303, message = "See Other A subscription with the same callbackURI and the same filter already exists and the policy of the VNFM is to not create redundant subscriptions. The HTTP response shall include a \"Location\" HTTP header that contains the resource URI of the existing subscription resource. The response body shall be empty. ", response = Void.class),
			@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request If the request is malformed or syntactically incorrect (e.g. if the request URI contains incorrect query parameters or a syntactically incorrect payload body), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and should include in the \"detail\" attribute more information about the source of the problem.   ---  If the request contains a malformed access token, the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided.   ---  If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond with this response code.The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  Specifically in case of this task resource, the reason can also be that the task is not supported for the VNF LCM operation occurrence represented by the parent resource, and that the task resource consequently does not exist. The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the sourceof the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 405, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable If the \"Accept\" HTTP header does not contain at least one name of a content type that is acceptable to the API producer, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case.         ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the \"Retry-After\" HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	@PostMapping(consumes = { "application/json" }, produces = { "application/json" })
	ResponseEntity<LccnSubscription> subscriptionsPost(@ApiParam(value = "Details of the subscription to be created. ", required = true) @RequestBody LccnSubscriptionRequest lccnSubscriptionRequest);

	@io.swagger.annotations.ApiOperation(value = "", notes = "Terminate Subscription  The DELETE method terminates an individual subscription. ", response = Void.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 204, message = "No Content The subscription resource was deleted successfully. The response body shall be empty. ", response = Void.class),
			@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request If the request is malformed or syntactically incorrect (e.g. if the request URI contains incorrect query parameters or a syntactically incorrect payload body), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and should include in the \"detail\" attribute more information about the source of the problem.   ---  If the request contains a malformed access token, the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided.   ---  If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond with this response code.The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  Specifically in case of this task resource, the reason can also be that the task is not supported for the VNF LCM operation occurrence represented by the parent resource, and that the task resource consequently does not exist. The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the sourceof the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 405, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable If the \"Accept\" HTTP header does not contain at least one name of a content type that is acceptable to the API producer, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case.         ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the \"Retry-After\" HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	@DeleteMapping(value = "/{subscriptionId}", consumes = { "application/json" }, produces = { "application/json" })
	ResponseEntity<Void> subscriptionsSubscriptionIdDelete(@ApiParam(value = "Identifier of this subscription. This identifier can be retrieved from the resource referenced by the \"Location\" HTTP header in the response to a POST request creating a new subscription resource. It can also be retrieved from the \"id\" attribute in the payload body of that response. ", required = true) @PathVariable("subscriptionId") String subscriptionId);

	@io.swagger.annotations.ApiOperation(value = "", notes = "Query Subscription Information  The GET method retrieves information about a subscription by reading an individual subscription resource. ", response = LccnSubscription.class, tags = {})
	@io.swagger.annotations.ApiResponses(value = {
			@io.swagger.annotations.ApiResponse(code = 200, message = "OK The operation has completed successfully. The response body shall contain a representation of the subscription resource. ", response = LccnSubscription.class),
			@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request If the request is malformed or syntactically incorrect (e.g. if the request URI contains incorrect query parameters or a syntactically incorrect payload body), the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided, and should include in the \"detail\" attribute more information about the source of the problem.   ---  If the request contains a malformed access token, the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided.   ---  If there is an application error related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond with this response code.The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  Specifically in case of this task resource, the reason can also be that the task is not supported for the VNF LCM operation occurrence represented by the parent resource, and that the task resource consequently does not exist. The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the sourceof the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 405, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 406, message = "Not Acceptable If the \"Accept\" HTTP header does not contain at least one name of a content type that is acceptable to the API producer, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case.         ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The \"ProblemDetails\" structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@io.swagger.annotations.ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the \"Retry-After\" HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	@GetMapping(value = "/{subscriptionId}", consumes = { "application/json" }, produces = { "application/json" })
	ResponseEntity<LccnSubscription> subscriptionsSubscriptionIdGet(@ApiParam(value = "Identifier of this subscription. This identifier can be retrieved from the resource referenced by the \"Location\" HTTP header in the response to a POST request creating a new subscription resource. It can also be retrieved from the \"id\" attribute in the payload body of that response. ", required = true) @PathVariable("subscriptionId") String subscriptionId);

}