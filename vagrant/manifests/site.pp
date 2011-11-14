$mysql_root_password = 'tintmysqlpasschangeme'

$tomcat_port = 8080
$tomcat_password = 'tinttomcatpasschangeme'

group { "puppet":
  ensure => "present"
}

class security {

  file { "/etc/security/limits.conf":
    owner => 'root',
    content => '*                         soft  nofile  16384\n*                         hard  nofile  65536'
  }

}

class tint_base {
  include security
  include aptitude
}

class tint_metadata_server {
  include tint_base
  include tint_metadata
  include mysql
}

class tint_ssh_server {
  include tint_storage
  include tint_metadata
  include rsyslog
  include sshguard  

  tomcat::deployment { 'tint-ssh' :
    path => '/tint/projects/TropixSshServer/build/wars/tint-ssh.war'
  }

}

class tint_webapp {
  include tint_base
  include tint_metadata

  tomcat::deployment { 'tint' :
    path => '/tint/projects/TropixWebGui/build/wars/tint.war'
  }

}

node "sshserver" {
  include tint_metadata_server
  include tint_ssh_server
}

node "webapp" {
  include tint_metadata_server
  include tint_webapp

}
