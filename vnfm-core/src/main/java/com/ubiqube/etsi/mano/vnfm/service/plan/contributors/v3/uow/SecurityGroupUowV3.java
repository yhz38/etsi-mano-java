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
package com.ubiqube.etsi.mano.vnfm.service.plan.contributors.v3.uow;

import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.v2.vnfm.SecurityGroupTask;
import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.SecurityGroupNode;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.vim.Vim;

public class SecurityGroupUowV3 extends AbstractVnfmUowV3<SecurityGroupTask> {
	private final Vim vim;
	private final VimConnectionInformation vimConnectionInformation;
	private final SecurityGroupTask task;

	public SecurityGroupUowV3(final VirtualTaskV3<SecurityGroupTask> task, final Vim vim, final VimConnectionInformation vimConnectionInformation) {
		super(task, SecurityGroupNode.class);
		this.task = task.getTemplateParameters();
		this.vim = vim;
		this.vimConnectionInformation = vimConnectionInformation;
	}

	@Override
	public String execute(final Context3d context) {
		return vim.network(vimConnectionInformation).createSecurityGroup(task.getAlias());
	}

	@Override
	public String rollback(final Context3d context) {
		vim.network(vimConnectionInformation).deleteSecurityGroup(task.getVimResourceId());
		return null;
	}

}