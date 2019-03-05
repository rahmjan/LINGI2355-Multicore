## Introduction

In this second practical assignment we will explore the Java memory model and concurrent updates visibility, experiment with various lock implementations and compare their performance.

You must fill in the [Report.md](Report.md) file with your answers.
Most questions require you to write some code.
Your answer must therefore include a link to the corresponding code and associated test case(s) in your GitHub repository.
The nature of the response to each question is explicit in the assignment text.

The text also introduces the notion of *Actions*.
There is no reply needed for Actions in the report, but you are strongly encouraged to perform them to understand the corresponding explanations.
Actions are here to help you answering the mandatory *Questions*.

The assignment spans over two weeks.
The content for these two weeks is below.
The two first parts can be realized during the first week.
Realizing the three following parts will require assisting to the firth lecture on Wednesday 13, 2019.

- [JavaMemory.md](JavaMemory.md): the Java memory model and use of atomic variables.
- [ClassicalLocks.md](ClassicalLocks.md): implementing the classical Peterson, Filter, and Bakery locks.
- [RMWLocks.md](RMWLocks.md): implementing locks based on the *test-and-set* atomic Read-Modify-Write operation.
- [BetterLocks.md](BetterLocks.md): implementing the Anderson and CLH locks. 
- [Evaluation.md](Evaluation.md): evaluating all the locks in a common benchmark.

### Deliverable

The deliverable will cover all questions included in the pages listed above, in the [Report.md](Report.md) file.
The deadline is **March 19, 22:00**.
We will use the last commit before this date as the final version, unless explicitly told otherwise.
You must fill in the assignment in Moodle in order to let us know there is something to grade.
This assignment uses groups.
If you worked with another student, the two must first register to one of the groups.
If you worked alone, you must still register yourself in a group, in order to be able to submit.
