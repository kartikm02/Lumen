package com.lumen.api.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/** Run one tenant operation on an exclusively owned, non-privileged connection. */
public final class TenantTransaction {
    private TenantTransaction() {}
    @FunctionalInterface public interface SqlWork<T> { T run(Connection connection) throws SQLException; }

    public static <T> T execute(Connection connection, UUID tenantId, SqlWork<T> work) throws SQLException {
        if (tenantId == null || work == null) throw new IllegalArgumentException("tenant and work required");
        if (!connection.getAutoCommit()) throw new IllegalStateException("connection already has a transaction");
        try (var statement = connection.createStatement();
             var result = statement.executeQuery("SELECT rolsuper, rolbypassrls FROM pg_roles WHERE rolname = current_user")) {
            if (!result.next() || result.getBoolean(1) || result.getBoolean(2))
                throw new IllegalStateException("runtime database role must not bypass RLS");
        }
        connection.setAutoCommit(false);
        boolean safeToReset = false;
        try {
            try (var statement = connection.prepareStatement("SELECT set_config('app.tenant_id', ?, true)")) {
                statement.setString(1, tenantId.toString());
                statement.execute();
            }
            T value = work.run(connection);
            connection.commit();
            safeToReset = true;
            return value;
        } catch (SQLException | RuntimeException | Error e) {
            try {
                connection.rollback();
                safeToReset = true;
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
                try { connection.close(); } catch (SQLException closeError) { e.addSuppressed(closeError); }
            }
            throw e;
        } finally {
            if (safeToReset) connection.setAutoCommit(true);
        }
    }
}
