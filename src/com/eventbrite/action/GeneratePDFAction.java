package com.eventbrite.action;

import com.eventbrite.domain.Certificate;
import com.eventbrite.mapper.CertificateMapper;
import com.eventbrite.utility.PdfGenerateHelper;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GeneratePDFAction extends ActionSupport implements ServletResponseAware, ServletRequestAware {
    private static final long serialVersionUID = 1L;
    private static final int NO_ID = -1;
    private static final String NO_XML = "";

    public InputStream getInputStream() {
        return inputStream;
    }

    private InputStream inputStream;
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private String result;

    // For access to the raw servlet request / response, eg for cookies
    protected HttpServletResponse servletResponse;
    protected HttpServletRequest servletRequest;


    @Override
    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    @Override
    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }




    public String execute() throws IOException {
        String eventId = servletRequest.getParameter("eventId");
        String fileName = servletRequest.getParameter("fileName");

        CertificateMapper mapper = new CertificateMapper();
        Certificate certificate = mapper.search(new Certificate(NO_ID,eventId,NO_XML));
        String xml = certificate.getXml();

        //get user name
        String userName = fileName.split(",")[0];
        // get date
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        xml = xml.replaceAll("username",userName);
        xml = xml.replaceAll("date",dateString);


        String path = "E://pdf";
        File file = new File(path);

        if(!file.exists()){
            file.mkdir();
        }
        path += "//" + eventId;
        file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        path += "//" + fileName + ".pdf";
        PdfGenerateHelper.CreatePdfFileByContent(xml,path);
        inputStream = new StringBufferInputStream("success");
        return "success";
    }






}
