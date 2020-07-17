# Multi-Threaded Client-Server Project Refactoring
TODO list:
1) Add more unit tests

Problems after refactoring:
2) After login requests get processed successfully there is a side-effect of changing the user's name from guest to the specific username. 
Separating the request handling into a hierarchy of classes created by a factory pattern makes it impossible to elegantly implement this
side-effect because there is no clear connection between ClientRunnable and the request handlers. Thus this has to be achieved by checking
if each processed request is a successful login by ClientRunnable itself, which hurts performance.
3) Improving control flow to be more readable and putting the most common case first in the encryption and decryption request handlers
leads to having to do the same validation twice, which hurts performance.

Future expansion ideas:
1) Add option to edit or remove users
2) Add option to edit or remove cards
3) Add more Encyption algorithms
4) Change how users are saved to a file
5) Change how cards are stored to a file