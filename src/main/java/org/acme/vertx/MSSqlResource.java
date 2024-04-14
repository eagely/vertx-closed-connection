package org.acme.vertx;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public class MSSqlResource implements QuarkusTestResourceLifecycleManager {
	
	public static MSSQLServerContainer<?> mssqlserver = new MSSQLServerContainer<>(
				DockerImageName.parse("mcr.microsoft.com/mssql/server").withTag("2022-latest")
			)
			.withInitScript("db/init.sql")
	        .acceptLicense();

	@Override
	public Map<String, String> start() {
		mssqlserver.start();
		String url = "sqlserver://" + mssqlserver.getHost() + ":" + mssqlserver.getMappedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT);
		return Map.of(
				"quarkus.datasource.reactive.url",url + "/CISDB",
				"quarkus.datasource.jdbc.url",url + ";databaseName=CISDB",
				"tenant","tenant"
		);
	}

	@Override
	public void stop() {
		mssqlserver.stop();
	}
}
