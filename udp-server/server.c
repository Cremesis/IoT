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

#define UDP_PORT 5555

/*---------------------------------------------------------------------------*/

static void
receiver(struct simple_udp_connection *c,
         const uip_ipaddr_t *sender_addr,
         uint16_t sender_port,
         const uip_ipaddr_t *receiver_addr,
         uint16_t receiver_port,
         const uint8_t *data,
         uint16_t datalen) {
  printf("HO RICEVUTO QUALCOSA!\n");
  leds_toggle(LEDS_RED);
  simple_udp_sendto(c, "CIAO", 5, sender_addr);
}

PROCESS(udp_server_process, "UDP server process");
PROCESS(reset_process, "Reset process");
AUTOSTART_PROCESSES(&reset_process, &udp_server_process);

/*---------------------------------------------------------------------------*/

PROCESS_THREAD(udp_server_process, ev, data)
{

  PROCESS_BEGIN();
  static uip_ipaddr_t ipaddr;
  rpl_dag_t *dag;
  int i;
  uint8_t state;
  static struct etimer timer;
  static struct simple_udp_connection connection;
  static int packet = 0;

  uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
  uip_ds6_set_addr_iid(&ipaddr, &uip_lladdr);
  uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF); 

  //uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
  //dag = rpl_set_root(RPL_DEFAULT_INSTANCE, (uip_ip6addr_t*) &ipaddr);
  //rpl_set_prefix(dag, &ipaddr, 64);

  simple_udp_register(&connection, UDP_PORT-1, NULL, UDP_PORT, receiver);

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
  printf("UDP server started\n");
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
    printf("*** REPAIRING RPL ROOT\n");
    rpl_repair_root(RPL_DEFAULT_INSTANCE);
  }
  PROCESS_END();
}

/*---------------------------------------------------------------------------*/
