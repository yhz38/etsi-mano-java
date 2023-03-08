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
package com.ubiqube.etsi.mano.service.mon.jms;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.ubiqube.etsi.mano.mon.dao.TelemetryMetricsResult;
import com.ubiqube.etsi.mano.service.mon.model.MonitoringDataSlim;

@Mapper
public interface MonitoringDataMapping {
	MonitoringDataMapping INSTANCE = Mappers.getMapper(MonitoringDataMapping.class);

	@Mapping(target = "status", ignore = true)
	@Mapping(target = "vnfcId", ignore = true)
	@Mapping(target = "timestamp", ignore = true)
	@Mapping(target = "txt", source = "text")
	TelemetryMetricsResult fromDto(MonitoringDataSlim ci);
}
