package edu.umn.msi.tropix.client.directory.impl;

import java.util.Map;

import org.python.google.common.base.Preconditions;

import com.google.common.base.Function;
import com.google.common.base.Objects;

import edu.umn.msi.tropix.client.directory.GridUser;

public class GridUserFunctionImpl implements Function<String, GridUser> {
  private Iterable<GridUser> usersIterable;
  private Map<String, GridUser> cachedUsersMap;

  public GridUserFunctionImpl(final Iterable<GridUser> usersIterable,
      final Map<String, GridUser> cachedUsersMap) {
    this.usersIterable = usersIterable;
    this.cachedUsersMap = cachedUsersMap;
  }

  public GridUser apply(final String input) {
    GridUser gridUser = cachedUsersMap.get(input);
    if(gridUser == null) {
      for(final GridUser user : usersIterable) {
        if(Objects.equal(user.getGridId(), input)) {
          gridUser = user;
          break;
        }
      }
    }
    Preconditions.checkNotNull(gridUser, String.format("Could not find user with grid id [%s]", input));
    return gridUser;
  }

}
