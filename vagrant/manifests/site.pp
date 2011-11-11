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

node "webapp" {
  include security
  include aptitude
  include mysql
  include rsyslog
  include sshguard

  include tint_metadata

  tomcat::deployment { 'tint-ssh' :
    path => '/tint/projects/TropixSshServer/build/wars/tint-ssh.war'
  }

  tomcat::deployment { 'tint' :
    path => '/tint/projects/TropixWebGui/build/wars/tint.war'
  }

}
