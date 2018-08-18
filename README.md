# SE306 Group 14: Travelling Oliver Problem

Master: [![Build Status](https://travis-ci.com/encryptededdy/SE306_TravellingOliverProblem.svg?token=yWUrDYvrGQFpxBXqf7zH&branch=master)](https://travis-ci.com/encryptededdy/SE306_TravellingOliverProblem)

See releases for built JARs and source code archives for each milestone!

## Project Overview
### Description
The main goal of the project was to solve a problem using artificial intelligence and parallel processing, while trying to stick to a strict waterfall model. The problem is a shceduling problem which involves scheduling a full graph made up of tasks, onto multiple processors. Each task can have dependencies to parents or constraints on children, which indicates that they have a communication cost. The communication cost further extends the state space for this problem as it is an extra constraint to test for, and all these dependencies have to be fulfilled for a tasks parents for it to be scheduled. Once all tasks on the graph have been scheduled the schedule is seen as complete. An optimal solution is such that no other "correct" schedule has an earlier end time than the current one.
There are three other goals for this project, and they are:
* Optimal schedule output
* Visualisation
* Parallelisation

For more information about the algorithm(s) and optimizations, please see the Wiki.

## Instructions
### Usage
* Look at the github wiki for further information

### Running the jar
* java −jar scheduler.jar INPUT.dot P [OPTION]

### Necessary CLI arguments
* INPUT. dot&nbsp;&nbsp;&nbsp;&nbsp;  a  task  graph  with  integer  weights  in  dot  format
* P &nbsp;&nbsp;&nbsp;&nbsp; number  of  processors  to  schedule  the INPUT graph on

### Optional CLI arguments
* −p N  &nbsp;&nbsp;&nbsp;&nbsp;  use N cores  for  execution  in  parallel  ( default  is  sequential )
* −v  &nbsp;&nbsp;&nbsp;&nbsp;  visualise  the  search
* −o OUPUT &nbsp;&nbsp;&nbsp;&nbsp;     output  file  is  named OUTPUT ( default  is INPUT−output.dot )

### Building/Compiling
Maven can handle all building/compiling/packaging.

Use `mvn build` to build TOP Scheduler. Use `mvn package` to package into an executable JAR. `mvn install` to do everything and install the package. `mvn test` runs unit tests.

To use this in an IDE (like IntelliJ IDEA), simply import it as a Maven project.

## Navigation
### Wiki
The wiki is the location for majority of our documentation and research behind our projet, including the algorithm optimisations, assumptions and other similar information
### Issues
The issue tracker contains a portion of the issues encountered throughout our project, despite this a number of our issues were just managed through our messaging app telegram as this provided a faster way to get peoples attention when a bug occured
### Releases
Where you find the release for both the milestones of the project

## Group Members
| Name         | Username | Uni ID | GitHub    | Email (Personal) |
| ------------ | ------- | ------ | --------- | --- |
| Edward Zhang | [ezha210](mailto:ezha210@aucklanduni.ac.nz) | 438229106 | [encryptededdy](http://www.github.com/encryptededdy) | [edward@zhang.nz](mailto:edward@zhang.nz) |
| Blair Cox    | [bcox280](mailto:bcox280@aucklanduni.ac.nz) | 119106197 | [bcox280](http://www.github.com/bcox280) | blairacox@gmail.com
| Zach Huxford | [zhux228](mailto:zhux228@aucklanduni.ac.nz) | 106644914 | [zachbwh](http://www.github.com/zachbwh) | me@zachhuxford.io
| Benny Chun   | [bchu352](mailto:bchu@aucklanduni.ac.nz) | 902908060 | [BennyChun](http://www.github.com/BennyChun) | Bennychun97@gmail.com
| Jack Mao     | [wmao492](mailto:wmao492@aucklanduni.ac.nz) | 429606066 | [Pathical](http://www.github.com/Pathical) | [pathicalmine@gmail.com](pathicalmine@gmail.com) |
