class tint_client {
  include tint_client::params
  
  file { "$tint_client::params::tint_client_config_dir":
    owner => 'tomcat6',
    mode => '700',
    ensure => directory,	
  }

  file { "$tint_client::params::tint_client_config_dir/deploy.properties":
    owner => 'tomcat6',
    mode => '700',
    content => template('tint_client/deploy.properties.erb'),
    require => Package['tomcat6-user']
  }

}

