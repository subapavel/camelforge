package com.camelforge;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;


@Path("/api/kubernetes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KubernetesResource {

@Inject
    KubernetesService kubernetesService;

    @GET
    @Path("/cluster-info")
    @Produces(MediaType.TEXT_PLAIN)
    public String getClusterInfo() {
        return kubernetesService.getClusterInfo();
    }

    @GET
    @Path("/namespaces")
    public Response listNamespaces() {
        try {
            List<String> namespaces = kubernetesService.listNamespaces();
            return Response.ok(Map.of("namespaces", namespaces))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/pods/{namespace}")
    public Response listPods(@PathParam("namespace") String namespace) {
        try {
            List<String> pods = kubernetesService.listPods(namespace);
            return Response.ok(Map.of(
                "namespace", namespace,
                "pods", pods
            )).build();
        } catch (Exception e) {
            return Response.serverError()
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
}
