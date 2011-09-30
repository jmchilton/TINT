package edu.umn.msi.tropix.ssh;

import java.io.File;

import javax.inject.Inject;

import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.keyprovider.PEMGeneratorHostKeyProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KeyPairProviderComponent {
  private String conifgDir;

  @Inject
  public KeyPairProviderComponent(@Value("${tropix.ssh.config.dir}") final String configDir) {
    this.conifgDir = configDir;
  }
  
  @Bean
  public KeyPairProvider getKeyPairProvider() {
    final File hostkeyFile = new File(conifgDir, "hostkey.pem");
    final KeyPairProvider provider;
    if(hostkeyFile.exists()) {
      final FileKeyPairProvider key = new org.apache.sshd.common.keyprovider.FileKeyPairProvider();
      key.setFiles(new String[] {hostkeyFile.getAbsolutePath()});
      provider = key;
    } else {
      provider = new PEMGeneratorHostKeyProvider(hostkeyFile.getAbsolutePath());
    }
    return provider;
  }
   
  
}
