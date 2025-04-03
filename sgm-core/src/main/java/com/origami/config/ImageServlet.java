/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jodd.util.URLDecoder;

/**
 *
 * @author CarlosLoorVargas
 */
@WebServlet("/content/*")
public class ImageServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6276010640659534878L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String filename = URLDecoder.decode(request.getPathInfo().substring(1), "UTF-8");
        //System.out.println("filename "+filename+" - "+request.getContextPath());
        File file = new File(getServletContext().getRealPath(filename));
        response.setHeader("Content-Type", getServletContext().getMimeType(filename));
        //response.setHeader("Content-Type", URLConnection.guessContentTypeFromName(file.getName()));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        Files.copy(file.toPath(), response.getOutputStream());
    }

}
