package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.ModelRepository;
import br.com.motur.dealbackendservice.core.dataproviders.repository.TrimRepository;
import br.com.motur.dealbackendservice.core.model.ModelEntity;
import br.com.motur.dealbackendservice.core.model.TrimEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class ModelService {

    private final ModelRepository modelRepository;
    private final BrandService brandService;

    private final TrimRepository trimRepository;

    @Autowired
    public ModelService(ModelRepository modelRepository, BrandService brandService, TrimRepository trimRepository) {
        this.modelRepository = modelRepository;
        this.brandService = brandService;
        this.trimRepository = trimRepository;
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


        /*String path = "C:\\Users\\Eduardo\\Desktop\\trims__select_m2_nome_model_v_nome_name_v_anoinicial_year_from_v_anofi_202401121756.csv"; // Substitua pelo caminho do seu arquivo
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            final List<ModelEntity> list = modelRepository.findAll();

            int i = 0;
            while ((line = br.readLine()) != null) {
                // Usando vírgula como separador
                if (i > 0){

                    String[] values = line.trim().replace("\"", "").split(",");
                    final TrimEntity trim = new TrimEntity();
                    trim.setModel(list.stream().filter(brand -> brand.getName().equals(values[0])).findFirst().orElse(null));
                    trim.setName(values[1]);
                    try {
                        trim.setYearFrom(!StringUtils.isAllEmpty(values[2]) ? Integer.parseInt(values[2]) : null);
                    }catch (Exception e){
                        trim.setYearFrom(null);
                    }
                    try {
                        trim.setYearTo(!StringUtils.isAllEmpty(values[3]) ? Integer.parseInt(values[3]) : null);
                    }catch (Exception e){
                        trim.setYearTo(null);
                    }
                    trim.setTorque(!StringUtils.isAllEmpty(values[4]) ? Float.parseFloat(values[4]) : null);
                    trim.setCodaA(values[5]);
                    trim.setWeight(!StringUtils.isAllEmpty(values[6]) ? Float.parseFloat(values[6]) : null);
                    trim.setEngineHp(!StringUtils.isAllEmpty(values[7]) ? Integer.parseInt(values[7]) : null);
                    trim.setTraction(values[8]);
                    trim.setQtoors(!StringUtils.isAllEmpty(values[9]) ? Short.parseShort(values[9]) : null);

                    if (trim.getModel() != null)
                        trimRepository.save(trim);
                }

                i++;
            }

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }
}
