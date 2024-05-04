import static java.nio.file.StandardOpenOption.CREATE;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Path("monika")
public class MonikaResource {

  @GET
  @Path("health")
  public void health() {
    // Implies 201 No Content, i. e. HEALTHY state
  }

  @GET
  @Path("status")
  public Response status(@Context final Configuration config) {
    final var monitoredPaths = (Collection<java.nio.file.Path>) config.getProperty("MONITORED_PATHS");
    assert monitoredPaths != null && !monitoredPaths.isEmpty() : "Monitored paths cannot be null nor empty";
    final var minimumFree = monitoredPaths.stream().mapToInt(path -> {
        try {
            final var fileStore = Files.getFileStore(path);
            final var percentFree = Math.toIntExact(100 * fileStore.getUsableSpace() / fileStore.getTotalSpace());
            System.out.printf("%s is %d %% free%n", path, percentFree);
            return percentFree;
        } catch (final IOException e) {
            e.printStackTrace();
            return 100; // no need to bother *the invoker* with hardware problems - we can implement health check to cover this
        }
    }).min().getAsInt();
    return minimumFree < 20 ? Response.status(901, "A volume is (nearly) full!").build() :
        Response.status(200 + minimumFree, String.format("Smallest free volume is %d %%.", minimumFree)).build();
  }

}
