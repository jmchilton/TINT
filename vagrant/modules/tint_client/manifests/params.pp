class tint_client::params {
  include tint_config::params

  $tint_client_config_dir = "$tint_config::params::tint_config_dir/client"
}
