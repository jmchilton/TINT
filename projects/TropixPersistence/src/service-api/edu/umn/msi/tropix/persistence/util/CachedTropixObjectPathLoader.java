package edu.umn.msi.tropix.persistence.util;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class CachedTropixObjectPathLoader {
  private static final Log LOG = LogFactory.getLog(CachedTropixObjectPathLoader.class);
  private static final Function<String, String> ENCODE = new Function<String, String>() {
    public String apply(String input) {
      return StringEscapeUtils.escapeXml(input);
    }
  };
  private static final Joiner JOINER = Joiner.on(">");
  private Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).build();
  private TropixObjectService tropixObjectService;

  @Inject
  public CachedTropixObjectPathLoader(final TropixObjectService tropixObjectService) {
    this.tropixObjectService = tropixObjectService;
  }

  public TropixObject getPath(String identity, String[] pathParts) {
    return getPath(identity, Arrays.asList(pathParts));
  }

  public TropixObject getPath(String identity, List<String> pathParts) {
    final List<String> encodedPathParts = Lists.transform(pathParts, ENCODE);
    int pathSize = encodedPathParts.size();
    final String encodedParent = encode(identity, encodedPathParts.subList(0, pathSize - 1));
    final String encoded = String.format("%s>%s", encodedParent, encodedPathParts.get(pathSize - 1));
    TropixObject object = attemptCacheLoad(identity, encoded, encodedParent, pathParts);
    if(object == null) {
      object = tropixObjectService.getPath(identity, pathParts);
    }
    cacheEncoded(encoded, object);
    return object;
  }

  public void cache(final String identity, final List<String> pathParts, final TropixObject object) {
    final List<String> encodedPathParts = Lists.transform(pathParts, ENCODE);
    final String encoded = encode(identity, encodedPathParts);
    cacheEncoded(encoded, object);
  }

  public void cache(final String identity, final String[] pathParts, final TropixObject object) {
    cache(identity, Arrays.asList(pathParts), object);
  }

  private void cacheEncoded(final String encoded, final TropixObject object) {
    if(object != null) {
      cache.put(encoded, object.getId());
    }
  }

  private TropixObject checkObject(final TropixObject object) {
    // Is this needed? - Seems so, yes at least if tos.load() is used.
    if(object != null && Boolean.TRUE == object.getCommitted() && object.getDeletedTime() == null) {
      return object;
    } else {
      return null;
    }
  }

  private TropixObject attemptCacheLoad(final String identity, String encoded, final String encodedParent, final List<String> pathParts) {
    final int pathSize = pathParts.size();
    TropixObject object = null;
    final String objectId = cache.getIfPresent(encoded);
    if(objectId != null) {
      try {
        object = tropixObjectService.load(identity, objectId);
      } catch(RuntimeException e) {
        object = null;
      }
    }
    // This is really slow for absent parent I guess. Do not use getPath for checking?
    /*
     * if(object == null) {
     * if(pathSize > 1) {
     * final String parentId = cache.getIfPresent(encodedParent);
     * if(parentId != null) {
     * try {
     * object = tropixObjectService.getChild(identity, parentId, pathParts.get(pathSize - 1));
     * LOG.debug("Cache getChild returned non-null object?" + (object != null));
     * } catch(RuntimeException e) {
     * ExceptionUtils.logQuietly(LOG, e, "getChild threw exception");
     * object = null;
     * }
     * }
     * } else {
     * System.out.println("Object is NULL but no parent");
     * }
     * }
     */
    return checkObject(object);
  }

  private String encode(final String identity, List<String> encodedParts) {
    return identity + ">" + JOINER.join(encodedParts);
  }

}
