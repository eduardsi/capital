package net.sizovs.capital.adhoc

import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.ValueEventListener

root = new Firebase("https://money-edc4a.firebaseio.com")

def doWithReservations = { DataSnapshot it ->
    it.value.each {
        entry -> println "$entry.key , $entry.value"
        root.child("orders").child(entry.key).value = entry.value
    }
}

def listener = [
        onDataChange: doWithReservations,
        onCancelled : { println(it) }] as ValueEventListener

root.child("reservations").addListenerForSingleValueEvent(listener)



synchronized (this) {
    wait(5000)
}


