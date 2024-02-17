package com.drobucs.histology.documents.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Controller
public class DocumentController {
    private final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Value("${appFiles.path}")
    private String appFiles;

    @Value("${appFiles.pdf.path}")
    private String pdfPath;
    @Value("${appFiles.html.path}")
    private String htmlPath;
    @Value("${content.type.html}")
    private String contentTypeHtml;
    @Value("${user-agreement}.pdf")
    private String userAgreementPath;
    @Value("${user-agreement}.html")
    private String userAgreementHtmlFileName;
    @Value("${privacy-policy}.pdf")
    private String privacyPolicyPath;
    @Value("${privacy-policy}.html")
    private String privacyPolicyHtmlFileName;
    @Value("${content.type.pdf}")
    private String contentTypePdf;
    @GetMapping("/documents/privacy-policy")
    private void getPrivacyPolicy(HttpServletResponse response) throws IOException {
        returnPdfFile(appFiles + "/" + privacyPolicyPath, response);
    }

    @GetMapping("/documents/terms-of-use")
    private void getUserAgreement(HttpServletResponse response) throws IOException {
        returnPdfFile(appFiles + "/" + userAgreementPath, response);
    }

    private void returnPdfFile(@NotNull String pathToPdf, HttpServletResponse response) throws IOException {
        File file = new File(pathToPdf);
        if (!file.exists()) {
            return;
        }
        response.setContentType(contentTypePdf);
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @GetMapping("/documents/cookie-policy")
    private void getCookiePolicy(HttpServletResponse response) {
        // TODO: make cookie-policy
    }

    private void returnFile(@NotNull String pdfFileName,
                            @NotNull String htmlFileName,
                            @NotNull HttpServletResponse response) throws IOException {
        createAppDirs();
        File pdfFile = new File(pdfPath, pdfFileName);
        if (!pdfFile.exists()) {
            logger.info("No pdf file.");
            return;
        }
        File htmlFile = getCreatedHtmlFile(htmlFileName);
        if (htmlFile == null) {
            logger.info("htmlFile is null.");
            return;
        }
        generateHTMLFromPDF(pdfFile, htmlFile);
        response.setContentType(contentTypeHtml);
        Files.copy(htmlFile.toPath(), response.getOutputStream());
    }

    private void generateHTMLFromPDF(File pdfFile, File htmlFile) throws IOException {
        PDDocument pdf = PDDocument.load(pdfFile);
        try (Writer output = new PrintWriter(htmlFile, StandardCharsets.UTF_8)) {
            PDFDomTree pdfdomtree = new PDFDomTree();
            pdfdomtree.writeText(pdf, output);
        }
    }

    @Nullable
    private File getCreatedHtmlFile(@NotNull String name) throws IOException {
        File file = createStartDir(htmlPath);
        file.mkdirs();
        File htmlFile = new File(file, name);
        if (!htmlFile.exists()) {
            if (!htmlFile.createNewFile()) {
                return null;
            }
        }
        return htmlFile;
    }

    @Nullable
    private File createStartDir(@NotNull String absolutePath) {
        File file = new File(absolutePath);
        if (file.exists()) {
            return file;
        }
        if (!file.mkdirs()) {
            return null;
        }
        return file;
    }

    private void createAppDirs() {
        createStartDir(appFiles);
        createStartDir(pdfPath);
        createStartDir(htmlPath);
    }
}
