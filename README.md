# Simple Email Sender
    Sends email to an unauthenticated SMTP server.

# How do I build simple email sender?

    mvn package 

# Unit tests

    mvn test

# How do I run simple email sender

    java -jar ./target/simple-email-sender-1.0.jar --smtp.host=<SMTP Host> --smtp.port=<SMTP Port> --from.email=<from email address> 
    --to.email=<to email address> --reply.email=<reply email address> --email.subject=<email subject> --email.message=<email content> 


