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
package com.ubiqube.etsi.mano.orchestrator;

import java.util.List;

import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 * @param <U>
 * @param <T>
 * @param <P>
 * @deprecated Contributor role as this one defined, doesn't exist any more.
 */
@Deprecated(forRemoval = true)
public interface PlanContributor<U, T extends VirtualTaskV3<U>, P> {

	List<T> contribute(Bundle bundle, P parameters);

	Class<? extends Node> getNode();

}
