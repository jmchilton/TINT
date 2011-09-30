package edu.umn.msi.tropix.proteomics.client;

import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.spring.TypedBeanProcessor;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;

@ManagedBean
class IdentificationParametersSerializerImpl extends TypedBeanProcessor implements IdentificationParametersSerializer,
    IdentificationParameterExpander, IdentificationParametersDeserializer {

  IdentificationParametersSerializerImpl() {
    super(IdentificationParametersTypeSerializer.class);
  }

  public <T> T loadParameters(final IdentificationType paramType, final InputContext parameterContext, final Class<T> parameterClass) {
    @SuppressWarnings("unchecked")
    final T parameters = (T) getTypeSerializer(paramType).loadRawParameters(parameterContext);
    return parameters;
  }

  public Map<String, String> loadParameterMap(final IdentificationType paramType, final InputContext parameterDownloadContext) {
    return getTypeSerializer(paramType).loadParametersAndExpandMap(parameterDownloadContext);
  }

  public String serializeParameters(final IdentificationType parameterType, final Map<String, String> parametersMap) {
    return getTypeSerializer(parameterType).serializeParameterMap(parametersMap);
  }

  private final List<IdentificationParametersTypeSerializer> typeSerializers = Lists.newArrayList();

  private IdentificationParametersTypeSerializer getTypeSerializer(final IdentificationType identificationType) {
    IdentificationParametersTypeSerializer serializer = null;
    for(final IdentificationParametersTypeSerializer querySerializer : typeSerializers) {
      if(querySerializer.getIdentificationType().equals(identificationType)) {
        serializer = querySerializer;
        break;
      }
    }
    return serializer;
  }

  protected void processBeans(final Iterable<Object> typeSerializers) {
    for(Object typeSerializer : typeSerializers) {
      this.typeSerializers.add((IdentificationParametersTypeSerializer) typeSerializer);
    }
  }

}