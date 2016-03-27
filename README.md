# WordBrain Solver
A program to solve [WordBrain](http://wordbrain.maginteractive.com) puzzles,
written in Java.

In WordBrain the player is presented with a square grid of letters. The player
must use the letters to create words. Words must be made by joining adjacent or
diagonal letters. The lengths and order of the words is specified.

When one of the words is found, its letters are removed from the board, and the
letters above "fall" down, to fill any gaps. For this reason, the order in which
words are found is often important to finding the full solution.

The program uses a command-line interface. It prompts the user to enter the
board layout and word lengths, and prints possible solutions to standard out. At
the moment it doesn't attempt to show which letter tiles were used in which
order, it just displays the words making up each solution.


## Build and run
### Linux
From the root directory:
```sh
./gradlew installDist
./wordbrain-cli/build/install/wordbrain-cli/bin/wordbrain-cli
```
### Windows
Not tested, but something very similar with gradlew.bat should work.


## Notes
The solution uses Java 8 Streams and a data structure called a Trie.

### A Trie?
The main challenge is that there are A LOT of permutations of potential words to
check against a dictionary. It turned out that the way to do this efficiently is
using a fun data structure called a [Trie](https://en.wikipedia.org/wiki/Trie),
which allows us to exclude whole swathes of invalid solutions as soon as we
encounter a prefix which does not appear in any known words.

### Java 8 Streams
Even using the word trie, it still takes quite a while to find all possible
solutions. For this reason I wanted to print out solutions as they were found,
so that you can start trying them in the app before the program terminates. To
achieve this cleanly, the solver class returns a Java 8 Stream of solutions. The
caller can then handle them as they are found, rather than waiting til the end
and then dealing with them all at once. Streams also made parallisation pretty
easy.
