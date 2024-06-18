# OasisChecker
Checks OASIS for new grades, sends email if there are any
OASIS is the student information system used in Izmir University of Economics

It needs the geckodriver and Firefox to run, chromedriver and Chrome can also be used with a small change in the code.

Since the code needs to constanly run to check Oasis, it is better to run it on a server.

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
