package edu.umn.msi.tropix.client.test;

import info.minnesotapartnership.tropix.directory.models.Person;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/client/directory/testContext.xml")
public class DirectoryInteractionTest extends AbstractTestNGSpringContextTests {

  @Resource
  private Supplier<Multimap<String, Person>> ldapPersonSupplier;

  @Test(groups = "interaction")
  public void testList() {
    Multimap<String, Person> persons = ldapPersonSupplier.get();

    System.out.println("Persons");
    int count = 0;
    for(Person person : persons.values()) {
      count++;
      if(person.getCagridIdentity().contains("chilton")) {
        System.out.println(person.getCagridIdentity());
      }
    }
    System.out.println("/Persons" + count);

  }

}
