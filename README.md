## About Project
ds-sim is a set of software tools used for simulating distributed system. ds-server is one of the programs included in ds-sim package. ds-sim generates jobs and simulates servers that execute those jobs. So what we are doing in this project is connecting our client application to that ds-server, ask it for a new job, then we decide which server to schedule that job and then we send the scheduling request back to the ds-server. All the communication follows the ds-sim protocol and is implimented using socket programming.

## About Author
- name: Rostyslav Sysenko
- SID: 46216340

## Instructions for use
1. download this repo & open clientCode folder in your terminal
2. run "•	./ds-server -c [configFileLocation] -p 50018 -n -v brief" to start ds-server
3. run "java Client_entrypoint" to start ds-client


