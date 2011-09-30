/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.jobs.activities.descriptions;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;

@Entity
public class SubmitGalaxyDescription extends SubmitJobDescription {

  @Entity
  public static class PersistentRootInput implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;

    @OneToMany(mappedBy="rootInput", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Set<PersistentInput> inputs = new HashSet<PersistentInput>();
    
    public void setId(final String id) {
      this.id = id;
    }
    
    public String getId() {
      return id;
    }    
    
    public Set<PersistentInput> getInputs() {
      return inputs;
    }

    public void setInputs(final Set<PersistentInput> inputs) {
      this.inputs = inputs;
    }    
    
  }
  
  @Entity 
  public static class PersistentInput implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;

    @Nullable
    @ManyToOne(fetch=FetchType.EAGER, optional = true)
    private PersistentRootInput rootInput;

    @Nullable
    @ManyToOne(fetch=FetchType.EAGER, optional = true)
    private PersistentInput input;

    @OneToMany(mappedBy="input", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private Set<PersistentInput> inputs = new HashSet<PersistentInput>();

    private String name;

    public PersistentInput getInput() {
      return input;
    }

    public void setInput(final PersistentInput input) {
      this.input = input;
    }

    @Nullable
    public PersistentRootInput getRootInput() {
      return rootInput;
    }

    @Nullable
    public void setRootInput(final PersistentRootInput rootInput) {
      this.rootInput = rootInput;
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(final String value) {
      this.value = value;
    }

    @Column(length = 4096)
    private String value;
    
    public Set<PersistentInput> getInputs() {
      return inputs;
    }

    public void setInputs(final Set<PersistentInput> inputs) {
      this.inputs = inputs;
    }
    
    public void setId(final String id) {
      this.id = id;
    }
    
    public String getId() {
      return id;
    }
    
  }
  
  
  
  private String galaxyToolId;
  
  @Transient
  private RootInput input;
  
  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private PersistentRootInput persistentRootInput;
  
  protected PersistentRootInput getPersistentRootInput() {
    return persistentRootInput;
  }
  
  protected void setPersistentRootInput(final PersistentRootInput persistentRootInput) {
    this.persistentRootInput = persistentRootInput;
  }

  // Would just Iterables.addAll, but this is not available on GWT.
  private <T> void addAll(final Collection<T> collection, final Iterable<T> elementsToAdd) {
    for(T element : elementsToAdd) {
      collection.add(element);
    }
  }
  
  @PostLoad
  public void postLoad() {
    input = new RootInput();
    addAll(input.getInput(), convertFromPersistent(persistentRootInput.getInputs()));
  }

  @PrePersist
  public void prePersist() {
    this.persistentRootInput = new PersistentRootInput();
    Set<PersistentInput> pInputs = null;
    if(input.getInput() != null) {
      pInputs = new HashSet<PersistentInput>();
      for(final Input childInput : input.getInput()) {
        final PersistentInput pChildInput = convertToPersistent(childInput);
        pChildInput.setRootInput(persistentRootInput);
        pInputs.add(pChildInput);        
      }
    }
    persistentRootInput.setInputs(pInputs);
  }

  @Nullable
  private Set<Input> convertFromPersistent(@Nullable final Iterable<PersistentInput> pInputs) {
    Set<Input> inputs = null;
    if(pInputs != null) {
      inputs = new HashSet<Input>();
      for(final PersistentInput pInput : pInputs) {
        inputs.add(convertFromPersistent(pInput));
      }
    }
    return inputs;
  }

  private Input convertFromPersistent(final PersistentInput pInput) {
    final Input input = new Input();
    input.setName(pInput.getName());
    input.setValue(pInput.getValue());
    addAll(input.getInput(), convertFromPersistent(pInput.getInputs()));
    return input;
  }

  private PersistentInput convertToPersistent(final Input input) {
    final PersistentInput pInput = new PersistentInput();
    pInput.setName(input.getName());
    pInput.setValue(input.getValue());
    Set<PersistentInput> pInputs = null;
    if(input.getInput() != null) {
      pInputs = new HashSet<PersistentInput>();
      for(final Input childInput : input.getInput()) {
        final PersistentInput pChildInput = convertToPersistent(childInput);
        pChildInput.setInput(pInput);
        pInputs.add(pChildInput);        
      }
    }
    pInput.setInputs(pInputs);
    return pInput;
  }
  
  public String getGalaxyToolId() {
    return galaxyToolId;
  }

  public void setGalaxyToolId(final String galaxyToolId) {
    this.galaxyToolId = galaxyToolId;
  }
  
  public RootInput getInput() {
    return input;
  }
  
  public void setInput(final RootInput input) {
    this.input = input;
  }
  
}
