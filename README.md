![](https://puu.sh/Glggr/465da8cfd9.png)

This repository contains my solutions to the following tasks:

### Task 1
Complete the function (scramble str1 str2) that returns true if a portion of str1 characters can be rearranged to match str2, otherwise returns false

*Notes:*
- *Only lower case letters will be used (a-z). No punctuation or digits will be included.*
- *Performance needs to be considered*

Examples:
```clojure
(scramble? “rekqodlw” ”world') ==> true
(scramble? “cedewaraaossoqqyt” ”codewars”) ==> true
(scramble? “katas”  “steak”) ==> false
```

### Task 2
Create a web service that accepts two strings in a request and applies function scramble? from previous task to them.

### Task 3
Create a UI in ClojureScript with two inputs for strings and a scramble button. When the button is fired it should call the API from previous task and display a result.


## How to run

To start the web server from Task 2, execute `lein run`.  
This will start the server on port 8080.

To open the ClojureScript UI from Task 3, execute `lein fig`.  
This will start figwheel on port 9500 and open the browser.


## Approach & Thoughts

### Task 1

At first I just wanted to make a set for both input strings and then check if the second set is contained in the first. This would have passed the 3 given test cases, but does not match the specification since it won't account for multiple occurences of the same character.

Which brought me to my [current implementation](https://github.com/NPException/scramblies/blob/master/src/scramblies/core.clj#L14-L25):  
Count the available characters in the first string via the `frequencies` function.  
Then iterate through the second string while checking if enough characters are left in the map.

I chose to convert the available-characters map to a `transient`, in order to make updating the map more performant. It doesn't really make a difference for smaller input sizes, but saves some time for larger inputs.

I have also added [another possible implementation](https://github.com/NPException/scramblies/blob/master/src/scramblies/experiment.clj#L50-L58) in the `scramblies.experiment` namespace, which is a lot quicker. But to achieve that I reimplemented `frequencies` with a regular Java `HashMap`. Benchmarking results are at the bottom of that namespace.

*[Edit]* After talking about the challenge with a friend, he decided to implement the function in Rust as an excercise. He was using a BitSet to keep track of the characters of the first String that had already been used. I was curious how fast the equivalent code in Clojure would be, so I added it as [a third implementation](https://github.com/NPException/scramblies/blob/master/src/scramblies/experiment.clj#L79-L89) and added benchmark results in the namespace as well.

### Task 2

I decided to use Jetty as a server, with a combination of [**reitit**](https://github.com/metosin/reitit) and some Ring middleware to do the routing and request/response parsing.

[This piece of code](https://github.com/NPException/scramblies/blob/master/src/scramblies/core.clj#L34-L47) contains the routing logic, input and output validation, as well as parameter coercion, as provided by *reitit*.

I added a [small middleware function](https://github.com/NPException/scramblies/blob/master/src/scramblies/core.clj#L50-L60) to create a basic error response for requests which don't go to the right endpoint or don't use the right HTTP method.

Last but not least, [starting the server](https://github.com/NPException/scramblies/blob/master/src/scramblies/core.clj#L63-L73) is just passing the `router` and additional middleware to the Ring Jetty adapter.

### Task 3

This was an interesting task, since I had not done anything with ClojureScript before.  
I had heard of [**Figwheel**](https://figwheel.org/) and [**re-frame**](https://github.com/day8/re-frame) before, so I decided to start my CS journey with those. I looked up some getting started guides for both, and built my UI by iterating on some example code until it did what I wanted. In order to have something that is not just plain HTML, I used [**mvp.css**](https://andybrewer.github.io/mvp/).

The final UI consists of the [HTML skeleton](https://github.com/NPException/scramblies/blob/master/resources/public/index.html) and [some ClojureScript code](https://github.com/NPException/scramblies/blob/master/src/scramblies/page.cljs).

I was surprised how little code there was in the end. Some setup code aside, there's basically just the [event listener which does the API call](https://github.com/NPException/scramblies/blob/master/src/scramblies/page.cljs#L63-L78), and the [main render code](https://github.com/NPException/scramblies/blob/master/src/scramblies/page.cljs#L103-L136).

One major thing that could be improved is to properly handle error responses from the API, and give the user better feedback for it.
