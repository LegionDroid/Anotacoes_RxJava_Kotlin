//REFERENCE: https://medium.com/@gabrieldemattosleon/fundamentals-of-rxjava-with-kotlin-for-absolute-beginners-3d811350b701

val donna: Observable<Mistakes>
        val josh: Subscriber

        donna = Observable.just(
            Mistakes("Wrong change"),
            Mistakes("Dropped change"),
            Mistakes("Wrong change")
        )

        josh = donna.subscribe({ whatHappened -> reportToMangement(whatHappened) })
/*
 In this situation, Josh is the Observer and Donna is the data. Josh was told to watch and report Donna as her state changes, and he’s to make a callback to whoever is listening to him (the manager).
*/
/*
 An OBSERVABLE is where the data stream comes from, it does some work and emits values.
An OPERATOR has the capability to modify the data from one form to another.
An OBSERVER receives the values.
Think of it this way, Observable is the Speaker, Operator is the Translator, and the Observer is the Listener.
*/


/********************************************** */

Observable.just("Apple", "Orange", "Banana")
            .map({ input -> throw RuntimeException() })
            .subscribe(
                { value -> println("Received: $value") }, // onNext
                { error -> println("Error: $error") },    // onError
                { println("Completed!") }
            )

Observable.fromArray("Apple", "Orange", "Banana")
            .subscribe {
                println(it)
            }

Observable.fromIterable(listOf("Apple", "Orange", "Banana"))
            .subscribe(
                { value -> println("Received: $value") },      // onNext
                { error -> println("Error: $error") },         // onError
                { println("Completed") }                       // onComplete
            )

Observable.intervalRange(
    10L,     // Start
    5L,      // Count
    0L,      // Initial Delay
    1L,      // Period
    TimeUnit.SECONDS
).subscribe { println("Result we just received: $it") }
/**
Result we just received: 10
Result we just received: 11
Result we just received: 12
Result we just received: 13
Result we just received: 14
 */

 Observable.interval(1000, TimeUnit.MILLISECONDS)
    .subscribe { println("Result we just received: $it") }
/*
Result we just received: 0
Result we just received: 1
Result we just received: 2
...
The code above will keep emitting the value each second indefinitely.
 */

val observable = PublishSubject.create<Int>()
observable
    .toFlowable(BackpressureStrategy.DROP)
    .observeOn(Schedulers.computation())
    .subscribe (
        {
            println("The Number Is: $it")
        },
        {t->
            print(t.message)
        }
    )
for (i in 0..1000000){
    observable.onNext(i)
}
/*
The above code is using the Backpressure strategy DROP which will drop some of the items in order to preserve memory capabilities
 */

/******************************************************* */
/******OBSERVABLE -> PODE SER SUBSTITUIDO POR*********** */
/**********************EMITTERS************************* */

observable -> pode_ser_substituido_por
Flowable.just("This is a Flowable")
    .subscribe(
        { value -> println("Received: $value") },
        { error -> println("Error: $error") },
        { println("Completed") }
    )
//It works exactly like an Observable but it supports Backpressure.

Maybe.just("This is a Maybe")
    .subscribe(
        { value -> println("Received: $value") },
        { error -> println("Error: $error") },
        { println("Completed") }
    )
/*
apenas um deles é chamado. Se houver um valor emitido, ele chama onSuccess, se não houver valor, chama onCompleteou se houver um erro, chama onError.
 */

 Single.just("This is a Single")
    .subscribe(
        { v -> println("Value is: $v") },
        { e -> println("Error: $e")}
    )
//It’s used when there’s a single value to be returned

Completable.create { emitter ->
    emitter.onComplete()
    emitter.onError(Exception())
}
/*
A completable won’t emit any data, what it does is let you know whether the operation was successfully completed. If it was, it calls onComplete and if it wasn’t it calls onError . A common use case of completable is for REST APIs, where successful access will return HTTP 204 , and errors can ranger from HTTP 301 , HTTP 404 , HTTP 500 , etc. We might do something with the information.
 */

 Observable.just("Hello")
    .doOnSubscribe { println("Subscribed") }
    .doOnNext { s -> println("Received: $s") }
    .doAfterNext { println("After Receiving") }
    .doOnError { e -> println("Error: $e") }
    .doOnComplete { println("Complete") }
    .doFinally { println("Do Finally!") }
    .doOnDispose { println("Do on Dispose!") }
    .subscribe { println("Subscribe") }
/*
You can also manually call the methods doOnSubscribe, doOnNext, doOnError, doOnComplete.
 */

 /******************************************************************** */
 /******************* subscribeOn ******************************* */

 //The subscribeOn (as well as the observeOn ) needs the Scheduler param to know which thread to run on

 Observable.just("Apple", "Orange", "Banana")
    .subscribeOn(Schedulers.io())
    .subscribe{ v -> println("Received: $v") }
 /*
 Scheduler.io() -> This is the most common types of Scheduler that are used. They’re generally used for IO related stuff, such as network requests, file system operations, and it’s backed by a thread pool. A Java Thread Pool represents a group of worker threads that are waiting for the job and reuse many times.
  */

Observable.just("Apple", "Orange", "Banana")
    .subscribeOn(Schedulers.computation())
    .subscribe{ v -> println("Received: $v") }
/*
Scheduler.computation() -> This is quite similar to IO as it’s also backed up by the thread pool, however, the number of threads that can be used is fixed to the number of cores present in the device. Say you have 2 cores, it means you’ll get 2 threads, 4 cores, 4 threads, and so on.
 */

Observable.just("Apple", "Orange", "Banana")
    .subscribeOn(Schedulers.newThread())
    .subscribe{ v -> println("Received: $v") }
/*
Scheduler.newThread() The name here is self-explanatory, as it will create a new thread for each active Observable . You may want to be careful using this one as if there are a high number of Observable actions it may cause instability.
Remember, you can also set how many concurrent threads you want running, so you could do */
.subscribeOn(Schedulers.newThread(), 8)
/* to have a maximum of 8 concurrent threads.
 */

 Observable.just("Apple", "Orange", "Banana")
    .subscribeOn(Schedulers.single())
    .subscribe{ v -> println("Received: $v") }
/*
Scheduler.single() This Scheduler is backed up by a single thread. No matter how many Observable there are, it will only run in a single thread. Think about it as a replacement for the main thread.
*/

Observable.just("Apple", "Orange", "Banana")
    .subscribeOn(Schedulers.trampoline())
    .subscribe{ v -> println("Received: $v") }
/*
Scheduler.trampoline() This will run on whatever the current thread is. If it’s the main thread, it will run the code on the queue of the main thread. Similar to Immediate Scheduler, it also blocks the thread. The trampoline may be used when we have more than one Observable and we want them to execute in order.
 */

val executor = Executors.newFixedThreadPool(10)
val pooledScheduler = Schedulers.from(executor)
Observable.just("Apple", "Orange", "Banana")
    .subscribeOn(pooledScheduler)
    .subscribe{ v -> println("Received: $v") }
/*
Executor Scheduler This is a custom IO Scheduler, where we can set a custom pool of threads by specifying how many threads we want in that pool. It can be used in a scenario where the number of Observable can be huge for IO thread pool.
 */
/*
AndroidSchedulers.mainThread() Calling this on observeOn will bring the thread back to the Main UI thread, and thus make any modification you need to your UI.
 */

 /******************************************************************** */
 /******************* observeOn ******************************* */

Observable.just("Apple", "Orange", "Banana")
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe{ v -> println("Received: $v") }
/*
The method subscribeOn() will instruct the source Observable which thread to emit the items on and push the emissions on our Observer . But if it finds an observeOn() in the chain, it switches the emissions using the selected scheduler for the remaining operation.
Usually, the observing thread in Android is the Main UI thread.
*/


 /******************************************************************** */
 /******************* Transformers ******************************* */
 /* 
 With a transformer, we can avoid repeating some code by applying the most commonly used chains among your Observable , we’ll be chaining subscribeOn and observeOn to a couple of Observable below.
 */
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    Observable.just("Apple", "Orange", "Banana")
        .compose(applyObservableAsync())
        .subscribe { v -> println("The First Observable Received: $v") }

    Observable.just("Water", "Fire", "Wood")
        .compose(applyObservableAsync())
        .subscribe { v -> println("The Second Observable Received: $v") }

}

fun <T> applyObservableAsync(): ObservableTransformer<T, T> {
    return ObservableTransformer { observable ->
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
/*
The example above will print:
The First Observable Received: Apple
The First Observable Received: Orange
The First Observable Received: Banana
The Second Observable Received: Water
The Second Observable Received: Fire
The Second Observable Received: Wood
 */
 /*
It’s important to keep in mind that this example is for Observable , and if you’re working with other Emitters you need to change the type of the transformer, as follows.

ObservableTransformer
FlowableTransformer
SingleTransformer
MaybeTransformer
CompletableTransformer
 */

/******************************************************************** */
 /******************* Operators ******************************* *//*

There are many operators that you can add on the Observable chain, but let’s talk about the most common ones.
*/

map() // -> Transforms values emitted by an Observable stream into a single value.
 Observable.just("Water", "Fire", "Wood")
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .map { m -> m + " 2" }
    .subscribe { v -> println("Received: $v") }
/*RESULT
Received: Water 2
Received: Fire 2
Received: Wood 2
*/

flatMap() // -> Unlike the map() operator, the flatMap() will transform each value in an Observable stream into another Observable , which are then merged into the output Observable after processing. Let’s use the same example from the map() above in a flatMap() and change the thread to computation.
Observable.just("Water", "Fire", "Wood")
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .flatMap { m ->
        Observable.just(m + " 2")
            .subscribeOn(Schedulers.io())
    }
    .subscribe { v -> println("Received: $v") }
/*
RESULT:
Received: Water 2
Received: Fire 2
Received: Wood 2
The above example simply transformed each value emitted by the Observable into separate Observable .
 */

zip() // ->The zip() operator will combine the values of multiple Observable together through a specific function.
Observable.zip(
    Observable.just(
        "Roses", "Sunflowers", "Leaves", "Clouds", "Violets", "Plastics"),
    Observable.just(
        "Red", "Yellow", "Green", "White or Grey", "Purple"),
    BiFunction<String, String, String> { type, color ->
        "$type are $color"
    }
)
    .subscribe { v -> println("Received: $v") }
/*
RESULT:
Received: Roses are Red
Received: Sunflowers are Yellow
Received: Leaves are Green
Received: Clouds are White or Grey
Received: Violets are Purple
 */
/*
Notice that the value "Plastics" did not reach its destination. That’s because the second Observable had no corresponding value.
A real-world use case would be attaching a picture to an API result, such as avatar to name, so on and so forth.
The BiFunction <String, String, String> simply means that the value of the first and secondObservable are both a string and the resulting Observable is also a String.
 */









 

