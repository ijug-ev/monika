import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.UriBuilder;

public class MonikaBootstrap {
	public static void main(final String[] args) throws InterruptedException, ExecutionException {
		final var port = Integer.parseInt(System.getenv().getOrDefault("IP_PORT", "8080"));

		if (args.length > 0 && "--check-health".equals(args[0])) {
			final var ignoreErrors = args.length > 1 && "--ignore-errors".equals(args[1]);
			final var exitCode = checkHealth(port, ignoreErrors).ordinal();
			System.exit(exitCode);
		}

		final var config = SeBootstrap.Configuration.builder().port(port).build();
		SeBootstrap.start(MonikaApplication.class, config).thenAccept(instance -> {
			instance.stopOnShutdown(stopResult -> {
				try {
					System.err.printf("Stop result: %s%n", stopResult);
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
			});

			/*
			 * AppCDS: Stop immediately once service is up and running
			 */
			if (args.length > 0 && "--stop".equals(args[0])) {
				instance.stop().thenRun(() -> System.exit(0));
				System.out.println("Stopping immediately...");
			}

			final var actualPort = instance.configuration().port();
			System.out.printf("Process %d listening to port %d - Send SIGKILL to shutdown.%n", ProcessHandle.current().pid(), actualPort);
		});

		Thread.currentThread().join();
	}

	private static class MonikaApplication extends Application {
        @Override
		public Set<Class<?>> getClasses() {
			return Set.of(MonikaResource.class);
		}

        private static final Map<String, Object> PROPERTIES;
        static {
            final var monitoredPaths = System.getenv("MONITORED_PATHS");
            
            final var mp = monitoredPaths == null || monitoredPaths.isBlank() ? Set.of(Path.of("/"))
                    : Arrays.asList(monitoredPaths.split(File.pathSeparator)).stream().map(Path::of).toList();
            mp.forEach(monitoredPath -> System.out.printf("Monitoring: %s%n", monitoredPath));
            PROPERTIES = Map.of("MONITORED_PATHS", mp);
        }

        @Override
        public Map<String, Object> getProperties() {
            return PROPERTIES;
        }
    }

	private static enum HEALTH { SUCCESS, UNHEALTHY, RESERVED }

	private static HEALTH checkHealth(final int port, final boolean ignoreErrors) {
		try {
			final var uri = UriBuilder.newInstance().scheme("http").host("localhost").port(port).path(MonikaResource.class).path(MonikaResource.class, "getHealth");
			System.out.println("HEALTHCHECK " + uri);
			ClientBuilder.newClient().target(uri).request().get(String.class);
            
			System.out.println("HEALTHY");
			return HEALTH.SUCCESS;
		} catch (final Throwable t) {
			System.out.printf("Unhealthy state detected: %s%n", t.getMessage());
			return ignoreErrors ? HEALTH.SUCCESS : HEALTH.UNHEALTHY;
		}
	}

}
