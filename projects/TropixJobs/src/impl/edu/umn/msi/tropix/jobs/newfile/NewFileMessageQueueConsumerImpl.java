package edu.umn.msi.tropix.jobs.newfile;

import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.camel.Consume;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.spring.AnnotatedBeanProcessor;
import edu.umn.msi.tropix.files.NewFileMessageQueue;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.TropixObjectLoaderService;

@ManagedBean
public class NewFileMessageQueueConsumerImpl extends AnnotatedBeanProcessor<ForExtension> implements NewFileMessageQueue {
  private Map<StockFileExtensionEnum, NewFileProcessor> fileProcessorMap = Maps.newHashMap();
  private TropixObjectLoaderService loader;

  @Inject
  public NewFileMessageQueueConsumerImpl(final TropixObjectLoaderService loader) {
    super(ForExtension.class);
    this.loader = loader;
  }

  protected void processBeans(final Iterable<Object> annotatedBeans) {
    for(Object annotatedBean : annotatedBeans) {
      final NewFileProcessor processor = (NewFileProcessor) annotatedBean;
      final ForExtension forExtension = getAnnotation(processor);
      for(StockFileExtensionEnum extension : forExtension.value()) {
        fileProcessorMap.put(extension, processor);
      }
    }
  }

  @Nullable
  private NewFileProcessor getNewFileProcessor(@Nullable final StockFileExtensionEnum extension) {
    NewFileProcessor processor = null;
    if(extension != null) {
      processor = fileProcessorMap.get(extension);
    }
    return processor;
  }

  @Consume(uri = NewFileMessageQueue.ROUTE)
  public void newFile(final NewFileMessage message) {
    final TropixFile savedFile = (TropixFile) loader.load(message.getCredential().getIdentity(), message.getObjectId(), TropixObjectTypeEnum.FILE);
    final StockFileExtensionEnum extension = StockFileExtensionEnum.loadForFile(savedFile);
    final NewFileProcessor newFileProcessor = getNewFileProcessor(extension);
    if(newFileProcessor != null) {
      newFileProcessor.processFile(message, savedFile);
    }
  }

}
