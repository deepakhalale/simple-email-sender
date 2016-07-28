package com.locusintellect;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;

public class EmailSenderTest {

    private static final String SMTP_HOST = "deepak@unknown.com";
    private static final int SMTP_PORT = 587;
    private static final String FROM_EMAIL = "someone@unknown.com";
    private static final String TO_EMAIL = "deepak@unknown.com";
    private static final String REPLY_EMAIL = "reply@unknown.com";
    private static final String EMAIL_SUBJECT = "Test";
    private static final String EMAIL_MESSAGE = "Some text here ...";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private EmailSender underTest;

    @Before
    public void setUp() {
        underTest = new EmailSender(SMTP_HOST, SMTP_PORT, FROM_EMAIL, TO_EMAIL, REPLY_EMAIL, EMAIL_SUBJECT, EMAIL_MESSAGE);
    }

    @Test
    public void shouldSendAnEmail() throws Exception {

        underTest.send();

        Session session = Session.getInstance(new Properties());
        final Store store = session.getStore("pop3s");
        store.connect(SMTP_HOST, null);
        final Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        assertThat(inbox.getMessageCount(), is(1));
        assertThat(inbox.getMessage(1).getFrom(), arrayContaining(InternetAddress.parse(FROM_EMAIL)[0]));
        assertThat(inbox.getMessage(1).getReplyTo(), arrayContaining(InternetAddress.parse(REPLY_EMAIL)[0]));
        assertThat(inbox.getMessage(1).getAllRecipients(), arrayContaining(InternetAddress.parse(TO_EMAIL)[0]));
        assertThat(inbox.getMessage(1).getSubject(), is(EMAIL_SUBJECT));
        assertThat(inbox.getMessage(1).getContent().toString(), is(EMAIL_MESSAGE));
        inbox.close(true);
    }

    @Test
    public void shouldFailWhenSmtpHostNotSpecified() {
        underTest = new EmailSender(null, SMTP_PORT, FROM_EMAIL, TO_EMAIL, REPLY_EMAIL, EMAIL_SUBJECT, EMAIL_MESSAGE);
        expectedException.expect(MissingRequiredPropertiesException.class);
        expectedException.expectMessage("Property smtp.host is either null or empty.");

        underTest.send();
    }
}
