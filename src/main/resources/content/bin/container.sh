#! /bin/sh
#

LD_LIBRARY_PATH=$LD_LIBRARY_PATH:${SERVER_RUNTIME_DIR}/lib
export LD_LIBRARY_PATH

# cd ${DISTRIBUTION_GRIDLIB_DIR}/apache2/bin
# cd ${SERVER_RUNTIME_DIR}/bin

if [ ! -d ${SERVER_RUNTIME_DIR}/logs ]
then
	mkdir ${SERVER_RUNTIME_DIR}/logs
fi

if [ $1 = start ]
then
	${SERVER_RUNTIME_DIR}/bin/httpd -E ${SERVER_RUNTIME_DIR}/logs/startup_error.log -f ${SERVER_RUNTIME_DIR}/conf/httpd.conf -k start
fi

if [ $1 = stop ]
then
	${SERVER_RUNTIME_DIR}/bin/httpd -f ${SERVER_RUNTIME_DIR}/conf/httpd.conf -k stop
fi
