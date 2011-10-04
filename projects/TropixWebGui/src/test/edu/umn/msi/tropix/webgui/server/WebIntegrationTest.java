package edu.umn.msi.tropix.webgui.server;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import net.jmchilton.concurrent.CountDownLatch;

import org.gwtwidgets.server.spring.ServletUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.TropixObjectDescription;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.FileTypeService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ModelStorageDataFactory;
import edu.umn.msi.tropix.webgui.server.messages.MessageManager;
import edu.umn.msi.tropix.webgui.server.security.LoginHandler;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;
import edu.umn.msi.tropix.webgui.services.message.Message;
import edu.umn.msi.tropix.webgui.services.message.ObjectAddedMessage;
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;
import edu.umn.msi.tropix.webgui.services.object.FolderService;
import edu.umn.msi.tropix.webgui.services.object.ObjectService;
import edu.umn.msi.tropix.webgui.services.session.LoginService;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

@ContextConfiguration("classpath:/WEB-INF/applicationContext.xml")
public class WebIntegrationTest extends FreshConfigTest {
  private static final String TEST_STORAGE_SERVICE_URL = "local://";
  private MockHttpSession session = new MockHttpSession();
  private MockHttpServletRequest request = new MockHttpServletRequest();

  @Inject
  private LoginHandler loginHandler;

  @Inject
  private LoginService loginService;

  @Inject
  private MessageManager<Message> messageManager;

  @Inject
  private JobSubmitService submitService;

  @Inject
  private TropixObjectService tropixObjectService;

  @Inject
  private FolderService folderService;

  @Inject
  private ObjectService objectService;

  @Inject
  private ModelStorageDataFactory storageDataFactory;

  @Inject
  private UserSession userSession;

  @Inject
  private FileTypeService fileTypeService;

  protected ModelStorageDataFactory getStorageDataFactory() {
    return storageDataFactory;
  }

  protected TropixObjectService getTropixObjectService() {
    return tropixObjectService;
  }

  protected void assertActivityCompleteNormally(final ActivityDescription activityDescription) {
    assertJobCompleteNormally(activityDescription.getJobDescription());
  }

  protected void assertJobCompleteNormally(final JobDescription jobDescription) {
    final ProgressMessage progressMessage = waitForJobComplete(jobDescription);
    assert progressMessage.getJobStatus() == ProgressMessage.JOB_COMPLETE;
  }
  
  protected InputContext getDownloadContextForObjectId(final String objectId) {
    return getStorageDataForObjectId(objectId).getDownloadContext();
  }
  
  protected InputContext getDownloadContextForFileId(final String fileId) {
    return getStorageDataForFileId(fileId).getDownloadContext();
  }
  
  protected ModelStorageData getStorageDataForFileId(final String fileId) {
    return storageDataFactory.getStorageData(fileId, TEST_STORAGE_SERVICE_URL, userSession.getProxy());
  }
  
  protected ModelStorageData getStorageDataForObjectId(final String objectId) {
    final TropixFile file = (TropixFile) tropixObjectService.load(getUserGridId(), objectId, TropixObjectTypeEnum.FILE);
    return storageDataFactory.getStorageData(file, userSession.getProxy());
  }
  
  protected ModelStorageData newPersistedStorageData(final String name) {
    return newPersistedStorageData(name, null);
  }

  protected ModelStorageData newPersistedStorageData(final String name, final String extension) {
    final TropixFile file = new TropixFile();
    file.setName(name);
    file.setCommitted(true);
    file.setFileId(UUID.randomUUID().toString()); // NOT THE RIGHT WAY TO DO THIS
    file.setStorageServiceUrl(TEST_STORAGE_SERVICE_URL);
    final TropixFile result = tropixObjectService.createFile(userSession.getGridId(), getUserHomeFolderId(), file,
                fileTypeService.loadPrimaryFileTypeWithExtension(userSession.getGridId(), ".mzxml").getId());
    return storageDataFactory.getStorageData(result, userSession.getProxy());    
  }

  private List<String> addedObjectIds = Lists.newArrayList();
  private AtomicBoolean continueReadingMessages;
  private CountDownLatch finishedReadingMessagesLatch;
  private ConcurrentMap<String, ProgressMessage> finishedJobs;
  private SecurityContext securityContext;
  private User user;

  @SuppressWarnings("unchecked")
  protected static <T extends ActivityDescription> T getActivityDescriptionOfType(final Class<T> clazz,
      final Iterable<ActivityDescription> descriptions) {
    return (T) Iterables.find(descriptions, Predicates.instanceOf(clazz));
  }
  
  @SuppressWarnings("unchecked")
  protected static <T extends ActivityDescription> T getActivityDescriptionOfTypeWithNamePrefix(final Class<T> clazz,
      final Iterable<ActivityDescription> descriptions, final String prefix) {
    return (T) Iterables.find(descriptions, Predicates.and(Predicates.instanceOf(clazz), new Predicate<ActivityDescription>() {
      public boolean apply(final ActivityDescription input) {
        if(input instanceof TropixObjectDescription) {
          return ((TropixObjectDescription) input).getName().startsWith(prefix);
        }
        return false;
      }
    }));
  }
  
  

  @BeforeMethod(alwaysRun = true)
  public void init() {
    addedObjectIds.clear();
    user = null;
    request.setSession(session);
    securityContext = null;
    finishedJobs = new ConcurrentHashMap<String, ProgressMessage>();
  }

  protected void submit(final Set<ActivityDescription> activityDescriptions) {
    submitService.submit(activityDescriptions);
  }

  protected void finishMessageProcessing() {
    continueReadingMessages.set(false);
    finishedReadingMessagesLatch.await();
  }

  protected void launchMessageProcessingThread() {
    continueReadingMessages = new AtomicBoolean(true);
    finishedReadingMessagesLatch = new CountDownLatch(1);
    final CountDownLatch startUpLatch = new CountDownLatch(1);
    new Thread(new Runnable() {
      public void run() {
        initThisThread();
        startUpLatch.countDown();
        readMessages();
        finishedReadingMessagesLatch.countDown();
      }
    }).start();
    startUpLatch.await();
  }

  private void readMessages() {
    while(continueReadingMessages.get()) {
      final Collection<Message> messages = messageManager.drainQueue();
      for(final Message message : messages) {
        handleMessage(message);
      }
    }
    finishedReadingMessagesLatch.countDown();
  }

  private void handleMessage(final Message message) {
    System.out.println("Handling message " + message);
    if(message instanceof ProgressMessage) {
      final ProgressMessage progressMessage = (ProgressMessage) message;
      if(progressMessage.getJobStatus() != ProgressMessage.JOB_RUNNING) {
        System.out.println("Finishing job with message " + progressMessage.getName());
        finishedJobs.put(progressMessage.getName(), progressMessage);
      }
    } else if(message instanceof ObjectAddedMessage) {
      addedObjectIds.add(((ObjectAddedMessage) message).getObjectId());
    }
  }

  protected JobDescription newJobDescription() {
    final JobDescription description = new JobDescription();
    description.setName(UUID.randomUUID().toString());
    return description;
  }

  protected ProgressMessage waitForJobComplete(final JobDescription jobDescription) {
    return waitForJobComplete(jobDescription.getName());
  }

  private ProgressMessage waitForJobComplete(final String jobName) {
    while(!finishedJobs.containsKey(jobName)) {
      try {
        Thread.sleep(10);
      } catch(InterruptedException e) {
        e.printStackTrace();
      }
    }
    return finishedJobs.get(jobName);
  }

  protected void initThisThread() {
    ServletUtils.setRequest(request);
    if(securityContext != null) {
      SecurityContextHolder.setContext(securityContext);
    }
  }

  protected void logon() {
    initThisThread();
    loginHandler.loginAttempt("admin", "admin", "Local");
    securityContext = SecurityContextHolder.getContext();
    user = getSessionInfo().getUser();
  }

  protected String getUserGridId() {
    return user.getCagridId();
  }

  protected SessionInfo getSessionInfo() {
    return loginService.getUserIfLoggedIn().getSessionInfo();
  }

  protected User getUser() {
    return user;
  }

  protected TropixObject loadLastAddedObject() {
    return loadObject(addedObjectIds.get(addedObjectIds.size() - 1));
  }

  protected TropixObject loadObject(final String objectId) {
    return tropixObjectService.load(user.getCagridId(), objectId);
  }

  protected String getUserHomeFolderId() {
    return user.getHomeFolder().getId();
  }

  protected Iterable<TropixObject> loadHomeFolderContents() {
    return folderService.getFolderContents(getUserHomeFolderId(), null);
  }


}
