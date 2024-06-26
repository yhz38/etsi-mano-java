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
package com.ubiqube.etsi.mano.nfvo.v261.controller.vnflcm;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ubiqube.etsi.mano.controller.vnflcm.VnfLcmNotificationFrontController;
import com.ubiqube.etsi.mano.vnfm.v261.model.vnflcm.VnfIdentifierDeletionNotification;

/**
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
@RestController
public class VnfIdentifierDeletionNotification261Sol003Controller implements VnfIdentifierDeletionNotification261Sol003Api {
	private final VnfLcmNotificationFrontController fc;

	public VnfIdentifierDeletionNotification261Sol003Controller(final VnfLcmNotificationFrontController fc) {
		this.fc = fc;
	}

	@Override
	public ResponseEntity<Void> vnfIdentifierDeletionNotificationGet() {
		return fc.deletionCheck();
	}

	@Override
	public ResponseEntity<Void> vnfIdentifierDeletionNotificationPost(@Valid final VnfIdentifierDeletionNotification body) {
		return fc.deletionNotification(body, "2.6.1");
	}

}
