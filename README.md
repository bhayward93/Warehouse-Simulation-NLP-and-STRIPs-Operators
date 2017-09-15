# warehouse-sim-clj-v2

Author @ Ben Hayward 
Teesside University
P4117178

## Usage

To start open both the warehouse-sim-clj and warehouse-sim-nl. Type in Clojure the command to connect to socket.
$ (-main 2222)
Next press setup on NetLogo, add the forklifts you require, and press send dynamic-state and send world-state. This should display a message in Clojure to indicate that the startes have been recieved. If this fails, try entering another port number, for example 2223 2224 2225 etc.

To run ops-search the simplest option is to run the function time returned, feeding in one or multiple goal-states.
To run the test suite run: 
$(timed-ops-search i) 

where i is the number of test itterations you would like for each test.
 

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
