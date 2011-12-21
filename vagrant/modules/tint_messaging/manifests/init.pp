class tint_messaging(tint_messaging_enabled = "true") {
  include tint_config
  include tint_messaging::params
  
  file { "$tint_messaging::params::tint_messaging_config_dir":
    owner => 'tomcat6',
    mode => '700',
    ensure => 'directory',
  }

  file { "$tint_messaging::params::tint_messaging_config_dir/deploy.properties":
    owner => 'tomcat6',
    mode => '700',
    content => template('tint_messaging/deploy.properties.erb'),
    require => Package['tomcat6-user']
  }

}

