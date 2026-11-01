package com.lumen.api.db;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
class TenantIsolationIntegrationTest {
    @Container static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>(
        DockerImageName.parse("pgvector/pgvector:pg17").asCompatibleSubstituteFor("postgres"));

    @Test void restrictedRoleSeesOnlyItsTenantAndCannotWriteAcrossTenants() throws Exception {
        String a = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
        String b = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
        String migration;
        try (var in = getClass().getResourceAsStream("/db/migration/V1__tenant_foundation.sql")) {
            migration = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        try (var c = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword()); var s = c.createStatement()) {
            s.execute(migration);
            s.execute("CREATE ROLE lumen_test NOLOGIN NOSUPERUSER NOBYPASSRLS");
            s.execute("GRANT USAGE ON SCHEMA public TO lumen_test");
            s.execute("GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO lumen_test");
            s.execute("INSERT INTO tenants(id,name,quota_tokens_per_day) VALUES ('" + a + "','A',100), ('" + b + "','B',100)");
            s.execute("SET ROLE lumen_test");
            try (var rows = s.executeQuery("SELECT count(*) FROM tenants")) { rows.next(); assertEquals(0, rows.getInt(1)); }
            var count = TenantTransaction.execute(c, java.util.UUID.fromString(a), connection -> {
                try (var query = connection.createStatement(); var rows = query.executeQuery("SELECT count(*) FROM tenants")) {
                    rows.next(); return rows.getInt(1);
                }
            });
            assertEquals(1, count);
            try (var rows = s.executeQuery("SELECT count(*) FROM tenants")) { rows.next(); assertEquals(0, rows.getInt(1)); }
            c.setAutoCommit(false);
            s.execute("SELECT set_config('app.tenant_id', '" + a + "', true)");
            try (var rows = s.executeQuery("SELECT id FROM tenants")) { assertTrue(rows.next()); assertEquals(a, rows.getString(1)); assertFalse(rows.next()); }
            var denied = assertThrows(java.sql.SQLException.class, () -> s.execute("INSERT INTO documents(id,tenant_id,status,sha256,s3_key) VALUES ('cccccccc-cccc-cccc-cccc-cccccccccccc','" + b + "','PENDING',repeat('a',64),'private')"));
            assertEquals("42501", denied.getSQLState());
            c.rollback();
            try (var rows = s.executeQuery("SELECT count(*) FROM tenants")) { rows.next(); assertEquals(0, rows.getInt(1)); }
        }
    }
}
