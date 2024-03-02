- Briefly summarize the requirements and goals of the app you developed. 

In this project, I developed a weight tracking app with the goals of allowing users to record weights, associate these weights with a date, set a goal weight, and to notify the user via SMS when a weight is recorded that is less than or equal to the goal weight.

To achieve these goals, the app required a database with three tables: one to store the recorded weights, another to store the goal weight, and the final table to store user login information. In the rubric, the data was required to be displayed in a grid. Additionally, buttons were added that navigated users to activities away from the main screen to record daily weights and set goal weights. A login screen was also required so, in order to add a layer of security, user passwords were hashed using SHA-256 prior to being stored and later verified on login. Lastly, the app needed to continue to function if the user disallowed SMS notifications.

- What user needs was this app designed to address?

This app was designed to meet user needs of recording weights from specific dates and to notify the user when a goal weight has been met.

- What screens and features were necessary to support user needs and produce a user-centered UI for the app? How did your UI designs keep users in mind? Why were your designs successful?

The application consists of 5 total screens: a login screen, the main data display screen, an edit weight screen, an add weight screen, and a set goal weight screen.

The login screen consists of a basic layout with entries for a username and password with a login button below the entries. All elements on this page are centered on the screen to make it easily reachable by users' thumbs when holding their mobile device.

The main screen features buttons to log out, enable/disable notifications, add weight, and set a goal weight. Once a weight has been added, a user may click on that entry to either edit the details of the entry or to delete it. For notifications, a user will be asked for SMS permissions and upon granting them will be sent an SMS notification once the goal weight has been met. If a user wishes to disable notifications after granting permission, they may simply hit the bell icon once more to disable the notifications, indicated by changing the icon to a bell with a slash through it. 

The add weight and edit weight screens are nearly identical with the primary differences being that the edit weight screen pre-fills the EditText widgets with the currently held weight and date values for that entry and also features a "DELETE" button to delete the existing entry. 

My UI designs kept users in mind by creating distinct, easily visible buttons with clear labels to indicate what each button's function was. For the notification button, which has no label, I chose the bell icon as this is a commonly used icon for notifications across many other applications so many users will be inherently familiar with the button's function.

- How did you approach the process of coding your app? What techniques or strategies did you use? How could those be applied in the future?

I approached coding my app by creating functions for one component at a time, in the logical order that a user might interact with them. For instance, I began by coding the login page logic, first by confirming that I could gather text from the EditText widgets, then that I could hash the user's password and successfully verify an entered password against the stored hash in the app's database, and finally that the login button could either navigate the user to the main activity screen OR display an error message depending on the success of the login operation.

The primary strategy that aided in this method of development was having a well thought out and descriptive plan for how the application should look and function prior to writing a single line of code. This helped me to not get lost in the "code hole" when designing functions on one screen that reference objects from another screen that may have not been created yet. However, because I knew that these objects were going to eventually exist, I felt confident in using references to them while coding.

- How did you test to ensure your code was functional? Why is this process important and what did it reveal?

I made sure to include error messages on all applicable screens and then I thought about what could possibly go wrong during specific processes and coded around these cases. For instance, if the user enters values for a date that cannot be transformed into a valid date (like setting the month to 13), the application displays an error message instead of crashing as it would had I not accounted for this error. Similar catches were put in place for failed database operations, incorrect login information, and empty input values that otherwise may have crashed the application. I also performed manual tests both by trying to intentionally break the application as someone with intimate knowledge of how the application's inner workings operate and by asking my spouse to try to use and break the application as someone who had never seen it before. Specifically having my spouse test my application revealed weak spots in my app's UI that were not obvious in their function that were then adjusted. By the end, I was able to simply inform them what was possible within the app and they were able to navigate the app's UI flawlessly with no direction.

- Considering the full app design and development process, from initial planning to finalization, where did you have to innovate to overcome a challenge?

There are two points of innovation that, although small, I am particularly proud of.

First, on the login screen, the login button dynamically changes text to either "Register" or "Login" based on if a user account exists within the database already. I achieved this by using a simple database query returning the count of lines within the accounts table.

Second, the notification icon dynamically changes based on whether notifications are enabled or disabled. I first tried to achieve this by reading the drawable value of the notification button but soon after realized that this was needlessly complicated. Instead, I programatically assign a tag to the notification button depending on the permissions value or, after permission has been granted, to the opposite of its current value. When a goal weight has been reached, this tag is read and notifications are either sent or not sent accordingly.

- In what specific component from your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience?

I believe the component from my mobile app that best demonstrates my skills is the ability for the Edit Weight screen to have its EditText widgets be pre-filled by the currently held weight and date values for a given entry. This data gathered by taking the tag from the button, which is based on the button's position on the main screen which is then used to gather a "Weight" object from a previously created list of all weight entries which has been sorted by descending date values. Once the Weight object has been retrieved, the weight, date, and ID values are passed as "extras" to the Edit Weight Activity to be used for filling the EditText widgets with weight and date values AND for updating the database entry of the associated ID.