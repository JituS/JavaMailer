import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Mailer {
  public static void main(String[] args) throws FileNotFoundException, InterruptedException {
    final String username = "";
    final String password = "";

    String host = "smtp.gmail.com";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", "587");
    Session session = Session.getInstance(props,
      new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      });

    Contacts.contacts.entrySet().stream().forEach(e -> {
      String email = e.getValue();
      try {
        sendMessage(email, username, session, e.getKey());
      } catch (FileNotFoundException e1) {
        e1.printStackTrace();
      }
    });
  }

  private static void sendMessage(String to, String from, Session session, String userName) throws FileNotFoundException {
    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(from));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject("Invitation Geek Night NCR | Platform, language and tools. Its all about iOS");

      FileReader reader = new FileReader("./index.html");
      BufferedReader br = new BufferedReader(reader);
      String fileData = "";

      String line;
      while ((line = br.readLine()) != null) {
        fileData += line + "\n";
      }

      String fileData1 = fileData.replaceAll("_NAME_", userName);

      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setContent(fileData1, "text/html");

      MimeMultipart multipart = new MimeMultipart("related");

      BodyPart imageBP1 = new MimeBodyPart();

      DataSource fds1 = new FileDataSource(
        "./geeknight.jpeg");

      imageBP1.setDataHandler(new DataHandler(fds1));

      imageBP1.setHeader("Content-ID", "<image1>");

      multipart.addBodyPart(messageBodyPart);
      multipart.addBodyPart(imageBP1);

      message.setContent(multipart);

      Transport.send(message);

      System.out.println("Sent message successfully to: " + to);

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
