package domainapp.modules.simple.dom.impl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DownloadFileServlet extends HttpServlet {
    public void  doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException{
        String filepath = request.getParameter("filename");
        File downloadFile = new File(filepath);
        FileInputStream instream = new FileInputStream(downloadFile);

        String relativePath = getServletContext().getRealPath("");
        System.out.println("relativePath = "+relativePath);

        ServletContext context = getServletContext();

        String mimeType = context.getMimeType(filepath);
        if(mimeType == null){
            mimeType = "application/octet-stream";
        }
        System.out.println("MIME Type = " + mimeType);


        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey,headerValue);

        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while((bytesRead = instream.read(buffer)) != -1){
            outStream.write(buffer, 0 , bytesRead);
        }
        instream.close();
        outStream.close();
        File f = new File(filepath);
        f.delete();
    }
}
