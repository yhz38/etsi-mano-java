/**
 *     Copyright (C) 2019-2024 Ubiqube.
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RequestRangeNotSatisfiableException extends ResponseStatusException {

	/** Serial. */
	private static final long serialVersionUID = 1L;

	public RequestRangeNotSatisfiableException(final String detail) {
		super(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, detail);
	}

	public RequestRangeNotSatisfiableException(final String message, final IllegalArgumentException e) {
		super(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, message, e);
	}
}
