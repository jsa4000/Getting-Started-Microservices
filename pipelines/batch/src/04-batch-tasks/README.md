# Spring Cloud Task

## Usage

1. Use `gradle build` to create all the packages.
1. Then deploy the `docker-compose` file inside the project.
1. Use `gradle bootRun` to run all the projects at once
1. Use the endpoint load to launch the job with the task and the partitions. `http://localhost:8080/load`

## Problems Encountered

- If a worker fails sometimes the master is not notified, so it freezes.
- For slaves the tomcat must the disabled, so there is no port created
- The method of the slave steps must be the same as the used in the PartitionHandler and the step partitioner
- Jar must be compiled, son the slave are loaded dinamically by the master
- It seems there is no channel used to communicate master and slaves. This could be a reason because the master freezes without any responses when a slave crashes.
- ThreadTaskExecutor is troublesome if is defined with a maxqueuesize, so it must be disabled.