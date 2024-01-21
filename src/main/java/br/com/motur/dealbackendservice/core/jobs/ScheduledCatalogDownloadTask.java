package br.com.motur.dealbackendservice.core.jobs;

import br.com.motur.dealbackendservice.core.service.CatalogDownloadService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledCatalogDownloadTask {

    private final CatalogDownloadService catalogDownloadService;

    public ScheduledCatalogDownloadTask(CatalogDownloadService catalogDownloadService) {
        this.catalogDownloadService = catalogDownloadService;
    }

    //@Scheduled(fixedRate = 8000) // Defina a frequência conforme necessário
    @EventListener(ApplicationReadyEvent.class)
    public void executeDownloadTask() {
        catalogDownloadService.downloadCatalogData();
    }
}
