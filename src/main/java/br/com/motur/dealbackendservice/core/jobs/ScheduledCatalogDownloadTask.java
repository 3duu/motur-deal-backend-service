package br.com.motur.dealbackendservice.core.jobs;

import br.com.motur.dealbackendservice.core.service.CatalogDownloadService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Essa classe é responsável por agendar a tarefa de download do catálogo
 */
@Service
public class ScheduledCatalogDownloadTask {

    private final CatalogDownloadService catalogDownloadService;

    public ScheduledCatalogDownloadTask(CatalogDownloadService catalogDownloadService) {
        this.catalogDownloadService = catalogDownloadService;
    }

    @EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    //@Scheduled(fixedRate = 800000) // Defina a frequência conforme necessário
    public void executeDownloadTask() {
        catalogDownloadService.downloadCatalogData();
    }
}
