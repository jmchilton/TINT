class tint_storage {
  include mongodb
  include tint_storage::params
  include tint_storage::truststore
  
  file { "$tint_storage::params::tint_storage_config_dir":
    owner => 'tomcat6',
    mode => '700',
    ensure => directory,	
    require => Class["tint_config"],
  }

  file { "$tint_storage::params::tint_storage_config_dir/deploy.properties":
    owner => 'tomcat6',
    mode => '700',
    content => template('tint_storage/deploy.properties.erb'),
    require => Package['tomcat6-user']
  }

  file { "$tint_config::params::tint_config_dir/tint_storage_service.jks":
    owner => 'tomcat6',
    mode => '700',
    source => 'puppet:///modules/tint_storage/service.jks',
    require => Package['tomcat6-user']
  }

}

