class tint_config {
  include tint_config::params

  file { $tint_config::params::tint_config_dir :
    owner => 'tomcat6',
    mode => '700',
    require => Package['tomcat6-user']
  }

}
