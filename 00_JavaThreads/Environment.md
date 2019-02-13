## Our resources for this course

We will use primarily the [Burattini](https://en.wikipedia.org/wiki/Tito_Livio_Burattini) multicore machine for our experiments.
Upon request, the smaller [Pascaline](https://en.wikipedia.org/wiki/Pascal%27s_calculator) server can also be reserved.

### Burattini

Burattini is a a HP DL-165G7 server.
It features two Opteron 6176 with 12 physical cores each at 2.3 GHz, for a total of 24 physical cores and 48 GB of RAM.
Memory access is NUMA: accesses to the memory banks attached to one of the processors are faster than accesses to the memory banks attached to the other processor.

Burattini is booked for our use every Thursday from 16:00 to 19:00, plus another slot that is to be determined through an agreement in class.
We can also book other slots when there is a request by the class.
This should happen on the Moodle forum and not by email.
This way, multiple students can join the request and we can make a single reservation.

There is no exclusive use reservation mechanism for Burattini.
With the limited number of groups in LINGI2355, it would be an overkill to use such a mechanism.
Instead, you can ask for an exclusive use time during the practical session to the other students, and groups will take turns to perform final performance measurements.

### Pascaline

Pascaline, a HP DL-160G6 server, will be our backup execution server. 
It features two [Intel Xeon E5540 processors](https://ark.intel.com/products/37104/Intel-Xeon-Processor-E5540-8M-Cache-2_53-GHz-5_86-GTs-Intel-QPI) of the Nehalem generation, with 24 GB of RAM.
Each processor features 4 cores with the Intel hyper-threading technology, for a total of 8 physical cores and 16 logical cores.
It is also a NUMA machine.

### Your laptop

It is recommended to test the correctness (safety and liveness) of your code on your laptop before testing on the Burattini server.
This will limit the risk of interference between the codes of different students, biasing the measurements.

## Connecting to the execution server

The server is only accessible during the planned reservations.

You must use your INGI login to copy you ssh public key on your home folder.
Then, you can access the server using SSH.
The following details the procedure:

- to store your ssh key on your student home dir, connect to `studssh.info.ucl.ac.be` with ssh:
	- `ssh username@studssh.info.ucl.ac.be`
- go to `/etinfo/users2/LOGIN` where `LOGIN` is your INGI login;
- if it does not exists, create the `.ssh` folder;
- if it does not exists, create the `authorized_keys` file in the `.ssh` folder;
- append your public ssh key to the `authorized_keys` file.

:warning: There can be up to a small delay between the first time you register the key and the time when you can access the server using it.

The access to the `burattini.info.ucl.ac.be` machine requires using `studssh.info.ucl.ac.be` as a proxy.
On your own machine, go to your home dir and edit `.ssh/config` (create the file if it does not exists).
Enter the following configuration:

```
Host ingi-burattini
	User YOUR_INGI_USERNAME
	Hostname burattini.info.ucl.ac.be
	IdentityFile PATH_TO_YOUR_SSH_PRIVATE_KEY
	StrictHostKeyChecking no
	ProxyJump studssh.info.ucl.ac.be
```

You can now access the server with `ssh ingi-burattini`.
Your home folder is mounted at `/etinfo/users/LOGIN`.
This folder is backed-up automatically.

<!-- ## Setting a number of threads on Linux

By default, the JVM (or any process running on Linux) will always use all available cores.
While you can use a limited number of threads (as we will do in the first tutorials), sometimes we also want to limit the physical cores, or we may want to measure the difference between the synchronization on two cores of the same CPU and between two cores across different CPUs.
More specifically, we will want to:

- Use a configurable number of cores, e.g. from 1 to 8 on the Pascaline machine, and from 1 to 24 on the more powerful Burattini machine.
- Use cores on single CPU die, or cores across CPU dies, to see the impact (or lack thereof) of inter-CPU memory synchronization traffic.

:warning:
We will update this section with instructions when we will need such functionality. -->

## Measuring time properly in Java

We will need to benchmark execution times in Java.
The current time in nanoseconds is obtained using `System.nanoTime()`.
Divide this time by 1.000.000 to get the time in milliseconds.

```Java
long startTime = System.nanoTime();
/* do some work */
System.out.println((System.nanoTime()-currentTime)/(1000*1000));
```

Note that measuring the performance of a concurrent program properly requires some care:

- Obviously, we do not want the collection of statistics to require any synchronization outside of a final collection after all measured threads have run. For instance, having a shared counter or a shared object maintaining live statistics about the duration of critical section for all threads, that we would have to synchronize, would probably be [a contention point](https://media.tenor.com/images/8431be8b5a5edf329af0c1128e4097c7/tenor.gif).
- As a result, you will want to keep statistics for each thread, either as private members of the `Runnable` object itself, or as `ThreadLocal` variables as part of the shared object.
- But, you must also make sure these statistics do not consume too much memory. If they do, they may take up valuable space in the cache that would normally be used by the measured shared data structure.
- Calling `System.out.println()` is time-consuming. You will probably notice that your program crashes or deadlocks when removing debug outputs, but works fine when enabling them. It might be that the time taken for printing messages simply reduces the probability that a race condition in your code triggers. It is therefore better to collect the information in memory and print all statistics only at the end of the program execution.
