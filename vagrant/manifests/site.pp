$mysql_root_password = 'tintmysqlpasschangeme'

$tomcat_port = 8080
$tomcat_password = 'tinttomcatpasschangeme'

group { "puppet":
  ensure => "present",
}

node "webapp" {
  include aptitude
  include mysql

  tomcat::deployment { "tint-ssh":
    path => '/tint/projects/TropixSshServer/build/wars/tint-ssh.war'
  }

}
