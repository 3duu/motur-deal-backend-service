package br.com.motur.dealbackendservice.core.jobs;

import br.com.motur.dealbackendservice.core.service.CatalogDownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Essa classe é responsável por agendar a tarefa de download do catálogo
 */
@Service
public class ScheduledCatalogDownloadTask {

    private final CatalogDownloadService catalogDownloadService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ScheduledCatalogDownloadTask(CatalogDownloadService catalogDownloadService) {
        this.catalogDownloadService = catalogDownloadService;
    }

    //@EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    @Scheduled(fixedRate = 800000) // Defina a frequência conforme necessário
    public void executeDownloadTask() {
        logger.info("Iniciando o download do catálogo...");
        //catalogDownloadService.downloadCatalogData();
        logger.info("Download do catálogo concluído.");
    }
}
