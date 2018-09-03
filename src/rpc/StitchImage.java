package rpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import algorithm.ImageStitcher;

/**
 * Servlet implementation class StitchImage
 */
@WebServlet("/stitchimage")
public class StitchImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    private static final String UPLOAD_DIRECTORY = "uploaded_image";
 
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 5;  // 5MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StitchImage() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 检测是否为多媒体上传
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果不是则停止
            PrintWriter writer = response.getWriter();
            writer.println("Error: data form must include enctype=multipart/form-data");
            writer.flush();
            return;
        }
 
        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);
        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);
        // 中文处理
        upload.setHeaderEncoding("UTF-8"); 

        // 构造临时路径来存储上传的文件, 这个路径相对当前应用的目录
        String uploadPath = request.getServletContext().getRealPath("./") + UPLOAD_DIRECTORY;
         
        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
 
        try {
            // 解析请求的内容提取文件数据
            List<FileItem> formItems = upload.parseRequest(request);
            	
            if (formItems != null && formItems.size() > 0) {
	        		File[] files = new File[formItems.size()];
	        		for (int i = 0; i < files.length; i++) {
	        			FileItem item = formItems.get(i);
					// 处理不在表单中的字段
					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator + fileName;
						File storeFile = new File(filePath);
						// 在控制台输出文件的上传路径
						System.out.println(filePath);
						// 保存文件到硬盘
						item.write(storeFile);
						files[i] = storeFile;
					}
	        		}
	            		
	        		ImageStitcher stitcher = new ImageStitcher(files);
	        		File stitchedImage = stitcher.getOutput(uploadPath + File.separator + request.getRemoteHost() + "-output.png");
	        		
	        		FileInputStream fis = new FileInputStream(stitchedImage);
	        		int size = fis.available();
	        		byte data[] = new byte[size] ;
	        		fis.read(data) ;
	        		fis.close();
	        				
	        		//设置返回的文件类型
	        		response.setContentType("image/png");
	        		OutputStream os = response.getOutputStream() ;
	        		os.write(data);
	        		os.flush();
            }
        } catch (Exception e) {
            	e.printStackTrace();
        }
	}
}
