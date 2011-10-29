package edu.umn.msi.tropix.client.directory.impl;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.client.directory.GridUser;

public class GridUserFunctionImplTest {
  private List<GridUser> users = Lists.newArrayList();
  private GridUser cachedGridUser, uncachedGridUser;
  private GridUserFunctionImpl gridUserFunction;

  @BeforeTest(groups = "unit")
  public void init() {
    final Map<String, GridUser> map = Maps.newHashMap();
    cachedGridUser = new GridUser();
    map.put("FOO", cachedGridUser);

    uncachedGridUser = new GridUser();
    uncachedGridUser.setGridId("BAR");

    final GridUser otherUser = new GridUser();
    otherUser.setGridId("FOO");

    users.clear();
    users.add(otherUser);
    users.add(uncachedGridUser);
    gridUserFunction = new GridUserFunctionImpl(users, map);
  }

  @Test(groups = "unit")
  public void testHitsMapFirst() {
    assert gridUserFunction.apply("FOO") == cachedGridUser;
  }

  @Test(groups = "unit")
  public void testSearchesIterable() {
    assert gridUserFunction.apply("BAR").equals(uncachedGridUser);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testErrosOnUnfound() {
    gridUserFunction.apply("OTHER");
  }

}
