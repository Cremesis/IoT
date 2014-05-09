/*
 */

#include "contiki.h"
#include <stdio.h>		/* For printf() */
//#include "net/uip.h"
//#include "net/uip-ds6.h"
//#include "net/uip-debug.h"
#include "sys/etimer.h"
//#include "simple-udp.h"
#include "dev/leds.h"
#include "dev/button-sensor.h"
//#include "net/rpl/rpl.h"

#include "contiki-net.h"
#include "er-coap-13-engine.h"
#include <string.h>

/*---------------------------------------------------------------------------*/

PROCESS(coap_client_process, "CoAP client process");
PROCESS(reset_process, "Reset process");
AUTOSTART_PROCESSES(&reset_process, &coap_client_process);

/*---------------------------------------------------------------------------*/

void client_observing_handler(void *response) {
  const uint8_t *chunk;
  printf("Ho la risposta OSSERVATA!!\n");
  int len = coap_get_payload(response, &chunk);
  int status = ((coap_packet_t*)response)->code;
  printf("Reply: %d %.*s\n", status, len, (char*) chunk);
}

/*---------------------------------------------------------------------------*/

void client_put_handler(void *response) {
  const uint8_t *chunk;
  printf("Ho la risposta dalla PUT!!!!\n");
  int len = coap_get_payload(response, &chunk);
  int status = ((coap_packet_t*)response)->code;
  printf("Reply: %d %.*s\n", status, len, (char*) chunk);
}

/*---------------------------------------------------------------------------*/

PROCESS_THREAD(coap_client_process, ev, data)
{

  PROCESS_BEGIN();
  static uip_ipaddr_t ipaddr;
  int i;
  uint8_t state;
  static struct etimer timer;
  
  uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
  uip_ds6_set_addr_iid(&ipaddr, &uip_lladdr);
  uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF); 

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

  coap_receiver_init();

  printf("CoAP client started\n");

  etimer_set(&timer, CLOCK_SECOND >> 1);
  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    etimer_restart(&timer);
    leds_toggle(LEDS_BLUE);
  }
  PROCESS_END();
}

PROCESS_THREAD(reset_process, ev, data)
{
  PROCESS_BEGIN();

  static uip_ipaddr_t server_ipaddr;
  static coap_packet_t request[2];
  static const char* service_url = "hello";
  uip_ip6addr(&server_ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 1);
  static int registered = 0;

  printf("Button thread started\n");
  SENSORS_ACTIVATE(button_sensor);
  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(ev == sensors_event && data == &button_sensor);
    if(!registered) {
      printf("MI REGISTRO!\n");
      uip_ip6addr(&server_ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 1);
      coap_init_message(&request[0], COAP_TYPE_CON, COAP_GET, 0);
      coap_set_header_uri_path(&request[0], service_url);
      coap_set_header_observe(&request[0], 1);
      COAP_BLOCKING_REQUEST(&server_ipaddr, REMOTE_PORT, &request[0], client_observing_handler);
      registered = 1;
      printf("MI SONO REGISTRATO!\n");
    } else  {
      printf("*** Ma ciao! FACCIO PUT\n");
      coap_init_message(&request[1], COAP_TYPE_CON, COAP_PUT, 0);
      coap_set_header_uri_path(&request[1], service_url);
      coap_set_header_uri_query(&request[1], "numero=12");
      COAP_BLOCKING_REQUEST(&server_ipaddr, REMOTE_PORT, &request[1], client_put_handler);
      printf("*** Ho finito di FARE PUT!\n");
    }
  }
  PROCESS_END();
}

/*---------------------------------------------------------------------------*/
