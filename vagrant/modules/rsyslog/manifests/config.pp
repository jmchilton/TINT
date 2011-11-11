class rsyslog::config {
  file { $rsyslog::params::rsyslog_conf:
         source => "puppet:///modules/rsyslog/rsyslog.conf",
         ensure => file,
         owner => root,
         group => root,
         notify  => Class["rsyslog::service"],
  }
}
