import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/noun", handler(Suppliers.memoize(() -> randomWord("nouns"))));
        server.createContext("/verb", handler(Suppliers.memoize(() -> randomWord("verbs"))));
        server.createContext("/adjective", handler(Suppliers.memoize(() -> randomWord("adjectives"))));
        server.start();
        System.out.println("Server started.");
    }

    private static String randomWord(String table) {
        String PGHOST = System.getenv("PGHOST");
        String PGPORT = System.getenv("PGPORT");
        String POSTGRES_HOST_AUTH_METHOD = System.getenv("POSTGRES_HOST_AUTH_METHOD");

	PGHOST = (PGHOST != null) ? PGHOST : "db";
	PGPORT = (PGPORT != null) ? PGPORT : "5432";

        if (POSTGRES_HOST_AUTH_METHOD != null && POSTGRES_HOST_AUTH_METHOD.equals("trust")) {
            // If trust is used, avoid using PGUSER and PGPASSWORD
            String connectionString = String.format("jdbc:postgresql://%s:%s/postgres", PGHOST, PGPORT);
            try (Connection connection = DriverManager.getConnection(connectionString)) {
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet set = statement.executeQuery("SELECT word FROM " + table + " ORDER BY random() LIMIT 1")) {
                        while (set.next()) {
                            return set.getString(1);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Use PGUSER and PGPASSWORD
            String PGUSER = System.getenv("PGUSER");
            String PGPASSWORD = System.getenv("PGPASSWORD");

            PGUSER = (PGUSER != null) ? PGUSER : "postgres";
            PGPASSWORD = (PGPASSWORD != null) ? PGPASSWORD : "";

            String connectionString = String.format("jdbc:postgresql://%s:%s/postgres", PGHOST, PGPORT);
            try (Connection connection = DriverManager.getConnection(connectionString, PGUSER, PGPASSWORD)) {
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet set = statement.executeQuery("SELECT word FROM " + table + " ORDER BY random() LIMIT 1")) {
                        while (set.next()) {
                            return set.getString(1);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        throw new NoSuchElementException(table);
    }

    private static HttpHandler handler(Supplier<String> word) {
        return t -> {
            String response = "{\"word\":\"" + word.get() + "\"}";
            byte[] bytes = response.getBytes(Charsets.UTF_8);

            System.out.println(response);
            t.getResponseHeaders().add("content-type", "application/json; charset=utf-8");
            t.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = t.getResponseBody()) {
                os.write(bytes);
            }
        };
    }
}
