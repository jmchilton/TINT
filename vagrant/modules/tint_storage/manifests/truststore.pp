class tint_storage::truststore {
  include tint_config
  
  file { "$tint_config::params::tint_config_dir/tint_storage_truststore.jks":
    owner => 'tomcat6',
    mode => '700',
    source => 'puppet:///modules/tint_storage/truststore.jks',
    require => Package['tomcat6-user']
  }

}

