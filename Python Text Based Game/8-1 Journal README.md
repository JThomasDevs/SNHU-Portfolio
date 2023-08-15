# 8-1 Journal Submission

(1)
This project was the final project for my first computer science course, Intro to Scripting (Python).
It is a simple text-based game in which the player navigates a factory, finding OSHA violations along the way and then confronts + fines the "Factory Owner" (the game's villain) to win.
There is no particular problem that this program solves but that is not really the intention of a game.

(2)
Despite being one of my earliest CS projects, I think the code in this project is exceptionally clean and easy to understand.
This may be because it is a fairly straightforward program but I also give credit to my coding style and naming conventions.

(3)
With such a simple program, there is not /much/ room for improvement BUT one immediate thing I noticed upon revisiting this code
is the difference between how I wrote if-else statements back then and how I write them now.
Oftentimes, when you have a single if qualifier, followed by an "else", you do not need to write "else:"

You can simply write it as:
```
if "this thing":
   then "other thing"
"different thing"
```
Instead of:
```
if "this thing":
   then "other thing"
else:
   "different thing"
```

I don't know that this change would make my code more efficient or more secure but stylistically, it looks cleaner and is just as easy to understand.

Additionally, I could "class-ify" my program to avoid the use of the global keyword.

I only try to avoid the global keyword as it has caused issues in other, personal projects.

(4)
At the time, the most challenging piece of code to write was the nested dictionary which links the rooms of the factory to one another via cardinal directions and then ties items to each room.
I overcame this challenge with a mix of Stack Overflow answers from 6 years ago, brute force, and ChatGPT explaining dictionaries to me like I was five.
Since this time, I have continued to employ the use of AI assistance in my projects (NEVER code suggestions).
At this time, I believe AI code is too unreliable to use its auto-suggestions but it is an extremely capable tool when writing switch/if-else statements or when moving a code block from one place to another.

(5)
Funnily enough, I believe that knowing the ins and outs of how dictionaries and classes work across multiple languages is an invaluable skill in my pursued career.
While dictionaries have different names in other languages, the basic structure of key-value pairs remains the same.
I try to knock out a few LeetCode problems when I am feeling up for it and so many times, the best possible solution to a problem involves either using a class archetype or using a dictionary to swiftly sort or reference values.

(6)
Like in all of my code, I try to avoid abbreviation in my naming conventions and try to make my variable names as descriptive as possible without being too verbose.
Further, every non-main function does exactly one thing. This would make it very easy to change any particular part of this program with extreme ease.
