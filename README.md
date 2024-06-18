# OasisChecker
 This bot was written in Java and its main purpose is to check OASIS for new grades, and send email if there are any.
OASIS is the student information system used in Izmir University of Economics.

It needs the _geckodriver_ and _Firefox_ to run but _chromedriver_ and _Chrome_ can also be used with a small change in the _Oasis.OasisSession.startWebDriver()_ method.

Since the code needs to constanly run to check OASIS, it is better to run it on a **server**.

**Maven dependencies:**

    <dependencies>
 
        <dependency>
            <groupId>com.sun.mail</groupId>
            <artifactId>javax.mail</artifactId>
            <version>1.6.2</version>
        </dependency>


        <dependency>
            <groupId>javax.activation</groupId>
            <artifactId>activation</artifactId>
            <version>1.1.1</version>
        </dependency>


        <dependency>
            <groupId>com.deque.html.axe-core</groupId>
            <artifactId>selenium</artifactId>
            <version>4.9.1</version>
        </dependency>
 
    </dependencies>
