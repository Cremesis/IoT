/*
 */

#include "contiki.h"
#include <stdio.h>		/* For printf() */
#include "net/uip.h"
#include "net/uip-ds6.h"
#include "net/uip-debug.h"
#include "sys/etimer.h"
#include "simple-udp.h"
#include "dev/leds.h"
#include "dev/button-sensor.h"
#include "net/rpl/rpl.h"

#include "contiki-net.h"
#include "erbium.h"
#include "er-coap-13.h"
#include <string.h>

/*---------------------------------------------------------------------------*/

void resource1_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
	static int32_t value = 1;
  	leds_toggle(LEDS_RED);
	uint8_t length;
	uint8_t method = REST.get_method_type(request);
	if(method & METHOD_GET) {
		sprintf(buffer, "%ld", value);
		length = strlen(buffer)+1;
		REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
		REST.set_header_etag(response, (uint8_t *) &length, 1);
		REST.set_response_payload(response, buffer, length);
	} else if(method & METHOD_PUT) {
		const char *tmpbuf;
		REST.get_post_variable(request, "v", &tmpbuf);
		value = atoi(tmpbuf);
		REST.set_response_status(response, REST.status.CHANGED);
	} else
		REST.set_response_status(response, REST.status.BAD_REQUEST);
}

RESOURCE(resource1, METHOD_GET|METHOD_PUT, "hello", "title=\"Resource\";rt=\"Text\"");

PROCESS(coap_server_process, "CoAP server process");
PROCESS(reset_process, "Reset process");
AUTOSTART_PROCESSES(&reset_process, &coap_server_process);

/*---------------------------------------------------------------------------*/

PROCESS_THREAD(coap_server_process, ev, data)
{

  PROCESS_BEGIN();
  static uip_ipaddr_t ipaddr;
  rpl_dag_t *dag;
  int i;
  uint8_t state;
  static struct etimer timer;
  static int packet = 0;

  uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
  uip_ds6_set_addr_iid(&ipaddr, &uip_lladdr);
  uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF); 

  //uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
  //dag = rpl_set_root(RPL_DEFAULT_INSTANCE, (uip_ip6addr_t*) &ipaddr);
  //rpl_set_prefix(dag, &ipaddr, 64);

  rest_init_engine();
  rest_activate_resource(&resource_resource1);

  printf("IPv6 addresses: ");
  for(i = 0; i < UIP_DS6_ADDR_NB; i++) {
    state = uip_ds6_if.addr_list[i].state;
    if(uip_ds6_if.addr_list[i].isused &&
       (state == ADDR_TENTATIVE 
       || state == ADDR_PREFERRED)) {
            uip_debug_ipaddr_print(
               &uip_ds6_if.addr_list[i].ipaddr);
            printf("\n");
    }
  }
  printf("CoAP server started\n");
  etimer_set(&timer, CLOCK_SECOND >> 1);
  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    etimer_restart(&timer);
    leds_toggle(LEDS_GREEN);
  }
  PROCESS_END();
}

PROCESS_THREAD(reset_process, ev, data)
{

  PROCESS_BEGIN();

  printf("Reset started\n");
  SENSORS_ACTIVATE(button_sensor);
  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(ev == sensors_event && data == &button_sensor);
    printf("*** RPL ROOT\n");
    rpl_repair_root(RPL_DEFAULT_INSTANCE);
  }
  PROCESS_END();
}

/*---------------------------------------------------------------------------*/
