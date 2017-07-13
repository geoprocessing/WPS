package cn.edu.whu.opso.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.edu.whu.opso.OPSOConfig;
import cn.edu.whu.opso.WorkflowInstanceToProcess;
import cn.edu.whu.opso.algorithm.OPSOAlgorithmRepository;
 
/**
 * A Java servlet that handles file upload from client.
 *
 * @author www.codejava.net
 * 
 * modified by mzhang
 */
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
     
    // location to store file uploaded
    private static final String UPLOAD_DIRECTORY = "config"+ File.separator+ OPSOConfig.OPSO_FOLDER;
 
    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
 
    private static final String Algorithm_Name = "algorithm";
    private void responseMsg(PrintWriter writer,String msg){
    	writer.println(msg);
    	writer.flush();
    }
    
    /**
     * Upon receiving file upload submission, parses the request to read
     * upload data and saves the file on disk.
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    	PrintWriter writer = response.getWriter();
    	
    	String respMsg = "";
        // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            responseMsg(writer, "Error: Form must has enctype=multipart/form-data.");
            return;
        }
 
        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
         
        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);
         
        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        // constructs the directory path to store upload file
        // this path is relative to application's directory
        String uploadPath = getServletContext().getRealPath("")
                + File.separator + UPLOAD_DIRECTORY;
         
        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        String fileName = "";
        FileItem fileItem = null;
        try {
            // parses the request's content to extract file data
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);
 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                	if(item.isFormField()){
                		String fieldName = item.getFieldName();
                		String fieldValue = item.getString();
                		if(fieldName.equals(Algorithm_Name))
                			fileName = fieldValue;
                	}
                	else {
                       // String fileName = new File(item.getName()).getName();
                		fileItem = item;
                    }
                }
            }
        } catch (Exception ex) {
        	respMsg += "There was an error: "+ ex.getMessage();
        }
        
        if(fileName==null || fileName.equals("")){
        	responseMsg(writer, "Please input the algorithm name");
        	return;
        }
        
        if(fileItem == null){
        	responseMsg(writer, "Please choose the OPSO document.");
        	return;
        }
        
        if(OPSOAlgorithmRepository.getInstance().containsAlgorithm(fileName)){
        	responseMsg(writer, "The algorithm named "+fileName+" is already existed.");
        	return;
        }
        String filePath = uploadPath + File.separator + fileName + ".rdf";
        File storeFile = new File(filePath);
        // saves the file on disk
        try {
			fileItem.write(storeFile);
			WorkflowInstanceToProcess workflowInstanceToProcess = new WorkflowInstanceToProcess(filePath);
			workflowInstanceToProcess.setProcessName(fileName);
			boolean saved = workflowInstanceToProcess.save(new File(uploadPath + File.separator + fileName + ".xml"));
			if(saved){
				OPSOAlgorithmRepository.getInstance().addAlgorithm(fileName);
				respMsg += "Upload has been done successfully!";
			}else {
				respMsg += "Failed to parse the OPSO document to WPS process.";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			respMsg += "error occurred "+e.getMessage();
		}
        
        responseMsg(writer, respMsg);
    }
}