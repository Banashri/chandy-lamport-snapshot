## About

This repository cotains the implementation of a distributed account system which is implemented with Chandy Lamport snapshot algorithm. Here, 3 different accounts are considered, treated as processes. They send random amount of money to each other without leading to any negative account balance.

## Design

* **UI**

This initiates the simualtion of the system. The money transaction is displayed in the GUI in a separate process. Click on any button to enable the sending of a market message to one of the 3 processes manually. This way, you start the `Snapshot` algorithm. You can see the result of the algorithm in the Info panel. 

* **Communication Channel**

To simulate the communication delay FIFO queue is employed with random delay (by sleep) for each communication channel in the algorithm. The communication is carried out through UDP sockets.

## Chandy-Lamport Algorithm

[Concept](https://www.cs.princeton.edu/courses/archive/fall16/cos418/docs/P8-chandy-lamport.pdf)

## Execute

- Run the ant script following [this](https://www.mkyong.com/ant/how-to-apache-ant-on-mac-os-x/).
- Navigate to `ant` folder.
- Run command : 
  ```
  ant // runs Lamport GUI

## License
Licensed under the MIT license


