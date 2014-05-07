all: chmod receiver sender

chmod: 
	sudo chmod 777 /dev/ttyUSB*

CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
CFLAGS += -DUIP_CONF_IPV6_RPL -DRPL_CONF_STATS=1

CONTIKI=/home/user/contiki-2.6

WITH_UIP6=1
UIP_CONF_IPV6=1
CFLAGS+= -DUIP_CONF_IPV6=1 -DWITH_UIP6=1

include $(CONTIKI)/Makefile.include
