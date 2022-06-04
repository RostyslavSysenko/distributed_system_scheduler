## About Project
ds-sim is a set of software tools used for simulating distributed system. ds-server is one of the programs included in ds-sim package. ds-sim generates jobs and simulates servers that execute those jobs. In this project i connect my client application to the ds-server, ask ds-server for jobs and schedule those jobs back to the ds-server's simulated servers until no jobs are remaining. All the communication follows the ds-sim protocol and is implimented using socket programming.

## About Author
- name: Rostyslav Sysenko
- SID: 46216340

## How to use
### Pre-requisits
1. Install ubantu if dont have already
2. download this repo & open "implimentations" folder in your terminal

### Commands
- start ds-server: run "./ds-server -c [configFileLocationNoQuotes] -n -v brief" 
- start our client: run "java Client_entrypoint"
- if there are any problems during testing stage: run "fuser -k -n tcp 50000" to terminate all processes running on the default port. It usually helps
- running tests
  1. move to "compiled And Tests" folder
  2. run tests using: {./stage2-test-x86 "java Client_entrypoint [algoName]" -o tt -n}

### Note to myself
- run visualisation tool:
  1. move to the folder from local mac terminal (not ubantu) using : {cd "/Users/ross/Uni cloud/2022 - uni/distributed systems/master"}
  2. run visualisation: {python3 ./ds_viz.py configFile log -c cellSpace -s ScalingFactor}
- running tests:
  1. move to folder using: {cd "/Users/ross/Uni cloud/2022 - uni/distributed systems/project/scheduler/compiled And Tests"}
  2. run tests using: {./stage2-test-x86 "java [-ea] Client_entrypoint [algoName]" -o tt -n} where the inclusion of -en symbolises that assertions are enabled. Also algos are in {"FATFC","FC","LRR"}. The best performing algorithm for average turn around time is FATFC.
- running ds-server:
  1. from ubantu cd using: {cd "/Users/ross/Uni cloud/2022 - uni/distributed systems/project/scheduler/compiled And Tests"}
  2. run {./ds-server -c configFile -n -v brief > out.txt}
- running ds-client:
  1. from from ubantu cd using: {cd "/Users/ross/Uni cloud/2022 - uni/distributed systems/project/scheduler/compiled And Tests"}
  2. recompile the java program using: {javac Client_entrypoint.java}
  3. run {java Client_entrypoint}

## Skills Learnt
- Socket programming
- Distributed system scheduling
- OOP
- Command line tools
