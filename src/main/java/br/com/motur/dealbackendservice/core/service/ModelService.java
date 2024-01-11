package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.core.dataproviders.repository.ModelRepository;
import br.com.motur.dealbackendservice.core.model.BrandEntity;
import br.com.motur.dealbackendservice.core.model.ModelEntity;
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

    @Autowired
    public ModelService(ModelRepository modelRepository, BrandService brandService) {
        this.modelRepository = modelRepository;
        this.brandService = brandService;
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

    }
}
