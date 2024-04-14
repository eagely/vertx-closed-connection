import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import io.vertx.sqlclient.ClosedConnectionException;
import jakarta.inject.Inject;
import org.acme.vertx.MSSqlResource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
@QuarkusTestResource(MSSqlResource.class)
public class OverloadTest {

    @Inject
    io.vertx.mutiny.mssqlclient.MSSQLPool client;

    @Test
    public void testLength186() {
        client.query("CREATE TABLE Test (text NVARCHAR(MAX))").execute().await().indefinitely();
        client.preparedQuery("INSERT INTO Test (text) VALUES ('" + "a".repeat(186) + "')").execute().await().indefinitely();
    }

    @Test
    public void testLength187() {
        client.query("CREATE TABLE Test2 (text NVARCHAR(MAX))").execute().await().indefinitely();
        assertThrows(ClosedConnectionException.class, () -> {
            client.preparedQuery("INSERT INTO Test2 (text) VALUES ('" + "a".repeat(187) + "')").execute().await().indefinitely();
        });
    }
}
