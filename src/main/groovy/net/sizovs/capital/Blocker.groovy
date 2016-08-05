package net.sizovs.capital

import rx.functions.Action0

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Blocker {

    private def latch = new CountDownLatch(1)

    private def release = { latch.countDown() }

    void block() {
        latch.await(20, TimeUnit.SECONDS)
    }

    Action0 release() {
        release
    }


}
