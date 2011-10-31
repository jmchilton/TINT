$mysql_root_password = 'tintmysqlpasschangeme'

$tomcat_port = 8080
$tomcat_password = 'tinttomcatpasschangeme'

group { "puppet":
  ensure => "present"
}

node "webapp" {
  include aptitude
  include mysql
  include rsyslog

  class { 'fail2ban::configure': 
             full_jails => ["[tintssh]enabled=true\nfilter=ssh\nport=all"] 
        }

  #fail2ban::filter { 'tintssh' : 
  #		       failregex => "^.*Failed (?:password|publickey) for .* from <HOST>(?: port \d*)?(?: tintssh\d*)?$" }
  
  tomcat::deployment { 'tint-ssh' :
    path => '/tint/projects/TropixSshServer/build/wars/tint-ssh.war'
  }

}
