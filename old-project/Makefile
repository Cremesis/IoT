all: chmod client

chmod: 
	sudo chmod 777 /dev/ttyUSB*

#CFLAGS += -DUIP_CONF_IPV6_RPL -DRPL_CONF_STATS=1

CONTIKI=/home/user/contiki-2.6
SMALL=1

WITH_UIP6=1
UIP_CONF_IPV6=1

WITH_COAP=13
REST=coap_rest_implementation

CFLAGS += -DUIP_CONF_IPV6=1
CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
CFLAGS += -DWITH_COAP=13
CFLAGS += -DREST=coap_rest_implementation
CFLAGS += -DUIP_CONF_TCP=0

APPS += er-coap-13
APPS += erbium

include $(CONTIKI)/Makefile.include
