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
package com.ubiqube.etsi.mano.vnfm.service.plan.contributors.v2.uow;

import java.util.UUID;

import com.ubiqube.etsi.mano.dao.mano.k8s.K8sServers;
import com.ubiqube.etsi.mano.dao.mano.v2.vnfm.MciopTask;
import com.ubiqube.etsi.mano.orchestrator.Context;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.HelmNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.OsContainerNode;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTask;
import com.ubiqube.etsi.mano.service.vim.VimException;
import com.ubiqube.etsi.mano.service.vim.k8s.K8sClient;
import com.ubiqube.etsi.mano.vnfm.jpa.K8sServerInfoJpa;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class HelmDeployUow extends AbstractUowV2<MciopTask> {
	private final K8sClient client;
	private final MciopTask task;
	private final K8sServerInfoJpa serverInfoJpa;
	private final String clientKey;

	public HelmDeployUow(final VirtualTask<MciopTask> task, final K8sClient client, final K8sServerInfoJpa serverInfoJpa, final String clientKey) {
		super(task, HelmNode.class);
		this.task = task.getParameters();
		this.client = client;
		this.serverInfoJpa = serverInfoJpa;
		this.clientKey = clientKey;
	}

	@Override
	public String execute(final Context context) {
		final String parent = context.get(OsContainerNode.class, task.getParentVdu());
		final K8sServers server = serverInfoJpa.findById(UUID.fromString(parent)).orElseThrow(() -> new VimException("Could not find OS container: " + parent));
		return client.deploy(server, clientKey, task.getMciop().getImage().getUrl());
	}

	@Override
	public String rollback(final Context context) {
		client.undeploy(task.getVimResourceId());
		return null;
	}

}
