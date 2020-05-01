package com.ubiqube.etsi.mano.controller.nslcm;

import static com.ubiqube.etsi.mano.Constants.ensureInstantiated;
import static com.ubiqube.etsi.mano.Constants.ensureIsEnabled;
import static com.ubiqube.etsi.mano.Constants.ensureIsOnboarded;
import static com.ubiqube.etsi.mano.Constants.ensureNotInstantiated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.VnfInstance;
import com.ubiqube.etsi.mano.dao.mano.VnfPackage;
import com.ubiqube.etsi.mano.factory.LcmFactory;
import com.ubiqube.etsi.mano.model.nslcm.NsLcmOpType;
import com.ubiqube.etsi.mano.model.nslcm.VnfVirtualLinkResourceInfo;
import com.ubiqube.etsi.mano.model.nslcm.VnfcResourceInfo;
import com.ubiqube.etsi.mano.model.nslcm.sol003.CreateVnfRequest;
import com.ubiqube.etsi.mano.model.nslcm.sol003.InstantiateVnfRequest;
import com.ubiqube.etsi.mano.model.nslcm.sol003.TerminateVnfRequest;
import com.ubiqube.etsi.mano.model.nslcm.sol003.TerminateVnfRequest.TerminationTypeEnum;
import com.ubiqube.etsi.mano.model.nslcm.sol005.NsLcmOpOcc;
import com.ubiqube.etsi.mano.model.vnf.PackageUsageStateType;
import com.ubiqube.etsi.mano.repository.NsLcmOpOccsRepository;
import com.ubiqube.etsi.mano.repository.VnfInstancesRepository;
import com.ubiqube.etsi.mano.repository.VnfPackageRepository;
import com.ubiqube.etsi.mano.service.event.ActionType;
import com.ubiqube.etsi.mano.service.event.EventManager;
import com.ubiqube.etsi.mano.service.event.NotificationEvent;

import ma.glasnost.orika.MapperFacade;

/**
 * NFVO+VNFM & VNFM Implementation. TODO: Make terminate Async and this will be
 * generic again.
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
//@Profile({ "!NFVO" })
@Service
public class VnfInstanceLcm {

	private static final Logger LOG = LoggerFactory.getLogger(VnfInstanceLcm.class);

	private final VnfInstancesRepository vnfInstancesRepository;
	private final VnfPackageRepository vnfPackageRepository;
	private final NsLcmOpOccsRepository lcmOpOccsMsa;
	private final EventManager eventManager;
	private final MapperFacade mapper;

	public VnfInstanceLcm(final VnfInstancesRepository vnfInstancesRepository, final VnfPackageRepository vnfPackageRepository, final NsLcmOpOccsRepository _lcmOpOccsRepository, final EventManager _eventManager, final MapperFacade _mapper) {
		super();
		this.vnfInstancesRepository = vnfInstancesRepository;
		this.vnfPackageRepository = vnfPackageRepository;
		lcmOpOccsMsa = _lcmOpOccsRepository;
		eventManager = _eventManager;
		mapper = _mapper;
	}

	public List<com.ubiqube.etsi.mano.model.nslcm.VnfInstance> get(final Map<String, String> queryParameters, final LcmLinkable links) {
		final String filter = queryParameters.get("filter");
		final List<VnfInstance> result = vnfInstancesRepository.query(filter);
		return result.stream()
				.map(x -> {
					final com.ubiqube.etsi.mano.model.nslcm.VnfInstance v = mapper.map(x, com.ubiqube.etsi.mano.model.nslcm.VnfInstance.class);
					v.setLinks(links.getLinks(x.getId().toString()));
					return v;
				})
				.collect(Collectors.toList());
	}

	public com.ubiqube.etsi.mano.model.nslcm.VnfInstance post(final CreateVnfRequest createVnfRequest) {
		final String vnfId = createVnfRequest.getVnfdId();
		final VnfPackage vnfPkgInfo = vnfPackageRepository.get(UUID.fromString(vnfId));
		ensureIsOnboarded(vnfPkgInfo);
		ensureIsEnabled(vnfPkgInfo);
		VnfInstance vnfInstance = LcmFactory.createVnfInstance(createVnfRequest, vnfPkgInfo);
		vnfPkgInfo.getVnfCompute().forEach(x -> {
			final VnfcResourceInfo vnfcResourceInfoItem = new VnfcResourceInfo();
			vnfcResourceInfoItem.setVduId(x.getId().toString());
		});
		vnfPkgInfo.getVnfVl().forEach(x -> {
			final VnfVirtualLinkResourceInfo virtualLinkResourceInfoItem = new VnfVirtualLinkResourceInfo();
		});
		// VnfIdentifierCreationNotification NFVO + EM
		vnfInstance = vnfInstancesRepository.save(vnfInstance);
		eventManager.sendNotification(NotificationEvent.VNF_INSTANCE_CREATE, vnfInstance.getId().toString());
		return mapper.map(vnfInstance, com.ubiqube.etsi.mano.model.nslcm.VnfInstance.class);
	}

	public void delete(@Nonnull final String vnfInstanceId) {
		final VnfInstance vnfInstance = vnfInstancesRepository.get(UUID.fromString(vnfInstanceId));
		ensureNotInstantiated(vnfInstance);

		if (vnfInstancesRepository.isInstantiate(vnfInstance.getVnfPkg().getId().toString())) {
			final VnfPackage vnfPkg = vnfPackageRepository.get(vnfInstance.getVnfPkg().getId());
			vnfPkg.setUsageState(PackageUsageStateType.NOT_IN_USE);
			vnfPackageRepository.save(vnfPkg);
		}
		vnfInstancesRepository.delete(UUID.fromString(vnfInstanceId));
		// VnfIdentitifierDeletionNotification NFVO + EM
	}

	public void instantiate(@Nonnull final String vnfInstanceId, final InstantiateVnfRequest instantiateVnfRequest, @Nonnull final LcmLinkable links) {
		final VnfInstance vnfInstance = vnfInstancesRepository.get(UUID.fromString(vnfInstanceId));
		ensureNotInstantiated(vnfInstance);

		final UUID vnfPkgId = vnfInstance.getVnfPkg().getId();
		final VnfPackage vnfPkg = vnfPackageRepository.get(vnfPkgId);
		ensureIsEnabled(vnfPkg);
		eventManager.sendAction(ActionType.VNF_INSTANTIATE, vnfInstanceId, new HashMap<String, Object>());
		LOG.info("Instantiation Event Sucessfully sent.");
	}

	public void terminate(@Nonnull final String vnfInstanceId, final TerminateVnfRequest terminateVnfRequest) {
		// TODO: A little bit wrong , move this to async.
		if (terminateVnfRequest.getTerminationType() != TerminationTypeEnum.FORCEFUL) {
			LOG.warn("Terminaison should be set to FORCEFULL.");
		}
		final VnfInstance vnfInstance = vnfInstancesRepository.get(UUID.fromString(vnfInstanceId));
		ensureInstantiated(vnfInstance);
		eventManager.sendAction(ActionType.VNF_TERMINATE, vnfInstanceId, new HashMap<String, Object>());

		LOG.info("Terminate sent for instancce: {}", vnfInstanceId);
	}

	private NsLcmOpOcc addVnfOperation(final String _processId, final String _vnfInstanceId, final NsLcmOpType _lcmOperationType) {
		final NsLcmOpOcc lcmOpOccs = LcmFactory.createNsLcmOpOcc(_vnfInstanceId, _lcmOperationType);
		lcmOpOccsMsa.save(lcmOpOccs);
		lcmOpOccsMsa.attachProcessIdToLcmOpOccs(lcmOpOccs.getId(), _processId);
		return lcmOpOccs;
	}

}
