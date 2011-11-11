define mysql::createuser($db_user, $db_pass) {
    exec { "create-$db_name-db":
        # Only do this if the database doesn't already exist!
        # unless => "/usr/bin/mysql -uroot -p$mysql_root_password $db_name",
        command => "/usr/bin/mysql -uroot -p$mysql_root_password -e \"CREATE USER '$db_user'@'localhost' IDENTIFIED BY '$db_pass'; FLUSH PRIVILEGES;\"",
        require => Exec["set-mysql-root-password"],
    }
}
