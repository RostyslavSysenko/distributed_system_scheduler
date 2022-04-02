## About Project
ds-sim is a set of software tools used for simulating distributed system. ds-server is one of the programs included in ds-sim package. ds-sim generates jobs and simulates servers that execute those jobs. In this project i connect my client application to the ds-server, ask ds-server for jobs and schedule those jobs back to the ds-server's simulated servers until no jobs are remaining. All the communication follows the ds-sim protocol and is implimented using socket programming.

## About Author
- name: Rostyslav Sysenko
- SID: 46216340

## Instructions for use
1. Install ubantu if dont have already
2. download this repo & open clientCode folder in your terminal
3. run "•	./ds-server -c [configFileLocation] -p 50018 -n -v brief" to start ds-server
4. run "java Client_entrypoint" to start ds-client

## Skills Learnt
- Socket programming
- Distributed system scheduling
- Command line tools
