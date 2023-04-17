import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// http://www.gisdeveloper.co.kr/?p=10279
fun main() {
    println("Start Function")

    val job = GlobalScope.launch {
        launch {
            println("===================== Coroutine start!")
        }

        try {
            withTimeout(3000L) {
                println("suspend getValue() result: ${getValue()}")

                val v = withContext(Dispatchers.Default) {
                    var total = 0
                    // 10초 소요 (100 * 100 = 10000ms = 10s
                    for (i in 1..100) {
                        delay(100)
                        total += i
                    }
                    total
                }

                println("withContext sum result: ${v}")

                repeat(10) {
                    delay(1000L)
                    println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                }

                println("===================== Coroutine end!")
            }
        } catch (e: TimeoutCancellationException) {
            println(e.message)
        }
    }

    // 함수 실행 5초 이후 코루틴 중지
//    runBlocking {
//        delay(5000L)
//        job.cancel()
//    }

    // 코루틴이 완전히 끝날때까지 대기
    runBlocking {
        job.join()
    }

    runBlocking {
        println("Channel Test start")

        val channel = Channel<Int>()

        launch {
            for (x in 1..10) channel.send(x * x)
            channel.close()
        }

        for (v in channel) println(v)

//        repeat(8) {
//            val v = channel.receive()
//            println("$v")
//        }

        println("Channel Test end")
    }

    println("=========================================")

    runBlocking {
        println("Send first coroutines data to another coroutines Test start")
        val numbers = productNumbers()
        val squares = squares(numbers)
        for(i in 1..10) println("${squares.receive()}")
        println("end")
        coroutineContext.cancelChildren()
        println("Send first coroutines data to another coroutines Test end")
    }

    println("=========================================")

    runBlocking {
        print("Channel Functioning Test start")
        procedureSquares().consumeEach { print(it) }
        print("Channel Functioning Test end")
    }

    println("=========================================")

    runBlocking {
        println("One Coroutines for generate data and Several Coroutines for process data Test start")
        val producer = productNumbers()
        repeat(5) {
            launchProcessor(it, producer)
        }
        delay(3000L)
        producer.cancel()
        println("One Coroutines for generate data and Several Coroutines for process data Test end")
    }


    println("=========================================")

    runBlocking {
        println("Several Coroutines for generate data and one Coroutines for process data Test start")
        val channel = Channel<String>()
        launch {
            sendString(channel, "foo", 200L)
        }
        launch {
            sendString(channel, "BAR", 500L)
        }
        repeat(10) {
            println(channel.receive())
        }
        coroutineContext.cancelChildren()
        println("Several Coroutines for generate data and one Coroutines for process data Test End")
    }

    println("=========================================")

    runBlocking {
        println("Two Coroutines for processing one data Test start")
        val table = Channel<Ball>()
        launch {
            player("ping", table)
        }
        launch {
            player("pong", table)
        }
        table.send(Ball(0))

        delay(3000)

        coroutineContext.cancelChildren()
        println("Two Coroutines for processing one data Test end")
    }

    println("=========================================")

    println("End Function")
    readlnOrNull()
}

private fun CoroutineScope.squares(numbers:ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    for(x in numbers) {
        print("send $x on squares")
        send(x*x)
    }
}

fun CoroutineScope.launchProcessor(id:Int, channel: ReceiveChannel<Int>) {
    launch {
        for(msg in channel) {
            println("Processor #$id received $msg")
        }
    }
}
private fun CoroutineScope.productNumbers() = produce {
    var x = 1
    while(true) {
        println("send $x on productNumbers")
        send(x++)
        delay(500)
    }
}

private suspend fun sendString(channel: SendChannel<String>, s:String, time:Long) {
    while(true) {
        delay(time)
        channel.send(s)
    }
}

data class Ball(var hits:Int)

suspend fun player(name:String, table: Channel<Ball>) {
    for(ball in table) {
        ball.hits++
        println("$name $ball")
        delay(300)
        table.send(ball)
    }
}

fun CoroutineScope.procedureSquares(): ReceiveChannel<Int> = produce { for(x in 1..5) send(x*x) }

suspend fun getValue(): Int {
    return GlobalScope.async {
        var total = 0
        for (i in 1..10) total += i
        total
    }.await()
}