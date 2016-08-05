package net.sizovs.capital.infra.rx

import com.firebase.client.*
import rx.Observable
import rx.Subscriber
import rx.subscriptions.Subscriptions

class RxFirebase {

    static Observable<DataSnapshot> observe(Query ref) {
        Observable.create { Subscriber subscriber ->

            def onFirebaseEvent = new ValueEventListener() {

                @Override
                void onDataChange(DataSnapshot dataSnapshot) {
                    subscriber.onNext(dataSnapshot)
                    subscriber.onCompleted()
                }

                @Override
                void onCancelled(FirebaseError error) {
                    subscriber.onError(new FirebaseException(error.message))
                }
            }

            ref.addListenerForSingleValueEvent(onFirebaseEvent)

            def onDispose = Subscriptions.create {
                ref.removeEventListener(onFirebaseEvent)
            }

            subscriber.add(onDispose)
        }
    }
}