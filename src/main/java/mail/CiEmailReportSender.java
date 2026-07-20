package mail;

import managers.ConfigManager;

import javax.mail.MessagingException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CiEmailReportSender {
    private static final String DEFAULT_REPORTS_DIRECTORY = "surefire-reports";
    private static final long DEFAULT_MAX_ATTACHMENT_BYTES = 20L * 1024L * 1024L;

    private CiEmailReportSender() {
    }

    public static void main(String[] args) {
        boolean dryRun = isEnabled(value("EMAIL_DRY_RUN", "false"));
        if (!ConfigManager.isEmailReportEnabled() && !dryRun) {
            System.out.println("Email notification disabled.");
            return;
        }

        validateEmailConfiguration();

        TestSummary summary = readSurefireSummary(Path.of(value("SUREFIRE_REPORTS_DIR", DEFAULT_REPORTS_DIRECTORY)));
        String resultStatus = resultStatus(summary);
        String subject = ConfigManager.getReportTitle() + " - " + resultStatus;
        String body = buildBody(summary, resultStatus);
        List<String> attachments = collectAttachments();

        if (dryRun) {
            System.out.println("Email dry-run OK.");
            System.out.println("Recipients: " + String.join(",", ConfigManager.getEmailTo()));
            System.out.println("Subject: " + subject);
            System.out.println("Summary: total=" + summary.total + ", passed=" + summary.passed
                    + ", failed=" + summary.failed + ", skipped=" + summary.skipped);
            System.out.println("Attachments: " + (attachments.isEmpty() ? "none" : String.join(",", attachments)));
            return;
        }

        try {
            EmailAttachmentsSender.sendEmailWithAttachments(
                    ConfigManager.getEmailSmtpHost(),
                    ConfigManager.getEmailSmtpPort(),
                    ConfigManager.getEmailFrom(),
                    ConfigManager.getEmailPassword(),
                    ConfigManager.getEmailTo(),
                    subject,
                    body,
                    attachments.toArray(new String[0])
            );
            System.out.println("CI email report sent.");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send CI email report.", e);
        }
    }

    private static void validateEmailConfiguration() {
        List<String> missing = new ArrayList<>();
        if (isBlank(ConfigManager.getEmailSmtpHost())) {
            missing.add("EMAIL_SMTP_HOST");
        }
        if (isBlank(ConfigManager.getEmailSmtpPort())) {
            missing.add("EMAIL_SMTP_PORT");
        }
        if (isBlank(ConfigManager.getEmailFrom())) {
            missing.add("EMAIL_FROM");
        }
        if (isBlank(ConfigManager.getEmailPassword())) {
            missing.add("EMAIL_PASSWORD");
        }
        if (ConfigManager.getEmailTo().length == 0) {
            missing.add("EMAIL_TO");
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing email configuration: " + String.join(", ", missing));
        }
    }

    private static List<String> collectAttachments() {
        List<String> attachments = new ArrayList<>();
        maybeAttachDirectory(
                attachments,
                "Allure HTML",
                Path.of(value("ALLURE_HTML_DIR", "published-allure-html")),
                Path.of(value("ALLURE_HTML_ZIP", "target/allure-html-report.zip")),
                isEnabled(value("EMAIL_ATTACH_ALLURE_HTML", "true"))
        );
        maybeAttachDirectory(
                attachments,
                "Extent Report",
                Path.of(value("EXTENT_REPORTS_DIR", "extent-reports")),
                Path.of(value("EXTENT_REPORTS_ZIP", "target/extent-report.zip")),
                isEnabled(value("EMAIL_ATTACH_EXTENT_REPORT", "true"))
        );
        return attachments;
    }

    private static void maybeAttachDirectory(List<String> attachments, String label, Path sourceDirectory,
                                             Path outputZip, boolean enabled) {
        if (!enabled) {
            return;
        }
        if (!Files.isDirectory(sourceDirectory)) {
            System.out.println(label + " attachment source not found: " + sourceDirectory);
            return;
        }
        long sourceSize = directorySize(sourceDirectory);
        long maxBytes = maxAttachmentBytes();
        if (sourceSize > maxBytes) {
            System.out.println(label + " attachment skipped. Size " + sourceSize
                    + " bytes exceeds limit " + maxBytes + " bytes.");
            return;
        }
        zipDirectory(sourceDirectory, outputZip);
        attachments.add(outputZip.toString());
        System.out.println(label + " attachment prepared: " + outputZip);
    }

    private static long directorySize(Path directory) {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths.filter(Files::isRegularFile)
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to read file size: " + path, e);
                        }
                    })
                    .sum();
        } catch (IOException e) {
            throw new RuntimeException("Failed to calculate directory size: " + directory, e);
        }
    }

    private static void zipDirectory(Path sourceDirectory, Path outputZip) {
        try {
            Path parent = outputZip.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.deleteIfExists(outputZip);
            try (OutputStream output = Files.newOutputStream(outputZip);
                 ZipOutputStream zipOutput = new ZipOutputStream(output);
                 Stream<Path> paths = Files.walk(sourceDirectory)) {
                paths.filter(Files::isRegularFile)
                        .forEach(path -> addZipEntry(sourceDirectory, zipOutput, path));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to zip directory: " + sourceDirectory, e);
        }
    }

    private static void addZipEntry(Path sourceDirectory, ZipOutputStream zipOutput, Path path) {
        try {
            String entryName = sourceDirectory.relativize(path).toString().replace('\\', '/');
            zipOutput.putNextEntry(new ZipEntry(entryName));
            Files.copy(path, zipOutput);
            zipOutput.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("Failed to zip file: " + path, e);
        }
    }

    private static TestSummary readSurefireSummary(Path reportsDirectory) {
        TestSummary summary = new TestSummary();
        if (!Files.exists(reportsDirectory)) {
            System.out.println("Surefire reports directory not found: " + reportsDirectory);
            return summary;
        }

        try (Stream<Path> paths = Files.walk(reportsDirectory)) {
            paths.filter(path -> Files.isRegularFile(path)
                            && path.getFileName().toString().startsWith("TEST-")
                            && path.getFileName().toString().endsWith(".xml"))
                    .forEach(path -> addSuite(summary, path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Surefire reports: " + reportsDirectory, e);
        }
        summary.passed = Math.max(0, summary.total - summary.failed - summary.skipped);
        return summary;
    }

    private static void addSuite(TestSummary summary, Path path) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            var document = factory.newDocumentBuilder().parse(path.toFile());
            var suites = document.getElementsByTagName("testsuite");
            for (int i = 0; i < suites.getLength(); i++) {
                var node = suites.item(i);
                var attrs = node.getAttributes();
                int tests = intAttr(attrs, "tests");
                int failures = intAttr(attrs, "failures");
                int errors = intAttr(attrs, "errors");
                int skipped = intAttr(attrs, "skipped");
                summary.total += tests;
                summary.failed += failures + errors;
                summary.skipped += skipped;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Surefire report: " + path, e);
        }
    }

    private static int intAttr(org.w3c.dom.NamedNodeMap attrs, String name) {
        var node = attrs.getNamedItem(name);
        return node == null ? 0 : Integer.parseInt(node.getNodeValue());
    }

    private static String buildBody(TestSummary summary, String resultStatus) {
        String allureUrl = value("ALLURE_REPORT_URL", ConfigManager.getAllureReportUrl());
        String runUrl = value("GITHUB_RUN_URL", "");
        String pipelineStatus = value("CI_TEST_STATUS", "");
        return "<html><body>"
                + "<h2>" + ConfigManager.getReportTitle() + "</h2>"
                + "<p><b>Result:</b> " + resultStatus + "</p>"
                + (isBlank(pipelineStatus) ? "" : "<p><b>Pipeline:</b> " + pipelineStatus + "</p>")
                + "<table border=\"1\" cellpadding=\"8\" cellspacing=\"0\">"
                + "<tr><th>Total</th><th>Passed</th><th>Failed</th><th>Skipped</th></tr>"
                + "<tr><td>" + summary.total + "</td><td>" + summary.passed + "</td><td>"
                + summary.failed + "</td><td>" + summary.skipped + "</td></tr>"
                + "</table>"
                + link("Allure Report", allureUrl)
                + link("GitHub Actions Run", runUrl)
                + "<p><b>Repository:</b> " + value("GITHUB_REPOSITORY", "") + "</p>"
                + "<p><b>Branch:</b> " + value("GITHUB_REF_NAME", "") + "</p>"
                + "</body></html>";
    }

    private static String resultStatus(TestSummary summary) {
        if (summary.total == 0) {
            return "UNKNOWN";
        }
        return summary.failed > 0 ? "FAILED" : "PASSED";
    }

    private static String link(String label, String url) {
        return isBlank(url) ? "" : "<p><b>" + label + ":</b> <a href=\"" + url + "\">" + url + "</a></p>";
    }

    private static String value(String name, String defaultValue) {
        String systemValue = System.getProperty(name);
        if (!isBlank(systemValue)) {
            return systemValue;
        }
        String envValue = System.getenv(name);
        return isBlank(envValue) ? defaultValue : envValue;
    }

    private static boolean isEnabled(String value) {
        return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value);
    }

    private static long maxAttachmentBytes() {
        String maxMb = value("EMAIL_MAX_ATTACHMENT_MB", "");
        if (isBlank(maxMb)) {
            return DEFAULT_MAX_ATTACHMENT_BYTES;
        }
        return Long.parseLong(maxMb) * 1024L * 1024L;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class TestSummary {
        int total;
        int passed;
        int failed;
        int skipped;
    }
}
