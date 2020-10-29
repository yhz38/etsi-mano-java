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
package com.ubiqube.etsi.mano.service;

/**
 * Simple abstraction for a very complex task => Patching.
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public interface Patcher {
	/**
	 * Apply a JSON document to an entity.
	 * 
	 * @param _patchDocument A JSON document as a string.
	 * @param _entity        An object.
	 */
	void patch(String _patchDocument, Object _entity);
}
