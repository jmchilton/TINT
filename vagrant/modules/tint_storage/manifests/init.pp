class tint_storage {
  include mongodb
  include tint_config
  include tint_storage::params
  
  file { "$tint_storage::params::tint_storage_config_dir":
    owner => 'tomcat6',
    mode => '700',
    ensure => directory,	
  }

  file { "$tint_storage::params::tint_storage_config_dir/deploy.properties":
    owner => 'tomcat6',
    mode => '700',
    content => template('tint_storage/deploy.properties.erb'),
    require => Package['tomcat6-user']
  }

}

