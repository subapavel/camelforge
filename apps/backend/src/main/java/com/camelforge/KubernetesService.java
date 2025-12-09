package com.camelforge;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KubernetesService {

    private static final Logger LOG = Logger.getLogger(KubernetesService.class);
    
    @Inject
    KubernetesClient kubernetesClient;

    public String getClusterInfo() {
        try {
            String version = kubernetesClient.getKubernetesVersion().getGitVersion();
            String masterUrl = kubernetesClient.getMasterUrl().toString();
            String namespace = kubernetesClient.getNamespace();
            
            LOG.info("Connected to Kubernetes cluster: " + masterUrl);
            
            return String.format(
                "Cluster Version: %s\nMaster URL: %s\nNamespace: %s",
                version, masterUrl, namespace
            );
        } catch (Exception e) {
            LOG.error("Failed to get cluster info", e);
            return "Error connecting to cluster: " + e.getMessage();
        }
    }

    public List<String> listPods(String namespace) {
        try {
            PodList podList = kubernetesClient
                .pods()
                .inNamespace(namespace)
                .list();
            
            return podList.getItems().stream()
                .map(pod -> pod.getMetadata().getName())
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed to list pods", e);
            throw new RuntimeException("Error listing pods: " + e.getMessage());
        }
    }

    public List<String> listNamespaces() {
        try {
            return kubernetesClient
                .namespaces()
                .list()
                .getItems()
                .stream()
                .map(ns -> ns.getMetadata().getName())
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Failed to list namespaces", e);
            throw new RuntimeException("Error listing namespaces: " + e.getMessage());
        }
    }
}
