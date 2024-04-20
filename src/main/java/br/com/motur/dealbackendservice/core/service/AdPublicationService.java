package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.AdDto;
import br.com.motur.dealbackendservice.core.model.AdEntity;
import br.com.motur.dealbackendservice.core.model.DealerEntity;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Serviço responsável por publicar anúncios em integradores.
 */
@Service
public class AdPublicationService extends IntegrationService implements AdPublicationServiceInterface {

    private final  AdRepository adRepository;

    private final IntegrationService integrationService;

    private final DealerRepository dealerRepository;

    private final ProviderRepository providerRepository;

    private final AuthConfigRepository authConfigRepository;

    private final FieldMappingRepository fieldMappingRepository;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public AdPublicationService(AdRepository adRepository, IntegrationService integrationService, DealerRepository dealerRepository, ProviderRepository providerRepository, AuthConfigRepository authConfigRepository, FieldMappingRepository fieldMappingRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(authConfigRepository, fieldMappingRepository, restTemplate, objectMapper);
        this.adRepository = adRepository;
        this.integrationService = integrationService;
        this.dealerRepository = dealerRepository;
        this.providerRepository = providerRepository;
        this.authConfigRepository = authConfigRepository;
        this.fieldMappingRepository = fieldMappingRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }



    /**
     * Publica um anúncio em todos os integradores configurados para o dealer especificado.
     * @param adDto Dados do anúncio a ser publicado.
     */
    public void publishAd(final AdDto adDto) {
        AdEntity ad = adRepository.findById(adDto.getId()).orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado"));
        DealerEntity dealer = dealerRepository.findById(adDto.getDealerId()).orElseThrow(() -> new IllegalArgumentException("Dealer não encontrado"));

        for (ProviderEntity provider : dealer.getProviders()) {
            publishAdToProvider(ad, provider);
        }
    }

    @Override
    public AdDto getAdDto(Long id) {
        return adRepository.findById(id).map(this::convertToDto).orElse(null);
    }

    private AdDto convertToDto(final AdEntity adEntity) {
        return AdDto.builder()
                .id(adEntity.getId())
                .providerId(adEntity.getProvider().getId())
                .brandId(adEntity.getBrandId())
                .modelId(adEntity.getModelId())
                .trimId(adEntity.getTrimId())
                .modelYear(adEntity.getModelYear())
                .productionYear(adEntity.getProductionYear())
                .fuelType(adEntity.getFuelType())
                .transmissionType(adEntity.getTransmissionType())
                .licensePlate(adEntity.getLicensePlate())
                .color(adEntity.getColor())
                .km(adEntity.getKm())
                .price(adEntity.getPrice())
                .description(adEntity.getDescription())
                .dealerId(adEntity.getDealer().getId())
                .status(adEntity.getStatus())
                .build();
    }

    @Override
    public List<AdDto> getAdsPageable(Integer page, Integer size) {
        return List.of();
    }

    /**
     * Envia o anúncio para um integrador específico.
     * @param ad Anúncio a ser publicado.
     * @param provider Integrador para onde o anúncio será enviado.
     */
    private void publishAdToProvider(AdEntity ad, ProviderEntity provider) {
        // Implementação de publicação específica dependendo do tipo de integrador e sua API
        switch (provider.getApiType()) {
            case REST:
                publishViaRest(ad, provider);
                break;
            case SOAP:
                publishViaSoap(ad, provider);
                break;
            default:
                throw new IllegalStateException("Tipo de integrador não suportado: " + provider.getApiType());
        }
    }

    private void publishViaRest(AdEntity ad, ProviderEntity provider) {
        // Logica para chamar a API REST do integrador
    }

    private void publishViaSoap(AdEntity ad, ProviderEntity provider) {
        // Logica para chamar o serviço SOAP do integrador
    }
}
