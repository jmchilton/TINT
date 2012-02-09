class tint_metadata {
  include tint_metadata::params

  file { "$tint_metadata::params::tint_metadata_config_dir":
    owner => $tint_config::params::web_user,
    mode => '700',
    ensure => 'directory',
    require => Class["tint_config"]
  }

  mysql::createuser { 'create_tint_user':
    db_user => 'tint',
    db_pass => 'qs3Dd!moc$'
  }

  mysql::createdb { 'create_metadata_db':
    db_name => 'tint_metadata',
    db_user => 'tint',
    db_pass => 'qs3Dd!moc$'
  }

  file { "$tint_metadata::params::tint_metadata_config_dir/deploy.properties":
    owner => $tint_config::params::web_user,
    mode => '700',
    content => template('tint_metadata/deploy.properties.erb'),
    require => Package['tomcat6-user']
  }

}

