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
package com.ubiqube.etsi.mano.vnfm.service.plan.contributors.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.ChangeType;
import com.ubiqube.etsi.mano.dao.mano.ResourceTypeEnum;
import com.ubiqube.etsi.mano.dao.mano.VnfInstance;
import com.ubiqube.etsi.mano.dao.mano.VnfLinkPort;
import com.ubiqube.etsi.mano.dao.mano.VnfLiveInstance;
import com.ubiqube.etsi.mano.dao.mano.VnfPackage;
import com.ubiqube.etsi.mano.dao.mano.v2.ComputeTask;
import com.ubiqube.etsi.mano.dao.mano.v2.PlanOperationType;
import com.ubiqube.etsi.mano.dao.mano.v2.VnfBlueprint;
import com.ubiqube.etsi.mano.dao.mano.v2.VnfPortTask;
import com.ubiqube.etsi.mano.dao.mano.v2.VnfTask;
import com.ubiqube.etsi.mano.exception.GenericException;
import com.ubiqube.etsi.mano.orchestrator.Bundle;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.VnfPortNode;
import com.ubiqube.etsi.mano.vnfm.jpa.VnfLiveInstanceJpa;
import com.ubiqube.etsi.mano.vnfm.service.graph.VnfBundleAdapter;
import com.ubiqube.etsi.mano.vnfm.service.plan.contributors.v2.vt.VnfPortVt;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
@Service
public class VnfPortContributor extends AbstractContributorV2Base<VnfPortTask, VnfPortVt> {
	private final VnfLiveInstanceJpa vnfLiveInstanceJpa;

	public VnfPortContributor(final VnfLiveInstanceJpa vnfLiveInstanceJpa) {
		super();
		this.vnfLiveInstanceJpa = vnfLiveInstanceJpa;
	}

	@Override
	public List<VnfPortVt> vnfContribute(final Bundle bundle, final VnfBlueprint plan) {
		if (plan.getOperation() == PlanOperationType.TERMINATE) {
			return doTerminatePlan(plan.getVnfInstance());
		}
		final Set<VnfTask> tasks = plan.getTasks();
		final VnfPackage vnfPackage = ((VnfBundleAdapter) bundle).vnfPackage();
		final List<VnfPortVt> ret = new ArrayList<>();
		tasks.stream()
				.filter(ComputeTask.class::isInstance)
				.map(ComputeTask.class::cast)
				.forEach(x -> {
					if (x.getChangeType() == ChangeType.REMOVED) {
						removePort(ret, x, vnfPackage, plan.getInstance());
					} else {
						createPort(ret, x, vnfPackage);
					}
				});
		return ret;
	}

	private void removePort(final List<VnfPortVt> ret, final ComputeTask compute, final VnfPackage vnfPackage, final VnfInstance instance) {
		final List<VnfLinkPort> l = findPorts(compute.getToscaName(), vnfPackage.getVnfLinkPort());
		l.stream().forEach(x -> {
			final String toscaName = x.getToscaName() + "-" + compute.getAlias();
			final List<VnfLiveInstance> vli = vnfLiveInstanceJpa.findByTaskVnfInstanceAndToscaName(instance, toscaName);
			if (vli.isEmpty()) {
				return;
			}
			if (vli.size() > 1) {
				throw new GenericException("Live instance for " + instance.getId() + " have multiple instance of " + toscaName + " => " + vli.size());
			}
			final VnfLiveInstance oldVli = vli.get(0);
			final VnfPortTask task = createDeleteTask(VnfPortTask::new, oldVli);
			task.setType(ResourceTypeEnum.LINKPORT);
			final VnfPortTask old = (VnfPortTask) oldVli.getTask();
			task.setVnfLinkPort(old.getVnfLinkPort());
			ret.add(new VnfPortVt(task));
		});
	}

	private static void createPort(final List<VnfPortVt> ret, final ComputeTask compute, final VnfPackage vnfPackage) {
		final List<VnfLinkPort> l = findPorts(compute.getToscaName(), vnfPackage.getVnfLinkPort());
		l.stream().forEach(y -> {
			final String toscaName = y.getToscaName() + "-" + compute.getAlias();
			final VnfPortTask task = createTask(VnfPortTask::new);
			task.setToscaName(toscaName);
			task.setAlias(toscaName + RandomStringUtils.random(5, true, true));
			task.setChangeType(ChangeType.ADDED);
			task.setType(ResourceTypeEnum.LINKPORT);
			task.setVnfLinkPort(findVnfLink(vnfPackage, compute.getToscaName()));
			ret.add(new VnfPortVt(task));
		});
	}

	private static List<VnfLinkPort> findPorts(final String toscaName, final Set<VnfLinkPort> vnfLinkPort) {
		return vnfLinkPort.stream().filter(x -> x.getVirtualBinding().equals(toscaName)).toList();
	}

	private static VnfLinkPort findVnfLink(final VnfPackage vnfPackage, final String vdu) {
		return vnfPackage.getVnfLinkPort().stream().filter(x -> x.getVirtualBinding().equals(vdu)).findFirst().orElseThrow(() -> new GenericException("Unable to find VL " + vdu));
	}

	private List<VnfPortVt> doTerminatePlan(final VnfInstance vnfInstance) {
		final List<VnfLiveInstance> instances = vnfLiveInstanceJpa.findByVnfInstanceIdAndClass(vnfInstance, VnfPortTask.class.getSimpleName());
		return instances.stream().map(x -> {
			final VnfPortTask task = createDeleteTask(VnfPortTask::new, x);
			task.setType(ResourceTypeEnum.LINKPORT);
			final VnfPortTask old = (VnfPortTask) x.getTask();
			task.setVnfLinkPort(old.getVnfLinkPort());
			return new VnfPortVt(task);
		}).toList();
	}

	@Override
	public Class<? extends Node> getNode() {
		return VnfPortNode.class;
	}

}
