/*
 */

#include "contiki.h"
#include <stdio.h>		/* For printf() */
#include "net/uip.h"
#include "net/uip-ds6.h"
#include "net/uip-debug.h"
#include "sys/etimer.h"
#include "simple-udp.h"

#define UDP_PORT 5555

/*---------------------------------------------------------------------------*/

PROCESS(sender_process, "Sender ip process");
AUTOSTART_PROCESSES(&sender_process);

/*---------------------------------------------------------------------------*/
PROCESS_THREAD(sender_process, ev, data)
{

  PROCESS_BEGIN();
  static uip_ipaddr_t ipaddr, unicastaddr;
  int i;
  uint8_t state;
  static struct etimer timer;
  static struct simple_udp_connection connection;
  static char *packet = "Supertest";

  uip_ip6addr(&ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0);
  uip_ds6_set_addr_iid(&ipaddr, &uip_lladdr);
  uip_ds6_addr_add(&ipaddr, 0, ADDR_AUTOCONF); 

  uip_ip6addr(&unicastaddr, 0xfe80, 0, 0, 0, 0xc30c, 0, 0, 2);

  simple_udp_register(&connection, UDP_PORT, NULL, UDP_PORT, NULL);

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
  etimer_set(&timer, CLOCK_SECOND);
  while(1) {
    printf("STO MANDANDO UNICAST\n");
    simple_udp_sendto(&connection, packet, strlen(packet)+1, &unicastaddr);
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    etimer_restart(&timer);
  }
  PROCESS_END();
}

/*---------------------------------------------------------------------------*/
