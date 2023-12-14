- Who was the client? What issue did they want you to address?

The client in this project was Artemis Financial. In this assignment, I was tasked with finding and addressing vulnerabilities in the provided code base. Some of these vulnerabilities were found and addressed manually, like generating a checksum to be used for file verification and implementing use of the HTTPS protocol by using a self-signed certificate. Other vulnerabilities, primarily vulnerable dependencies, were found using automated tools and addressed by using up-to-date dependencies.

- What did you do very well when you found your client’s software security vulnerabilities? Why is it important to code securely? What value does software security add to a company’s overall wellbeing?

One thing I did particularly well was updating the versions of the vulnerable plugins being used in the original code base. While this is a simple step, it required independent research and brought the number of vulnerable dependencies in the dependency check report from around 30 to 4 vulnerable dependencies.

Secure coding practices are important as many times, these practices are the only things separating unsuspecting users from malicious actors. In addition to maintaining security over user data, software security builds trust between the company and the user.

-  What part of the vulnerability assessment was challenging or helpful to you?

Deploying the checksum cipher such that the /hash endpoint could be accessed on the specified localhost port and would produce a SHA-256 checksum from the provided string when accessed provided me a deeper understanding regarding how Java web applications are built and how they function. Before this assignment, I had only done web-development related work in Javascript and some Python.

- How did you increase layers of security? In the future, what would you use to assess vulnerabilities and decide which mitigation techniques to use?

I increased layers of security by producing a SHA-256 checksum for file verification, using a self-signed certificate to enable the use of the HTTPS protocol, and using up-to-date dependencies. In the future, I am likely to take a similar approach to security assessment as I used in this assignment: a combination of manual inspection aided by the Vulnerability Process Flow Diagram and automatic tools like the OWASP dependency check plugin.

- How did you make certain the code and software application were functional and secure? After refactoring the code, how did you check to see whether you introduced new vulnerabilities?

When my code implementation was completed, I ran the application through manual tests like altering the input string used to produce the checksum to ensure that the checksum value changed when the input changed. I also ran the automated dependency check tests until I brought all dependencies to their most up-to-date and still compatible versions.

- What resources, tools, or coding practices did you use that might be helpful in future assignments or tasks?

The two main tools that I believe might be helpful in the future are the Vulnerability Process Flow Diagram and the dependency check plugin. In conjunction, these tools make finding and addressing security vulnerabilities much easier than it would otherwise be.
