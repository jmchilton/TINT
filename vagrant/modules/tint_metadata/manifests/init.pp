class tint_metadata {

  notice("Setting up metadata")

  mysql::createuser { 'create_tint_user':
    db_user => 'tint',
    db_pass => 'qs3Dd!moc$'
  }

  mysql::createdb { 'create_metadata_db':
    db_name => 'tint_metadata',
    db_user => 'tint',
    db_pass => 'qs3Dd!moc$'
  }

  file { "/usr/share/tomcat6/.tropix/metadata/deploy.properties":
    owner => 'tomcat6',
    mode => '700',
    content => template('tint_metadata/deploy.properties.erb'),
    require => Package['tomcat6-user']
  }

}

