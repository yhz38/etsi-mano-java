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
package com.ubiqube.etsi.mano.nfvo.service.graph;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jgrapht.ListenableGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.NsLiveInstance;
import com.ubiqube.etsi.mano.dao.mano.NsdInstance;
import com.ubiqube.etsi.mano.dao.mano.NsdPackage;
import com.ubiqube.etsi.mano.dao.mano.ResourceTypeEnum;
import com.ubiqube.etsi.mano.dao.mano.v2.Blueprint;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsBlueprint;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsTask;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsVirtualLinkTask;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsVnfExtractorTask;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsVnfInstantiateTask;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsVnfTask;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsdExtractorTask;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsdTask;
import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgLoadbalancerTask;
import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgPortPairTask;
import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgPostTask;
import com.ubiqube.etsi.mano.exception.GenericException;
import com.ubiqube.etsi.mano.nfvo.jpa.NsLiveInstanceJpa;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.v3.AbstractNsdContributorV3;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.NsCreateVt;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.NsExtratorVt;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.NsInstantiateVt;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.NsVirtualLinkVt;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.NsVnfCreateVt;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.NsVnfExtractorVt;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.NsVnfInstantiateVt;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.NsVnffgPortPairVt;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.NsVnffgPostVt;
import com.ubiqube.etsi.mano.nfvo.service.plan.contributors.vt.VnffgLoadbalancerVt;
import com.ubiqube.etsi.mano.orchestrator.ContextHolder;
import com.ubiqube.etsi.mano.orchestrator.ExecutionGraph;
import com.ubiqube.etsi.mano.orchestrator.OrchExecutionResults;
import com.ubiqube.etsi.mano.orchestrator.Planner;
import com.ubiqube.etsi.mano.orchestrator.SclableResources;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.nodes.mec.NsdExtractorNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.mec.VnfExtractorNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.NsdCreateNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.NsdInstantiateNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.PortPairNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnfCreateNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnfInstantiateNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgLoadbalancerNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgPostNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;
import com.ubiqube.etsi.mano.orchestrator.v3.BlueprintBuilder;
import com.ubiqube.etsi.mano.orchestrator.v3.PreExecutionGraphV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.event.WorkflowV3;
import com.ubiqube.etsi.mano.service.graph.Edge2d;
import com.ubiqube.etsi.mano.service.graph.Vertex2d;

/**
 *
 * @author olivier
 *
 */
@Service
public class NfvoOrchestrationV3 implements WorkflowV3<NsdPackage, NsBlueprint, NsTask> {

	private static final Logger LOG = LoggerFactory.getLogger(NfvoOrchestrationV3.class);

	private final List<AbstractNsdContributorV3<NsTask>> contributors;
	private final BlueprintBuilder blueprintBuilder;
	private final NsPlanService planService;
	private final NsLiveInstanceJpa nsLiveInstanceJpa;
	private final Map<ResourceTypeEnum, Function<NsTask, VirtualTaskV3<? extends NsTask>>> vts;
	private final List<Class<? extends Node>> masterVertex;
	private final Planner<NsBlueprint, NsTask, NsTask> planv2;

	public NfvoOrchestrationV3(final List<AbstractNsdContributorV3<?>> contributors, final BlueprintBuilder blueprintBuilder, final NsPlanService planService,
			final NsLiveInstanceJpa nsLiveInstanceJpa, final Planner<NsBlueprint, NsTask, NsTask> planv2) {
		this.contributors = (List<AbstractNsdContributorV3<NsTask>>) ((Object) contributors);
		this.blueprintBuilder = blueprintBuilder;
		this.planService = planService;
		this.nsLiveInstanceJpa = nsLiveInstanceJpa;
		this.planv2 = planv2;
		vts = new EnumMap<>(ResourceTypeEnum.class);
		vts.put(ResourceTypeEnum.VL, x -> new NsVirtualLinkVt((NsVirtualLinkTask) x));
		vts.put(ResourceTypeEnum.VNFFG_LOADBALANCER, x -> new VnffgLoadbalancerVt((VnffgLoadbalancerTask) x));
		vts.put(ResourceTypeEnum.VNFFG_POST, x -> new NsVnffgPostVt((VnffgPostTask) x));
		vts.put(ResourceTypeEnum.VNFFG_PORT_PAIR, x -> new NsVnffgPortPairVt((VnffgPortPairTask) x));
		vts.put(ResourceTypeEnum.NSD_CREATE, x -> new NsCreateVt((NsdTask) x));
		vts.put(ResourceTypeEnum.NSD_INSTANTIATE, x -> new NsInstantiateVt((NsVnfInstantiateTask) x));
		vts.put(ResourceTypeEnum.NSD_EXTRACTOR, x -> new NsExtratorVt((NsdExtractorTask) x));
		vts.put(ResourceTypeEnum.VNF_CREATE, x -> new NsVnfCreateVt((NsVnfTask) x));
		vts.put(ResourceTypeEnum.VNF_INSTANTIATE, x -> new NsVnfInstantiateVt((NsVnfInstantiateTask) x));
		vts.put(ResourceTypeEnum.VNF_EXTRACTOR, x -> new NsVnfExtractorVt((NsVnfExtractorTask) x));
		masterVertex = List.of(Network.class, VnfCreateNode.class, NsdCreateNode.class, VnffgPostNode.class, VnffgLoadbalancerNode.class);
	}

	@Override
	public PreExecutionGraphV3<NsTask> setWorkflowBlueprint(final NsdPackage bundle, final NsBlueprint blueprint) {
		final List<SclableResources<NsTask>> sr = contributors.stream().flatMap(x -> x.contribute(bundle, blueprint).stream()).toList();
		final ListenableGraph<Vertex2d, Edge2d> g = planService.getPlanFor(bundle.getId());
		return blueprintBuilder.buildPlan(sr, g, x -> {
			LOG.trace("Running for {}={}", x.getType(), x.getToscaName());
			blueprint.addTask(x);
			final NsTask nc = x.copy();
			return (VirtualTaskV3<NsTask>) vts.get(x.getType()).apply(nc);
		}, buildContext(blueprint.getInstance()), masterVertex);
	}

	private List<ContextHolder> buildContext(final NsdInstance instance) {
		final List<NsLiveInstance> live = nsLiveInstanceJpa.findByNsInstanceId(instance.getId());
		return live.stream().map(this::convert).toList();
	}

	private ContextHolder convert(final NsLiveInstance x) {
		final Class<? extends Node> t = switch (x.getNsTask().getType()) {
		case VL -> Network.class;
		case VNF_CREATE -> VnfCreateNode.class;
		case VNF_INSTANTIATE -> VnfInstantiateNode.class;
		case VNF_EXTRACTOR -> VnfExtractorNode.class;
		case NSD_CREATE -> NsdCreateNode.class;
		case NSD_INSTANTIATE -> NsdInstantiateNode.class;
		case NSD_EXTRACTOR -> NsdExtractorNode.class;
		case VNFFG -> VnffgPostNode.class;
		case VNFFG_LOADBALANCER -> VnffgLoadbalancerNode.class;
		case VNFFG_POST -> VnffgPostNode.class;
		case VNFFG_PORT_PAIR -> PortPairNode.class;
		default -> throw new GenericException(x.getNsTask().getType() + " is not handled.");
		};
		return new ContextHolder(x.getId(), t, x.getNsTask().getToscaName(), x.getRank(), x.getResourceId());
	}

	@Override
	public OrchExecutionResults<NsTask> execute(final PreExecutionGraphV3<NsTask> plan, final NsBlueprint parameters) {
		plan.toDotFile("orch-added.dot");
		final ExecutionGraph imp = planv2.implement(plan);
		return planv2.execute(imp, new NsOrchListenetImpl(nsLiveInstanceJpa, parameters));
	}

	@Override
	public void refresh(final PreExecutionGraphV3<NsTask> prePlan, final Blueprint<NsTask, ?> localPlan) {
		prePlan.getPreTasks().forEach(x -> {
			final NsTask task = find(x.getTemplateParameters().getToscaId(), localPlan);
			x.setTemplateParameters(task);
		});
	}

	private static NsTask find(final String id, final Blueprint<NsTask, ?> localPlan) {
		return localPlan.getTasks().stream()
				.filter(x -> x.getToscaId().equals(id))
				.findFirst()
				.orElseThrow(() -> new GenericException("Could not find " + id));
	}

}
