#!/bin/sh

server_ip="192.168.5.25"
server_application="/dashu/application/taskcenter"
server_log_path="/dashu/log/"

# 1. check sshpass cmd
if hash sshpass 2>null; then
	echo "" >  /dev/null 2>&1
else
  echo "install sshpass..."
	wget http://sourceforge.net/projects/sshpass/files/sshpass/1.06/sshpass-1.06.tar.gz
	tar xf sshpass-1.06.tar.gz
	cd sshpass-1.06
	./configure
	make && make install
	cd ../
	rm -rf sshpass-1.06.tar.gz
	rm -rf sshpass-1.06
fi

# 2.maven
#mvn clean package -DskipTests

# 3.delete old package
sshpass -p Dashu0701 ssh root@192.168.5.25 << EOF
    if [ ! -d "$server_application" ] ; then mkdir -p $server_application ;fi;
    cd $server_application
    rm -rf *.jar;
    exit;
EOF


# 4.upload
echo "upload web/target/taskcenter-web-1.0.0-SNAPSHOT.jar ..."
sshpass -p Dashu0701 scp  web/target/taskcenter-web-1.0.0-SNAPSHOT.jar root@$server_ip:$server_application
sshpass -p Dashu0701 scp  app.sh root@$server_ip:$server_application/

# start
echo "start app..."
sshpass -p Dashu0701 ssh  root@192.168.5.25 << EOF
	cd $server_application;
	sh app.sh start;
	exit;
EOF

#open new terminal
sshpass -p Dashu0701 ssh  root@192.168.5.25 << EOF
	tail -500f $server_log_path/taskcenter.log
EOF

