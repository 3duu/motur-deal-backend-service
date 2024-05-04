package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.converter.AdConverter;
import br.com.motur.dealbackendservice.core.converter.AdVoConverter;
import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.entrypoints.v1.request.AdDto;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.service.vo.AdVo;
import br.com.motur.dealbackendservice.core.service.vo.PostResultsVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final AdConverter adConverter;

    private final AdVoConverter adVoConverter;


    private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    public AdPublicationService(AdRepository adRepository, IntegrationService integrationService,
                                DealerRepository dealerRepository, ProviderRepository providerRepository,
                                AuthConfigRepository authConfigRepository, FieldMappingRepository fieldMappingRepository,
                                RestTemplate restTemplate, ObjectMapper objectMapper, ApplicationContext applicationContext,
                                ProviderTrimsRepository providerTrimsRepository, AdConverter adConverter, AdVoConverter adVoConverter) {
        super(authConfigRepository, fieldMappingRepository, restTemplate, applicationContext, objectMapper);
        this.adRepository = adRepository;
        this.integrationService = integrationService;
        this.dealerRepository = dealerRepository;
        this.providerRepository = providerRepository;
        this.authConfigRepository = authConfigRepository;
        this.fieldMappingRepository = fieldMappingRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.adConverter = adConverter;
        this.adVoConverter = adVoConverter;
    }


    /**
     * Publica um anúncio em todos os integradores configurados para o dealer especificado.
     *
     * @param adDto Dados do anúncio a ser publicado.
     * @return PostResultsVo com os resultados da publicação.
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public PostResultsVo publishAd(final AdDto adDto) throws Exception {

        logger.info("Publicando anúncio: {}", adDto);

        logger.info("Buscando dealer: {}", adDto.getDealerId());
        final DealerEntity dealer = dealerRepository.findById(adDto.getDealerId()).orElseThrow(() -> new IllegalArgumentException("Dealer não encontrado"));

        final AdEntity ad = adConverter.convert(adDto);

        logger.info("Salvando anúncio: {}", ad);
        final AdEntity saved = adRepository.save(ad);

        final PostResultsVo results = new PostResultsVo();
        results.setAdId(saved.getId());


        ad.getAdPublicationList().forEach(publication -> {
            try {
                logger.info("Publicando anúncio no integrador: {}", publication.getProvider().getName());
                final AdVo adVo = adVoConverter.convert(ad, publication.getProvider());
                publishAdToProvider(ad, adVo, publication);
            } catch (Exception e) {
                logger.error("Erro ao publicar anúncio no integrador: {}", publication.getProvider().getName(), e);
                results.addError(publication.getProvider().getName(), e.getMessage());
            }
        });

        return results;
    }

    /**
     * Atualiza um anúncio em todos os integradores configurados para o dealer especificado.
     *
     * @param adDto Dados do anúncio a ser publicado.
     * @return PostResultsVo com os resultados da publicação.
     */
    public PostResultsVo updateAd(final AdDto adDto) throws Exception {
        AdEntity ad = adRepository.findById(adDto.getId()).orElseThrow(() -> new IllegalArgumentException("Anúncio não encontrado"));
        DealerEntity dealer = dealerRepository.findById(adDto.getDealerId()).orElseThrow(() -> new IllegalArgumentException("Dealer não encontrado"));

        final PostResultsVo results = new PostResultsVo();

        /*for (ProviderEntity provider : dealer.getProviders()) {
            publishAdToProvider(ad, provider);
        }*/

        return results;
    }

    @Override
    public AdDto getAdDto(Long id) {
        return adRepository.findById(id).map(this::convertToDto).orElse(null);
    }

    private AdDto convertToDto(final AdEntity adEntity) {
        return adConverter.invert(adEntity);
    }

    @Override
    public List<AdDto> getAdsPageable(Integer page, Integer size) {
        return List.of();
    }

    /**
     * Envia o anúncio para um integrador específico.
     * @param ad Anúncio a ser publicado.
     * @param publication dados do Integrador para onde o anúncio será enviado.
     */
    private void publishAdToProvider(final AdEntity ad, AdVo adVo, final AdPublicationEntity publication) throws Exception {
        // Implementação de publicação específica dependendo do tipo de integrador e sua API

        integrateVehicle(ad, adVo, publication.getProvider());
    }

    /**
     * Envia o veículo para o provedor.
     *
     * @param ad    O veículo a ser enviado.
     * @param provider O ID do provedor para o qual o veículo deve ser enviado.
     */
    public void sendVehicleToProvider(final Object ad, final ProviderEntity provider) throws Exception {
        // Obter configuração de autenticação
        final AuthConfigEntity authConfig = authConfigRepository.findByProviderId(provider.getId());
        if (authConfig == null) {
            //throw new IllegalStateException("Configuração de autenticação não encontrada para o provedor: " + provider.getId());
        }

        // Autenticar com o provedor e obter o token
        String authToken = authenticateWithProvider(authConfig);

        //Enviar veículo
        //sendVehicleData(createAdUrl, vehicleData, authToken);

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

    /**
     * Envia o veículo para o provedor.
     *
     * @param ad    O anuncio a ser enviado.
     * @param provider Fornecedor para o qual o veículo deve ser enviado.
     */
    public void integrateVehicle(final AdEntity ad, AdVo adVo, final ProviderEntity provider) throws Exception {
        // Encontrar a configuração de autenticação e mapeamento de campos para o provedor
        //AuthConfigEntity authConfig = authConfigRepository.findByProviderId(providerId);
        List<FieldMappingEntity> fieldMappings = fieldMappingRepository.findByProviderId(provider.getId());

        // Adaptar o veículo para o formato esperado pela API do provedor
        final Object providerAd = adaptVehicleToProviderFormat(ad, adVo, provider, fieldMappings);

        // Realizar a autenticação e enviar a solicitação para a API do provedor
        //String token = authenticateWithProvider(authConfig);
        sendVehicleToProvider(providerAd, provider);
    }

    private void publishViaRest(Object ad, ProviderEntity provider) {
        // Logica para chamar a API REST do integrador
    }

    private void publishViaSoap(Object ad, ProviderEntity provider) {
        // Logica para chamar o serviço SOAP do integrador
    }
}
