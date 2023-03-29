/**
 * Copyright (C) 2019-2023 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <https://www.gnu.org/licenses/>.
 */
module com.ubiqube.etsi.mano.auth.cert {
	requires com.ubiqube.etsi.mano.auth.cert.common;
	//
	requires com.ubiqube.etsi.mano.auth;
	requires org.bouncycastle.provider;
	requires org.bouncycastle.pkix;
	requires io.swagger.v3.oas.models;
	requires spring.boot;
	requires spring.security.web;
	requires java.sql;
	requires org.slf4j;
}