package br.com.motur.dealbackendservice.core.entrypoints.v1.converter;


import br.com.motur.dealbackendservice.common.ValueObjectConverter;
import br.com.motur.dealbackendservice.core.entrypoints.v1.pojo.config.Provider;
import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import org.springframework.stereotype.Component;

@Component
public class ProviderConverter extends ValueObjectConverter<ProviderEntity, Provider> {

    @Override
    public Provider convert(ProviderEntity provider) {

        if (provider == null)
            return null;

        return new Provider(provider.getId(), provider.getName(), provider.getUrl(), provider.getApiType());
    }
}
