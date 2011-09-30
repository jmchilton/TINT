package edu.umn.msi.tropix.common.jobqueue.cloud;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

@Named("computeServiceSupplier")
public class ComputeServiceSupplierImpl implements Supplier<ComputeService> {
  @Resource
  private AccessKey ec2AccessKey;

  public ComputeService get() {
    final ComputeServiceContext context = new ComputeServiceContextFactory().createContext(
        "aws-ec2",
        ec2AccessKey.getAccessKey(),
        ec2AccessKey.getSecretAccessKey(),
        ImmutableSet.of(new JschSshClientModule()));
    return context.getComputeService();
  }

}
