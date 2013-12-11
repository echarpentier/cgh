package fr.pfgen.cgh.server.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpStatus;

import fr.pfgen.cgh.server.utils.IOUtils;

@SuppressWarnings("serial")
public class FileUploaderServlet extends HttpServlet{

	private Hashtable<String, File> appFiles; 

	@SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		appFiles = (Hashtable<String, File>)getServletContext().getAttribute("ApplicationFiles");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
		

		if (!(ServletFileUpload.isMultipartContent(request))){
			throw new ServletException("Not a multipart request");
		}

		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		PrintWriter pw = null;
		// Parse the request
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> items = upload.parseRequest(request);

			// Process the uploaded items
			@SuppressWarnings("rawtypes")
			Iterator iter = items.iterator();
			//String studyName = new String();
			String respString = new String();
			//String user = new String();
			//byte[] data = null;
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {  // Process a regular form field
					//String name = item.getFieldName();
					//String value = item.getString();
					//if (name.equals("studyName")){
					//	studyName = value;
					//}
					//if (name.equals("user")){
					//	user = value;
					//}
				} else {  // Process a file upload
					if (item.getSize()>2000000000){
						//response.setStatus(HttpStatus.SC_BAD_REQUEST);
						respString = "Error: File greater than 2Gb";
					}else{
						String suffix;
						if (item.getName().toLowerCase().endsWith(".pdf")){
							suffix = ".pdf";
						}else if (item.getName().toLowerCase().endsWith(".txt")){
							suffix = ".txt";
						}else if (item.getName().toLowerCase().endsWith(".zip")){
							suffix = ".zip";
						}else if (item.getName().toLowerCase().endsWith(".txt.gz")){
							suffix = ".txt.gz";
						}else{
							respString = "Error: Invalid file extension";
							break;
						}
						
						//data = item.get();
						File tmpFolder = appFiles.get("temporaryFolder");
						//File pedigreeFolder = new File(studyFolder, "Pedigree");
						if (!tmpFolder.exists()){
							tmpFolder.mkdir();
						}
						
						//File uploadedFile = File.createTempFile("upload_", suffix, tmpFolder);
						File uploadedFile = new File(tmpFolder, item.getName());
						OutputStream out = null;
						InputStream in = null;
						try{
							out = new FileOutputStream(uploadedFile);
							in = item.getInputStream();
							IOUtils.copyTo(in, out);

							out.flush();
							out.close();
							in.close();
							respString = uploadedFile.getAbsolutePath();
						}catch (IOException e) {
							e.printStackTrace();
							respString = "Error: can't copy file to server";
						}finally{
							IOUtils.safeClose(in);
							IOUtils.safeClose(out);
						}
						
					}
				}
			}
			
			response.setContentType("text/html");
			response.setHeader("Pragma", "No-cache");
			response.setDateHeader("Expires", 0);
			response.setHeader("Cache-Control", "no-cache");
			pw = response.getWriter();
			pw.println("<html>");
			pw.println("<body>");
			pw.println("<script type=\"text/javascript\">");
			pw.println("if (parent.uploadComplete) parent.uploadComplete('" + respString + "');");
			pw.println("</script>");
			pw.println("</body>");
			pw.println("</html>");
			pw.flush();
			pw.close();
		} catch (FileUploadException e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.getWriter().write("Error:"+e.getMessage());
			throw new IOException(e);
		} catch (IOException e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.getWriter().write("Error:"+e.getMessage());
			throw e;
		} finally {
			IOUtils.safeClose(pw);
		}
	}
}
