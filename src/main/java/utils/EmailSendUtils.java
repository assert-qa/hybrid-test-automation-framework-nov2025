package utils;

import mail.EmailAttachmentsSender;
import managers.ConfigManager;
import utils.LogUtils;

import javax.mail.MessagingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static mail.EmailConfig.*;

public class EmailSendUtils {
    private EmailSendUtils() {
        super();
    }

    public static void sendEmail(int count_totalTCs, int count_passedTCs, int count_failedTCs, int count_skippedTCs) {
        if (!ConfigManager.isEmailReportEnabled()) {
            return;
        }
        if (!isEmailConfigurationValid()) {
            LogUtils.warn("Email report enabled but SMTP configuration is incomplete. Email skipped.");
            return;
        }

        System.out.println("*******************************");
        System.out.println("Send Email - START");
        System.out.println("*******************************");

        List<String> attachments = getExtentReportAttachments();
        String messageBody = getTestCasesCountInFormat(
                count_totalTCs,
                count_passedTCs,
                count_failedTCs,
                count_skippedTCs,
                ConfigManager.getAllureReportUrl()
        );

        try {
            EmailAttachmentsSender.sendEmailWithAttachments(SERVER, PORT, FROM, PASSWORD, TO, SUBJECT, messageBody,
                    attachments.toArray(new String[0]));

            System.out.println("*******************************");
            System.out.println("Email sent successfully.");
            System.out.println("Send Email - END");
            System.out.println("*******************************");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static boolean isEmailConfigurationValid() {
        return isNotBlank(SERVER)
                && isNotBlank(PORT)
                && isNotBlank(FROM)
                && isNotBlank(PASSWORD)
                && !PASSWORD.trim().matches("\\*+")
                && TO != null
                && TO.length > 0;
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static List<String> getExtentReportAttachments() {
        List<String> attachments = new ArrayList<>();
        if (!ConfigManager.isExtentReportEnabled()) {
            return attachments;
        }

        ZipUtils.zipReportFolder();
        Path zipPath = Path.of(ConfigManager.getExtentReportZipPath());
        if (Files.exists(zipPath)) {
            attachments.add(zipPath.toString());
            System.out.println("Extent Report attachment: " + zipPath);
            return attachments;
        }

        Path extentReportPath = Path.of(ConfigManager.getExtentReportPath());
        if (Files.exists(extentReportPath)) {
            attachments.add(extentReportPath.toString());
            System.out.println("Extent Report attachment: " + extentReportPath);
        } else {
            System.out.println("Extent Report attachment not found: " + extentReportPath);
        }
        return attachments;
    }

    private static String getTestCasesCountInFormat(int count_totalTCs, int count_passedTCs, int count_failedTCs,
                                                    int count_skippedTCs, String allureReportUrl) {
        System.out.println("count_totalTCs: " + count_totalTCs);
        System.out.println("count_passedTCs: " + count_passedTCs);
        System.out.println("count_failedTCs: " + count_failedTCs);
        System.out.println("count_skippedTCs: " + count_skippedTCs);

        String allureLink = allureReportUrl == null || allureReportUrl.isBlank()
                ? "<p>Allure Report link is not configured.</p>\r\n"
                : "<p><b>Allure Report:</b> <a href=\"" + allureReportUrl + "\">" + allureReportUrl + "</a></p>\r\n";

        return "<html>\r\n" + "\r\n" + " \r\n" + "\r\n"
                + "        <body> \r\n<table class=\"container\" align=\"center\" style=\"padding-top:20px\">\r\n<tr align=\"center\"><td colspan=\"4\"><h2>"
                + ConfigManager.getReportTitle() + "</h2></td></tr>\r\n<tr><td>\r\n\r\n"
                + "       <table style=\"background:#67c2ef;width:120px\" >\r\n"
                + "                     <tr><td style=\"font-size: 36px\" class=\"value\" align=\"center\">"
                + count_totalTCs + "</td></tr>\r\n"
                + "                     <tr><td align=\"center\">Total</td></tr>\r\n" + "       \r\n"
                + "                </table>\r\n" + "                </td>\r\n" + "                <td>\r\n"
                + "               \r\n" + "                 <table style=\"background:#79c447;width:120px\">\r\n"
                + "                     <tr><td style=\"font-size: 36px\" class=\"value\" align=\"center\">"
                + count_passedTCs + "</td></tr>\r\n"
                + "                     <tr><td align=\"center\">Passed</td></tr>\r\n" + "       \r\n"
                + "                </table>\r\n" + "                </td>\r\n" + "                <td>\r\n"
                + "                <table style=\"background:#ff5454;width:120px\">\r\n"
                + "                     <tr><td style=\"font-size: 36px\" class=\"value\" align=\"center\">"
                + count_failedTCs + "</td></tr>\r\n"
                + "                     <tr><td align=\"center\">Failed</td></tr>\r\n" + "       \r\n"
                + "                </table>\r\n" + "                \r\n" + "                </td>\r\n"
                + "                <td>\r\n" + "                <table style=\"background:#fabb3d;width:120px\">\r\n"
                + "                     <tr><td style=\"font-size: 36px\" class=\"value\" align=\"center\">"
                + count_skippedTCs + "</td></tr>\r\n"
                + "                     <tr><td align=\"center\">Skipped</td></tr>\r\n" + "       \r\n"
                + "                </table>\r\n" + "                \r\n" + "                </td>\r\n"
                + "                </tr>\r\n" + "               \r\n" + "                \r\n"
                + "            </table>\r\n" + allureLink + "       \r\n" + "    </body>\r\n" + "</html>";
    }
}
