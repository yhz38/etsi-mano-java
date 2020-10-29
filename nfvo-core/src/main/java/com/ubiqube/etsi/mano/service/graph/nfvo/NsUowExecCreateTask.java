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
package com.ubiqube.etsi.mano.service.graph.nfvo;

import java.util.Map;

import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.jpa.NsInstantiatedBaseJpa;
import com.ubiqube.etsi.mano.service.VnfmInterface;
import com.ubiqube.etsi.mano.service.vim.Vim;

public class NsUowExecCreateTask extends AbstractNsTaskUow {
	/** Serial. */
	private static final long serialVersionUID = 1L;

	public NsUowExecCreateTask(final VimConnectionInformation vimConnectionInformation, final Vim vim, final NsUnitOfWork uaow, final NsInstantiatedBaseJpa _resourceHandleEntityJpa, final Map<String, String> _context, final VnfmInterface vnfm) {
		super(vimConnectionInformation, vim, uaow, _resourceHandleEntityJpa, _context, true, vnfm);
	}

}
