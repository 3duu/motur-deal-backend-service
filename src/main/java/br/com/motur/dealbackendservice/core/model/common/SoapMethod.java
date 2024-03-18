package br.com.motur.dealbackendservice.core.model.common;

import br.com.motur.dealbackendservice.core.model.ProviderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.xml.namespace.QName;
import java.io.Serializable;

@AllArgsConstructor
@Data
public class SoapMethod implements Serializable {

    private ProviderEntity provider;
    private String operationName;
    private QName qName;

}
