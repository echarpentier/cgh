package fr.pfgen.cgh.server.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.pfgen.cgh.server.utils.GlobalDefs;
import fr.pfgen.cgh.server.utils.IOUtils;

@SuppressWarnings("serial")
public class FileProviderServlet extends HttpServlet{

	@Override
	protected synchronized void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		String f = req.getParameter("file");
		if(f==null) throw new ServletException("Undefined file");
		File file=new File(f);
		if (!file.exists() || !file.isFile()) throw new ServletException("File not found on server");
		
		if (!file.getAbsolutePath().toLowerCase().startsWith(GlobalDefs.getInstance().getCghPath().toLowerCase())){
			throw new RuntimeException("Cannot access file not under main CGH path:\n"+"file: "+file.getAbsolutePath()+"\nmain path: "+GlobalDefs.getInstance().getCghPath());
		}

		if (file.getName().toLowerCase().endsWith(".jpg")){
			resp.setHeader("Content-Type", "image/jpeg");
		}else if (file.getName().toLowerCase().endsWith(".png")){
			resp.setHeader("Content-Type", "image/png");
		}else if (file.getName().toLowerCase().endsWith(".pdf")){
			resp.setHeader("Content-Type", "application/pdf");
		}else if (file.getName().toLowerCase().endsWith(".txt") || file.getName().toLowerCase().endsWith(".gff") || file.getName().toLowerCase().endsWith(".wig")){
			resp.setHeader("Content-Type", "text/plain");
		}else if (file.getName().toLowerCase().endsWith(".zip")){
			resp.setHeader("Content-Type", "application/zip");
		}else{
			throw new ServletException("Can't find file extension");
		}
		
		FileInputStream reader=null;
		OutputStream out = null;
		try {
			reader = new FileInputStream(f);
			resp.setHeader("Content-Length", String.valueOf(file.length()));
			resp.setHeader("Content-disposition", "attachment;filename=\"" + file.getName().replaceAll("__\\w+\\.", ".") + "\"");
			out = resp.getOutputStream();
			IOUtils.copyTo(reader, out);
			out.close();
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			IOUtils.safeClose(out);
			IOUtils.safeClose(reader);
		}
	}
}
