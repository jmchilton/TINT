package edu.umn.msi.tropix.persistence.service;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.TropixObject;

public class CachedTropixObjectPathLoader implements TropixObjectPathLoader {
  private static final Function<String, String> ENCODE = new Function<String, String>() {
    public String apply(String input) {
      return StringEscapeUtils.escapeXml(input);
    }
  };
  private static final Joiner JOINER = Joiner.on(">");
  private Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(10000).build();
  private TropixObjectService tropixObjectService;

  @Inject
  public CachedTropixObjectPathLoader(final TropixObjectService tropixObjectService) {
    this.tropixObjectService = tropixObjectService;
  }
  
  public TropixObject getPath(String identity, String[] pathParts) {
    final List<String> encodedPathParts = Lists.transform(Arrays.asList(pathParts), ENCODE);
    TropixObject object = attemptCacheLoad(identity, pathParts, encodedPathParts);
    if(object == null) {
      object = tropixObjectService.getPath(identity, pathParts);
    }
    cacheEncoded(identity, encodedPathParts, object);
    return object;
  }
  
  public void cache(final String identity, final List<String> pathParts, final TropixObject object) {
    final List<String> encodedPathParts = Lists.transform(pathParts, ENCODE);
    cacheEncoded(identity, encodedPathParts, object);    
  }
  
  public void cache(final String identity, final String[] pathParts, final TropixObject object) {
    cache(identity, Arrays.asList(pathParts), object);
  }
  
  private void cacheEncoded(final String identity, final List<String> encodedPathParts, final TropixObject object) {
    if(object != null) {
      final String hash = encode(identity, encodedPathParts);
      cache.put(hash, object.getId());
    }
  }
  
  private TropixObject checkObject(final TropixObject object) {
    // Is this needed?
    if(Boolean.TRUE != object.getCommitted() || object.getDeletedTime() != null) {
      return null;
    } else {
      return object;      
    }
  }
  
  private TropixObject attemptCacheLoad(final String identity, String[] pathParts, final List<String> encodedPathParts) {
    int pathSize = encodedPathParts.size();
    TropixObject object = null;
    if(pathSize > 1) {
      final String encodedParent = encode(identity, encodedPathParts.subList(0, pathSize - 1));
      final String parentId = cache.getIfPresent(encodedParent);
      if(parentId != null) {
        try {
          object = checkObject(tropixObjectService.getChild(identity, parentId, pathParts[pathParts.length - 1]));
        } catch(RuntimeException e) {
          object = null;
        }
      }
    }
    return object;
  }

  private String encode(final String identity, List<String> encodedParts) {
    return identity + ">" + JOINER.join(encodedParts);
    
  }
  
}
