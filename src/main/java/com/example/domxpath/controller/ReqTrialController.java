package com.example.domxpath.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@RestController
public class ReqTrialController {

    @PostMapping(value = "/reqtrial", produces = "application/xml; charset=UTF-8")
    public String requestTrial(@RequestBody String requestBody) {

        try (
                InputStream is = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        ) {
            DocumentBuilderFactory documentBuilderfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderfactory.newDocumentBuilder();
            Document document = documentBuilder.parse(is);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xPath = xPathfactory.newXPath();

            // 要素を追加
            Node user = (Node)xPath.evaluate("/request/user", document, XPathConstants.NODE);
            ((Element)user).setAttribute("age", "100");

            // 要素の値を変更   
            Node name = (Node)xPath.evaluate("/request/user/name", document, XPathConstants.NODE);
            name.setTextContent(name.getTextContent() + " さん");

            return documentToString(document);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String documentToString(Document document) throws Exception {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(output);
            transformer.transform(domSource, streamResult);
            return output.toString(StandardCharsets.UTF_8);
        }
    }
}
