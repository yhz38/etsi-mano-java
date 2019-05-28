package com.ubiqube.api.rs.endpoints.nfvo.vnf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ubiqube.api.ejb.nfvo.utils.RangeHeader;
import com.ubiqube.api.ejb.nfvo.vnf.InlineResponse2001;
import com.ubiqube.api.ejb.nfvo.vnf.NotificationVnfPackageOnboardingNotification;
import com.ubiqube.api.ejb.nfvo.vnf.NotificationsMessage;
import com.ubiqube.api.ejb.nfvo.vnf.ProblemDetails;
import com.ubiqube.api.ejb.nfvo.vnf.SubscriptionObject;
import com.ubiqube.api.ejb.nfvo.vnf.SubscriptionsPkgmSubscription;
import com.ubiqube.api.ejb.nfvo.vnf.SubscriptionsPkgmSubscriptionFilter;
import com.ubiqube.api.ejb.nfvo.vnf.SubscriptionsPkgmSubscriptionRequestAuthentication;
import com.ubiqube.api.ejb.nfvo.vnf.SubscriptionsPostQuery;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPackageChangeNotification;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPackagePostQuery;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPackagesVnfPkgIdGetResponse;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPackagesVnfPkgIdPackageContentUploadFromUriPostRequest;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPackagesVnfPkgIdPatchQuery;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPackagesVnfPkgInfoLinks;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPackagesVnfPkgInfoLinksSelf;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPkgInfo;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPkgInfo.OnboardingStateEnum;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPkgInfo.OperationalStateEnum;
import com.ubiqube.api.ejb.nfvo.vnf.VnfPkgInfo.UsageStateEnum;
import com.ubiqube.api.entities.repository.RepositoryElement;
import com.ubiqube.api.exception.ServiceException;
import com.ubiqube.api.interfaces.lookup.LookupService;
import com.ubiqube.api.interfaces.orchestration.OrchestrationService;
import com.ubiqube.api.interfaces.repository.RepositoryService;
import com.ubiqube.api.rs.endpoints.nfvo.GenericException;
import com.ubiqube.api.rs.endpoints.nfvo.SubscriptionRepository;
import com.ubiqube.api.rs.endpoints.nfvo.VnfPackageRepository;
import com.ubiqube.api.rs.exception.etsi.BadRequestException;
import com.ubiqube.api.rs.exception.etsi.ConflictException;
import com.ubiqube.api.rs.exception.etsi.NotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.jaxrs.PATCH;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * SOL005 - VNF Package Management Interface
 *
 * <p>
 * SOL005 - VNF Package Management Interface IMPORTANT: Please note that this
 * file might be not aligned to the current version of the ETSI Group
 * Specification it refers to and has not been approved by the ETSI NFV ISG. In
 * case of discrepancies the published ETSI Group Specification takes
 * precedence. Please report bugs to
 * https://forge.etsi.org/bugzilla/buglist.cgi?component=Nfv-Openapis
 *
 */
@Path("/vnfpkgm/v1")
@Api(value = "/vnfpkgm/v1", description = "")
public class DefaultApiServiceImpl implements DefaultApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultApiServiceImpl.class);
	private final static String PROCESS_BASE_PATH = "Process";
	private final static String PROCESS_NFVO_BASE_PATH = PROCESS_BASE_PATH + "/NFVO";
	private final static String PROCESS_VNF_VNF_PCKGM_BASE_PATH = PROCESS_NFVO_BASE_PATH + "/VNF_PCKGM";
	private final static String DATAFILE_BASE_PATH = "Datafiles";
	private final static String NVFO_DATAFILE_BASE_PATH = "Datafiles/NFVO";
	private final static String REPOSITORY_NVFO_DATAFILE_BASE_PATH = "Datafiles/NFVO/vnf_packages";
	private final static String REPOSITORY_SUBSCRIPTION_BASE_PATH = NVFO_DATAFILE_BASE_PATH + "/subscriptions";

	private final SubscriptionRepository subscriptionRepository = new SubscriptionRepository();
	private final VnfPackageRepository vnfPackageRepository = new VnfPackageRepository();

	private final String SERVICE_NANE = new StringBuilder().append(PROCESS_VNF_VNF_PCKGM_BASE_PATH).append("vnf_packages").toString();
	private final ObjectMapper mapper = new ObjectMapper();
	private OrchestrationService orchestrationService;
	private RepositoryService repositoryService;
	private LookupService lookupService;

	public DefaultApiServiceImpl() {
		try {
			final InitialContext jndiContext = new InitialContext();
			orchestrationService = (OrchestrationService) jndiContext.lookup(OrchestrationService.RemoteJNDIName);
			repositoryService = (RepositoryService) jndiContext.lookup(RepositoryService.RemoteJNDIName);
			lookupService = (LookupService) jndiContext.lookup(LookupService.RemoteJNDIName);

			init();
		} catch (final NamingException e) {
			throw new RuntimeException(e);
		} catch (final ServiceException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Map YAML file to JsonObject.
	 *
	 * @param yaml VNF Package Metadata from repository
	 * @return the JsonObject as a String
	 */
	private static String convertYamlToJson(final String yaml) {
		try {
			final ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
			final Object obj = yamlReader.readValue(yaml, Object.class);
			final ObjectMapper jsonWriter = new ObjectMapper();
			return jsonWriter.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (final JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * MSA related stuff.
	 *
	 * @throws ServiceException
	 */
	private void init() throws ServiceException {
		if (!repositoryService.exists(PROCESS_BASE_PATH)) {
			repositoryService.addDirectory(PROCESS_BASE_PATH, "", "SOL005", "ncroot");
		}
		if (!repositoryService.exists(PROCESS_NFVO_BASE_PATH)) {
			repositoryService.addDirectory(PROCESS_NFVO_BASE_PATH, "", "SOL005", "ncroot");
		}

		if (!repositoryService.exists(PROCESS_VNF_VNF_PCKGM_BASE_PATH)) {
			repositoryService.addDirectory(PROCESS_VNF_VNF_PCKGM_BASE_PATH, "", "SOL005", "ncroot");
		}

		if (!repositoryService.exists(DATAFILE_BASE_PATH)) {
			repositoryService.addDirectory(DATAFILE_BASE_PATH, "", "SOL005", "ncroot");
		}
		if (!repositoryService.exists(NVFO_DATAFILE_BASE_PATH)) {
			repositoryService.addDirectory(NVFO_DATAFILE_BASE_PATH, "", "SOL005", "ncroot");
		}
		if (!repositoryService.exists(REPOSITORY_NVFO_DATAFILE_BASE_PATH)) {
			repositoryService.addDirectory(REPOSITORY_NVFO_DATAFILE_BASE_PATH, "", "SOL005", "ncroot");
		}
		if (!repositoryService.exists(REPOSITORY_SUBSCRIPTION_BASE_PATH)) {
			repositoryService.addDirectory(REPOSITORY_SUBSCRIPTION_BASE_PATH, "", "SOL005", "ncroot");
		}
	}

	@GET
	@Path("/subs")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Query multiple subscriptions.", tags = {})
	public List<InlineResponse2001> subscriptionsGet(InlineResponse2001 inlineResponse2001, @Context SecurityContext securityContext) {
		return new ArrayList<InlineResponse2001>();
	}

	/**
	 * Query multiple subscriptions.
	 *
	 * The GET method queries the list of active subscriptions of the functional
	 * block that invokes the method. It can be used e.g. for resynchronization
	 * after error situations. This method shall follow the provisions specified in
	 * the Tables 9.4.7.8.2-1 and 9.4.8.3.2-2 for URI query parameters, request and
	 * response data structures, and response codes. ²
	 */
	@GET
	@Path("/subscriptions")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Query multiple subscriptions.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "200 OK Active subscriptions of the functional block that invokes the method. ", response = Object.class, responseContainer = "List"), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class),
			@ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	@Override
	public List<InlineResponse2001> subscriptionsGet(@HeaderParam("Accept") String accept, @QueryParam("filter") String filter, @Context SecurityContext securityContext) {
		List<String> listFilesInFolder;
		try {
			listFilesInFolder = repositoryService.doSearch(REPOSITORY_SUBSCRIPTION_BASE_PATH, "");
		} catch (final ServiceException e) {
			throw new GenericException(e);
		}
		final List<InlineResponse2001> response = new ArrayList<InlineResponse2001>();
		for (final String entry : listFilesInFolder) {
			final RepositoryElement repositoryElement = repositoryService.getElement(entry);
			final String content = new String(repositoryService.getRepositoryElementContent(repositoryElement));
			try {
				final SubscriptionObject subscriptionObject = mapper.readValue(content, SubscriptionObject.class);

				final InlineResponse2001 pack = new InlineResponse2001();
				final SubscriptionsPkgmSubscription subscriptionsPkgmSubscription = subscriptionObject.getSubscriptionsPkgmSubscription();
				pack.setPkgmSubscription(subscriptionsPkgmSubscription);
				response.add(pack);
			} catch (final Exception e) {
				throw new GenericException(e);
			}
		}
		return response;
	}

	/**
	 * Subscribe to notifications related to on-boarding and/or changes of VNF
	 * packages.
	 *
	 * The POST method creates a new subscription. This method shall follow the
	 * provisions specified in the Tables 9.4.8.3.1-1 and 9.4.8.3.1-2 for URI query
	 * parameters, request and response data structures, and response codes.
	 * Creation of two subscription resources with the same callbackURI and the same
	 * filter can result in performance degradation and will provide duplicates of
	 * notifications to the OSS, and might make sense only in very rare use cases.
	 * Consequently, the NFVO may either allow creating a subscription resource if
	 * another subscription resource with the same filter and callbackUri already
	 * exists (in which case it shall return the \&quot;201 Created\&quot; response
	 * code), or may decide to not create a duplicate subscription resource (in
	 * which case it shall return a \&quot;303 See Other\&quot; response code
	 * referencing the existing subscription resource with the same filter and
	 * callbackUri).
	 *
	 */
	@POST
	@Path("/subscriptions")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Subscribe to notifications related to on-boarding and/or changes of VNF packages.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 201, message = "201 Created Representation of the created subscription resource. The HTTP response shall include a \"Location\" HTTP header that points to the created subscription resource. ", response = Object.class, responseContainer = "List"), @ApiResponse(code = 303, message = "See Other A subscription with the same callbackURI and the same filter already exists and the policy of the VNFM is to not create redundant subscriptions. The HTTP response shall include a \"Location\" HTTP header that contains the resource URI of the existing subscription resource. The response body shall be empty. "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class),
			@ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class), @ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class),
			@ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class), @ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class),
			@ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class), @ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	@Override
	public List<InlineResponse2001> subscriptionsPost(@HeaderParam("Accept") String accept, @HeaderParam("Content-Type") String contentType, String body, @Context SecurityContext securityContext, @Context UriInfo uriInfo) {
		SubscriptionsPostQuery subscriptionsPostQuery;
		try {
			subscriptionsPostQuery = mapper.readValue(body, SubscriptionsPostQuery.class);
		} catch (final Exception e) {
			throw new GenericException(e);
		}

		// Job
		final String id = UUID.randomUUID().toString();
		// Response
		final ArrayList<InlineResponse2001> response = new ArrayList<InlineResponse2001>();
		final String callback = subscriptionsPostQuery.getPkgmSubscriptionRequest().getCallbackUri();
		final String href = Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(this.getClass(), "subscriptionsSubscriptionIdGet")).build(id).getUri().toString();
		final SubscriptionsPkgmSubscriptionFilter filter = subscriptionsPostQuery.getPkgmSubscriptionRequest().getFilter();
		final SubscriptionsPkgmSubscription subscription = new SubscriptionsPkgmSubscription(callback, id, href, filter);

		final InlineResponse2001 pack = new InlineResponse2001();
		pack.setPkgmSubscription(subscription);
		final SubscriptionObject subscriptionObject = new SubscriptionObject(subscriptionsPostQuery.getPkgmSubscriptionRequest().getAuthentication(), subscription);
		subscriptionRepository.save(subscriptionObject);

		response.add(pack);

		return response;
	}

	/**
	 * Terminate a subscription.
	 *
	 * The DELETE method terminates an individual subscription.
	 *
	 */
	@Override
	@DELETE
	@Path("/subscriptions/{subscriptionId}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Terminate a subscription.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content The subscription resource was deleted successfully. "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class),
			@ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public void subscriptionsSubscriptionIdDelete(@PathParam("subscriptionId") String subscriptionId, @Context SecurityContext securityContext) {
		subscriptionRepository.delete(subscriptionId);
	}

	/**
	 * Read an individual subscription resource.
	 *
	 * Query Subscription Information The GET method reads an individual
	 * subscription.
	 *
	 */
	@Override
	@GET
	@Path("/subscriptions/{subscriptionId}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Read an individual subscription resource.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "200 OK Representation of the subscription resource. ", response = Object.class), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class),
			@ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public String subscriptionsSubscriptionIdGet(@PathParam("subscriptionId") String subscriptionId, @HeaderParam("Accept") String accept, @Context SecurityContext securityContext) {
		final InlineResponse2001 inlineResponse2001 = new InlineResponse2001();
		inlineResponse2001.setPkgmSubscription(subscriptionRepository.get(subscriptionId).getSubscriptionsPkgmSubscription());
		try {
			return mapper.writeValueAsString(inlineResponse2001);
		} catch (final JsonProcessingException e) {
			throw new GenericException(e);
		}
	}

	/**
	 * Test the notification endpoint
	 *
	 * The GET method allows the server to test the notification endpoint that is
	 * provided by the client, e.g. during subscription. This method shall follow
	 * the provisions specified in the Tables 9.4.10.3.2-1 and 9.4.10.3.2-2 for URI
	 * query parameters, request and response data structures, and response codes.
	 *
	 */
	@Override
	@GET
	@Path("/URI_is_provided_by_the_client_when_creating_the_subscription-VnfPackageChangeNotification")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Test the notification endpoint", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 204, message = "204 No Content The notification endpoint was tested successfully. The response body shall be empty.  "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public void uRIIsProvidedByTheClientWhenCreatingTheSubscriptionVnfPackageChangeNotificationGet(@HeaderParam("Accept") String accept, @Context SecurityContext securityContext) {
		// Nothing.
	}

	/**
	 * Notify about VNF package onboarding or change
	 *
	 * The POST method delivers a notification from the server to the client. This
	 * method shall follow the provisions specified in the Tables 9.4.10.3.1-1 and
	 * 9.4.10.3.1-2 for URI query parameters, request and response data structures,
	 * and response codes.
	 *
	 */
	@Override
	@POST
	@Path("/vnfPackageChangeNotification")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Notify about VNF package onboarding or change", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 204, message = "204 No Content The notification was delivered successfully. "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public void vnfPackageChangeNotificationPost(String body, @Context UriInfo uriInfo, @Context SecurityContext securityContext) {
		final NotificationsMessage notificationsMessage;

		try {
			notificationsMessage = mapper.readValue(body, NotificationsMessage.class);
		} catch (final Exception e) {
			throw new GenericException(e);
		}

		final Notifications notifications = new Notifications();
		final String id = UUID.randomUUID().toString();
		final String vnfPkgId = notificationsMessage.getVnfPkgId();
		final String vnfdId = notificationsMessage.getVnfdId();
		final String subscriptionId = notificationsMessage.getSubscriptionId();

		final SubscriptionObject subscriptionsRepository = subscriptionRepository.get(subscriptionId);
		final SubscriptionsPkgmSubscriptionRequestAuthentication auth = subscriptionsRepository.getSubscriptionsPkgmSubscriptionRequestAuthentication();
		final String callbackUri = subscriptionsRepository.getSubscriptionsPkgmSubscription().getCallbackUri();

		final String hrefVnfPackage = Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(this.getClass(), "vnfPackagesVnfPkgIdGet")).build(vnfPkgId).getUri().toString();
		final String hrefSubscription = Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(this.getClass(), "subscriptionsSubscriptionIdGet")).build(subscriptionId).getUri().toString();

		final VnfPackageChangeNotification vnfPackageChangeNotification = new VnfPackageChangeNotification(id, vnfPkgId, vnfdId, subscriptionId, hrefVnfPackage, hrefSubscription);
		notifications.doNotification(vnfPackageChangeNotification, callbackUri, auth);
	}

	/**
	 * Notify about VNF package onboarding or change
	 *
	 * The POST method delivers a notification from the server to the client. This
	 * method shall follow the provisions specified in the Tables 9.4.10.3.1-1 and
	 * 9.4.10.3.1-2 for URI query parameters, request and response data structures,
	 * and response codes.
	 *
	 */
	@Override
	@POST
	@Path("/vnfPackageOnboardingNotification")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Notify about VNF package onboarding or change", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 204, message = "204 No Content The notification was delivered successfully.   "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public void vnfPackageOnboardingNotificationPost(String body, @Context UriInfo uriInfo, @Context SecurityContext securityContext) {
		final NotificationsMessage notificationsMessage;

		try {
			notificationsMessage = mapper.readValue(body, NotificationsMessage.class);
		} catch (final Exception e) {
			throw new GenericException(e);
		}
		final Notifications notifications = new Notifications();
		final String id = UUID.randomUUID().toString();

		final String subscriptionId = notificationsMessage.getSubscriptionId();
		final SubscriptionObject subscriptionsRepository = subscriptionRepository.get(subscriptionId);
		final SubscriptionsPkgmSubscription req = subscriptionsRepository.getSubscriptionsPkgmSubscription();
		final String cbUrl = req.getCallbackUri();
		final String vnfPkgId = notificationsMessage.getVnfPkgId();
		final String vnfdId = notificationsMessage.getVnfdId();
		final SubscriptionsPkgmSubscriptionRequestAuthentication auth = subscriptionsRepository.getSubscriptionsPkgmSubscriptionRequestAuthentication();

		final String hrefSubscription = Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(this.getClass(), "subscriptionsSubscriptionIdGet")).build(subscriptionId).getUri().toString();
		final String hrefPackage = Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(this.getClass(), "vnfPackagesVnfPkgIdGet")).build(vnfPkgId).getUri().toString();
		final NotificationVnfPackageOnboardingNotification notificationVnfPackageOnboardingNotification = new NotificationVnfPackageOnboardingNotification(id, "", subscriptionId, vnfPkgId, vnfdId, hrefSubscription, hrefPackage);

		notifications.doNotification(notificationVnfPackageOnboardingNotification, cbUrl, auth);
	}

	/**
	 * Query VNF packages information.
	 *
	 * The GET method queries the information of the VNF packages matching the
	 * filter. This method shall follow the provisions specified in the Tables
	 * 9.4.2.3.2-1 and 9.4.2.3.2-2 for URI query parameters, request and response
	 * data structures, and response codes.
	 *
	 */
	@Override
	@GET
	@Path("/vnf_packages")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Query VNF packages information.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "200 OK Information of the selected VNF packages. ", response = Object.class, responseContainer = "List"), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class),
			@ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public Response vnfPackagesGet(@HeaderParam("Accept") String accept, @Context UriInfo uriInfo, @Context SecurityContext securityContext) throws ServiceException {

		LOGGER.info(new StringBuilder("Method called: ").append(new Object() {
		}.getClass().getEnclosingMethod().getName()).toString());

		final List<String> vnfPkgsIdsList = getVnfPkgIdsFromRepository();

		// TODO - Refactor this method to map vnfpkgInfo from YAML to VnfPkgInfo object
		// and return a list of VnfPackagesVnfPkgIdGetResponse.

		JSONArray vnfPkginfos = new JSONArray();
		for (final String vnfPckId : vnfPkgsIdsList) {
			final String uri = new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/")
					.append(vnfPckId).append("/").append("Metadata.yaml").toString();
			final RepositoryElement repositoryElement = repositoryService.getElement(uri);
			final String content = new String(repositoryService.getRepositoryElementContent(repositoryElement));
			final JSONObject contentJson = JSONObject.fromObject(convertYamlToJson(content));
			vnfPkginfos = applyAttributebasedFilteringAndSelectors(uriInfo, vnfPkginfos, contentJson);
		}
		return Response.ok(vnfPkginfos).build();
	}

	/**
	 * Create a new individual VNF package resource. 9.5.2.4
	 *
	 * The POST method creates a new individual VNF package resource.
	 *
	 */
	@Override
	@POST
	@Path("/vnf_packages")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Create a new individual VNF package resource.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 201, message = "201 Created             An individual VNF package resource has been created successfully. The response body shall contain a representation of the new individual VNF package resource, as defined in clause 9.5.2.4. The HTTP response shall include a \"Location\" HTTP header that contains the resource URI of the individual VNF package resource. ", response = VnfPackagesVnfPkgIdGetResponse.class) })
	public Response vnfPackagesPost(@HeaderParam("Accept") String accept, @HeaderParam("Content-Type") String contentType, String body, @Context SecurityContext securityContext, @Context UriInfo uriInfo) {
		VnfPackagePostQuery vnfPackagePostQuery;
		try {
			vnfPackagePostQuery = mapper.readValue(body, VnfPackagePostQuery.class);
		} catch (final Exception e) {
			throw new GenericException(e);
		}
		final String vnfPkgId = UUID.randomUUID().toString();
		final Object jsonString = vnfPackagePostQuery.getCreateVnfPkgInfoRequest().getUserDefinedData();
		final VnfPkgInfo vnfPkgInfo = new VnfPkgInfo();
		vnfPkgInfo.setId(vnfPkgId);
		vnfPkgInfo.setOnboardingState(OnboardingStateEnum.CREATED);

		try {
			final StringBuilder sb = new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/").append(vnfPkgId);
			String uri = sb.toString();
			if (!repositoryService.exists(uri.toString())) {
				repositoryService.addDirectory(uri, "", "SOL005", "ncroot");
			}

			uri = sb.append("/").append("Metadata.yaml").toString();
			try {
				repositoryService.addFile(uri, "", "Added: SOL005", mapper.writeValueAsString(jsonString).toString(), "ncroot");
			} catch (final JsonProcessingException e) {
				throw new GenericException(e);
			}
		} catch (final ServiceException e) {
			throw new RuntimeException(e);
		}

		vnfPkgInfo.setUserDefinedData(jsonString);

		/*
		 * VnfPackagesVnfPkgInfoChecksum checksum = new VnfPackagesVnfPkgInfoChecksum();
		 * checksum.algorithm("SHA-256"); String hash =
		 * Hashing.sha256().hashString(jsonString, StandardCharsets.UTF_8).toString();
		 * checksum.setHash(hash); gud.setChecksum(checksum);
		 */

		final VnfPackagesVnfPkgIdGetResponse vnfPackagesVnfPkgIdGetResponse = new VnfPackagesVnfPkgIdGetResponse();
		vnfPackagesVnfPkgIdGetResponse.setVnfPkgInfo(vnfPkgInfo);
		final VnfPackagesVnfPkgInfoLinks links = new VnfPackagesVnfPkgInfoLinks();
		final VnfPackagesVnfPkgInfoLinksSelf self = new VnfPackagesVnfPkgInfoLinksSelf();
		self.setHref(Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(this.getClass(), "vnfPackagesVnfPkgIdGet")).build(vnfPkgId).getUri().toString());
		links.self(self);
		vnfPkgInfo.setLinks(links);
		vnfPkgInfo.setOperationalState(OperationalStateEnum.DISABLED);
		vnfPkgInfo.setUsageState(UsageStateEnum.NOT_IN_USE);
		vnfPackageRepository.save(vnfPkgInfo);
		// Return.
		URI uri;
		try {
			uri = new URI(self.getHref());
		} catch (final URISyntaxException e) {
			throw new GenericException(e);
		}
		return Response.status(201).contentLocation(uri).entity(vnfPackagesVnfPkgIdGetResponse).build();
	}

	/**
	 * Fetch individual VNF package artifact.
	 *
	 * The GET method fetches the content of an artifact within a VNF package. This
	 * method shall follow the provisions specified in the Tables 9.4.7.3.2-1 and
	 * 9.4.7.3.2-2 for URI query parameters, request and response data structures,
	 * and response codes.
	 *
	 */
	@Override
	@GET
	@Path("/vnf_packages/{vnfPkgId}/artifacts/{artifactPath:.*}")
	@Produces({ MediaType.APPLICATION_JSON, "application/zip" })
	@ApiOperation(value = "Fetch individual VNF package artifact.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "200 OK  On success, the content of the artifact is returned. The payload body shall contain a copy of the artifact file from the VNF package, as defined by ETSI GS NFV-SOL 004. The \"Content-Type\" HTTP header shall be set according to the content type of the artifact file. If the content type cannot be determined, the header shall be set to the value \"application/octet-stream\". "), @ApiResponse(code = 206, message = "Partial Content. On success, if the NFVO supports range requests, a single consecutive byte range from the content of the VNF package file is returned. The response body shall contain the requested part of the VNF package file. The \"Content-Range\" HTTP header shall be provided according to IETF RFC 7233. The \"Content-Type\" HTTP header shall be set as defined above for the \"200 OK\" response. "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class),
			@ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class), @ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class),
			@ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class), @ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class),
			@ApiResponse(code = 409, message = "Conflict. Error: The operation cannot be executed currently, due to a conflict with the state of the resource. Typically, this is due to any of the following scenarios: - Disable a VNF package resource of hich the operational state is not ENABLED - Enable a VNF package resource of which the operational state is not DISABLED The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute shall convey more information about the error. ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class),
			@ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class), @ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public Response vnfPackagesVnfPkgIdArtifactsArtifactPathGet(@PathParam("vnfPkgId") String vnfPkgId, @PathParam("artifactPath") String artifactPath, @HeaderParam("Accept") String accept, @Context SecurityContext securityContext, @HeaderParam("Range") String range) throws ServiceException {

		LOGGER.info(new StringBuilder("Method called: ").append(new Object() {
		}.getClass().getEnclosingMethod().getName()).toString());

		getVnfPkgIndividualInfoOrCheckOnboardingStatus(vnfPkgId, true);

		final List<String> listvnfPckgFiles = repositoryService.doSearch(new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/").append(vnfPkgId).append("/").append(artifactPath.trim()).toString(), "");

		if (listvnfPckgFiles.size() != 0) {
			final Response response = getZipArchive(range, listvnfPckgFiles);
			return response;
		}
		throw new NotFoundException(new StringBuilder("VNF package artifact not found for vnfPack with id: ")
				.append(vnfPkgId).append(" artifactPath: ").append(artifactPath).toString());
	}

	/**
	 * Delete an individual VNF package.
	 *
	 * The DELETE method deletes an individual VNF Package resource.
	 *
	 */
	@Override
	@DELETE
	@Path("/vnf_packages/{vnfPkgId}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Delete an individual VNF package.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 204, message = "204 No Content The VNF package was deleted successfully. The response body shall be empty. "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class),
			@ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class), @ApiResponse(code = 409, message = "Conflict. Error: The operation cannot be executed currently, due to a conflict with the state of the resource. Typically, this is due to any of the following scenarios: - Disable a VNF package resource of hich the operational state is not ENABLED - Enable a VNF package resource of which the operational state is not DISABLED The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute shall convey more information about the error. ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class),
			@ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class), @ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public Response vnfPackagesVnfPkgIdDelete(@PathParam("vnfPkgId") String vnfPkgId, @Context SecurityContext securityContext) {
		final String uri = new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/").append(sanitize(vnfPkgId)).toString();
		getVnfPkgIndividualInfoOrCheckOnboardingStatus(vnfPkgId, true);
		boolean isVnfdMetafile = false;
		try {
			isVnfdMetafile = repositoryService.exists(uri);
		} catch (final ServiceException e) {
			throw new NotFoundException("No such object: " + vnfPkgId);
		}
		if (isVnfdMetafile) {
			final RepositoryElement repositoryElement = repositoryService.getElement(uri);
			repositoryService.deleteRepositoryElement(repositoryElement, "ncroot");
			return null;
		}
		throw new NotFoundException("No such object: " + vnfPkgId);
	}

	/**
	 * Read information about an individual VNF package.
	 *
	 * The GET method reads the information of a VNF package.
	 *
	 */
	@Override
	@GET
	@Path("/vnf_packages/{vnfPkgId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Read information about an individual VNF package.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "200 OK Information of the VNF package.             ", response = VnfPackagesVnfPkgIdGetResponse.class), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class),
			@ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public Response vnfPackagesVnfPkgIdGet(@PathParam("vnfPkgId") String vnfPkgId, @HeaderParam("Accept") String accept) throws ServiceException {

		LOGGER.info(new StringBuilder("Method called: ").append(new Object() {
		}.getClass().getEnclosingMethod().getName()).toString());

		final VnfPkgInfo vnfPkgInfo = getVnfPkgIndividualInfoOrCheckOnboardingStatus(vnfPkgId, false);
		final VnfPackagesVnfPkgIdGetResponse vnfPackagesVnfPkgIdGetResponse = new VnfPackagesVnfPkgIdGetResponse();
		vnfPackagesVnfPkgIdGetResponse.setVnfPkgInfo(vnfPkgInfo);
		return Response.ok(vnfPackagesVnfPkgIdGetResponse).build();
	}

	/**
	 * Fetch an on-boarded VNF package.
	 *
	 * The GET method fetches the content of a VNF package identified by the VNF
	 * package identifier allocated by the NFVO. This method shall follow the
	 * provisions specified in the Tables 9.4.5.3.2-1 and 9.4.5.3.2-2 for URI query
	 * parameters, request and response data structures, and response codes.
	 *
	 * @throws IOException
	 * @throws ServiceException
	 *
	 */
	@Override
	@GET
	@Path("/vnf_packages/{vnfPkgId}/package_content")
	@Produces({ MediaType.APPLICATION_JSON, "application/zip" })
	@ApiOperation(value = "Fetch an on-boarded VNF package.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "200 OK On success, a copy of the VNF package file is returned. The response body shall include a copy of the VNF package file. The \"Content-Type\" HTTP header shall be set according to the type of the file, i.e. to \"application/zip\" for a VNF Package as defined in ETSI GS NFV-SOL 004 [5]. "), @ApiResponse(code = 206, message = "Partial Content. On success, if the NFVO supports range requests, a single consecutive byte range from the content of the NSD file is returned. The response body shall contain the requested part of the NSD file. The \"Content-Range\" HTTP header shall be provided according to IETF RFC 7233 [23]. The \"Content-Type\" HTTP header shall be set as defined above for the \"200 OK\" response.       ", response = ProblemDetails.class), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class),
			@ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class), @ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class),
			@ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class), @ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class),
			@ApiResponse(code = 409, message = "Conflict. Error: The operation cannot be executed currently, due to a conflict with the state of the resource. Typically, this is due to the fact that \"onboardingState\" of the VNF package has a value different from \"ONBOARDED\". The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute shall convey more information about the error. ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class),
			@ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class), @ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public Response vnfPackagesVnfPkgIdPackageContentGet(@PathParam("vnfPkgId") String vnfPkgId, @HeaderParam("Accept") String accept, @Context SecurityContext securityContext, @HeaderParam("Range") String range) throws ServiceException {

		LOGGER.info(new StringBuilder("Method called: ").append(new Object() {
		}.getClass().getEnclosingMethod().getName()).toString());

		getVnfPkgIndividualInfoOrCheckOnboardingStatus(vnfPkgId, true);

		// List vnfd package from repository
		final List<String> listvnfPckgFiles = repositoryService.doSearch(new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/").append(vnfPkgId).toString(), "");

		if (listvnfPckgFiles.size() != 0) {
			final Response response = getZipArchive(range, listvnfPckgFiles);
			return response;
		}
		throw new NotFoundException("VNF package content not found for vnfPkgId: " + vnfPkgId);
	}

	/**
	 * Upload a VNF package by providing the content of the VNF package.
	 *
	 * The PUT method uploads the content of a VNF package. This method shall follow
	 * the provisions specified in the Tables 9.4.5.3.3-1 and 9.4.5.3.3-2 for URI
	 * query parameters, request and response data structures, and response codes.
	 *
	 */
	@Override
	@PUT
	@Path("/vnf_packages/{vnfPkgId}/package_content")
	@Consumes({ "multipart/form-data" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Upload a VNF package by providing the content of the VNF package.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 202, message = "202 Accepted The VNF package was accepted for uploading, but the processing has not been completed. It is expected to take some time for processing. The response body shall be empty. "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class),
			@ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class), @ApiResponse(code = 409, message = "Conflict. Error: The operation cannot be executed currently, due to a conflict with the state of the resource. Typically, this is due to the fact that the on boarding state of the VNF package resource is not CREATED . The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute shall convey more information about the error. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public Response vnfPackagesVnfPkgIdPackageContentPut(@PathParam("vnfPkgId") String vnfPkgId, @HeaderParam("Accept") String accept, @Context SecurityContext securityContext,
			/* @Multipart(value = "file", required = false) FileInputStream fileDetail, */
			// OK:FormDataMultiPart multipart
			@FormDataParam("file") InputStream fileDetail, @FormDataParam("file") FormDataBodyPart part) {
		final String uri = new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/").append(vnfPkgId).toString();
		try {
			if (!repositoryService.exists(uri)) {
				throw new NotFoundException("No such object: " + vnfPkgId);
			}
		} catch (final ServiceException e) {
			throw new GenericException(e);
		}
		if (isZip(part.getMediaType().toString())) {
			// Normally we should do an asynchrone call
			try {
				unzip(vnfPkgId, fileDetail);
			} catch (final Exception e) {
				throw new GenericException(e);
			}
		}
		return Response.ok().build();
	}

	private boolean isZip(String httpAccept) {
		if ("application/zip".equals(httpAccept)) {
			return true;
		}
		if ("application/x-zip-compressed".equals(httpAccept)) {
			return true;
		}
		return false;
	}

	private void unzip(String vnfPkgId, InputStream fileDetail) throws IOException, ServiceException {
		final ZipInputStream zis = new ZipInputStream(fileDetail);
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			String fileName = ze.getName();
			fileName = sanitize(fileName);
			if (ze.isDirectory()) {
				// XXX/ Fix Path ending by '/'
				if (fileName.endsWith("/")) {
					fileName = fileName.substring(0, fileName.length() - 1);
				}
				final String uri = new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/").append(vnfPkgId).append("/").append(fileName).toString();
				repositoryService.addDirectory(uri, "", "SOL005", "ncroot");
				continue;
			}

			final String uri = new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/").append(vnfPkgId).append("/").append(fileName).toString();
			final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			final byte buffer[] = new byte[1024];
			int read;
			while ((read = zis.read(buffer)) != -1) {
				baos.write(buffer, 0, read);
			}
			repositoryService.addFile(uri, "SOL005", "", baos.toByteArray(), "ncroot");
		}
	}

	/**
	 * Prevent directory traversal.
	 *
	 * @param fileName
	 * @return
	 */
	private String sanitize(String fileName) {
		return fileName.replaceAll("\\.\\.", "");
	}

	/**
	 * Upload a VNF package by providing the address information of the VNF package.
	 *
	 * The POST method provides the information for the NFVO to get the content of a
	 * VNF package. This method shall follow the provisions specified in the Tables
	 * 9.4.6.3.1-1 and 9.4.6.3.1-2 for URI query parameters, request and response
	 * data structures, and response codes.
	 *
	 */
	@Override
	@POST
	@Path("/vnf_packages/{vnfPkgId}/package_content/upload_from_uri")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Upload a VNF package by providing the address information of the VNF package.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 202, message = "202 Accepted The information about the VNF package was received successfully, but the on-boarding has not been completed. It is expected to take some time for processing. The response body shall be empty. "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class),
			@ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class), @ApiResponse(code = 409, message = "Conflict. Error: The operation cannot be executed currently, due to a conflict with the state of the resource. Typically, this is due to the fact that the on boarding state of the VNF package resource is not CREATED . The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute shall convey more information about the error. ", response = ProblemDetails.class), @ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class),
			@ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public Response vnfPackagesVnfPkgIdPackageContentUploadFromUriPost(@HeaderParam("Accept") String accept, @HeaderParam("Content-Type") String contentType, @PathParam("vnfPkgId") String vnfPkgId, String body, @Context SecurityContext securityContext) {
		final VnfPkgInfo vnfPkgInfo = vnfPackageRepository.get(vnfPkgId);
		if (!"CREATED".equals(vnfPkgInfo.getOnboardingState())) {
			throw new ConflictException("Onboarding state is not correct.");
		}
		VnfPackagesVnfPkgIdPackageContentUploadFromUriPostRequest vnfPackagesVnfPkgIdPackageContentUploadFromUriPostRequest;
		try {
			vnfPackagesVnfPkgIdPackageContentUploadFromUriPostRequest = mapper.readValue(body, VnfPackagesVnfPkgIdPackageContentUploadFromUriPostRequest.class);
		} catch (final Exception e) {
			throw new GenericException(e);
		}
		final LinkedHashMap<String, String> uddList = (LinkedHashMap) vnfPackagesVnfPkgIdPackageContentUploadFromUriPostRequest.getUploadVnfPkgFromUriRequest().getUserDefinedData();
		final String uri = uddList.get("url");
		final InputStream content = getUrlContent(uri);
		try {
			unzip(vnfPkgId, content);
		} catch (final Exception e) {
			throw new GenericException(e);
		}

		return Response.status(202).build();
	}

	private InputStream getUrlContent(String uri) {
		URL url;
		try {
			url = new URL(uri);
			return (InputStream) url.getContent();
		} catch (final Exception e) {
			throw new GenericException(e);
		}
	}

	/**
	 * Update information about an individual VNF package.
	 *
	 * \&quot;The PATCH method updates the information of a VNF package.\&quot;
	 * \&quot;This method shall follow the provisions specified in the Tables
	 * 9.4.3.3.4-1 and 9.4.3.3.4-2 for URI query parameters, request and response
	 * data structures, and response codes.\&quot;
	 *
	 */
	@Override
	@PATCH
	@Path("/vnf_packages/{vnfPkgId}")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@ApiOperation(value = "Update information about an individual VNF package.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "200 OK The operation was completed successfully. The response body shall contain attribute modifications for an \"Individual VNF package\" resource ", response = Object.class), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class), @ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class),
			@ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class), @ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class),
			@ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class), @ApiResponse(code = 409, message = "Conflict. Error: The operation cannot be executed currently, due to a conflict with the state of the resource. Typically, this is due to any of the following scenarios: - Disable a VNF package resource of hich the operational state is not ENABLED - Enable a VNF package resource of which the operational state is not DISABLED The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute shall convey more information about the error. ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class),
			@ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class), @ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public Object vnfPackagesVnfPkgIdPatch(@PathParam("vnfPkgId") String vnfPkgId, String body, @HeaderParam("Content-Type") String contentType, @Context SecurityContext securityContext) {
		VnfPackagesVnfPkgIdPatchQuery vnfPackagesVnfPkgIdPatchQuery;
		try {
			vnfPackagesVnfPkgIdPatchQuery = mapper.readValue(body, VnfPackagesVnfPkgIdPatchQuery.class);
		} catch (final Exception e) {
			throw new GenericException(e);
		}
		final StringBuilder sb = new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/").append(vnfPkgId);
		try {
			if (!repositoryService.exists(sb.toString())) {
				throw new NotFoundException("No such object: " + vnfPkgId);
			}
		} catch (final ServiceException e) {
			throw new GenericException(e);
		}
		final VnfPkgInfo gud = new VnfPkgInfo();
		gud.setId(vnfPkgId);
		gud.setOnboardingState(OnboardingStateEnum.CREATED);

		final Object jsonString = vnfPackagesVnfPkgIdPatchQuery.getVnfPkgInfoModifications().getUserDefinedData();
		try {
			String uri = sb.toString();
			if (!repositoryService.exists(uri.toString())) {
				repositoryService.addDirectory(uri, "", "SOL005", "ncroot");
			}

			uri = sb.append("/").append("Metadata.yaml").toString();
			repositoryService.addFile(uri, "", "Added: SOL005", jsonString.toString(), "ncroot");
		} catch (final ServiceException e) {
			throw new GenericException(e);
		}

		gud.setUserDefinedData(jsonString);

		/*
		 * VnfPackagesVnfPkgInfoChecksum checksum = new VnfPackagesVnfPkgInfoChecksum();
		 * checksum.algorithm("SHA-256"); String hash =
		 * Hashing.sha256().hashString(jsonString, StandardCharsets.UTF_8).toString();
		 * checksum.setHash(hash); gud.setChecksum(checksum);
		 */

		final VnfPackagesVnfPkgIdGetResponse vnfPackagesVnfPkgIdGetResponse = new VnfPackagesVnfPkgIdGetResponse();
		vnfPackagesVnfPkgIdGetResponse.setVnfPkgInfo(gud);
		return vnfPackagesVnfPkgIdGetResponse;
	}

	/**
	 * Read VNFD of an on-boarded VNF package.
	 *
	 * The GET method reads the content of the VNFD within a VNF package. The VNFD
	 * can be implemented as a single file or as a collection of multiple files. If
	 * the VNFD is implemented in the form of multiple files, a ZIP file embedding
	 * these files shall be returned. If the VNFD is implemented as a single file,
	 * either that file or a ZIP file embedding that file shall be returned. The
	 * selection of the format is controlled by the \&quot;Accept\&quot; HTTP header
	 * passed in the GET request. • If the \&quot;Accept\&quot; header contains only
	 * \&quot;text/plain\&quot; and the VNFD is implemented as a single file, the
	 * file shall be returned; otherwise, an error message shall be returned. • If
	 * the \&quot;Accept\&quot; header contains only \&quot;application/zip\&quot;,
	 * the single file or the multiple files that make up the VNFD shall be returned
	 * embedded in a ZIP file. • If the \&quot;Accept\&quot; header contains both
	 * \&quot;text/plain\&quot; and \&quot;application/zip\&quot;, it is up to the
	 * NFVO to choose the format to return for a single-file VNFD; for a multi-file
	 * VNFD, a ZIP file shall be returned. The default format of the ZIP file shall
	 * be the one specified in ETSI GS NFV-SOL 004 [5] where only the YAML files
	 * representing the VNFD, and information necessary to navigate the ZIP file and
	 * to identify the file that is the entry point for parsing the VNFD (such as
	 * TOSCA-meta or manifest files or naming conventions) are included. This method
	 * shall follow the provisions specified in the Tables 9.4.4.3.2-1 and
	 * 9.4.4.3.2-2 for URI query parameters, request and response data structures,
	 * and response codes.
	 *
	 */
	@Override
	@GET
	@Path("/vnf_packages/{vnfPkgId}/vnfd")
	@Produces({ MediaType.TEXT_PLAIN, "application/zip" })
	@ApiOperation(value = "Read VNFD of an on-boarded VNF package.", tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "200 OK On success, the content of the VNFD is returned. The payload body shall contain a copy of the file representing the VNFD or a ZIP file that contains the file or multiple files representing the VNFD, as specified above. The \"Content-Type\" HTTP header shall be set according to the format of the returned file, i.e. to \"text/plain\" for a YAML file or to \"application/zip\" for a ZIP file. "), @ApiResponse(code = 400, message = "Bad Request. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute should convey more information about the error.        ", response = ProblemDetails.class),
			@ApiResponse(code = 401, message = "Unauthorized If the request contains no access token even though one is required, or if the request contains an authorization token that is invalid (e.g. expired or revoked), the API producer should respond with this response. The details of the error shall be returned in the WWW-Authenticate HTTP header, as defined in IETF RFC 6750 and IETF RFC 7235. The ProblemDetails structure may be provided. ", response = ProblemDetails.class), @ApiResponse(code = 403, message = "Forbidden If the API consumer is not allowed to perform a particular request to a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure shall be provided.  It should include in the \"detail\" attribute information about the source of the problem, and may indicate how to solve it. ", response = ProblemDetails.class),
			@ApiResponse(code = 404, message = "Not Found If the API producer did not find a current representation for the resource addressed by the URI passed in the request, or is not willing to disclose that one exists, it shall respond with this response code.  The \"ProblemDetails\" structure may be provided, including in the \"detail\" attribute information about the source of the problem, e.g. a wrong resource URI variable. ", response = ProblemDetails.class), @ApiResponse(code = 405, message = "Method Not Allowed If a particular HTTP method is not supported for a particular resource, the API producer shall respond with this response code. The \"ProblemDetails\" structure may be omitted in that case. ", response = ProblemDetails.class), @ApiResponse(code = 406, message = "If the \"Accept\" header does not contain at least one name of a content type for which the NFVO can provide a representation of the VNFD, the NFVO shall respond with this response code.         ", response = ProblemDetails.class),
			@ApiResponse(code = 409, message = "Error: The operation cannot be executed currently, due to a conflict with the state of the resource. Typically, this is due to the fact that \"onboardingState\" of the VNF package has a value different from \"ONBOARDED\". The response body shall contain a ProblemDetails structure, in which the \"detail\" attribute shall convey more information about the error.         ", response = ProblemDetails.class), @ApiResponse(code = 416, message = "Requested Range Not Satisfiable The byte range passed in the \"Range\" header did not match any available byte range in the VNF package file (e.g. \"access after end of file\"). The response body may contain a ProblemDetails structure. ", response = ProblemDetails.class),
			@ApiResponse(code = 500, message = "Internal Server Error If there is an application error not related to the client's input that cannot be easily mapped to any other HTTP response code (\"catch all error\"), the API producer shall respond withthis response code. The ProblemDetails structure shall be provided, and shall include in the \"detail\" attribute more information about the source of the problem. ", response = ProblemDetails.class), @ApiResponse(code = 503, message = "Service Unavailable If the API producer encounters an internal overload situation of itself or of a system it relies on, it should respond with this response code, following the provisions in IETF RFC 7231 [13] for the use of the Retry-After HTTP header and for the alternative to refuse the connection. The \"ProblemDetails\" structure may be omitted. ", response = ProblemDetails.class) })
	public Response vnfPackagesVnfPkgIdVnfdGet(@PathParam("vnfPkgId") String vnfPkgId, @HeaderParam("Accept") String accept, @Context SecurityContext securityContext) throws ServiceException {

		LOGGER.info(new StringBuilder("Method called: ").append(new Object() {
		}.getClass().getEnclosingMethod().getName()).toString());

		final List<String> listvnfPckgFiles = new LinkedList();

		getVnfPkgIndividualInfoOrCheckOnboardingStatus(vnfPkgId, true);

		// TODO - Implement VNFD multi-files support
		final String uri = new StringBuilder().append(REPOSITORY_NVFO_DATAFILE_BASE_PATH).append("/").append(vnfPkgId).append("/").append("vnfd.json").toString();
		listvnfPckgFiles.add(uri);

		boolean isVnfd;

		isVnfd = repositoryService.exists(uri);

		if (isVnfd) {
			if (MediaType.TEXT_PLAIN.equals(accept)) {
				final RepositoryElement repositoryElement = repositoryService.getElement(uri);
				final String content = new String(repositoryService.getRepositoryElementContent(repositoryElement));
				return Response.ok(JSONObject.fromObject(content)).build();
			} else if ("application/zip".equals(accept)
					|| ("application/zip".equals(accept) && MediaType.TEXT_PLAIN.equals(accept))) {
				return getZipArchive(null, listvnfPckgFiles);
			}
		}
		throw new NotFoundException("VNFD not found for vnfPkg with id: " + vnfPkgId);
	}

	/**
	 * Get the list of VNF Packages Information corresponding IDs.
	 *
	 * @return <b>vnfPackageIdList</b> VNF Packages details IDs list.
	 * @throws ServiceException
	 */
	private List<String> getVnfPkgIdsFromRepository() throws ServiceException {

		// List vnfd package from repository
		final List<String> listFilesInFolder = repositoryService.doSearch(REPOSITORY_NVFO_DATAFILE_BASE_PATH, "");
		final List<String> vnfPackageIdList = new ArrayList();
		final JSONArray jsonArray = new JSONArray();
		final JSONObject jsonObject = new JSONObject();

		// Split files path and store VNF Pckg Id
		for (final String filePath : listFilesInFolder) {
			final String[] splitArray = filePath.split("/", -1);
			final String retrievedVnfPckId = splitArray[3];
			vnfPackageIdList.add(retrievedVnfPckId);
		}

		final Set<String> set = new HashSet(vnfPackageIdList);
		vnfPackageIdList.clear();
		vnfPackageIdList.addAll(set);

		return vnfPackageIdList;
	}

	private JSONArray applyAttributebasedFilteringAndSelectors(UriInfo uriInfo, JSONArray vnfPkgInfos, JSONObject contentJson) {
		// Get the dynamic paramaters attribute / value(s)
		MultivaluedMap<String, String> queryParams;
		queryParams = uriInfo.getQueryParameters();
		String attributesParams = "";
		List<String> attributesValuesParams = new LinkedList();

		// Get the filter parameter from the query params object.
		for (final Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
			attributesParams = entry.getKey();
			attributesValuesParams = entry.getValue();
		}

		// List of the fields exclude by default from the VNF Package Info
		final ArrayList<String> listOfDefaultExcludeFields = new ArrayList();
		listOfDefaultExcludeFields.add("softwareImages");
		listOfDefaultExcludeFields.add("additionalArtifacts");

		// Filtering operation
		if ("all_fields".equals(attributesParams)) {
			vnfPkgInfos.add(contentJson);
		} else if ("fields".equals(attributesParams)) {
			keepFieldsInVnfPckgInfo(contentJson, attributesValuesParams, listOfDefaultExcludeFields);
			vnfPkgInfos.add(contentJson);
		} else if ("exclude_fields".equals(attributesParams) || "exclude_default".equals(attributesParams)) {
			removeFields(contentJson, listOfDefaultExcludeFields);
			vnfPkgInfos.add(contentJson);
		} else {
			final boolean isFilterMatched = isFilterMatched(attributesParams, attributesValuesParams, contentJson);
			if (isFilterMatched) {
				removeFields(contentJson, listOfDefaultExcludeFields);
				vnfPkgInfos.add(contentJson);
			}
		}
		return vnfPkgInfos;
	}

	/**
	 * Method allows to archive VNF Package contents and artifacts.
	 *
	 * @param range
	 * @param listvnfPckgFiles
	 * @return
	 *
	 */
	private Response getZipArchive(String range, List<String> listvnfPckgFiles) {

		final ZipFileHandler zip = new ZipFileHandler(repositoryService, listvnfPckgFiles);
		ByteArrayOutputStream bos;

		try {
			if (range == null) {
				bos = zip.getZipFile();
				final Response.ResponseBuilder response = Response.status(Response.Status.OK).type("application/zip").entity(new ByteArrayInputStream(bos.toByteArray()));
				return response.build();

			} else {
				final RangeHeader rangeHeader = RangeHeader.fromValue(range);
				bos = zip.getByteRangeZipFile((int) rangeHeader.getFrom(), (int) rangeHeader.getTo());
				final String contentRange = new StringBuilder().append("bytes").append(rangeHeader.getFrom()).append("-")
						.append(rangeHeader.getTo()).append("/").append(zip.zipFileByteArrayLength()).toString();
				final Response.ResponseBuilder response = Response.status(Response.Status.PARTIAL_CONTENT).type("application/zip").entity(new ByteArrayInputStream(bos.toByteArray())).header("Content-Range", contentRange);
				return response.build();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private VnfPkgInfo getVnfPkgIndividualInfoOrCheckOnboardingStatus(String vnfPkgId, boolean isCheckOnbordingStatus) {
		final VnfPkgInfo vnfPkgInfo = vnfPackageRepository.get(vnfPkgId);

		if (isCheckOnbordingStatus) {
			final String onboardingState = vnfPkgInfo.getOnboardingState();
			if (OnboardingStateEnum.PROCESSING.toString().equalsIgnoreCase(onboardingState)
					|| OnboardingStateEnum.UPLOADING.toString().equalsIgnoreCase(onboardingState.toUpperCase())) {
				throw new ConflictException("Conflict with the current VNF Package onbording state: " + onboardingState.toUpperCase());
			}
		} else {
			return vnfPkgInfo;
		}
		return vnfPkgInfo;
	}

	/**
	 * Private Method allows to search in VNFPackInfos the matched values based-on
	 * filter input.
	 *
	 * @param attributesParams
	 * @param attributesValuesParams
	 * @param contentJson
	 * @return TRUE if matched and FALSE in opposite.
	 */
	private boolean isFilterMatched(String attributesParams, List<String> attributesValuesParams, JSONObject contentJson) {
		String filterOperator = "";

		if (!("".equals(attributesParams) && attributesValuesParams.isEmpty())) {

			final ArrayList<String> listOfAttribute = splitStringObj("\\.", attributesParams);

			final String attributeValues = attributesValuesParams.get(0);
			final ArrayList<String> listOfExpectedValues = splitStringObj(",", attributeValues);

			// Retrieve the filter operator from filter attribute.
			filterOperator = getOperatorFromList(listOfAttribute);

			// Extract input attribute matched value(s).
			final ArrayList<String> attrMatchedValues = extractAttrMatchedValues(listOfAttribute, contentJson);
			// Apply the filter and return matched VNFPackage info(s)
			return compareValuesBasedOnFilterOperator(listOfExpectedValues, attrMatchedValues, filterOperator);
		}
		return true;
	}

	/**
	 * Private method allows to extract from VFND Infos (Json object) the attribute
	 * value(s).
	 *
	 * @param listOfAttrName  Attribute
	 * @param vnfPackInfoJson VNFD infos
	 * @return List of the value(s)
	 */
	private ArrayList<String> extractAttrMatchedValues(ArrayList<String> listOfAttrName, JSONObject vnfPackInfoJson) throws BadRequestException {
		final ArrayList<String> matchedValues = new ArrayList();
		JSONObject vnfPackInfo = vnfPackInfoJson;

		int index = 0;
		if (isthereAnOperatorFilterInList(listOfAttrName)) {
			index = 1;
		}

		// TODO - Extract object from Map Entry loop

		for (final String key : listOfAttrName) {
			final Object object = vnfPackInfo.get(key);
			if (object == null) {
				throw new com.ubiqube.api.rs.exception.etsi.BadRequestException("Wrong filter name: " + key);
			}

			if (((object instanceof JSONArray) || (object instanceof JSONObject)) && (index < (listOfAttrName.size() - index))) {
				if (object instanceof JSONArray) {
					for (int i = 0; i < ((JSONArray) object).size(); i++) {
						final String value = (String) ((JSONArray) object).get(i);
						if (value instanceof String) {
							matchedValues.add(value);
						}
					}
				} else {
					vnfPackInfo = (JSONObject) object;
				}
			} else if (((object instanceof String) || (object instanceof Integer) || (object instanceof Boolean) || (object instanceof Character))
					&& (index < (listOfAttrName.size() - index))) {
				matchedValues.add(String.valueOf(object));
			}
		}
		return matchedValues;
	}

	/**
	 * private method allows to filter the inputs attributes matched values based-on
	 * filtering operator.
	 *
	 * @param listOfExpectedValues
	 * @param attrMatchedValues
	 * @param filterOperator
	 * @return boolean True if the operation result is true else false.
	 */
	private boolean compareValuesBasedOnFilterOperator(List<String> listOfExpectedValues, ArrayList<String> attrMatchedValues, String filterOperator) {
		for (final String value : attrMatchedValues) {
			for (final String expectedValue : listOfExpectedValues) {
				// Attribute equal to one of the expect values in the list.
				if ("".equals(filterOperator) || "eq".equals(filterOperator)) {
					if (expectedValue.equals(value)) {
						return true;
					}
				}
				// Attribute not equal to any of the values in the list
				if ("neq".equals(filterOperator)) {
					if (!expectedValue.equals(value)) {
						return true;
					}
				}
				// Attribute greater than <value>

				// TODO - Implementation for the other operators.
			}
		}
		return false;
	}

	/**
	 * Private method allows to slip a String object.
	 *
	 * @param regex
	 * @param object
	 * @return List of the split values
	 */
	private ArrayList<String> splitStringObj(String regex, String object) {
		final ArrayList<String> list = new ArrayList();
		list.addAll(Arrays.asList(object.split(regex)));

		return list;
	}

	/**
	 * Private method allows to check if a filtering operator in the list of
	 * attributes name.
	 *
	 * @param listOfAttrName
	 * @return If there is an filtering operator in the list return TRUE alse FALSE.
	 */
	private boolean isthereAnOperatorFilterInList(ArrayList<String> listOfAttrName) {
		// Check the last attribute name if it an filtering operator or not.
		final String lastAttribute = listOfAttrName.get(listOfAttrName.size() - 1);

		if (!"".equals(getOperatorFromList(listOfAttrName))) {
			return true;
		}
		return false;
	}

	private String getOperatorFromList(ArrayList<String> listOfAttrName) {
		for (final String attribute : listOfAttrName) {
			if ("eq".equals(attribute) || "neq".equals(attribute) || "gt".equals(attribute)
					|| "lt".equals(attribute) || "gte".equals(attribute) || "lte".equals(attribute) || "cont".equals(attribute) || "ncont".equals(attribute)) {
				return attribute;
			}
		}
		return "";
	}

	private void keepFieldsInVnfPckgInfo(JSONObject contentJson, List<String> attributesValuesParams, ArrayList<String> listOfDefaultExcludeFields) {
		final String attributeValues = attributesValuesParams.get(0);
		final ArrayList<String> fieldsFromInput = splitStringObj(",", attributeValues);
		final ArrayList<String> fieldsToRemove = listOfDefaultExcludeFields;

		// Compare the both lists contents and remove them from the "excluded fields by
		// default" the matched values.
		fieldsToRemove.removeAll(fieldsFromInput);

		// Remove the exclude fields not matched to the inputs fields list.
		for (final String field : fieldsToRemove) {
			removeField(contentJson, field);
		}

	}

	private void removeField(JSONObject contentJson, String field) {
		if (contentJson.containsKey(field)) {
			contentJson.remove(field);
		}

	}

	private void removeFields(JSONObject contentJson, ArrayList<String> fields) {
		for (final String field : fields) {
			removeField(contentJson, field);
		}
	}
}
