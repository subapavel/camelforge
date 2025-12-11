package com.camelforge;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.camel.v1.Integration;

@Path("/api/integrations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IntegrationResource {
    
    @Inject
    KubernetesClient kubernetesClient;

    @POST
    @Path("/timer")
    public Response createTimerIntegration(Map<String, Object> request) {
        String name = (String) request.get("name");
        String message = (String) request.get("message");
        Integer period = (Integer) request.getOrDefault("period", 10000);

        String yaml = String.format("""
            apiVersion: camel.apache.org/v1
            kind: Integration
            metadata:
              name: %s
            spec:
              flows:
                - from:
                    uri: "timer:tick?period=%d"
                    steps:
                      - setBody:
                          simple: "%s - Time: ${date:now:HH:mm:ss}"
                      - to: "log:camelforge?level=INFO"
            """, name, period, message);

        Integration integration = Serialization.unmarshal(yaml, Integration.class);

        Integration created = kubernetesClient
            .resources(Integration.class)
            .inNamespace("default")
            .resource(integration)
            .create();

        return Response.ok(Map.of(
            "status", "created",
            "name", created.getMetadata().getName()
        ))
        .header("Access-Control-Allow-Origin", "*")
        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        .header("Access-Control-Allow-Headers", "Content-Type")
        .build();
    }

    @GET
    public Response listIntegrations() {
        List<Integration> integrations = kubernetesClient
            .resources(Integration.class)
            .inNamespace("default")
            .list()
            .getItems();

        return Response.ok(Map.of(
            "count", integrations.size(),
            "integrations", integrations.stream()
                .map(i -> Map.of(
                    "name", i.getMetadata().getName(),
                    "phase", i.getStatus() != null ? i.getStatus().getPhase() : "Unknown"
                ))
                .toList()
        )).build();
    }

        @GET
    @Path("/{name}/logs")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getIntegrationLogs(@PathParam("name") String name) {
        try {
            // Najdi pod pro tuto integraci
            var pods = kubernetesClient
                .pods()
                .inNamespace("default")
                .withLabel("camel.apache.org/integration", name)
                .list()
                .getItems();

            if (pods.isEmpty()) {
                return Response.status(404)
                    .entity("No pod found for integration: " + name)
                    .build();
            }

            String podName = pods.get(0).getMetadata().getName();
            String logs = kubernetesClient
                .pods()
                .inNamespace("default")
                .withName(podName)
                .getLog(true);

            return Response.ok(logs)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type")
                .build();
        } catch (Exception e) {
            return Response.serverError()
                .entity("Error getting logs: " + e.getMessage())
                .build();
        }
    }

    @DELETE
    @Path("/{name}")
    public Response deleteIntegration(@PathParam("name") String name) {
        kubernetesClient
            .resources(Integration.class)
            .inNamespace("default")
            .withName(name)
            .delete();

        return Response.ok(Map.of(
            "status", "deleted",
            "name", name
        )).build();
    }
}
