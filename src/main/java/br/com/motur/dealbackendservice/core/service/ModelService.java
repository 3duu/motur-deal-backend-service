package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.finder.BodyTypeFinder;
import br.com.motur.dealbackendservice.core.finder.TractionTypeFinder;
import br.com.motur.dealbackendservice.core.finder.TransmissionFinder;
import br.com.motur.dealbackendservice.core.dataproviders.repository.ModelRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.TrimRepository;
import br.com.motur.dealbackendservice.core.model.ModelEntity;
import br.com.motur.dealbackendservice.core.model.TrimEntity;
import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelService {

    private final ModelRepository modelRepository;
    private final BrandService brandService;

    private final TrimRepository trimRepository;

    private final TransmissionFinder transmissionFinder;

    private final TractionTypeFinder tractionTypeFinder;
    private final BodyTypeFinder bodyTypeConverter;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public ModelService(ModelRepository modelRepository, BrandService brandService, TrimRepository trimRepository, TransmissionFinder transmissionFinder, TractionTypeFinder tractionTypeFinder, BodyTypeFinder bodyTypeConverter) {
        this.modelRepository = modelRepository;
        this.brandService = brandService;
        this.trimRepository = trimRepository;
        this.transmissionFinder = transmissionFinder;
        this.tractionTypeFinder = tractionTypeFinder;
        this.bodyTypeConverter = bodyTypeConverter;
    }

    public List<ModelEntity> findAllModels() {
        return modelRepository.findAll();
    }

    // Outros métodos de serviço...

    /**
     * Este método é executado após o início do aplicativo.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {

        /*String path = "C:\\Users\\Eduardo\\Desktop\\models__select_m_nome_brand_m2_nome_name_m2_sinonimo_m2_indicebusca_m2__202401111120.csv"; // Substitua pelo caminho do seu arquivo
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            final List<BrandEntity> list = brandService.findAllBrands();

            int i = 0;
            while ((line = br.readLine()) != null) {
                // Usando vírgula como separador
                String[] values = line.split(",");

                if (i > 0){
                    final ModelEntity model = new ModelEntity();
                    model.setBrand(list.stream().filter(brand -> brand.getName().equals(values[0])).findFirst().orElse(null));
                    model.setName(values[1]);
                    model.setSynonym((values[2] != null && !values[2].trim().isEmpty()) || (values[2] != null && values[2].equals("\"\"")) ? values[2] : null);
                    try{
                        model.setSearchIndex(values[3] != null && !values[3].isEmpty() ? Float.parseFloat(values[3]) : null);
                    }
                    catch (Exception e){
                        model.setSearchIndex(null);
                    }

                    if (model.getBrand() != null)
                        modelRepository.save(model);
                }

                i++;
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }*/


        String path = "C:\\Users\\Eduardo\\Desktop\\trims_marca_modelo_versao_202401261043.json"; // Substitua pelo caminho do seu arquivo
        String line = "";

        logger.info("INICIO");

        /*try {

            Map<String, Object> map = new ObjectMapper().readValue(new File(path),
                    new TypeReference<HashMap<String, Object>>() {});

            final List<ModelEntity> list = modelRepository.findAll();

            final List<Map> data = (List<Map>) map.get("data");

            data.forEach(d -> {

                final TrimEntity trim = new TrimEntity();
                trim.setModel(list.stream().filter(brand -> brand.getName().equals(d.get("model"))).findFirst().orElse(null));

                trim.setName(d.get("name").toString().trim());
                try {
                    if (d.get("year_from") != null)
                        trim.setYearFrom(!StringUtils.isAllEmpty(d.get("year_from").toString().trim()) ? Integer.parseInt(d.get("year_from").toString()) : null);
                }catch (Exception e){
                    trim.setYearFrom(null);
                }
                try {
                    if (d.get("year_to") != null)
                        trim.setYearTo( !StringUtils.isAllEmpty(d.get("year_to").toString().trim()) ? Integer.parseInt(d.get("year_to").toString()) : null);
                }catch (Exception e){
                    trim.setYearTo(null);
                }

                try{
                    if (d.get("torque") != null)
                        trim.setTorque(!StringUtils.isAllEmpty(d.get("torque").toString().trim()) ? Float.parseFloat(d.get("torque").toString().trim()) : null);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                if (d.get("code_a") != null)
                    trim.setCodaA(d.get("code_a").toString().trim());


                if (d.get("weight") != null)
                    trim.setWeight(!StringUtils.isAllEmpty(d.get("weight").toString().trim()) ? Float.parseFloat(d.get("weight").toString().trim()) : null);

                if (d.get("engine_hp") != null)
                    trim.setEngineHp(!StringUtils.isAllEmpty(d.get("engine_hp").toString().trim()) ? Float.parseFloat(d.get("engine_hp").toString().trim()) : null);

                if (d.get("traction") != null)
                    trim.setTraction(tractionTypeConverter.categorizeTraction(d.get("traction").toString().trim()));

                if (d.get("qt_doors") != null)
                    trim.setQtoors(!StringUtils.isAllEmpty(d.get("qt_doors").toString().trim()) ? Short.parseShort(d.get("qt_doors").toString().trim()) : null);

                if (d.get("seats") != null)
                    trim.setSeats(!StringUtils.isAllEmpty(d.get("seats").toString().trim()) ? Short.parseShort(d.get("seats").toString().trim()) : null);


                if (d.get("body_type") != null)
                    trim.setBodyType(bodyTypeConverter.categorizeBodyType(Integer.parseInt(d.get("body_type").toString().trim())));


                if (d.get("transmission") != null){
                    trim.setTransmissionType(transmissionConverter.fromString(d.get("transmission").toString().trim()));
                }

                if (d.get("fuel") != null){
                    trim.setTransmissionType(transmissionConverter.fromString(d.get("fuel").toString().trim()));
                }

                if (trim.getModel() != null)
                    trimRepository.save(trim);
            });

            System.out.println("FIM");

        } catch (Exception e) {
            e.printStackTrace();
        }*/


        /*try {

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Map<String, Object> map = null;

            try (InputStream inputStream = classLoader.getResourceAsStream("trims_marca_modelo_versao_202401261043.json");
                 InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {

                map = new ObjectMapper().readValue(reader,
                        new TypeReference<HashMap<String, Object>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }


            final List<TrimEntity> list = trimRepository.findAll();

            final List<Map> data = (List<Map>) map.get("data");

            list.forEach(trim -> {

                if (trim.getTransmissionType() != null && trim.getTransmissionType() == TransmissionType.NONE){

                    var dt = data.stream().filter(d -> d.get("name").equals(trim.getName()) && d.get("year_from").toString().equals(trim.getYearFrom().toString()) && d.get("year_to").toString().equals(trim.getYearTo().toString())).findFirst().orElse(new HashMap());
                    trim.setTransmissionType(transmissionFinder.fromString(dt.get("transmission").toString().trim()));

                    if (trim.getTransmissionType() == TransmissionType.NONE || trim.getTransmissionType() == null)
                        trim.setTransmissionType(transmissionFinder.fromString(trim.getName().trim()));
                }
            });

            logger.info("Salvando");
            trimRepository.saveAll(list);

            logger.info("FIM");

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }


}
