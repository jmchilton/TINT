class tomcat {

  notice("Establishing http://$hostname:$tomcat_port/")

  Package { # defaults
    ensure => installed,
  }

  package { 'tomcat6':
  }

  package { 'tomcat6-user':
    require => Package['tomcat6'],
  }
 
  package { 'tomcat6-admin':
    require => Package['tomcat6'],
  }

  file { "/etc/tomcat6/tomcat-users.xml":
    owner => 'root',
    require => Package['tomcat6'],
    notify => Service['tomcat6'],
    content => template('tomcat/tomcat-users.xml.erb')
  }

  file { "/usr/share/tomcat6/":
    owner => 'tomcat6',
    mode => '700'
  }

  file { '/etc/tomcat6/server.xml':
     owner => 'root',
     require => Package['tomcat6'],
     notify => Service['tomcat6'],
     content => template('tomcat/server.xml.erb'),
  }

  file { '/etc/tomcat6/context.xml':
     owner => 'root',
     require => Package['tomcat6'],
     notify => Service['tomcat6'],
     content => template('tomcat/context.xml.erb'),
  }

  service { 'tomcat6':
    ensure => running,
    require => Package['tomcat6'],
  }   

}

define tomcat::deployment($path) {

  include tomcat
  notice("Establishing http://$hostname:${tomcat::tomcat_port}/$name/")

  file { "/var/lib/tomcat6/webapps/${name}.war":
    owner => 'root',
    source => $path,
  }

}

