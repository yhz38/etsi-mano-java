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
package com.ubiqube.etsi.mano.repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.service.Configuration;

@Service
public class DefaultNamingStrategy implements NamingStrategy {
	private final ClassPathConverter cpConverter = new ClassPathConverter();
	private final String root;

	public DefaultNamingStrategy(final Configuration configuration) {
		root = configuration.build("repository.phys.root").notNull().build();
	}

	protected static final String sanitize(final String filename) {
		// It's ok for path segment not for a full path.
		return filename.replaceAll("\\.+", ".");
	}

	@Override
	public Path getRoot(final Class<?> clazz) {
		return Paths.get(root, cpConverter.convert(clazz));
	}

	@Override
	public Path getRoot(final Class<?> clazz, final UUID _id) {
		return Paths.get(root, cpConverter.convert(clazz), _id.toString());
	}

	@Override
	public Path getRoot(final Class<?> clazz, final UUID _id, final String _filename) {
		return Paths.get(root, cpConverter.convert(clazz), _id.toString(), sanitize(_filename));
	}

	@Override
	public Path getRoot() {
		return Paths.get(root);
	}

}
