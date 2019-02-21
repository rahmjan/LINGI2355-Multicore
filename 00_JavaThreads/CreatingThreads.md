## Using Java threads

We start by reviewing the basics of working with Threads in Java.

### Creating threads

A thread executes a single sequential program.
The class containing the thread program must implement the [`Runnable`](https://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html) interface.
This interface contains only one method `run()` that must be the entry point to the thread program.
The following example [(also here)](src/threads/HelloWorld.java) defines a simple thread that outputs a message:

```java
public class HelloWorld implements Runnable {
	private final String s ;

	public HelloWorld(String s) {
		this.s = s ;
	}

	public void run() {
		System.out.println(s);
	}

}
```

The [`Thread`](https://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html) class constructor takes an object implementing `Runnable` and creates a new thread:

```java
public static void main(String[] args) {
	Thread t = new Thread(new HelloWorld("Hello world!"));
}
````

Note that the thread is not running yet: the program above terminates without displaying any message.
In order to start the thread, we must call the `start()` method of the `Thread` class.

:exclamation: Calling the `run()` method will not trigger an error, but the code in the `run()` method will _not_ execute as a independent sequential program. Instead, it will execute only as if it was a regular method access by the caller, as part of the caller's own sequential program.

```java
public static void main(String[] args) {
	Thread t = new Thread(new HelloWorld("Hello world!"));

	t.start();
}
```

### Anonymous inner classes

In some cases, it can be useful to write the code of a new thread inlined in the context of the class that creates it (when the code of the thread is simple enough).
Java allows doing this by using an *anonymous inner class*.
An example is given by the following code [(available here)](src/threads/FinalCountdowns.java), where 8 threads wish to collectively count down to 0, before the main thread declares it is time for ignition:

```java
public class FinalCountdowns {
	public static void main(String[] args) {
		Thread[] t = new Thread[8];
		for (int i=0; i<8; i++) {
			t[i] = new Thread(new Runnable() {
				public void run() {
					for (int c=5; c>0; c--) {
						System.out.print(c+"... ");
					}
					System.out.println("ready!");
				}
			});
		}
		for (int i=0; i<8; i++) {
			t[i].start();
		}
		System.out.println("IGNITION!!!");
	}
}
```

#### Questions

- [ ] Run the code above. What do you observe? Is this the *expected* behaviour?
- [ ] Fix the problem identified by using the appropriate method from the `Thread` class.
- [ ] Modify the code so that each thread counts down exactly 5 seconds and not as fast as it can.
- [ ] You will see that some of the methods you used may throw an [`InterruptedException`](https://docs.oracle.com/javase/7/docs/api/java/lang/InterruptedException.html). What is the purpose of this exception?
- [ ] We cannot distinguish between the outputs of each thread, in particular for the line showing `ready`. Modify the program so that each thread gets an identity (thread number) and uses it for its output.
