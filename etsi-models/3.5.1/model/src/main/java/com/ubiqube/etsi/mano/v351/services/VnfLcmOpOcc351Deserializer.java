/**
 *     Copyright (C) 2019-2023 Ubiqube.
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
package com.ubiqube.etsi.mano.v351.services;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ubiqube.etsi.mano.em.v351.model.vnfconfig.ProblemDetails;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.AffectedVipCp;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.CancelModeType;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.ChangeCurrentVnfPkgRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.ChangeExtVnfConnectivityRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.ChangeVnfFlavourRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.CreateVnfSnapshotRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.ExtVirtualLinkInfo;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.HealVnfRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.InstantiateVnfRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.LcmOperationStateType;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.LcmOperationType;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.ModificationsTriggeredByVnfPkgChange;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.OperateVnfRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.RevertToVnfSnapshotRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.ScaleVnfRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.ScaleVnfToLevelRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.TerminateVnfRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.VnfInfoModificationRequest;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.VnfInfoModifications;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.VnfLcmOpOcc;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.VnfLcmOpOccLcmCoordinations;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.VnfLcmOpOccLinks;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.VnfLcmOpOccRejectedLcmCoordinations;
import com.ubiqube.etsi.mano.em.v351.model.vnflcm.VnfLcmOpOccResourceChanges;
import com.ubiqube.etsi.mano.exception.GenericException;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class VnfLcmOpOcc351Deserializer extends StdDeserializer<VnfLcmOpOcc> {

	/** Serial. */
	private static final long serialVersionUID = 1L;

	public VnfLcmOpOcc351Deserializer() {
		super(VnfLcmOpOcc.class);
	}

	@Override
	public VnfLcmOpOcc deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
		final VnfLcmOpOcc ret = new VnfLcmOpOcc();
		while (JsonToken.END_OBJECT != p.nextToken()) {
			switch (p.currentName()) {
			case "id":
				ret.setId(p.nextTextValue());
				break;
			case "operationState":
				ret.setOperationState(LcmOperationStateType.fromValue(p.nextTextValue()));
				break;
			case "stateEnteredTime":
				p.nextToken();
				ret.setStateEnteredTime(p.getCodec().readValue(p, OffsetDateTime.class));
				break;
			case "startTime":
				ret.setStartTime(getNextObject(p, OffsetDateTime.class));
				break;
			case "vnfInstanceId":
				ret.setVnfInstanceId(p.nextTextValue());
				break;
			case "grantId":
				ret.setGrantId(p.nextTextValue());
				break;
			case "operation":
				ret.setOperation(LcmOperationType.fromValue(p.nextTextValue()));
				break;
			case "isAutomaticInvocation":
				ret.setIsAutomaticInvocation(p.nextBooleanValue());
				break;
			case "operationParams":
				ret.setOperationParams(mapOperation(ret.getOperation(), p));
				break;
			case "isCancelPending":
				ret.setIsCancelPending(p.nextBooleanValue());
				break;
			case "cancelMode":
				ret.setCancelMode(CancelModeType.fromValue(p.nextTextValue()));
				break;
			case "error":
				ret.setError(getNextObject(p, ProblemDetails.class));
				break;
			case "resourceChanges":
				ret.setResourceChanges(getNextObject(p, VnfLcmOpOccResourceChanges.class));
				break;
			case "changedInfo":
				ret.setChangedInfo(getNextObject(p, VnfInfoModifications.class));
				break;
			case "affectedVipCps":
				ret.setAffectedVipCps(getNextObject(p, new TypeReference<List<AffectedVipCp>>() {
					// Nothing.
				}));
				break;
			case "changedExtConnectivity":
				ret.setChangedExtConnectivity(getNextObject(p, new TypeReference<List<ExtVirtualLinkInfo>>() {
					// Nothing.
				}));
				break;
			case "modificationsTriggeredByVnfPkgChange":
				ret.setModificationsTriggeredByVnfPkgChange(getNextObject(p, ModificationsTriggeredByVnfPkgChange.class));
				break;
			case "vnfSnapshotInfoId":
				ret.setVnfSnapshotInfoId(p.nextTextValue());
				break;
			case "lcmCoordinations":
				ret.setLcmCoordinations(getNextObject(p, new TypeReference<List<VnfLcmOpOccLcmCoordinations>>() {
					// Nothing.
				}));
				break;
			case "rejectedLcmCoordinations":
				ret.setRejectedLcmCoordinations(getNextObject(p, new TypeReference<List<VnfLcmOpOccRejectedLcmCoordinations>>() {
					// Nothing.
				}));
				break;
			case "warnings":
				ret.setWarnings(getNextObject(p, new TypeReference<List<String>>() {
					// Nothing.
				}));
				break;
			case "_links":
				ret.setLinks(getNextObject(p, VnfLcmOpOccLinks.class));
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + p.currentName());
			}
		}
		return ret;
	}

	private static Object mapOperation(final LcmOperationType op, final JsonParser p) throws IOException {
		p.nextToken();
		return switch (op) {
		case INSTANTIATE -> map(p, InstantiateVnfRequest.class);
		case SCALE -> map(p, ScaleVnfRequest.class);
		case SCALE_TO_LEVEL -> map(p, ScaleVnfToLevelRequest.class);
		case CHANGE_FLAVOUR -> map(p, ChangeVnfFlavourRequest.class);
		case TERMINATE -> map(p, TerminateVnfRequest.class);
		case HEAL -> map(p, HealVnfRequest.class);
		case OPERATE -> map(p, OperateVnfRequest.class);
		case CHANGE_EXT_CONN -> map(p, ChangeExtVnfConnectivityRequest.class);
		case MODIFY_INFO -> map(p, VnfInfoModificationRequest.class);
		case CREATE_SNAPSHOT -> map(p, CreateVnfSnapshotRequest.class);
		case REVERT_TO_SNAPSHOT -> map(p, RevertToVnfSnapshotRequest.class);
		case CHANGE_VNFPKG -> map(p, ChangeCurrentVnfPkgRequest.class);
		default -> throw new IllegalArgumentException("Unexpected value: " + op);
		};
	}

	private static <U> U getNextObject(final JsonParser p, final Class<U> clazz) throws IOException {
		p.nextToken();
		return p.getCodec().readValue(p, clazz);
	}

	private static <U> U map(final JsonParser p, final Class<U> clazz) {
		try {
			return p.getCodec().readValue(p, clazz);
		} catch (final IOException e) {
			throw new GenericException(e);
		}
	}

	private static <U> U getNextObject(final JsonParser p, final TypeReference<U> typeReference) throws IOException {
		p.nextToken();
		return p.getCodec().readValue(p, typeReference);
	}

}
